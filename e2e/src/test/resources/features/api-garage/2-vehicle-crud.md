## Diagrama de Sequência

**Cadastro de Veículo**

<div style="overflow-x: auto; width: 100%;">
<div style="min-width: 1300px;">

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
        emp->>front: Solicita login<br/>(CPF / e-mail e senha)
        front->>lambda: POST /auth/login<br/>{ username, password }
        lambda->>keycloak: POST /realms/garage/protocol/openid-connect/token<br/>(grant_type=password)
        keycloak->>keycloakDb: Consulta usuário e valida credenciais
        keycloakDb-->>keycloak: Retorna dados do usuário
        keycloak-->>lambda: Retorna Access Token JWT (RS256)
        lambda-->>front: Retorna Access Token JWT (Bearer)
    end

    Note over emp,garageDb: Cadastro de Veículo Vinculado ao Cliente
    emp->>front: Cadastra veículo<br/>(placa, modelo, marca, ano, id do cliente)
    front->>apiGateway: POST /v1/vehicles<br/>(Authorization: Bearer JWT)
    apiGateway->>apiGarage: POST /v1/vehicles<br/>(Authorization: Bearer JWT)
    apiGarage->>apiGarage: Valida JWT via JWKS (Stateless)
    apiGarage->>garageDb: Valida cliente e salva veículo<br/>(garage.vehicle)
    garageDb-->>apiGarage: Retorna id e dados do veículo
    apiGarage-->>apiGateway: Retorna 201 Created<br/>com dados e id do veículo
    apiGateway-->>front: Retorna 201 Created<br/>com dados e id do veículo
```

</div>
</div>