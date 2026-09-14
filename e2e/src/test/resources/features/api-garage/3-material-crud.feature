# language: pt
@e2e @material @estoque @crud
Funcionalidade: Gestão de Materiais de Estoque
  Como operador de estoque
  Quero cadastrar e consultar materiais e insumos
  Para garantir disponibilidade de peças na oficina

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação

  @cadastro-material
  Cenário: Cadastro de novo material de estoque com sucesso
    Quando um novo material de estoque é cadastrado com preço e quantidade válidos
    Então o material deve ser persistido com status 201
    E os dados do material devem ser consultados com sucesso por seu identificador
