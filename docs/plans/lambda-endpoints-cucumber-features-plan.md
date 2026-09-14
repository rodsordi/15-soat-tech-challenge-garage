# Plano de Implementação: Features Cucumber BDD para Endpoints do AWS Lambda Auth

Este documento estabelece o planejamento detalhado para a criação de especificações executáveis em Cucumber BDD (`.feature`) e suas respectivas definições de passos (Step Definitions em Java) no módulo `e2e` do projeto `api-garage`, cobrindo todos os endpoints expostos pelo microserviço serverless `15-soat-tech-challenge-lamda`.

---

## 🎯 Contexto e Objetivos

O serviço serverless `15-soat-tech-challenge-lamda` atua como o componente de borda (Edge / Auth Handler) responsável pelo ciclo de vida de autenticação, cadastro unificado de identidades com orquestração Saga, emissão de tokens JWT assinados pelo Keycloak e consulta de status de usuários.

O objetivo é criar arquivos `.feature` dedicados para cada endpoint do Lambda no módulo `e2e/src/test/resources/features/`:
1. **`lambda_health.feature`**: Endpoint `GET /` e `GET /health` (Health Check e Descoberta de Rotas)
2. **`lambda_register.feature`**: Endpoint `POST /register` (Cadastro de Usuários com Validação de CPF/CNPJ e Saga)
3. **`lambda_user_search.feature`**: Endpoint `GET /users/{cpf}` (Consulta de Usuário por CPF)
4. **`lambda_auth_login.feature`**: Endpoint `POST /auth/login` (Autenticação Estrita por CPF e Emissão de Token JWT)

---

## 🏛️ Abordagens Técnicas

### 🥇 Opção 1 (Recomendada - Best Practice): Features Dedicadas por Endpoint com Step Definitions em `LambdaSteps` e `AwsSigV4Filter`
- **Como funciona**:
  - Criar 4 arquivos `.feature` independentes, um para cada operação do Lambda:
    - `lambda_health.feature`
    - `lambda_register.feature`
    - `lambda_user_search.feature`
    - `lambda_auth_login.feature`
  - Criar classe de steps `LambdaSteps.java` em `br.com.fiap.garage.e2e.steps` utilizando `RestAssured`, `AwsSigV4Filter` e a URL do Lambda obtida via `E2eConfig.getLambdaAuthUrl()`.
  - Reutilizar `ScenarioTestContext` para armazenar o `lastResponse` e compartilhar payloads entre passos `Dado`, `Quando` e `Então`.
  - Atualizar `E2eConfig` e `application-prd.properties` com a URL ativa do Lambda (`https://25wfrx7qruoodzfh5nm4sk26ty0sldzb.lambda-url.us-east-1.on.aws`).
  - Utilizar tags específicas (ex: `@lambda`, `@health`, `@register`, `@search`, `@login`) permitindo execuções granulares.
- **Vantagens**:
  - **Alta Coesão**: Cada endpoint possui seu próprio contrato e documentação viva BDD.
  - **Rastreabilidade 1:1**: Alinhamento direto com a arquitetura serverless e testes de regressão automatizados.
  - **Compatibilidade Multiambiente**: Executável tanto localmente (com mocks/WireMock se desejado) quanto em ambiente de nuvem real na AWS via autenticação IAM SigV4.

### 🥈 Opção 2 (Alternativa): Arquivo Único `lambda_endpoints.feature` Agrupando Todos os Endpoints
- **Como funciona**:
  - Centralizar todos os cenários em um único arquivo `.feature`, divididos por seções `Rule` do Gherkin.
- **Trade-offs**:
  - Arquivo muito longo; desvia da solicitação explícita de "um .feature para cada end point do lambda".

### 🥉 Opção 3 (Abordagem Minimalista): Apenas Cenários de Caminho Feliz
- **Como funciona**:
  - Criar os arquivos `.feature` apenas com cenários de sucesso (HTTP 200/201), sem validar regras de borda (rejeição de e-mail, formato de CPF inválido, credenciais erradas).
- **Trade-offs**:
  - Baixa cobertura de validações críticas e regressões de segurança.

---

## 📋 Detalhamento dos Cenários BDD a Implementar

### 1. `lambda_health.feature` (Endpoint: `GET /` e `GET /health`)
```gherkin
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
```

### 2. `lambda_register.feature` (Endpoint: `POST /register`)
```gherkin
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
```

### 3. `lambda_user_search.feature` (Endpoint: `GET /users/{cpf}`)
```gherkin
# language: pt
@e2e @lambda @consulta-usuario
Funcionalidade: Consulta de Usuário por CPF no Lambda Auth
  Como atendente ou sistema da oficina
  Quero consultar a existência de um usuário a partir do seu CPF
  Para verificar o status do cadastro antes de abrir ordens de serviço

  @consulta-usuario-existente
  Cenário: Consulta de usuário previamente cadastrado por CPF válido
    Dado que existe um usuário previamente cadastrado com CPF válido no sistema
    Quando uma requisição de consulta por CPF é enviada ao endpoint "/users/{cpf}" do Lambda
    Então a resposta do Lambda deve ter status 200
    E o usuário retornado deve conter o CPF consultado e status cadastrado

  @consulta-cpf-invalido
  Cenário: Rejeição de consulta com formato de CPF inválido no path
    Quando uma requisição de consulta por CPF é enviada ao endpoint "/users/00000000000" do Lambda
    Então a resposta do Lambda deve ter status 400
    E a resposta deve apresentar o erro "Invalid CPF"

  @consulta-cpf-nao-encontrado
  Cenário: Consulta de CPF inexistente na base
    Quando uma requisição de consulta por CPF é enviada ao Lambda com um CPF válido não cadastrado
    Então a resposta do Lambda deve ter status 404
    E a resposta deve indicar usuário não encontrado
```

### 4. `lambda_auth_login.feature` (Endpoint: `POST /auth/login`)
```gherkin
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
```

---

## 🛠️ Alterações de Código Necessárias

1. **Arquivos `.feature`**:
   - `e2e/src/test/resources/features/lambda_health.feature`
   - `e2e/src/test/resources/features/lambda_register.feature`
   - `e2e/src/test/resources/features/lambda_user_search.feature`
   - `e2e/src/test/resources/features/lambda_auth_login.feature`
2. **Definições de Passos (Java)**:
   - `e2e/src/test/java/br/com/fiap/garage/e2e/steps/LambdaSteps.java`
     - Métodos `@Quando` e `@Entao` correspondentes.
     - Suporte a geração de CPF dinâmico válido (Módulo 11) para evitar conflitos de duplicidade em execuções repetidas.
     - Envio de chamadas HTTP via `io.restassured.RestAssured` utilizando `AwsSigV4Filter` para autenticação IAM da Function URL.
3. **Configuração de Propriedades**:
   - Atualizar a URL padrão em `E2eConfig.java` e `application-prd.properties` para:
     `https://25wfrx7qruoodzfh5nm4sk26ty0sldzb.lambda-url.us-east-1.on.aws`
4. **Validação**:
   - Execução seletiva dos testes BDD via Maven (`mvn test -pl e2e -Dcucumber.filter.tags="@lambda"`).
