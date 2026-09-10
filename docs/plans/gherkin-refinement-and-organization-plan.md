# Plano de Melhoria, Organização e Estruturação do Arquivo Gherkin (*.feature)

## 🎯 Objetivo
Aprimorar o arquivo de especificação BDD `work_order_lifecycle.feature`, organizando a narrativa de negócio em fases bem demarcadas com comentários de contexto, enriquecendo o vocabulário para a Linguagem Ubíqua (DDD) de oficina mecânica e adicionando tags para rastreabilidade e governança de testes.

---

## 📐 Opções de Abordagem

### 🥇 Opção 1 (Recomendada - Best Practice): Cenário E2E Estruturado em 8 Fases com Comentários de Seção e Tags
- **Descrição**:
  - **Tags**: Adicionar `@e2e`, `@ordem-de-servico` e `@ciclo-completo` para permitir execução seletiva.
  - **Contexto**: Manter a validação prévia de infraestrutura sob `Contexto:`, com comentário explicativo.
  - **Organização em Fases**: Dividir o ciclo de vida em 8 blocos lógicos delimitados por comentários estruturados:
    1. `# --- 1. Massa de Dados e Cadastros Iniciais (Setup de Entidades) ---`
    2. `# --- 2. Abertura e Recepção do Veículo na Oficina (Status: RECEIVED) ---`
    3. `# --- 3. Diagnóstico Técnico pelo Mecânico (Status: DIAGNOSING) ---`
    4. `# --- 4. Conclusão do Diagnóstico e Disparo de Notificação (Status: WAITING_FOR_APPROVAL) ---`
    5. `# --- 5. Aprovação do Orçamento pelo Cliente e Execução (Status: EXECUTING) ---`
    6. `# --- 6. Conclusão dos Serviços e Finalização da OS (Status: FINISHED) ---`
    7. `# --- 7. Entrega e Liberação do Veículo ao Cliente (Status: RELEASED) ---`
    8. `# --- 8. Processamento e Consolidação das Métricas de Tempo Médio ---`
  - **Refinamento dos Textos**: Padronizar as frases para máxima clareza e elegância em PT-BR, mantendo compatibilidade com os steps em Java (`WorkOrderLifecycleSteps.java`).
- **Vantagens**:
  - Torna a leitura do `.feature` extremamente intuitiva para Product Owners, desenvolvedores e auditores.
  - Preserva a execução ponta a ponta do ciclo de vida sem necessidade de recriar estados complexos em cenários isolados.
  - Suporta tags para execuções parciais no Maven (`-Dcucumber.filter.tags="@e2e"`).
- **Complexidade**: Baixa.

---

### 🥈 Opção 2 (Alternativa): Desmembramento em Múltiplos Cenários com Dependência de Estado
- **Descrição**:
  - Dividir a funcionalidade em 3 cenários separados:
    1. *Cenário 1*: Cadastro de entidades e abertura da ordem de serviço.
    2. *Cenário 2*: Ciclo de transição de status e aprovação pelo cliente.
    3. *Cenário 3*: Execução dos serviços, liberação e rotina de métricas.
- **Trade-offs**:
  - *Prós*: Cenários mais curtos individualmente.
  - *Contras*: Em testes E2E com persistência real, cenários dependentes violam o princípio de isolamento dos testes (F.I.R.S.T.), ou exigiriam recriar todas as entidades em cada cenário, tornando a execução 3x mais lenta.
- **Complexidade**: Alta.

---

### 🥉 Opção 3 (Minimalista): Apenas Inclusão de Comentários no Arquivo Atual
- **Descrição**:
  - Manter exatamente o texto atual sem tags e apenas inserir linhas `#` separando os grupos de passos.
- **Trade-offs**:
  - *Prós*: Modificação mínima.
  - *Contras*: Não agrega valor semântico nem suporte a filtros por tag.
- **Complexidade**: Mínima.

---

## 🛠️ Detalhamento da Opção 1 (Recomendada)

### Proposta de Conteúdo para `work_order_lifecycle.feature`:

```gherkin
# language: pt
@e2e @ordem-de-servico
Funcionalidade: Ciclo de Vida Completo da Ordem de Serviço
  Como cliente e funcionário da oficina mecânica
  Quero gerenciar o fluxo operacional completo das ordens de serviço
  Para que o veículo seja reparado com qualidade, notificado com transparência e liberado com métricas consolidadas

  # ============================================================================
  # Contexto Pré-Operacional: Validação de Disponibilidade do Sistema
  # ============================================================================
  Contexto:
    Dado que o sistema da oficina está em execução e operacional

  # ============================================================================
  # Fluxo Principal: Ciclo de Vida Completo da Ordem de Serviço
  # ============================================================================
  @ciclo-completo
  Cenário: Ciclo de vida completo da ordem de serviço desde a criação até a liberação

    # --- 1. Massa de Dados e Cadastros Iniciais (Setup das Entidades Base) ---
    Dado um cliente cadastrado com documento e e-mail únicos
    E um veículo cadastrado associado ao cliente
    E um funcionário mecânico cadastrado
    E um material de estoque cadastrado
    E um serviço cadastrado vinculado ao material

    # --- 2. Recepção do Veículo e Abertura da Ordem de Serviço (RECEIVED) ---
    Quando uma nova ordem de serviço é criada para o veículo e mecânico
    Então a ordem de serviço deve ser criada com o status "RECEIVED"

    # --- 3. Diagnóstico Técnico pelo Mecânico (DIAGNOSING) ---
    Quando o mecânico inicia o diagnóstico da ordem de serviço
    Então o status da ordem de serviço deve ser atualizado para "DIAGNOSING"

    # --- 4. Conclusão do Orçamento e Notificação ao Cliente (WAITING_FOR_APPROVAL) ---
    Quando o diagnóstico é concluído e aguarda aprovação do cliente
    Então o status da ordem de serviço deve ser atualizado para "WAITING_FOR_APPROVAL"
    E uma notificação deve ser gerada para a ordem de serviço

    # --- 5. Aprovação do Orçamento e Início dos Reparos (EXECUTING) ---
    Quando o cliente aprova o orçamento e a execução é iniciada
    Então o status da ordem de serviço deve ser atualizado para "EXECUTING"

    # --- 6. Conclusão dos Serviços e Encerramento Operacional (FINISHED) ---
    Quando o serviço solicitado é marcado como concluído
    E a ordem de serviço é finalizada
    Então o status da ordem de serviço deve ser atualizado para "FINISHED"

    # --- 7. Retirada e Liberação do Veículo ao Cliente (RELEASED) ---
    Quando o veículo é liberado para o cliente
    Então o status da ordem de serviço deve ser atualizado para "RELEASED"

    # --- 8. Fechamento de Métricas e Cálculo do Tempo Médio de Execução ---
    Quando a rotina de tempo médio de execução é acionada
    Então o tempo médio do serviço deve ser calculado e persistido
```

---

## 🧪 Plano de Verificação
1. Executar a suíte de testes com tags:
   ```powershell
   mvn test -pl e2e -Pmanual-e2e "-Dcucumber.filter.tags=@ciclo-completo"
   ```
2. Validar que 100% dos passos executam e passam com sucesso no relatório.
