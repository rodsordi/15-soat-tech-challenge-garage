# Plano de Refatoração: Contexto Gherkin (Background) em PT-BR

## 🎯 Objetivo
Mover o passo de verificação operacional `"que o sistema da oficina está em execução e operacional"` para uma seção `Contexto:` (equivalente a `Background:` em inglês) no arquivo `work_order_lifecycle.feature`. Dessa forma, esse pré-requisito de infraestrutura passa a ser executado automaticamente antes de cada cenário da funcionalidade.

---

## 📐 Opções de Abordagem

### 🥇 Opção 1 (Recomendada - Best Practice): Seção `Contexto:` nativa do Gherkin PT-BR
- **Descrição**:
  - Utilizar a palavra-chave oficial `Contexto:` logo abaixo da descrição da `Funcionalidade:`.
  - Mover o passo `Dado que o sistema da oficina está em execução e operacional` para dentro de `Contexto:`.
  - No `Cenário:`, iniciar a massa de teste de negócio com `Dado um cliente cadastrado com documento e e-mail únicos`.
  - Em `WorkOrderLifecycleSteps.java`, ajustar a anotação para `@Dado("um cliente cadastrado com documento e e-mail únicos")`.
- **Vantagens**:
  - Padrão oficial e idiomático do Cucumber BDD.
  - O `Contexto:` isola pré-condições técnicas comuns da narrativa de negócio do cenário.
  - Qualquer novo cenário adicionado à feature herdará a verificação de integridade operacional automaticamente.
- **Complexidade**: Baixa.

---

### 🥈 Opção 2 (Hook `@Before` nativo do Cucumber no código Java):
- **Descrição**:
  - Remover o passo textual do arquivo `.feature` e colocar a checagem de saúde diretamente em um método `@Before` em `CucumberSpringConfiguration.java` ou `WorkOrderLifecycleSteps.java`.
- **Trade-offs**:
  - *Prós*: Deixa o `.feature` com menos linhas.
  - *Contras*: Oculta do stakeholder e do relatório de testes a evidência explícita de validação de integridade do sistema. Não atende textualmente ao pedido de "mover para um contexto no .feature".
- **Complexidade**: Baixa.

---

## 🛠️ Detalhamento da Opção 1 (Recomendada)

### 1. `e2e/src/test/resources/features/work_order_lifecycle.feature`
```gherkin
# language: pt
Funcionalidade: Ciclo de Vida Completo da Ordem de Serviço
  Como cliente e funcionário da oficina mecânica
  Quero cadastrar veículos, serviços e gerenciar ordens de serviço
  Para que o veículo seja reparado, notificado e liberado com métricas consolidadas

  Contexto:
    Dado que o sistema da oficina está em execução e operacional

  Cenário: Ciclo de vida completo da ordem de serviço desde a criação até a liberação
    Dado um cliente cadastrado com documento e e-mail únicos
    E um veículo cadastrado associado ao cliente
    E um funcionário mecânico cadastrado
    E um material de estoque cadastrado
    E um serviço cadastrado vinculado ao material
    Quando uma nova ordem de serviço é criada para o veículo e mecânico
    Então a ordem de serviço deve ser criada com o status "RECEIVED"
    Quando o mecânico inicia o diagnóstico da ordem de serviço
    Então o status da ordem de serviço deve ser atualizado para "DIAGNOSING"
    Quando o diagnóstico é concluído e aguarda aprovação do cliente
    Então o status da ordem de serviço deve ser atualizado para "WAITING_FOR_APPROVAL"
    E uma notificação deve ser gerada para a ordem de serviço
    Quando o cliente aprova o orçamento e a execução é iniciada
    Então o status da ordem de serviço deve ser atualizado para "EXECUTING"
    Quando o serviço solicitado é marcado como concluído
    E a ordem de serviço é finalizada
    Então o status da ordem de serviço deve ser atualizado para "FINISHED"
    Quando o veículo é liberado para o cliente
    Então o status da ordem de serviço deve ser atualizado para "RELEASED"
    Quando a rotina de tempo médio de execução é acionada
    Então o tempo médio do serviço deve ser calculado e persistido
```

### 2. `WorkOrderLifecycleSteps.java`
- Garantir que o passo `um cliente cadastrado com documento e e-mail únicos` esteja anotado com `@Dado` (ou compatível com `@Dado` e `@E`).

---

## 🧪 Plano de Verificação
- Executar a suíte de testes manuais:
  ```powershell
  mvn test -pl e2e -Pmanual-e2e
  ```
- Assegurar 100% de sucesso na execução.
