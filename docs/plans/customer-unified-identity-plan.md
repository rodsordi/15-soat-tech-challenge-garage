# Plano de Arquitetura & Design: Unificação de Identidade (Keycloak ID) e Centralização do Onboarding de Clientes via Lambda

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-10  
> **Autor**: Arquiteto de Software Principal & Engenheiro Especialista  
> **Repositórios Impactados**: 15-soat-tech-challenge-garage e 15-soat-tech-challenge-lamda  

---

## 1. Contexto & Diagnóstico Arquitetural

Assim como identificado para Employee, a gestão de clientes apresentava o mesmo desafio de **desarticulação de identidades**:
1. **No Keycloak (IAM)**: O cliente se cadastra para autoatendimento via Lambda Serverless (POST /register com ole: "CUSTOMER"), onde o Keycloak gera um sub (UUID), armazena a senha criptografada e o CPF/CNPJ.
2. **Na pi-garage (Domínio da Oficina)**: O Customer possuía uma geração de chave primária isolada (@GeneratedValue), gerando um UUID independente da oficina.

Ao centralizar o onboarding de clientes na **AWS Lambda Serverless (garage-auth-handler)**:
- A Lambda torna-se o **Ponto Único de Entrada (Single Entry Point)** para criação de clientes.
- Valida o algoritmo oficial **Módulo 11 da Receita Federal** para CPF (11 dígitos) e CNPJ (14 dígitos).
- Provisiona as credenciais no Keycloak e garante a persistência no catálogo da oficina com a **mesma chave primária** (garage.customer.id = keycloak_user_id).
- No token JWT emitido no login, o claim sub torna-se estritamente idêntico ao id do cliente em garage.customer e às Foreign Keys em garage.vehicle (customer_id).

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): ID Unificado (customer.id = keycloak_user_id) com Suporte a UUID Pré-Definido e Fallback

* **Motivação Técnica**:
  Alinhada aos princípios de **Domain-Driven Design (DDD)** e **Clean Architecture**. A identidade do agregado Customer passa a ser fornecida pelo Identity Provider ou gerada defensivamente pelo domínio, eliminando o acoplamento à geração interna do banco.
* **Componentes Impactados no pi-garage**:
  1. **Customer.java**:
     - Remover a anotação @GeneratedValue do campo id.
     - Adicionar @Builder.Default private UUID id = UUID.randomUUID(); e lifecycle hook @PrePersist para garantir que um UUID recebido do Keycloak seja preservado, mantendo geração automática se nulo.
  2. **CustomerDef.java**:
     - Adicionar UUID getId() na interface CustomerDef.Request (anotado com Swagger/OpenAPI).
  3. **CustomerDto.java**:
     - Adicionar private UUID id; em CustomerDto.Request.
  4. **CustomerDtoMapper.java**:
     - Mapeamento transparente de Request.id para Customer.id.
  5. **CustomerAssertions.java & CustomerDtoFactory.java**:
     - Atualização das asserções e geradores de dados para o ciclo TDD.
  6. **1-customer-creation.md**:
     - Atualização do diagrama de sequência Mermaid refletindo a centralização do cadastro via Lambda.

* **Vantagens**:
  - ✅ **Zero Duplicação de Identidade**: keycloak.sub == garage.customer.id.
  - ✅ **Retrocompatibilidade 100% Garantida**: Chamadas que não informarem id continuam funcionando com geração aleatória.
  - ✅ **Integridade Relacional**: Chave estrangeira k_vehicle_customer em garage.vehicle permanece íntegra e de alta performance.

---

### 🥈 Opção 2 (Alternativa): Sincronização Assíncrona via Mensageria (Event-Driven / AWS SQS)

* **Como Funciona**:
  A Lambda cria o usuário no Keycloak e posta uma mensagem na fila customer-created-queue. Um listener no Spring Boot consome a fila e insere o registro no banco da oficina.
* **Trade-offs**:
  - Complexidade operacional adicional e consistência eventual (delay entre o cadastro no front e a persistência na oficina).

---

## 3. Plano de Verificação & TDD (Red-Green-Refactor)

1. **Fase Red**:
   - Adicionar teste unitário 	est2 em CustomerControllerTest: *"Given a customer with explicit Keycloak id"*.
   - Executar e confirmar a falha inicial esperada (NoSuchFieldException: id).
2. **Fase Green**:
   - Implementar UUID getId() em CustomerDef.Request, id em CustomerDto.Request, e remover @GeneratedValue em Customer.java.
   - Executar o teste e validar sucesso.
3. **Fase Refactor**:
   - Atualizar o diagrama de sequência 1-customer-creation.md.
   - Executar a suíte completa de testes (mvn test -pl domain,application) garantindo 100% de aprovação.
