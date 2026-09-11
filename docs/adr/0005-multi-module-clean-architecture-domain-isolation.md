# ADR 0005: Isolamento de Domínio e Clean Architecture em Estrutura Multi-Módulos Maven

* **Status**: Aceito (Accepted)
* **Data**: 2026-09-10
* **Autores**: Tech Challenge Garage Team
* **Decisores Técnicos**: Especialistas em Arquitetura de Software e Desenvolvimento Java

---

## 1. Contexto e Declaração do Problema

A aplicação `api-garage` concentra as regras de negócio centrais de uma oficina mecânica: ordens de serviço, orçamento, inventário de peças, catálogo de veículos, clientes e funcionários.

Em arquiteturas convencionais monolíticas ou de camada única em Spring Boot, é comum ocorrer o vazamento de preocupações de infraestrutura para o coração do negócio:
* Entidades de banco misturadas com anotações de validação de API (`@NotNull`, `@Size` de controllers);
* Casos de uso acoplados a classes HTTP (`HttpServletRequest`, `ResponseEntity`);
* Dificuldade de testar regras de negócio puras sem subir o contexto pesado do Spring (`@SpringBootTest`).

---

## 2. Decisão Arquitetural

Decidimos estruturar a aplicação seguindo os princípios da **Clean Architecture** (Robert C. Martin / Uncle Bob) e do **Domain-Driven Design (DDD)**, fisicamente segregados através de um **projeto multi-módulos Maven**:

```
api-garage (Root POM)
├── domain       (Módulo de Domínio Puro)
├── application  (Módulo de Casos de Uso, Controllers REST e Adapters)
└── e2e          (Módulo de Testes de Integração e Caixa-Preta BDD)
```

### 2.1. Responsabilidades de Cada Módulo

1. **`domain` (Core do Negócio)**:
   - Contém entidades ricas, objetos de valor (Value Objects), enums, exceções de negócio (`BusinessException`) e interfaces de repositório.
   - **Regra de Ouro da Dependência**: Não depende de nenhum outro módulo da aplicação e possui mínimo acoplamento com frameworks.
   - Seus testes unitários são executados com JUnit 5 e AssertJ puro, rodando centenas de asserções em milissegundos.

2. **`application` (Orquestração e Transporte)**:
   - Implementa a camada externa de interface do usuário / APIs: Controllers RESTful (`v1`), DTOs de entrada e saída (`Dto.Request`, `Dto.Response`), mappers MapStruct e configurações de segurança Spring Security.
   - Depende do módulo `domain`, convertendo DTOs de transporte em entidades de domínio.

3. **`e2e` (Garantia de Qualidade de Ponta a Ponta)**:
   - Módulo isolado de testes que executa cenários BDD/Cucumber tratando a aplicação compilada como caixa-preta via REST Assured.

---

## 3. Consequências

### Positivas (Benefícios):
* **Proteção das Regras de Negócio**: Mudanças em contratos de API ou bibliotecas web não impactam o módulo `domain`.
* **Velocidade de Feedback no Desenvolvimento**: Testes no módulo `domain` rodam em frações de segundo sem necessidade de subir contêineres ou contexto Spring.
* **Manutenibilidade e Baixo Acoplamento**: A regra de dependência é forçada em tempo de compilação pelo Maven; se alguém tentar injetar um Controller dentro do `domain`, o compilador quebra o build.
* **Alta Coesão**: DTOs, mappers e contratos OpenAPI ficam centralizados no módulo `application`.

### Negativas / Trade-offs:
* **Overhead de Mapeamento**: Exige mapeamento explícito entre DTOs (`CustomerDto.Request`) e entidades (`Customer`), gerenciado de forma automatizada e performática pelo MapStruct.
* **Dependência de Build Local**: Alterações em classes de teste do `domain` exigem compilação do `test-jar` para serem enxergadas pelo módulo `application`.

---

## 4. Alternativas Consideradas e Rejeitadas

| Alternativa | Veredito | Motivo da Rejeição |
| :--- | :--- | :--- |
| **1. Módulo Único com Separação Apenas por Pacotes** | **Rejeitada** | Permite que desenvolvedores quebrem acidentalmente a regra de dependência, importando anotações web ou controllers dentro de regras de negócio. |
| **2. Arquitetura em Camadas Tradicional (Controller -> Service -> Repository)** | **Rejeitada** | Tende a gerar modelos de domínio anêmicos (*Anemic Domain Model*), concentrando toda a lógica em services procedurais gigantes. |

---

## 5. Referências
* **Clean Architecture** - Robert C. Martin.
* **Domain-Driven Design: Tackling Complexity in the Heart of Software** - Eric Evans.
