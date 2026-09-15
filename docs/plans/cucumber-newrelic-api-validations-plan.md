# Planejamento Arquitetural: Validação de Dados no New Relic via API nos Cenários Cucumber

Este documento define o plano técnico para implementar validações automatizadas de observabilidade (Logs in Context, Distributed Tracing e Métricas de SLAs) diretamente via API GraphQL NerdGraph do New Relic ao final de todos os cenários da suíte de testes BDD do Cucumber.

---

## 1. Contexto e Desafios de Observabilidade no E2E

### Ingestão Assíncrona e Eventual Consistency
- Os logs estruturados gerados pelo HttpRequestLoggingFilter são enviados ao stdout, coletados pelo Fluent-Bit no daemonset do Kubernetes e enviados ao New Relic.
- As métricas Micrometer (garage.workorder.status.duration e http.server.requests) são coletadas via scraping pelo OpenTelemetry Prometheus agent.
- A janela de ingestão no New Relic leva normalmente entre **2 e 6 segundos**.
- **Solução**: O cliente de teste deve implementar **Polling com Retry e Timeout configurável** (ex: 15s com intervalo de 2s) para aguardar a indexação do evento antes de falhar a asserção.

---

## 2. Alternativas Técnicas

### 🥇 Opção 1 (Recomendada - Best Practice): Passo Declarativo Específico no BDD + NewRelicApiClient com Polling
- **Descrição**: Adição de um passo explícito ao final de cada cenário nas features BDD:
  E os dados de telemetria da requisição devem ser validados no New Relic via API
- **Validações Realizadas na API do New Relic**:
  1. Presença do log correspondente ao método HTTP, URI e status code do cenário.
  2. Presença de TraceId e SpanId válidos extraídos do log.
  3. No cenário de Work Order, validação da métrica de SLA por status (garage.workorder.status.duration).
- **Vantagens**: Máxima transparência nos relatórios Cucumber / Allure; cada cenário comprova que sua execução foi devidamente observada e auditada na nuvem.
- **Trade-offs**: Aumenta o tempo total de execução da suíte devido ao polling de ingestão.

---

### 🥈 Opção 2 (Alternativa): Hook Centralizado @After do Cucumber (Validação Global Automática)
- **Descrição**: A validação no New Relic é executada no hook @After do ScenarioHooks.java, sem modificar o texto dos arquivos .feature.
- **Vantagens**: Não necessita editar os 10 arquivos .feature.
- **Trade-offs**: Menos declarativo no relatório BDD.

---

### 🥉 Opção 3 (Abordagem Híbrida): Feature Dedicada de Auditoria de Observabilidade
- **Descrição**: Criação de um arquivo de feature dedicado (eatures/api-garage/6-observability-newrelic.feature) que audita em lote toda a telemetria gerada pelos fluxos anteriores.
- **Vantagens**: Execução dos CRUDs em altíssima velocidade.
- **Trade-offs**: Não valida cada cenário pontual individualmente.
