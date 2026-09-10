## Diagrama de Sequência

**Criação de Ordem de Serviço**

```mermaid
sequenceDiagram
    autonumber
    actor emp as Atendente
    participant front as Frontend (Web/App)
    participant lambda as Lambda (garage-auth-handler)
    participant keycloak as Keycloak (IdP / OIDC)
    participant keycloakDb as PostgreSQL (Keycloak DB)
    participant apiGateway as AWS API Gateway
    participant apiGarage as api-garage (Resource Server)
    participant garageDb as PostgreSQL (Garage DB)

    alt Autenticação
        emp->>front: Solicita login (CPF / e-mail e senha)
        front->>lambda: POST /auth/login { username, password }
        lambda->>keycloak: POST /realms/garage/protocol/openid-connect/token (grant_type=password)
        keycloak->>keycloakDb: Consulta usuário e valida credenciais
        keycloakDb-->>keycloak: Retorna dados do usuário
        keycloak-->>lambda: Retorna Access Token JWT (RS256)
        lambda-->>front: Retorna Access Token JWT (Bearer)
    end

    Note over emp,garageDb: Abertura da Ordem de Serviço (Recepção do Veículo)
    emp->>front: Cadastra ordem de serviço (veículo, mecânico responsável, serviços solicitados)
    front->>apiGateway: POST /v1/work-orders (Authorization: Bearer JWT)
    apiGateway->>apiGarage: POST /v1/work-orders (Authorization: Bearer JWT)
    apiGarage->>apiGarage: Valida JWT via JWKS (Stateless)
    apiGarage->>garageDb: Valida vínculos e salva OS com status RECEIVED (garage.work_orders)
    garageDb-->>apiGarage: Retorna id e status RECEIVED da ordem de serviço
    apiGarage-->>apiGateway: Retorna 201 Created com dados e id da ordem de serviço
    apiGateway-->>front: Retorna 201 Created com dados e id da ordem de serviço
```