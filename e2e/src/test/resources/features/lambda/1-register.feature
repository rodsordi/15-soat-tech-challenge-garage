# language: pt
@e2e @lambda @cadastro
Funcionalidade: Cadastro Unificado de Usuários no Lambda Auth
  Como cliente ou colaborador da oficina
  Quero realizar meu cadastro com validação algorítmica de documento
  Para acessar os serviços do sistema da oficina

  @cadastro-cliente-cpf
  Cenário: Cadastro de novo cliente com CPF válido e senha
    Quando uma requisição de cadastro de cliente é enviada ao Lambda com CPF válido e senha
    Então a resposta do Lambda deve ter status 201
    E a resposta deve confirmar a criação com identificador do usuário e catálogo da oficina

  @cadastro-cliente-cnpj
  Cenário: Cadastro de novo cliente corporativo com CNPJ válido e veículos
    Quando uma requisição de cadastro corporativo é enviada ao Lambda com CNPJ válido e dados do veículo
    Então a resposta do Lambda deve ter status 201
    E a resposta deve conter a identificação do cliente e o veículo associado

  @cadastro-cpf-invalido
  Cenário: Rejeição de cadastro com CPF de dígitos verificadores incorretos
    Quando uma requisição de cadastro é enviada ao Lambda com CPF "111.111.111-11"
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Invalid Document"

  @cadastro-dados-incompletos
  Cenário: Rejeição de cadastro por dados obrigatórios ausentes
    Quando uma requisição de cadastro é enviada ao Lambda sem o campo de senha
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Bad Request"
