# Plano de Arquitetura & Saneamento: Remoção de Tabelas e Código Legado de Usuários no `api-garage`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-05  
> **Autor**: Engenheiro Especialista em Desenvolvimento & Arquitetura de Software  
> **Repositório Alvo**: `15-soat-tech-challenge-garage`  

---

## 1. Contexto & Diagnóstico do Cenário Atual

Com a evolução da arquitetura do ecossistema da oficina mecânica:
1. A **gestão de identidades, credenciais (senhas) e autorizações** foi completamente transferida para o **Keycloak** (operando com Realm `garage`).
2. O cadastro de usuários e emissão de tokens JWT foi centralizado na **AWS Lambda Serverless** (`garage-auth-handler` / `15-soat-tech-challenge-lamda`), responsável por validar o algoritmo Módulo 11 (CPF/CNPJ) e interagir com a Admin API e OIDC do Keycloak.
3. O microsserviço `api-garage` passou a atuar como um **OAuth2 Resource Server** nativo e stateless (`KeycloakJwtAuthenticationConverter`), validando tokens JWT via JWKS criptográfico sem consultar o banco de dados.

Contudo, permaneceram no `api-garage` diversos artefatos legados da arquitetura antiga de autenticação interna baseada em tabelas locais, senhas criptografadas com BCrypt e tokens HS256 locais:
* **Tabelas do Banco de Dados**: `garage.auth`, `garage.users`, `garage.users_auth` e foreign keys de herança em `garage.customer` e `garage.employee`.
* **Entidades JPA & Persistência**: `User.java` (tabela `users` com estratégia `JOINED`), `Auth.java` (tabela `auth`), `UserRepository.java`, `UserRepositoryExt.java`.
* **Controllers & Serviços de Autenticação**: `AuthController.java` (`/auth/login`), `AuthSwagger.java`, `LoginService.java` (`UserDetailsService`), `TokenService.java` (`jwt.secret`).
* **Regras de Criptografia de Senha**: Dependências e injeções de `PasswordEncoder` em `CustomerCreationUseCase` e `EmployeeCreationUseCase`, e campos de senha em DTOs/requests de `Customer` e `Employee`.

Como a infraestrutura no **AWS Labs foi totalmente zerada** (conforme informado pelo usuário), não há necessidade de manter migrações legadas ou compatibilidade com dados pré-existentes no PostgreSQL.

---

## 2. Inventário Completo do que Deve ser Removido / Saneado

### 2.1 Banco de Dados (`V1__garage.sql`)

| Elemento no Banco | Ação | Justificativa Técnica |
| :--- | :--- | :--- |
| **`garage.auth`** | **REMOVER** | Tabela legada de papéis/autoridades locais. As roles agora são emitidas diretamente no claim `realm_access.roles` do JWT pelo Keycloak. |
| **`garage.users_auth`** | **REMOVER** | Tabela associativa (*join table*) many-to-many entre usuários e autoridades. Totalmente obsoleta. |
| **`garage.users`** | **REMOVER** | Tabela mãe de `customer` e `employee`. Guardava credenciais/senhas (`password`), e-mails e usernames legados. A gestão de contas é 100% Keycloak. |
| **`garage.customer`** | **REESTRUTURAR** | Remover a FK `fk_customer_users FOREIGN KEY (id) REFERENCES garage.users(id)`. A tabela torna-se autônoma contendo: `id`, `name`, `email`, `document` (CPF/CNPJ), `created_at`, `updated_at`. |
| **`garage.employee`** | **REESTRUTURAR** | Remover a FK `fk_employee_users FOREIGN KEY (id) REFERENCES garage.users(id)`. A tabela torna-se autônoma contendo: `id`, `name`, `email`, `cpf`, `created_at`, `updated_at`. |

### 2.2 Código-Fonte no Módulo `domain`

| Arquivo / Classe | Ação | Justificativa Técnica |
| :--- | :--- | :--- |
| **`User.java`** | **REMOVER** | Entidade JPA base com `UserDetails`, `password`, `authorities` e `@Inheritance(strategy = JOINED)`. |
| **`Auth.java`** | **REMOVER** | Entidade JPA de autoridades locais. |
| **`UserRepository.java`** | **REMOVER** | Interface de repositório Spring Data para `User`. |
| **`AuthorityFactory.java`** | **REMOVER** | Classe utilitária de testes para montagem de authorities locais. |
| **`Customer.java`** | **REFATORAR** | Passa a estender diretamente `AuditableEntity` (em vez de `User`). Mantém atributos próprios de domínio: `id`, `name`, `email`, `document`, `vehicles`. Elimina `password` e `authorities`. |
| **`Employee.java`** | **REFATORAR** | Passa a estender diretamente `AuditableEntity` (em vez de `User`). Mantém atributos próprios de domínio: `id`, `name`, `email`, `cpf`. Elimina `password` e `authorities`. |
| **`CustomerCreationUseCase.java`** | **REFATORAR** | Remover injeção de `PasswordEncoder` e chamada a `encodePassword()`. O use case apenas salva os dados de negócio da oficina. |
| **`EmployeeCreationUseCase.java`** | **REFATORAR** | Remover injeção de `PasswordEncoder` e chamada a `encodePassword()`. O use case apenas salva os dados de negócio da oficina. |
| **`CustomerFactory.java` / `EmployeeFactory.java`** | **REFATORAR** | Remover campos de senha, username redundante e `authority`. |

### 2.3 Código-Fonte no Módulo `application`

| Arquivo / Classe | Ação | Justificativa Técnica |
| :--- | :--- | :--- |
| **`AuthController.java`** | **REMOVER** | Endpoint `/auth/login` legado. Autenticação é realizada via Keycloak / Lambda. |
| **`AuthSwagger.java`** | **REMOVER** | Documentação Swagger/OpenAPI do endpoint de autenticação legado. |
| **`AuthControllerTest.java`** | **REMOVER** | Suíte de testes unitários do controller excluído. |
| **`LoginService.java`** | **REMOVER** | Implementação de `UserDetailsService` que consultava o banco a cada requisição. Inútil no Resource Server stateless. |
| **`TokenService.java`** | **REMOVER** | Emissor/validador de token HS256 local baseado em `jwt.secret`. |
| **`UserRepositoryExt.java`** | **REMOVER** | Adaptador JPA de `UserRepository`. |
| **`UserRepositoryExtTest.java`** | **REMOVER** | Testes de integração do repositório de usuários excluído. |
| **`SecurityConfig.java`** | **REFATORAR** | Remover rota `/auth/login` das regras de `permitAll()` e remover beans obsoletos `PasswordEncoder` e `AuthenticationManager`. |
| **`CustomerDef.java` / `EmployeeDef.java`** | **REFATORAR** | Remover o campo `password` de `Request` (não há tráfego nem armazenamento de senhas na API de negócio). |
| **`CustomerDto.java` / `EmployeeDto.java`** | **REFATORAR** | Remover o campo `password` de `Request`. |
| **`application*.properties`** | **REFATORAR** | Remover chaves órfãs `jwt.secret` e `jwt.expiration`. |

---

## 3. Opções Arquiteturais Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Saneamento Integral e Domínio Rico Autônomo (Clean DDD)

* **Motivação Técnica**:
  Alinhada aos princípios de **Clean Architecture**, **Domain-Driven Design (DDD)** e **KISS/YAGNI**. A responsabilidade de autenticação, credenciais e gerenciamento de identidade pertence ao Identity Provider (Keycloak / Lambda). O microsserviço `api-garage` é responsável estritamente pela gestão de clientes, veículos, funcionários, materiais e ordens de serviço.
* **Características da Solução**:
  1. **Remoção total do código morto**: Elimina `User`, `Auth`, `UserRepository`, `UserRepositoryExt`, `AuthController`, `LoginService` e `TokenService`.
  2. **Independência de Entidades**: `Customer` e `Employee` tornam-se entidades independentes estendendo `AuditableEntity`, com seus próprios identificadores e dados cadastrais.
  3. **Remoção de Senhas**: Elimina campos de senha de todos os DTOs de request e elimina `PasswordEncoder` dos use cases.
  4. **Atualização da Migration `V1__garage.sql`**: Limpeza na raiz do arquivo SQL, mantendo a migration inicial concisa e correta para o novo provisionamento no AWS Labs.
* **Vantagens**:
  - ✅ **Zero Débito Técnico**: Elimina 100% do código morto e tabelas desnecessárias.
  - ✅ **Segurança Máxima**: Nenhuma senha ou hash de credencial transita ou é armazenado no banco de dados da oficina (LGPD / PCI-DSS compliance).
  - ✅ **Alta Performance**: Elimina tabelas intermediárias e joins de herança JPA (`HT_users`, `HTE_customer`).
  - ✅ **Simplicidade Operacional**: Schema do PostgreSQL limpo e enxuto.
* **Trade-offs**:
  - Requer atualização pontual de testes unitários que mockavam `PasswordEncoder` ou usavam `AuthorityFactory`.

---

### 🥈 Opção 2 (Alternativa): Desacoplamento com Vínculo Explícito ao Keycloak (`keycloakUserId`)

* **Como Funciona**:
  Similar à Opção 1 na remoção de `User`, `Auth`, `LoginService` e `TokenService`, porém adiciona uma coluna explícita `keycloak_user_id UUID UNIQUE` nas tabelas `customer` e `employee`.
* **Vantagens**:
  - Facilita correlação direta entre o UUID interno da oficina e o UUID emitido pelo Keycloak, caso o cliente prefira que a chave primária da oficina não seja o próprio ID do Keycloak.
* **Desvantagens**:
  - Adiciona uma coluna adicional e complexidade de mapeamento quando o `document` (CPF/CNPJ) ou o próprio `id` já podem atuar como identificador unívoco no sistema.

---

### 🥉 Opção 3 (Minimalista / Conservadora): Remoção Apenas das Tabelas do BD mantendo Classes com `@Deprecated`

* **Como Funciona**:
  Remove as tabelas `users` e `auth` no SQL, mas mantém as classes Java marcadas com `@Deprecated` e `@Transient`.
* **Vantagens**:
  - Menos alterações imediatas em código de teste legado.
* **Desvantagens**:
  - ❌ **Anti-pattern**: Acúmulo de débito técnico grave, código confuso e classes zumbis. Não recomendado.

---

## 4. Plano de Execução Proposto (Opção 1 Recomendada)

1. **Atualização do DDL do Banco de Dados**:
   - Editar `application/src/main/resources/db/migrations/V1__garage.sql`:
     - Excluir definições de `garage.auth`, `garage.users`, `garage.users_auth`.
     - Atualizar `garage.customer` e `garage.employee` para conterem colunas próprias (`name`, `email`, etc.) sem FK para `garage.users`.
2. **Exclusão de Classes Obsoletas**:
   - Deletar `Auth.java`, `User.java`, `UserRepository.java`, `UserRepositoryExt.java`, `UserRepositoryExtTest.java`, `AuthorityFactory.java`.
   - Deletar `AuthController.java`, `AuthSwagger.java`, `AuthControllerTest.java`, `LoginService.java`, `TokenService.java`.
3. **Refatoração das Entidades e Use Cases**:
   - Atualizar `Customer.java` e `Employee.java`.
   - Atualizar `CustomerCreationUseCase.java` e `EmployeeCreationUseCase.java`.
   - Atualizar `SecurityConfig.java` e `application*.properties`.
   - Atualizar `CustomerDef.java`, `EmployeeDef.java`, `CustomerDto.java`, `EmployeeDto.java`.
4. **Adequação dos Testes (TDD / Red-Green-Refactor)**:
   - Atualizar `CustomerFactory`, `EmployeeFactory`, assertions e testes unitários de casos de uso e mappers.
   - Executar `mvn clean test` em todos os módulos para validar 100% de cobertura e sucesso.
5. **Governança & Git**:
   - Solicitar permissão explícita ao usuário antes de qualquer commit ou push.
