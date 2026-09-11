# ADR 0003: Padrão de Identidade Unificada (Keycloak UUID como Chave Primária Relacional)

* **Status**: Aceito (Accepted)
* **Data**: 2026-09-10
* **Autores**: Tech Challenge Garage Team
* **Decisores Técnicos**: Especialistas em Arquitetura de Software e Engenharia de Dados

---

## 1. Contexto e Declaração do Problema

Com a introdução do Keycloak como Provedor de Identidade (IdP) e Servidor de Autenticação OIDC centralizado, surgiu o desafio de como correlacionar os usuários autenticados (portadores de tokens JWT) com os registros de negócio nas tabelas da oficina (`garage.employee` e `garage.customer`).

No desenho anterior:
1. O Keycloak gerava seu próprio identificador primário universal (`UUID`) no momento da criação do usuário.
2. A `api-garage` utilizava identificadores auto-gerados internamente (`@GeneratedValue private UUID id = UUID.randomUUID()`) ou sequenciais na persistência.
3. Isso gerava um problema de **duplo identificador (Dual Identity)**: para saber qual funcionário ou cliente correspondia ao token JWT, era necessário:
   - Ou criar tabelas intermediárias de mapeamento (de-para / `user_mapping`);
   - Ou manter uma tabela redundante `users` na aplicação de negócio;
   - Ou realizar consultas pesadas por CPF/e-mail a cada requisição para descobrir o ID interno da entidade.

---

## 2. Decisão Arquitetural

Decidimos adotar o **Padrão de Identidade Unificada (*Unified Identity Pattern*)**:
O identificador universal gerado pelo Keycloak (`keycloak_user_id` / claim `sub` do JWT) é adotado **diretamente como a Chave Primária (`id`)** das entidades `Employee` e `Customer` no banco de dados relacional PostgreSQL da oficina.

### 2.1. Ajustes no Modelo de Domínio (JPA / Hibernate)
1. **Remoção de `@GeneratedValue`**: Removemos a anotação que forçava a geração automática do ID pelo provedor JPA.
2. **Definição Flexível com Lifecycle Hook (`@PrePersist`)**:
   - Se o `id` (Keycloak UUID) for explicitamente fornecido durante o onboarding, ele é preservado integralmente na persistência.
   - Caso não seja fornecido (ex: testes legados ou registros anônimos), um UUID aleatório é gerado como fallback:
     ```java
     @Builder.Default
     private UUID id = UUID.randomUUID();

     @PrePersist
     public void prePersist() {
         if (this.id == null) {
             this.id = UUID.randomUUID();
         }
     }
     ```
3. **Mapeamento nos DTOs e Endpoints**:
   - `EmployeeDto.Request` e `CustomerDto.Request` passam a aceitar o campo opcional `UUID id`, que é injetado pelo orquestrador de backend (Lambda) durante o provisionamento.

---

## 3. Consequências

### Positivas (Benefícios):
* **Zero Overhead de De-Para**: A claim `sub` (subject) do Access Token JWT RS256 é exatamente a chave estrangeira em tabelas de Ordens de Serviço (`garage.work_order.employee_id`) e Veículos (`garage.vehicle.customer_id`).
* **Eliminação de Tabelas Redundantes**: A aplicação não precisa manter tabelas de autenticação, senhas ou mapeamentos de usuários no PostgreSQL da oficina.
* **Consultas Ultrarrápidas**: Ao receber uma requisição autenticada, a aplicação obtém o `userId` direto do token e consulta `findById(UUID)` na chave primária indexada, com complexidade de tempo $O(1)$.
* **Integridade Referencial Forte**: Consistência semântica em todo o ciclo de vida do usuário na nuvem.

### Negativas / Trade-offs:
* **Dependência do IdP no Onboarding**: A criação do registro de negócio exige que a identidade já tenha sido provisionada previamente no Keycloak (resolvido via orquestração com Saga Rollback no [ADR 0002](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0002-centralized-onboarding-saga-orchestration.md)).

---

## 4. Alternativas Consideradas e Rejeitadas

| Alternativa | Veredito | Motivo da Rejeição |
| :--- | :--- | :--- |
| **1. Tabela de Mapeamento De-Para (`keycloak_id` <-> `local_id`)** | **Rejeitada** | Adiciona complexidade de junções (JOINs adicionais), manutenção de índices redundantes e mais pontos de falha. |
| **2. Tabela Genérica de Usuários Locais (`users`)** | **Rejeitada** | Viola os limites de contexto do DDD (Bounded Contexts). O domínio da oficina não gerencia credenciais de login, apenas perfis operacionais (mecânicos) e contratantes (clientes). |
| **3. Correlação por CPF/Documento em Runtime** | **Rejeitada** | Desempenho degradado; obriga o banco a realizar buscas por strings indexadas em vez de utilizar o índice de chave primária em formato binário UUID. |

---

## 5. Referências e Códigos Impactados
* [`Employee.java`](file:///c:/git/fiap/15-soat-tech-challenge-garage/domain/src/main/java/br/com/fiap/garage/domain/entity/Employee.java)
* [`Customer.java`](file:///c:/git/fiap/15-soat-tech-challenge-garage/domain/src/main/java/br/com/fiap/garage/domain/entity/Customer.java)
* [`0002-centralized-onboarding-saga-orchestration.md`](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0002-centralized-onboarding-saga-orchestration.md)
