# Plano de Arquitetura & Design: Unificação de Identidade (Keycloak ID) e Onboarding de Funcionários

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-10  
> **Autor**: Arquiteto de Software Principal & Engenheiro Especialista  
> **Repositórios Impactados**: 15-soat-tech-challenge-garage e 15-soat-tech-challenge-lamda  

---

## 1. Visão Geral & Problema Arquitetural

### 1.1 Contexto Atual
No modelo atual, a gestão de identidades e o domínio da oficina operam de forma desarticulada:
1. O cadastro de credenciais ocorre via AWS Lambda (POST /register), que cria o usuário no Keycloak e gera um keycloak_user_id (UUID).
2. O cadastro operacional do funcionário ocorre de forma isolada via POST /v1/employees no pi-garage, gerando **outro UUID aleatório** na tabela garage.employee.

### 1.2 Por que Manter a Tabela garage.employee?
A entidade Employee pertence legitimamente ao **Bounded Context da Oficina Mecânica** (DDD):
- **Integridade Referencial**: A tabela garage.work_order possui chave estrangeira CONSTRAINT fk_work_order_employee FOREIGN KEY (employee_id) REFERENCES garage.employee(id).
- **Performance Relacional**: Permite queries com JOIN direto no PostgreSQL (ex: listar ordens de serviço trazendo nome e CPF do mecânico) com tempo de resposta em sub-milissegundos, sem requisições HTTP adicionais ao Keycloak (eliminando o anti-pattern *N+1 HTTP Queries*).
- **Extensibilidade de Domínio**: Permite acomodar atributos operacionais da oficina (especialidades, status ativo/férias, comissões, métricas de tempo médio de execução).

### 1.3 O Anti-Pattern a ser Eliminado
O problema não é a existência da tabela, mas o **Anti-Pattern de Cadastro Duplo Desarticulado (*Double Registration*)**, onde a mesma pessoa física possui dois IDs distintos no ecossistema e o operador precisa disparar duas requisições manuais.

---

## 2. Abordagens de Implementação para a Opção 1

Apresentamos as duas variações técnicas para concretizar a **Opção 1 (ID Unificado + Onboarding Automatizado)**:

### 🥇 Abordagem 1.A (Recomendada - Best Practice): Onboarding Orquestrado Serverless (Single Entry Point)

* **Como Funciona**:
  1. O Frontend dispara uma **única requisição** para a Lambda Serverless:
     `http
     POST /register
     Content-Type: application/json

     {
       "name": "Carlos Mecânico",
       "email": "carlos@garage.com",
       "document": "529.982.247-25",
       "password": "SenhaForte@2026",
       "role": "EMPLOYEE"
     }
     `
  2. A Lambda valida o CPF (Módulo 11 da Receita Federal).
  3. A Lambda cria o usuário no Keycloak via Admin REST API (POST /admin/realms/garage/users) e obtém o keycloak_user_id.
  4. A Lambda chama a API da oficina (POST /v1/employees via VPC Link / NLB) enviando:
     `json
     {
       "id": "<keycloak_user_id>",
       "name": "Carlos Mecânico",
       "email": "carlos@garage.com",
       "cpf": "52998224725"
     }
     `
  5. A pi-garage persiste na tabela garage.employee com a mesma chave primária (id = keycloak_user_id).
  6. A Lambda responde 201 Created para o Frontend com os dados unificados.
* **Vantagens**:
  - ✅ **Ponto Único de Entrada**: O Frontend e o Administrador executam apenas uma chamada.
  - ✅ **Atomicidade Operacional**: Se o provisionamento no domínio falhar, a Lambda pode executar rollback compensatório no Keycloak.
  - ✅ **Idempotência**: keycloak.sub == garage.employee.id. O token JWT que o mecânico recebe no login tem o claim sub exatamente igual ao seu id no banco da oficina.

---

### 🥈 Abordagem 1.B (Alternativa): ID Unificado com Orquestração no Frontend / BFF

* **Como Funciona**:
  1. O Frontend chama a Lambda POST /register para criar a conta no Keycloak e recebe o user.id.
  2. O Frontend, em posse do user.id, chama a pi-garage (POST /v1/employees) passando explicitamente o campo id: "<user.id>".
  3. A pi-garage aceita o ID fornecido e grava na tabela garage.employee.
* **Vantagens**:
  - A Lambda não precisa conhecer a URL interna da pi-garage.
* **Desvantagens**:
  - O Frontend continua precisando coordenar duas chamadas de rede.

---

## 3. Plano de Mudanças no Código (pi-garage)

Para suportar o ID Unificado (tanto na Abordagem 1.A quanto 1.B):

### 3.1 Módulo domain
1. **Employee.java**:
   - Ajustar anotação @Id:
     `java
     @Id
     @Column(comment = "Employee id. Owner: db")
     private UUID id;
     `
   - Permitir a atribuição do id vindo do Keycloak via Builder, garantindo fallback para UUID.randomUUID() caso não informado:
     `java
     // No builder/construtor ou pré-persistência:
     if (this.id == null) {
         this.id = UUID.randomUUID();
     }
     `

### 3.2 Módulo pplication
1. **EmployeeDef.java & EmployeeDto.java**:
   - Adicionar o campo opcional UUID id em EmployeeDto.Request.
2. **EmployeeDtoMapper.java**:
   - Mapear id da Request para a entidade Employee.
3. **EmployeeCreationUseCase.java / EmployeeController.java**:
   - Manter contrato RESTful POST /v1/employees aceitando o id unificado.

### 3.3 Documentação de Sequência
1. Atualizar o diagrama docs/phase3/0-employee-creation.md para refletir o fluxo de entrada única orquestrado.

---

## 4. Plano de Verificação (TDD)

1. **Testes Unitários (EmployeeControllerTest, EmployeeCreationUseCaseTest)**:
   - Validar criação de funcionário informando um id pré-definido (Keycloak UUID) -> Deve persistir com o ID fornecido.
   - Validar criação de funcionário sem id (retrocompatibilidade) -> Deve gerar um UUID aleatório.
2. **Testes de Integração (EmployeeIntegrationTest)**:
   - Validar persistência no banco H2/PostgreSQL com id explícito.
   - Validar que a Ordem de Serviço vincula com sucesso ao employeeId correspondente ao Keycloak sub.
