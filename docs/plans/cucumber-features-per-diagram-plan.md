# Plano de Implementação: Features Cucumber BDD para Diagramas de Sequência

Este documento define o planejamento para criação e execução de suítes de testes automatizados Cucumber BDD para cada um dos fluxos descritos nos diagramas de sequência em `docs/phase3/`.

---

## 🎯 Objetivo
Garantir cobertura E2E BDD automatizada para todos os fluxos de negócio modelados na arquitetura da oficina:
1. **`0-employee-crud.md`** ➔ `employee_crud.feature` (Cadastro de Funcionários via Saga Orchestration)
2. **`1-customer-crud.md`** ➔ `customer_crud.feature` (Cadastro de Clientes com Identidade Unificada Keycloak)
3. **`2-vehicle-crud.md`** ➔ `vehicle_crud.feature` (Cadastro e Associação de Veículos ao Cliente)
4. **`3-material-crud.md`** ➔ `material_crud.feature` (Gestão de Materiais de Estoque e Insumos)
5. **`4-service-crud.md`** ➔ `service_crud.feature` (Catálogo de Serviços e Vínculo com Materiais)
6. **`5-work-order-lifecycle.md`** ➔ `work_order_lifecycle.feature` (Ciclo de Vida Completo - *Já implementado e validado 100% em PRD*)

---

## 🏛️ Abordagens Técnicas

### 🥇 Opção 1 (Recomendada - Best Practice): Modularização com Reuso de Steps e Contextos Compartilhados
- **Como funciona**:
  - Criar arquivos `.feature` independentes para cada domínio em `e2e/src/test/resources/features/`:
    - `employee_crud.feature`
    - `customer_crud.feature`
    - `vehicle_crud.feature`
    - `material_crud.feature`
    - `service_crud.feature`
  - Reutilizar `E2eTestContext` para propagar tokens JWT e IDs criados entre steps.
  - Extrair steps compartilhados (autenticação, asserções de status code, verificação de integridade) ou criar classes dedicadas por domínio (`EmployeeSteps`, `CustomerSteps`, etc.).
  - Totalmente executável tanto localmente com Testcontainers/mocks quanto remotamente em PRD (`-Denv=prd`).
- **Vantagens**:
  - Alta coesão e baixo acoplamento: cada feature testa seu domínio isoladamente.
  - Rastreabilidade 1:1 entre a documentação em `docs/phase3/` e os testes E2E.
  - Execução seletiva via tags (ex: `@employee`, `@customer`, `@vehicle`, `@material`, `@service`).

### 🥈 Opção 2 (Alternativa): Classes de Steps Unificadas em WorkOrderLifecycleSteps
- **Como funciona**:
  - Manter todos os steps centralizados na classe `WorkOrderLifecycleSteps.java`, apenas adicionando os novos passos Gherkin dos CRUDs isolados.
- **Trade-offs**:
  - Menos arquivos Java criados, porém a classe `WorkOrderLifecycleSteps` cresce muito, misturando responsabilidades de múltiplos domínios.

---

## 📋 Detalhamento dos Cenários BDD a Implementar

### 1. `employee_crud.feature`
```gherkin
# language: pt
@e2e @funcionario @crud
Funcionalidade: Gestão de Funcionários Mecânicos
  Como administrador da oficina
  Quero cadastrar e consultar funcionários
  Para gerenciar a equipe operacional habilitada para ordens de serviço

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação

  Cenário: Cadastro de novo funcionário mecânico com sucesso
    Quando um novo funcionário mecânico é cadastrado com CPF e e-mail válidos
    Então o funcionário deve ser persistido com status 201 Created
    E o funcionário deve ser consultado com sucesso por seu identificador
```

### 2. `customer_crud.feature`
```gherkin
# language: pt
@e2e @cliente @crud
Funcionalidade: Gestão de Clientes e Identidade Unificada
  Como atendente ou cliente da oficina
  Quero cadastrar e consultar clientes
  Para manter o vínculo de propriedade dos veículos e histórico de manutenções

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação

  Cenário: Cadastro de novo cliente com sucesso
    Quando um novo cliente é cadastrado com documento e e-mail válidos
    Então o cliente deve ser persistido com status 201 Created
    E os dados cadastrais do cliente devem ser consultados com sucesso
```

### 3. `vehicle_crud.feature`
```gherkin
# language: pt
@e2e @veiculo @crud
Funcionalidade: Gestão de Veículos Vinculados ao Cliente
  Como atendente da oficina
  Quero vincular veículos a clientes cadastrados
  Para registrar histórico de manutenções e abrir ordens de serviço

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação
    E um cliente cadastrado com documento e e-mail únicos

  Cenário: Cadastro de veículo associado ao cliente
    Quando um veículo é cadastrado com placa única para o cliente
    Então o veículo deve ser persistido com status 201 Created
    E o veículo deve constar na listagem de veículos do cliente
```

### 4. `material_crud.feature`
```gherkin
# language: pt
@e2e @material @estoque @crud
Funcionalidade: Gestão de Materiais de Estoque
  Como operador de estoque
  Quero cadastrar e atualizar materiais e insumos
  Para garantir disponibilidade de peças na oficina

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação

  Cenário: Cadastro de novo material de estoque
    Quando um novo material de estoque é cadastrado com preço e quantidade válidos
    Então o material deve ser persistido com status 201 Created
    E o material deve ser consultado com sucesso por seu identificador
```

### 5. `service_crud.feature`
```gherkin
# language: pt
@e2e @servico @catalogo @crud
Funcionalidade: Gestão do Catálogo de Serviços
  Como administrador da oficina
  Quero cadastrar serviços vinculados a insumos de estoque
  Para precificar e padronizar as manutenções realizadas

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação
    E um material de estoque cadastrado

  Cenário: Cadastro de serviço com insumos vinculados
    Quando um novo serviço é cadastrado vinculado ao material de estoque
    Então o serviço deve ser persistido com status 201 Created
    E o catálogo de serviços deve retornar o serviço cadastrado
```

---

## 🔬 Plano de Verificação
1. **Execução Local / Unitária**:
   ```bash
   mvn test -pl e2e
   ```
2. **Execução em Ambiente de Produção (AWS Academy)**:
   ```bash
   mvn test -pl e2e -Pmanual-e2e -Denv=prd --no-transfer-progress
   ```
   Validar que todas as 6 features (`employee_crud`, `customer_crud`, `vehicle_crud`, `material_crud`, `service_crud` e `work_order_lifecycle`) rodam com 100% de sucesso contra a API real.
