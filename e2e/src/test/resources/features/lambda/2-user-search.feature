# language: pt
@e2e @lambda @consulta-usuario
Funcionalidade: Consulta de Usuário por CPF no Lambda Auth
  Como atendente ou sistema da oficina
  Quero consultar a existência de um usuário a partir do seu CPF
  Para verificar o status do cadastro antes de abrir ordens de serviço

  @consulta-usuario-existente
  Cenário: Consulta de usuário previamente cadastrado por CPF válido
    Dado que existe um usuário previamente cadastrado com CPF válido no sistema
    Quando uma requisição de consulta por CPF do usuário cadastrado é enviada ao Lambda
    Então a resposta do Lambda deve ter status 200
    E o usuário retornado deve conter o CPF consultado e status cadastrado
    E os dados de telemetria da operação devem ser validados no New Relic via API

  @consulta-cpf-invalido
  Cenário: Rejeição de consulta com formato de CPF inválido no path
    Quando uma requisição de consulta por CPF é enviada ao endpoint "/users/00000000000" do Lambda
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Invalid CPF"
    E os dados de telemetria da operação devem ser validados no New Relic via API

  @consulta-cpf-nao-encontrado
  Cenário: Consulta de CPF inexistente na base
    Quando uma requisição de consulta por CPF é enviada ao Lambda com um CPF válido não cadastrado
    Então a resposta do Lambda deve ter status 404
    E a resposta deve indicar usuário não encontrado
    E os dados de telemetria da operação devem ser validados no New Relic via API
