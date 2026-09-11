# language: pt
@e2e @ordem-de-servico
Funcionalidade: Ciclo de Vida Completo da Ordem de Serviço
  Como cliente e funcionário da oficina mecânica
  Quero gerenciar o fluxo operacional completo das ordens de serviço
  Para que o veículo seja reparado com qualidade, notificado com transparência e liberado com métricas consolidadas

  # ============================================================================
  # Contexto: Validação Operacional e Carga da Massa de Dados Base
  # ============================================================================
  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E um cliente cadastrado com documento e e-mail únicos
    E um veículo cadastrado associado ao cliente
    E um funcionário mecânico cadastrado
    E um material de estoque cadastrado
    E um serviço cadastrado vinculado ao material

  # ============================================================================
  # Fluxo Principal: Ciclo de Vida Completo da Ordem de Serviço
  # ============================================================================
  @ciclo-completo
  Cenário: Ciclo de vida completo da ordem de serviço desde a criação até a liberação

    # --- 1. Recepção do Veículo e Abertura da Ordem de Serviço (RECEIVED) ---
    Quando uma nova ordem de serviço é criada para o veículo e mecânico
    Então a ordem de serviço deve ser criada com o status "RECEIVED"

    # --- 2. Diagnóstico Técnico pelo Mecânico (DIAGNOSING) ---
    Quando o mecânico inicia o diagnóstico da ordem de serviço
    Então o status da ordem de serviço deve ser atualizado para "DIAGNOSING"

    # --- 3. Conclusão do Orçamento e Notificação ao Cliente (WAITING_FOR_APPROVAL) ---
    Quando o diagnóstico é concluído e aguarda aprovação do cliente
    Então o status da ordem de serviço deve ser atualizado para "WAITING_FOR_APPROVAL"
    E uma notificação deve ser gerada para a ordem de serviço

    # --- 4. Aprovação do Orçamento e Início dos Reparos (EXECUTING) ---
    Quando o cliente aprova o orçamento e a execução é iniciada
    Então o status da ordem de serviço deve ser atualizado para "EXECUTING"

    # --- 5. Conclusão dos Serviços e Encerramento Operacional (FINISHED) ---
    Quando o serviço solicitado é marcado como concluído
    E a ordem de serviço é finalizada
    Então o status da ordem de serviço deve ser atualizado para "FINISHED"

    # --- 6. Retirada e Liberação do Veículo ao Cliente (RELEASED) ---
    Quando o veículo é liberado para o cliente
    Então o status da ordem de serviço deve ser atualizado para "RELEASED"

    # --- 7. Fechamento de Métricas e Cálculo do Tempo Médio de Execução ---
    Quando a rotina de tempo médio de execução é acionada
    Então o tempo médio do serviço deve ser calculado e persistido
