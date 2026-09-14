# Plano de ImplementaÃ§Ã£o - Fase 1: MigraÃ§Ã£o para AWS SSM Parameter Store

Este plano detalha a execuÃ§Ã£o da **Fase 1**, desacoplando os endpoints dinÃ¢micos gerados no provisionamento do cluster EKS (`keycloak_url`, `garage_api_url`, `api_gateway_url` e `lambda_auth_url`) atravÃ©s do **AWS Systems Manager (SSM) Parameter Store**.

---

## 1. Escopo da Fase 1

Eliminar 100% dos valores fixos/hardcoded que causaram falhas de comunicaÃ§Ã£o nos seguintes pontos:
1. **Keycloak NLB URL**: Publicada pelo `iac-k8s` em `/garage/keycloak/url` e consumida pela Lambda.
2. **Garage API NLB URL**: Publicada pelo `iac-k8s` em `/garage/api/url` e consumida pela Lambda.
3. **API Gateway URL**: Publicada pelo `iac-k8s` em `/garage/api-gateway/url` e consumida pelos testes E2E.
4. **Lambda Auth Function URL**: Publicada pelo `lamda` em `/garage/lambda/auth-url` e consumida pelos testes E2E.

---

## 2. MudanÃ§as por RepositÃ³rio

### ðŸ“ RepositÃ³rio `15-soat-tech-challenge-iac-k8s`

#### [NEW] `ssm.tf`
- Descobre os NLBs ativos criados pelo Kubernetes utilizando tags nativas:
  - `kubernetes.io/service-name = "garage/keycloak"`
  - `kubernetes.io/service-name = "garage/api-garage"`
- Cria os parÃ¢metros SSM do tipo `String`:
  - `/garage/keycloak/url` = `http://${data.aws_lb.keycloak_nlb.dns_name}:8080`
  - `/garage/api/url` = `http://${data.aws_lb.garage_nlb.dns_name}:8080`
  - `/garage/api-gateway/url` = `${module.api_gateway.api_gateway_url}/api`
- Taggeia todos os parÃ¢metros com `Project = "SOAT-TechChallenge"` e `ManagedBy = "Terraform"`.

#### [MODIFY] `outputs.tf`
- Adiciona os outputs dos parÃ¢metros SSM para consulta e auditoria.

---

### ðŸ“ RepositÃ³rio `15-soat-tech-challenge-lamda`

#### [MODIFY] `main.tf`
- Adiciona data sources para consulta dinÃ¢mica do SSM:
  - `data.aws_ssm_parameter.keycloak_url`
  - `data.aws_ssm_parameter.garage_api_url`
- Atualiza as variÃ¡veis de ambiente da Lambda (`KEYCLOAK_URL` e `GARAGE_API_URL`) para consumir diretamente os valores do SSM.
- Adiciona o recurso `aws_ssm_parameter.lambda_auth_url` para registrar a URL da funÃ§Ã£o Lambda em `/garage/lambda/auth-url`.

#### [MODIFY] `variables.tf`
- Remove os valores estÃ¡ticos dos defaults de `keycloak_url` e `garage_api_url`.

---

### ðŸ“ RepositÃ³rio `15-soat-tech-challenge-garage`

#### [MODIFY] `e2e/pom.xml`
- Adiciona a dependÃªncia `software.amazon.awssdk:ssm` no escopo `test`.

#### [NEW] `e2e/src/test/java/br/com/fiap/garage/e2e/aws/AwsEndpointResolver.java`
- Consulta os parÃ¢metros `/garage/api-gateway/url` e `/garage/lambda/auth-url` usando o `DefaultCredentialsProvider` do AWS SDK v2 (lendo `~/.aws/credentials` ou variÃ¡veis de ambiente).
- Cache estÃ¡tico para performance durante a execuÃ§Ã£o dos testes Cucumber.

#### [MODIFY] `e2e/src/test/java/br/com/fiap/garage/e2e/config/E2eConfig.java`
- IntegraÃ§Ã£o transparente com fallback para o `AwsEndpointResolver` quando `garage.base-uri` ou `garage.auth.lambda.url` estiverem com valor `"auto"` ou ausentes.

#### [MODIFY] `e2e/src/test/resources/application-prd.properties`
- Define os valores padrÃ£o como `auto`:
  ```properties
  garage.base-uri=${E2E_GARAGE_BASE_URI:auto}
  garage.auth.lambda.url=${E2E_LAMBDA_AUTH_URL:auto}
  ```

---

## 3. Ordem de ExecuÃ§Ã£o & VerificaÃ§Ã£o

1. **Implementar e Aplicar `iac-k8s`**:
   - Commitar e dar push no `15-soat-tech-challenge-iac-k8s` (sob autorizaÃ§Ã£o explÃ­cita).
   - Acompanhar pipeline e verificar se os parÃ¢metros foram criados no SSM (`aws ssm describe-parameters`).
2. **Implementar e Aplicar `lamda`**:
   - Commitar e dar push no `15-soat-tech-challenge-lamda` (sob autorizaÃ§Ã£o explÃ­cita).
   - Verificar se a Lambda subiu com as novas variÃ¡veis de ambiente e se publicou `/garage/lambda/auth-url`.
3. **Implementar e Executar no `api-garage`**:
   - Implementar `AwsEndpointResolver` e compilar o mÃ³dulo `e2e`.
   - Executar a suÃ­te completa de testes E2E do Lambda em produÃ§Ã£o:
     ```powershell
     mvn test -pl e2e -Pmanual-e2e -Dtest=RunCucumberTest -Denv=prd "-Dcucumber.filter.tags=@lambda"
     ```
   - Validar 100% de aprovaÃ§Ã£o em todos os 4 cenÃ¡rios (Health, Cadastro, Consulta, Login).

