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

  @cadastro-veiculo
  Cenário: Cadastro de veículo associado ao cliente com sucesso
    Quando um novo veículo é cadastrado com placa única para o cliente
    Então o veículo deve ser persistido com status 201
    E os dados do veículo devem ser consultados com sucesso por seu identificador
    E os dados de telemetria da operação devem ser validados no New Relic via API
