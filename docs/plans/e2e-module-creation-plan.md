# Plano de Arquitetura & Implementação: Novo Módulo `e2e` no `api-garage`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-10  
> **Autor**: Engenheiro Especialista em Desenvolvimento & QA Architect  
> **Repositório Alvo**: `15-soat-tech-challenge-garage`  

---

## 1. Visão Geral e Motivação

Atualmente, o projeto `api-garage` possui a seguinte segregação de módulos:
* **`domain`**: Entidades, regras de negócio puras e portas de entrada/saída (Arquitetura Hexagonal).
* **`application`**: Adaptadores REST, persistência Spring Data JPA, mensageria AWS SNS/SQS e runtime Spring Boot.
* **`iandt`**: Testes de integração (*Integration & Test*) isolados executados via **Testcontainers** (subindo instâncias temporárias de PostgreSQL e LocalStack no Docker).
* **`stress`**: Testes de carga, performance e saturação executados com Apache JMeter.

Entretanto, fluxos de ponta a ponta (E2E) — como o `CompleteWorkOrderCreationAwsManualTest.java` — estavam localizados dentro do módulo `iandt` e marcados com `@Disabled`. 

### Objetivos da Criação do Módulo `e2e`:
1. **Segregação Estrita da Pirâmide de Testes**: Diferenciar claramente os testes de integração (isolados por container em `iandt`) dos testes de ponta a ponta (executados contra a API ativa e seus componentes integrados em `e2e`).
2. **Execução On-Demand via Profile Maven**: Evitar que os testes E2E aumentem o tempo de compilação comum (`mvn test` / `mvn package`) ou falhem em builds onde os serviços não estejam no ar, utilizando ativação condicional (`-De2eTests=true`).
3. **Suporte Híbrido a Ambientes**: Permitir a execução local contra os contêineres do Docker Compose (`http://localhost:8080/api`) e remota contra ambientes de staging/AWS (via API Gateway e Keycloak).

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Submódulo Maven `e2e` Dedicado com Profile `e2eTests` e Testes em 3 Níveis (`@Nested`)

* **Motivação Técnica**:
  Alinha o repositório aos princípios modernos de engenharia de software e padrões de QA: desacoplamento total de responsabilidades, alta coesão, zero interferência no build padrão e flexibilidade entre ambientes locais e remotos.
* **Estrutura Proposta**:
  ```text
  15-soat-tech-challenge-garage/
  ├── pom.xml                                      # Adiciona <module>e2e</module> e profile e2eTests
  └── e2e/
      ├── pom.xml                                  # api-garage.e2e com RestAssured, Jackson e JUnit 5
      └── src/test/java/br/com/fiap/garage/e2e/
          ├── config/
          │   └── E2ETestConfig.java               # Configuração dinâmica de URL e autenticação
          └── work_order/
              └── CompleteWorkOrderE2ETest.java         # Teste E2E hierárquico em 3 níveis (@Nested)
  ```
* **Comportamento do Profile Maven**:
  - `mvn test`: Executa apenas testes unitários (`domain`, `application`).
  - `mvn test -pl iandt -DintegrationTests=true`: Executa os testes de integração com Testcontainers.
  - `mvn test -pl e2e -De2eTests=true`: Executa exclusivamente a suíte E2E.
* **Organização dos Testes em 3 Níveis (`@Nested`)**:
  ```text
  CompleteWorkOrderE2ETest
  └── @Nested CompleteLifecycle
      └── @Nested Success
          └── @Test shouldExecuteCompleteWorkOrderLifecycle()
  ```
* **Vantagens**:
  - ✅ **Velocidade de CI/CD**: Build comum do Maven permanece rápido e independente.
  - ✅ **Padrão Limpo de QA**: Módulo `iandt` fica 100% focado em Testcontainers e o `e2e` focado em jornadas de ponta a ponta.
  - ✅ **Flexibilidade**: Executa localmente (Docker Compose) ou remotamente (AWS) sem alterar código.

---

### 🥈 Opção 2 (Alternativa): Módulo `e2e` Focado Exclusivamente em Ambiente Local

* **Motivação Técnica**:
  Simplificar a suíte E2E para validar apenas o ambiente local rodando via Docker Compose (`http://localhost:8080/api`), com token estático local.
* **Trade-offs**:
  - ⚠️ Não permite reutilizar a suíte como *smoke test* pós-deploy em ambientes de nuvem (AWS EKS / API Gateway).

---

### 🥉 Opção 3 (Minimalista): Esqueleto Básico com Migração Direta

* **Motivação Técnica**:
  Criar a pasta `e2e/` e o `pom.xml` mínimo, apenas movendo o arquivo `CompleteWorkOrderCreationAwsManualTest.java` do `iandt` para o `e2e` sem refatorações estruturais.
* **Trade-offs**:
  - ⚠️ Mantém o teste com anotação `@Disabled`.
  - ⚠️ Não adota o padrão de 3 níveis de aninhamento com `@Nested` e `@DisplayName`.

---

## 3. Passo a Passo de Implementação (Opção 1 Recomendada)

1. **Configurar o Root `pom.xml`**:
   - Declarar `<module>e2e</module>`.
   - Adicionar profile `e2eTests` no root para desabilitar testes nos demais módulos quando ativo.
2. **Criar o Submódulo `e2e`**:
   - Criar `e2e/pom.xml` com parent `api-garage:0.0.1-SNAPSHOT`.
   - Importar `application` (test-jar), `domain` (test-jar), `rest-assured`, `junit-jupiter`, `assertj` e `jackson`.
3. **Implementar os Testes E2E**:
   - `E2ETestConfig.java`: Gestão de URLs e autenticação.
   - `CompleteWorkOrderE2ETest.java`: Fluxo completo (Cliente -> Veículo -> Mecânico -> Peça -> Serviço -> Ordem de Serviço -> Diagnóstico -> Aprovação -> Execução -> Finalização -> Liberação -> Cálculo Médio).
4. **Limpeza do Módulo `iandt`**:
   - Remover as classes manuais legadas em `iandt/src/test/java/br/com/fiap/garage/manual/`.
5. **Validação**:
   - `mvn clean test-compile`
   - `mvn test -pl iandt -DintegrationTests=true`
   - `mvn test -pl e2e -De2eTests=true`
