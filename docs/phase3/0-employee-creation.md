## Diagrama de Sequência

**Cadastro de Funcionários**

```mermaid
sequenceDiagram
    autonumber
    actor admin as Administrador
    participant front as Frontend (Web/App)
    participant lambda as Lambda
    participant keycloak as Keycloak
    participant db as Banco de Dados

    admin->>front: Cadastra funcionários
    front->>lambda: POST /employees
    lambda->>keycloak: POST /keycloak
    keycloak->>db: Salva no funcionário no banco de dados
```