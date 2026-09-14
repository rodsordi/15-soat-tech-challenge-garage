# language: pt
@e2e @lambda @health
Funcionalidade: Verificação de Saúde e Descoberta de Rotas do Lambda Auth
  Como integrador ou operador de infraestrutura
  Quero verificar a disponibilidade do serviço Lambda
  Para garantir que a camada de autenticação e registro está operacional

  @health-check
  Cenário: Consulta de saúde e disponibilidade do serviço Lambda
    Quando uma requisição GET é enviada para a raiz do serviço Lambda
    Então a resposta do Lambda deve ter status 200
    E o corpo da resposta deve conter o status "UP"
    E a lista de endpoints disponíveis deve conter as rotas de registro, consulta e autenticação

  @health-preflight
  Cenário: Requisição preflight CORS para endpoints do Lambda
    Quando uma requisição OPTIONS é enviada para o serviço Lambda
    Então a resposta do Lambda deve ter status 204
    E os cabeçalhos de resposta devem conter políticas CORS permitindo a origem
