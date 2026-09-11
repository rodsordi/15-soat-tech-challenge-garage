# Plano de Implementação: Autenticação AWS SigV4 no Cucumber via `~/.aws/credentials`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-11  
> **Repositório Alvo**: `15-soat-tech-challenge-garage` (`e2e`)  
> **Autor**: Engenheiro de Software & Especialista em Arquitetura Cloud/QA  

---

## 1. Visão Geral & Motivação

Para consumir com sucesso os endpoints da função serverless `garage-auth-handler` (`/auth/login` e `/register`) no ambiente AWS Academy, as requisições HTTP disparadas pelo **Cucumber / REST Assured** devem ser assinadas criptograficamente com o protocolo **AWS Signature Version 4 (SigV4)**.

O objetivo deste plano é estruturar a leitura das credenciais ativas diretamente do arquivo de configuração da AWS (`~/.aws/credentials` ou caminho customizável) e injetar a assinatura SigV4 de forma transparente nas chamadas HTTP do módulo `e2e`.

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Interceptor `AwsSigV4Filter` no REST Assured com AWS SDK v2 Auth

* **Motivação Técnica**:
  - O AWS SDK v2 para Java (`software.amazon.awssdk:auth`) fornece a implementação de referência do `Aws4Signer`, capaz de calcular assinaturas HMAC-SHA256 para URLs, headers, payloads e query parameters com suporte a tokens de sessão temporários (`x-amz-security-token`) do AWS Academy.
  - A resolução de credenciais utiliza o `ProfileCredentialsProvider` e `DefaultCredentialsProvider`, permitindo ler do arquivo local padrão (`~/.aws/credentials` / `~/.aws/config`), com fallback para variáveis de ambiente (`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`, `AWS_SESSION_TOKEN`) e suporte a arquivo/perfil customizável (`aws.credentials.path` / `aws.profile`).
* **Como Funciona**:
  1. No `e2e/pom.xml`, adicionar a dependência oficial:
     - `software.amazon.awssdk:auth`
  2. Criar a classe `AwsSigV4Filter` implementando `io.restassured.filter.Filter`:
     - Intercepta a requisição do REST Assured;
     - Lê as credenciais ativas do arquivo de credentials do usuário;
     - Gera o hash SHA256 do payload e calcula a assinatura SigV4 para o serviço `"lambda"` na região `"us-east-1"`;
     - Injeta os headers `Authorization: AWS4-HMAC-SHA256...`, `x-amz-date` e `x-amz-security-token` na requisição antes de despachá-la.
  3. No `LambdaAuthClient`:
     - Registrar o filtro na chamada REST Assured:
       ```java
       given()
           .filter(new AwsSigV4Filter())
           .contentType(JSON)
           .body(payload)
           .post(authUrl + "/auth/login")
       ```
* **Vantagens**:
  - ✅ **Zero Reinvenção de Roda**: Canonicalização HTTP e cálculo HMAC-SHA256 garantidos pela biblioteca oficial da AWS.
  - ✅ **Compatibilidade Nativa com AWS Academy**: Lida perfeitamente com a chave de sessão temporária (`aws_session_token`).
  - ✅ **Transparência no BDD**: O cenário do Cucumber e os step definitions continuam realizando requisições HTTP puras.
  - ✅ **Zero Intervenção no Cluster**: Opera 100% via rede externa com credenciais IAM, sem tunelamento.
* **Complexidade**: Baixa/Média.

---

### 🥈 Opção 2 (Alternativa): Leitor INI Customizado + Assinador Manual HMAC-SHA256

* **Motivação Técnica**:
  - Implementar um parser em Java para ler o arquivo `~/.aws/credentials` e montar manualmente os cabeçalhos SigV4 usando `javax.crypto.Mac`.
* **Trade-offs**:
  - ⚠️ Alto risco de incompatibilidades: a especificação AWS SigV4 possui regras rígidas de ordenação de cabeçalhos, URI encoding específico e formatação de timestamps ISO 8601. Qualquer divergência gera `403 SignatureDoesNotMatch`.
* **Complexidade**: Alta.

---

### 🥉 Opção 3 (Minimalista): Invocação do SDK Lambda Direta

* **Motivação Técnica**:
  - Substituir a chamada REST Assured da Function URL pela invocação via SDK (`LambdaClient.builder().credentialsProvider(...)`).
* **Trade-offs**:
  - ⚠️ Desvia do teste de borda da Function URL e consome a API de gerenciamento da AWS.
* **Complexidade**: Baixa.

---

## 3. Detalhamento dos Componentes a Alterar (Opção 1)

1. **`e2e/pom.xml`**:
   - Adicionar dependência `software.amazon.awssdk:auth:2.30.34` com escopo `test`.
2. **`e2e/src/test/resources/application-prd.properties`**:
   - Adicionar propriedades configuráveis:
     ```properties
     aws.region=${AWS_REGION:us-east-1}
     aws.profile=${AWS_PROFILE:default}
     aws.credentials.path=${AWS_SHARED_CREDENTIALS_FILE:}
     ```
3. **`e2e/src/test/java/br/com/fiap/garage/e2e/filter/AwsSigV4Filter.java` [NOVO]**:
   - Implementa `Filter` do REST Assured assinando a requisição HTTP com credenciais IAM.
4. **`e2e/src/test/java/br/com/fiap/garage/e2e/client/LambdaAuthClient.java`**:
   - Incorporar o `AwsSigV4Filter` nas chamadas `/auth/login` e `/register`.

---

## 4. Plano de Verificação

1. **Teste Unitário do Filtro**: Validar a leitura das credenciais em `~/.aws/credentials` e geração correta dos headers SigV4.
2. **Execução E2E em PRD**:
   ```powershell
   mvn test -pl e2e -Pmanual-e2e -Denv=prd --no-transfer-progress
   ```
   Validar que a Function URL da Lambda aceita a requisição assinada e emite o token JWT com sucesso.
