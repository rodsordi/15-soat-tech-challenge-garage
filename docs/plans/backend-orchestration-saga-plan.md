# Plano de Arquitetura: Orquestração no Backend via Lambda com Ação Compensatória (Saga) para Employee e Customer

## 1. Contexto e Diagnóstico

No desenho anterior, o Frontend assumia indevidamente o papel de coordenador transacional (*Smart Client / Client-Side Orchestration*):
1. Chamava `POST /register` no Lambda para criar credenciais no Keycloak.
2. Chamava `POST /v1/employees` ou `POST /v1/customers` na `api-garage` para persistir os dados no catálogo da oficina.

### Problemas Identificados:
- **Falha Parcial (*Partial Failure*)**: Se o usuário for criado no Keycloak, mas a chamada à `api-garage` falhar (queda de rede mobile, timeout, erro 500), o sistema entra em estado inconsistente. O usuário consegue se autenticar, mas não tem registro no banco da oficina (`garage.employee` ou `garage.customer`), quebrando regras de negócio em ordens de serviço, agendamentos e veículos.
- **Ausência de Mecanismo de Compensação**: Clientes frontend não são confiáveis para executar transações compensatórias (*rollback*).
- **Vazamento de Topologia**: O front-end precisa conhecer a topologia interna de múltiplos serviços e costurar IDs manualmente.

---

## 2. Visão Geral da Solução: Opção A (Employee & Customer)

A **Opção A** centraliza todo o onboarding no **AWS Lambda (`garage-auth-handler`)**, que atua como uma fachada atômica (*Backend For Frontend / API Gateway Facade*) implementando o **Padrão Saga Coreografado com Ação Compensatória** de forma simétrica para **Employee** e **Customer**:

```
[ Frontend (Web / App / Totem) ] 
               |
               | 1. POST /register { role, name, email, document, password, ... }
               v
  [ AWS Lambda (garage-auth-handler) ]
               |
               | 2. Valida Módulo 11 (CPF / CNPJ)
               | 3. POST /admin/realms/garage/users (Keycloak)
               v
       [ Keycloak IdP ]
               |
               +---> Retorna 201 Created com (keycloak_user_id)
               |
  [ AWS Lambda (garage-auth-handler) ]
               |
       +-------+---------------------------------------+
       | Se role == 'EMPLOYEE'                         | Se role == 'CUSTOMER'
       v                                               v
[ POST /v1/employees ]                          [ POST /v1/customers ]
{ id: keycloak_user_id,                         { id: keycloak_user_id,
  name, email, cpf }                              name, email, document, vehicles }
       \                                               /
        \                                             /
         +--------------------+----------------------+
                              |
                              v
                [ api-garage (Resource Server) ]
                              |
     +------------------------+------------------------+
     | Sucesso (201 Created)                           | Falha (4xx/5xx/Timeout)
     v                                                 v
Grava no PostgreSQL                             Lambda executa COMPENSAÇÃO (Rollback):
(garage.employee ou garage.customer)             DELETE /admin/realms/garage/users/{keycloak_user_id}
Lambda retorna 201 Created ao Front             Lambda retorna 502 Bad Gateway ao Front
```

---

## 3. Especificação dos Contratos de Integração

### 3.1. Onboarding de Employee
* **Entrada no Lambda (`POST /register`)**:
  ```json
  {
    "role": "EMPLOYEE",
    "name": "Carlos Silva",
    "email": "carlos.silva@garage.com",
    "document": "69005975059",
    "password": "SenhaForte@2026!"
  }
  ```
* **Chamada Interna Lambda -> `api-garage` (`POST /v1/employees`)**:
  ```json
  {
    "id": "7a403fc9-3c96-408c-984f-1fea2729b59f",
    "name": "Carlos Silva",
    "email": "carlos.silva@garage.com",
    "cpf": "69005975059"
  }
  ```

---

### 3.2. Onboarding de Customer
* **Entrada no Lambda (`POST /register`)**:
  ```json
  {
    "role": "CUSTOMER",
    "name": "Maria Santos",
    "email": "maria.santos@exemplo.com",
    "document": "27614623000100",
    "password": "SenhaForte@2026!",
    "vehicles": [
      {
        "make": "Toyota",
        "model": "Corolla",
        "licensePlate": "ABC1234",
        "manufactureYear": "2024"
      }
    ]
  }
  ```
* **Chamada Interna Lambda -> `api-garage` (`POST /v1/customers`)**:
  ```json
  {
    "id": "36c9df52-01eb-4ffd-a0c1-1494440aedef",
    "name": "Maria Santos",
    "email": "maria.santos@exemplo.com",
    "document": "27614623000100",
    "vehicles": [
      {
        "make": "Toyota",
        "model": "Corolla",
        "licensePlate": "ABC1234",
        "manufactureYear": "2024"
      }
    ]
  }
  ```

---

## 4. Tratamento de Falha e Ação Compensatória (Rollback)

Para **ambos os perfis (Employee e Customer)**:
1. Se a chamada para a `api-garage` resultar em status `>= 400` ou timeout de rede:
2. O Lambda captura a exceção no bloco `catch`;
3. Executa a compensação:
   ```javascript
   await deleteUser(createdUser.id);
   ```
4. Retorna resposta padronizada de erro:
   ```json
   {
     "error": "Catalog Integration Error",
     "message": "Falha ao registrar dados no catálogo da oficina. A conta no Keycloak foi revertida para garantir integridade. Tente novamente.",
     "statusCode": 502
   }
   ```

---

## 5. Plano de Mudanças por Componente

### 5.1. Repositório `15-soat-tech-challenge-garage`
- **[0-employee-creation.md](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/phase3/0-employee-creation.md)**: Atualizar diagrama com fluxo atômico e compensação para Employee.
- **[1-customer-creation.md](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/phase3/1-customer-creation.md)**: Atualizar diagrama com fluxo atômico e compensação para Customer.

### 5.2. Repositório `15-soat-tech-challenge-lamda`
- **`src/garageService.js` (Novo)**:
  - `createEmployee({ id, name, email, cpf })`
  - `createCustomer({ id, name, email, document, vehicles })`
- **`src/keycloakService.js`**:
  - `deleteUser(userId)`
- **`src/handlers/registerHandler.js`**:
  - Integrar chamadas para `garageService.createEmployee` ou `garageService.createCustomer` com try/catch e `deleteUser` em caso de erro.
- **`test/registerHandler.test.js`**:
  - Cenários de sucesso e rollback para Employee.
  - Cenários de sucesso e rollback para Customer.
- **Terraform**:
  - Variável `GARAGE_API_URL`.

---

## 6. Verificação e Testes

1. **Lambda Test Suite**: `npm test` em `15-soat-tech-challenge-lamda`.
2. **Garage API Test Suite**: `mvn test -pl domain,application` em `15-soat-tech-challenge-garage`.
