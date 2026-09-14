# language: pt
@e2e @lambda @autenticacao @jwt
Funcionalidade: Autenticação Estrita por CPF e Emissão de Token JWT
  Como usuário cadastrado na oficina
  Quero me autenticar utilizando estritamente meu CPF e senha
  Para obter um token JWT e acessar recursos protegidos da API

  @login-cpf-sucesso
  Cenário: Autenticação com CPF válido desmascarado e senha correta
    Dado que existe um usuário cadastrado com credenciais válidas
    Quando uma requisição de login é enviada com CPF e senha corretos
    Então a resposta do Lambda deve ter status 200
    E o corpo da resposta deve conter um token de acesso JWT válido e tempo de expiração

  @login-cpf-mascarado
  Cenário: Autenticação com CPF mascarado higienizado com sucesso
    Dado que existe um usuário cadastrado com credenciais válidas
    Quando uma requisição de login é enviada com CPF formatado com máscara e senha correta
    Então a resposta do Lambda deve ter status 200
    E o corpo da resposta deve conter o token de acesso emitido

  @login-rejeicao-email
  Cenário: Rejeição de tentativa de autenticação utilizando e-mail como identificador
    Quando uma requisição de login é enviada com e-mail "usuario@garage.com" e senha
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Invalid Document"

  @login-cpf-invalido
  Cenário: Rejeição de autenticação com CPF inválido
    Quando uma requisição de login é enviada com CPF "123.456.789-00" e senha
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Invalid Document"

  @login-credenciais-incorretas
  Cenário: Rejeição de autenticação com senha incorreta
    Dado que existe um usuário cadastrado com credenciais válidas
    Quando uma requisição de login é enviada com CPF válido e senha incorreta
    Então a resposta do Lambda deve ter status 401
    E a resposta deve apresentar o erro "Unauthorized"

  @login-campos-ausentes
  Cenário: Rejeição de autenticação com campos obrigatórios ausentes
    Quando uma requisição de login é enviada sem o campo de identificação
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Bad Request"
