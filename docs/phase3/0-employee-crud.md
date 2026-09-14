## Diagrama de Sequência

**Cadastro de Funcionários (Orquestração no Backend com Compensação Saga)**

<div style="overflow-x: auto; width: 100%;">
<div style="min-width: 1300px;">

```mermaid
sequenceDiagram
    autonumber
    actor admin as Administrador
    participant front as Frontend (Web/App)
    participant lambda as Lambda (garage-auth-handler)
    participant keycloak as Keycloak (IdP / OIDC)
    participant keycloakDb as PostgreSQL (Keycloak DB)
    participant apiGarage as api-garage (Resource Server)
    participant garageDb as PostgreSQL (Garage DB)

    Note over admin,front: Solicitação de Cadastro Autenticada (Admin)
    admin->>front: Cadastra funcionário<br/>(nome, email, CPF, senha, cargo)
    front->>lambda: POST /register<br/>(Authorization: Bearer Admin_JWT)<br/>{ role: "EMPLOYEE", name, email, document: CPF, password }
    
    Note over lambda,keycloakDb: 1. Validação RBAC e Provisionamento IAM
    lambda->>lambda: Valida permissão do Admin<br/>e Módulo 11 (CPF)
    lambda->>keycloak: POST /admin/realms/garage/users<br/>(Bearer Admin Token)
    keycloak->>keycloakDb: Salva credenciais e role EMPLOYEE
    keycloakDb-->>keycloak: Confirma persistência
    keycloak-->>lambda: Retorna 201 Created (keycloak_user_id)

    Note over lambda,garageDb: 2. Propagação Transacional via Rede Privada (VPC)
    lambda->>apiGarage: POST /v1/employees (Internal VPC / Service Token)<br/>{ id: keycloak_user_id, name, email, cpf }

    alt Sucesso no Catálogo da Oficina
        apiGarage->>garageDb: Salva funcionário<br/>(garage.employee.id = keycloak_user_id)
        garageDb-->>apiGarage: Confirma persistência
        apiGarage-->>lambda: Retorna 201 Created
        lambda-->>front: Retorna 201 Created (Onboarding Concluído)
    else Falha no Catálogo da Oficina (Compensação Saga / Rollback)
        apiGarage-->>lambda: Erro (4xx / 5xx / Timeout)
        Note over lambda,keycloak: Rollback Saga: expurga credencial órfã no Keycloak
        lambda->>keycloak: DELETE /admin/realms/garage/users/{keycloak_user_id}
        keycloak->>keycloakDb: Remove usuário
        keycloakDb-->>keycloak: Removido
        keycloak-->>lambda: 204 No Content (Rollback concluído)
        lambda-->>front: Retorna 502 Bad Gateway<br/>(Operação revertida, tente novamente)
    end
```

</div>
</div>