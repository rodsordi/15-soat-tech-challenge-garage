# Plano de ImplementaÃ§Ã£o: Descoberta DinÃ¢mica de Endpoints AWS (Auto-Discovery) nos Testes E2E

Este plano descreve a implementaÃ§Ã£o da **OpÃ§Ã£o 1 (Auto-Discovery DinÃ¢mico via AWS SDK)** no mÃ³dulo `e2e` do projeto `api-garage`. O objetivo Ã© eliminar o acoplamento a URLs estÃ¡ticas no `application-prd.properties`, coletando automaticamente os endpoints do API Gateway e da URL da funÃ§Ã£o Lambda atravÃ©s das credenciais do `~/.aws/credentials` ou variÃ¡veis de ambiente padrÃ£o da AWS.

---

## 1. Contexto e MotivaÃ§Ã£o

Atualmente, quando o Terraform provisiona ou recria o ambiente de produÃ§Ã£o na AWS (EKS cluster, API Gateway HTTP API e AWS Lambda), novos identificadores e URLs sÃ£o gerados dinamicamente:
- **API Gateway**: `https://<api-id>.execute-api.us-east-1.amazonaws.com/api`
- **Lambda Function URL**: `https://<lambda-id>.lambda-url.us-east-1.on.aws/`

Armazenar essas URLs de forma fixa no arquivo `application-prd.properties` exige ediÃ§Ã£o manual a cada subida do Terraform. A soluÃ§Ã£o adotarÃ¡ o padrÃ£o de **Descoberta DinÃ¢mica de Infraestrutura** orientada a credenciais existentes (`~/.aws/credentials` ou variÃ¡veis `AWS_ACCESS_KEY_ID`, etc.), mantendo fallback para overrides manuais.

---

## 2. MudanÃ§as Propostas

### A. MÃ³dulo `e2e` (`e2e/pom.xml`)
Adicionar as dependÃªncias do AWS SDK v2 jÃ¡ gerenciadas pelo BOM do Spring Cloud AWS:
- `software.amazon.awssdk:apigatewayv2`: Para consultar APIs HTTP no API Gateway v2.
- `software.amazon.awssdk:lambda`: Para consultar a Function URL da Lambda `garage-auth-handler`.

### B. Novo Componente: `AwsEndpointResolver`
Criar a classe `br.com.fiap.garage.e2e.aws.AwsEndpointResolver`:
- Utiliza `DefaultCredentialsProvider.create()` para autenticar via `~/.aws/credentials` ou variÃ¡veis de ambiente.
- Identifica a regiÃ£o (padrÃ£o `us-east-1`).
- MÃ©todo `resolveGarageBaseUri()`:
  - Consulta `ApiGatewayV2Client.getApis()`.
  - Filtra pela API com nome `techchallenge-cluster-api-gateway` ou tags `SOAT-TechChallenge`.
  - Retorna `ApiEndpoint + "/api"`.
- MÃ©todo `resolveLambdaAuthUrl()`:
  - Consulta `LambdaClient.getFunctionUrlConfig()` para a funÃ§Ã£o `garage-auth-handler`.
  - Retorna a `FunctionUrl`.
- Implementa cache estÃ¡tico em memÃ³ria para evitar chamadas repetidas Ã  AWS durante a execuÃ§Ã£o dos mÃºltiplos cenÃ¡rios Cucumber.

### C. RefatoraÃ§Ã£o de `E2eConfig`
Atualizar `br.com.fiap.garage.e2e.config.E2eConfig`:
- No mÃ©todo `getBaseUri()`: Se o valor resolvido for `"auto"`, nulo ou vazio em perfil `prd`, delega para `AwsEndpointResolver.resolveGarageBaseUri()`.
- No mÃ©todo `getLambdaAuthUrl()`: Se o valor resolvido for `"auto"`, nulo ou vazio em perfil `prd`, delega para `AwsEndpointResolver.resolveLambdaAuthUrl()`.
- MantÃ©m a precedÃªncia estrita: VariÃ¡veis de Ambiente / System Properties tÃªm prioridade sobre o auto-discovery.

### D. AtualizaÃ§Ã£o do `application-prd.properties`
Alterar [e2e/src/test/resources/application-prd.properties](file:///C:/git/fiap/15-soat-tech-challenge-garage/e2e/src/test/resources/application-prd.properties):
```properties
# ===================================================================
# E2E PRODUCTION ENVIRONMENT (AWS EKS / API Gateway / Remote)
# Dynamic auto-discovery via AWS SDK (~/.aws/credentials or env vars)
# ===================================================================
garage.base-uri=${E2E_GARAGE_BASE_URI:auto}
garage.auth.type=${E2E_AUTH_TYPE:lambda}
garage.auth.token=${E2E_AUTH_TOKEN:}
garage.auth.lambda.url=${E2E_LAMBDA_AUTH_URL:auto}
garage.auth.lambda.username=${E2E_AUTH_USERNAME:529.982.247-25}
garage.auth.lambda.password=${E2E_AUTH_PASSWORD:SenhaForte@2026}
```

---

## 3. Plano de VerificaÃ§Ã£o

1. **Testes UnitÃ¡rios da ResoluÃ§Ã£o**:
   - Atualizar `E2eConfigTest.java` para validar a resoluÃ§Ã£o com `"auto"` e o comportamento de cache.
2. **ExecuÃ§Ã£o Local dos Testes E2E com Auto-Discovery**:
   - Executar `mvn test -pl e2e -Pmanual-e2e -Dtest=RunCucumberTest -Denv=prd "-Dcucumber.filter.tags=@health"`
   - Confirmar que as URLs sÃ£o descobertas dinamicamente da conta AWS sem erro e que os testes `@health` passam.
3. **ValidaÃ§Ã£o de Override**:
   - Executar passando `-DE2E_GARAGE_BASE_URI=https://custom-url/api` e confirmar que a variÃ¡vel manual sobrescreve o auto-discovery.

