# Plano de Arquitetura & Refatoração: BDD Cucumber com Spring Context no Módulo `e2e`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-10  
> **Autor**: Engenheiro Especialista em Desenvolvimento & QA Architect  
> **Repositório Alvo**: `15-soat-tech-challenge-garage`  

---

## 1. Visão Geral & Motivação

Com a criação do submódulo `e2e`, a estratégia de testes de ponta a ponta será elevada para o padrão **Behavior-Driven Development (BDD)** utilizando **Cucumber 7** e **Gherkin**. 

A integração do Cucumber com o Spring Boot (`cucumber-spring`) é essencial para permitir:
1. **Injeção de Dependências (`@Autowired`) nos Steps**: Injetar serviços, utilitários, mappers (como Jackson `JsonMapper`), configurações e templates diretamente nas classes de definição de passos (*step definitions*).
2. **Contexto de Aplicação Sob Demanda**: Subida automática do contexto Spring Boot (`GarageApplication`) em porta aleatória (`webEnvironment = RANDOM_PORT`), ou roteamento dinâmico para uma instância já em execução via Docker Compose (`localhost:8080`) ou ambiente remoto.
3. **Legibilidade Executiva (Living Documentation)**: Especificação clara dos fluxos de negócio em arquivos `.feature` acessíveis tanto para desenvolvedores quanto para analistas de negócio e POs.

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Cucumber 7 + Spring Boot Test + RestAssured & Contexto Híbrido

* **Motivação Técnica**:
  Esta é a abordagem mais robusta e idiomática para testes BDD em ecossistemas Spring Boot 3.x / Java 25. Utiliza o `@CucumberContextConfiguration` em conjunto com `@SpringBootTest` e o runner padrão JUnit Platform Suite (`cucumber-junit-platform-engine`). 
* **Estrutura Proposta**:
  ```text
  e2e/
  ├── pom.xml                                    # Dependências cucumber-java, cucumber-spring, cucumber-junit-platform-engine
  └── src/test/
      ├── resources/
      │   ├── cucumber.properties                # Configurações do Cucumber (plugins, publish quiet)
      │   └── features/
      │       └── work_order_lifecycle.feature   # Cenário de ponta a ponta em Gherkin
      └── java/br/com/fiap/garage/e2e/
          ├── RunCucumberTest.java               # Runner JUnit Platform Suite
          ├── config/
          │   └── CucumberSpringConfiguration.java # @CucumberContextConfiguration + @SpringBootTest
          ├── context/
          │   └── ScenarioTestContext.java       # Contexto compartilhado em memória entre steps
          └── steps/
              ├── CommonSteps.java               # Steps genéricos de asserção HTTP
              └── WorkOrderLifecycleSteps.java   # Steps da jornada da Ordem de Serviço
  ```
* **Vantagens**:
  - ✅ **Injeção Completa**: `@Autowired` disponível em todos os Steps.
  - ✅ **Relatórios Vivos**: Geração automática de relatórios HTML em `target/cucumber-reports/`.
  - ✅ **Reuso com DTOs**: Compartilhamento type-safe de DTOs e Factories do módulo `application`.

---

### 🥈 Opção 2 (Alternativa): Cucumber com Injeção PicoContainer (Sem Spring Context)

* **Motivação Técnica**:
  Utilizar o `cucumber-picocontainer` para injeção de dependências leve apenas entre os steps do Cucumber, sem carregar o Spring Context.
* **Trade-offs**:
  - ⚠️ **Sem Spring**: Não permite injetar beans do Spring (`@Autowired`), nem configurações do `application.properties`, obrigando a inicialização manual de clientes HTTP e mappers em cada step.

---

### 🥉 Opção 3 (Minimalista): Step Único Monolítico

* **Motivação Técnica**:
  Criar um único arquivo `.feature` com apenas um step genérico que chama o método do teste manual já existente.
* **Trade-offs**:
  - ⚠️ Anti-pattern de BDD (perde a granularidade dos passos `Given/When/Then`).

---

## 3. Especificação do Cenário BDD (`.feature`)

```gherkin
Feature: Work Order End-to-End Lifecycle
  As a garage customer and employee
  I want to register vehicles, services and manage work orders
  So that the vehicle can be repaired, notified and released with tracked metrics

  Scenario: Complete Work Order Lifecycle from Creation to Release
    Given the garage system is running and operational
    And a registered customer with unique document and email
    And a registered vehicle associated with the customer
    And a registered mechanic employee
    And a registered inventory material
    And a registered service linked to the material
    When a new work order is created for the vehicle and mechanic
    Then the work order should be created with status "RECEIVED"
    When the mechanic starts diagnosing the work order
    Then the work order status should be updated to "DIAGNOSING"
    When the diagnosis is finished and awaits customer approval
    Then the work order status should be updated to "WAITING_FOR_APPROVAL"
    And a notification should be generated for the work order
    When the customer approves the estimate and execution begins
    Then the work order status should be updated to "EXECUTING"
    When the requested service is marked as completed
    And the work order is finalized
    Then the work order status should be updated to "FINISHED"
    When the vehicle is released to the customer
    Then the work order status should be updated to "RELEASED"
    When the average execution time routine is triggered
    Then the service average time should be calculated and persisted
```

---

## 4. Passo a Passo de Execução (Opção 1 Recomendada)

1. **Atualizar `e2e/pom.xml`**: Adicionar `cucumber-java`, `cucumber-spring`, `cucumber-junit-platform-engine` e `junit-platform-suite`.
2. **Criar Configuração Spring do Cucumber**: `CucumberSpringConfiguration.java` anotada com `@CucumberContextConfiguration` e `@SpringBootTest`.
3. **Criar o Runner**: `RunCucumberTest.java` com `@Suite` e `@IncludeEngines("cucumber")`.
4. **Criar a Feature**: `work_order_lifecycle.feature` na pasta `e2e/src/test/resources/features/`.
5. **Implementar os Steps**: `WorkOrderLifecycleSteps.java` com injeção Spring de `JsonMapper` e asserções com RestAssured e AssertJ.
6. **Remover o Teste Manual Legado**: Deletar `CompleteWorkOrderCreationAwsManualTest.java` de `e2e`, consolidando a suíte no BDD.
7. **Validação**: Executar `mvn test -pl e2e -De2eTests=true` e validar a aprovação de todos os cenários.
