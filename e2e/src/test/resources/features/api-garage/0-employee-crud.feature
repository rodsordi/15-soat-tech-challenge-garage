# language: pt
@e2e @funcionario @crud
Funcionalidade: Gestão de Funcionários Mecânicos
  Como administrador da oficina
  Quero cadastrar e consultar funcionários mecânicos
  Para gerenciar a equipe operacional habilitada para ordens de serviço

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação

  @cadastro-funcionario
  Cenário: Cadastro de novo funcionário mecânico com sucesso
    Quando um novo funcionário mecânico é cadastrado com CPF e e-mail válidos
    Então o funcionário deve ser persistido com status 201
    E os dados cadastrais do funcionário devem ser consultados com sucesso por seu identificador
