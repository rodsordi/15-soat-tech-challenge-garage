# language: pt
@e2e @cliente @crud
Funcionalidade: Gestão de Clientes e Identidade Unificada
  Como atendente ou cliente da oficina
  Quero cadastrar e consultar clientes
  Para manter o vínculo de propriedade dos veículos e histórico de manutenções

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação

  @cadastro-cliente
  Cenário: Cadastro de novo cliente com sucesso
    Quando um novo cliente é cadastrado com documento e e-mail válidos
    Então o cliente deve ser persistido com status 201
    E os dados cadastrais do cliente devem ser consultados com sucesso por seu identificador
