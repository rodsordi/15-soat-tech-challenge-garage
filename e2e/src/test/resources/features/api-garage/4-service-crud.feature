# language: pt
@e2e @servico @catalogo @crud
Funcionalidade: Gestão do Catálogo de Serviços
  Como administrador da oficina
  Quero cadastrar serviços vinculados a insumos de estoque
  Para precificar e padronizar as manutenções realizadas

  Contexto:
    Dado que o sistema da oficina está em execução e operacional
    E que o operador autentica no sistema através do serviço de autenticação
    E um material de estoque cadastrado

  @cadastro-servico
  Cenário: Cadastro de serviço vinculado ao material com sucesso
    Quando um novo serviço é cadastrado vinculado ao material de estoque
    Então o serviço deve ser persistido com status 201
    E os dados do serviço devem ser consultados com sucesso no catálogo
