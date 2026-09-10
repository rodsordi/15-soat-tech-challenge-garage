# Plano de Refatoração: Testes E2E sem Inicialização de Nova Instância (Black-Box Puro)

## 🎯 Objetivo
Eliminar a inicialização de uma nova instância embutida da aplicação (servidor Tomcat na JVM de testes do JUnit/Cucumber) durante a execução do módulo `e2e`. O teste deve atuar como **Black-Box E2E puro**, consumindo exclusivamente a aplicação que já se encontra em execução (ex: contêiner Docker `garage` em `http://localhost:8080/api` ou ambiente em nuvem).

---

## 🔍 Diagnóstico do Cenário Atual

1. **Por que uma nova instância sobre atualmente?**:
   - Em [`CucumberSpringConfiguration.java`](file:///c:/git/fiap/15-soat-tech-challenge-garage/e2e/src/test/java/br/com/fiap/garage/e2e/config/CucumberSpringConfiguration.java):
     ```java
     @SpringBootTest(classes = GarageApplication.class, webEnvironment = RANDOM_PORT)
     ```
   - A anotação `@SpringBootTest` com `webEnvironment = RANDOM_PORT` instrui o Spring a inicializar um servidor Web Tomcat em uma porta dinâmica e executar os testes contra essa porta local.
2. **O Desafio da Autenticação no Contêiner Docker Atual (`garage:0.0.1-SNAPSHOT`)**:
   - Ao direcionar as requisições para `http://localhost:8080/api`, a chamada `POST /v1/customers` recebe `401 Unauthorized`.
   - **Causa Raiz**: O contêiner Docker executa a classe de produção [`SecurityConfig.java`](file:///c:/git/fiap/15-soat-tech-challenge-garage/application/src/main/java/br/com/fiap/commons/config/SecurityConfig.java), que valida o token JWT contra o Keycloak (`KEYCLOAK_JWK_SET_URI`). Como o Keycloak não está em execução no Docker Compose, o contêiner rejeita qualquer token.
   - Na instância embutida do teste, isso não ocorria porque a classe de teste injetava o mock [`SecurityTestConfig`](file:///c:/git/fiap/15-soat-tech-challenge-garage/e2e/src/test/java/br/com/fiap/garage/e2e/config/SecurityTestConfig.java).

---

## 📐 Opções de Arquitetura & Implementação

### 🥇 Opção 1 (Recomendada - Best Practice Dev): Black-Box E2E sem Tomcat + `JwtDecoder` Local para Docker Compose
- **Descrição**:
  1. **No Módulo `e2e`**:
     - Alterar `CucumberSpringConfiguration`: substituir `webEnvironment = RANDOM_PORT` por `webEnvironment = NONE`.
     - O Spring continua injetado no Cucumber (`@CucumberContextConfiguration`) para gerenciar beans de teste como `ScenarioTestContext` e clientes auxiliares, porém **nenhum servidor Tomcat/HTTP é inicializado pela suíte de teste**.
     - Fixar a URL padrão do `RestAssured.baseURI` em `http://localhost:8080/api` (customizável via `-Dgarage.base-uri=...` ou variável de ambiente `GARAGE_BASE_URI`).
  2. **Na Aplicação (`application`)**:
     - Criar uma configuração `@Configuration @Profile("local")` com um `JwtDecoder` que decodifique tokens localmente quando a aplicação rodar com o perfil `local`, permitindo que o contêiner Docker na porta `8080` processe as requisições autenticadas mesmo sem um servidor Keycloak externo dedicado.
     - Recompilar a aplicação e recriar a imagem Docker: `docker compose up -d --build garage`.
- **Vantagens**:
  - Teste 100% Black-Box: consome exatamente o contêiner rodando em `http://localhost:8080/api`.
  - Inicialização dos testes E2E ultra rápida (economia de 10 a 15 segundos por execução, pois não sobe Tomcat).
  - Não requer subir contêiner pesado do Keycloak na máquina local do desenvolvedor.
- **Complexidade**: Baixa/Média.

---

### 🥈 Opção 2 (Alternativa Enterprise): Black-Box E2E com Keycloak Real no Docker Compose
- **Descrição**:
  1. **No Módulo `e2e`**:
     - Alterar `CucumberSpringConfiguration` com `webEnvironment = NONE`, consumindo diretamente `http://localhost:8080/api`.
     - `ScenarioTestContext` realiza chamada HTTP ao Keycloak para obter um token JWT real (grant type password/client credentials) antes de executar os cenários.
  2. **No Docker Compose**:
     - Adicionar o serviço oficial do Keycloak (`quay.io/keycloak/keycloak:26.1.0`) no `docker-compose.override.yml` na porta `8081`, importando o realm `garage`.
     - Configurar `KEYCLOAK_JWK_SET_URI: http://keycloak:8080/realms/garage/protocol/openid-connect/certs` no contêiner `garage`.
- **Trade-offs**:
  - *Prós*: Fidelidade idêntica ao ambiente de produção com OpenID Connect real.
  - *Contras*: Maior consumo de memória RAM (+1 GB para o Keycloak) e tempo de startup do Docker Compose consideravelmente maior.
- **Complexidade**: Média/Alta.

---

### 🥉 Opção 3 (Desacoplamento Total do Spring no Módulo E2E):
- **Descrição**:
  - Remover completamente `@CucumberContextConfiguration` e `@SpringBootTest` do módulo `e2e`.
  - Manter o Cucumber puro (apenas Java + RestAssured + AssertJ), gerenciando o contexto via singleton/PicoContainer nativo do Cucumber.
  - Exige resolver a autenticação no Docker Compose (conforme Opção 1 ou 2).
- **Trade-offs**:
  - *Prós*: Módulo `e2e` fica 100% independente do framework Spring.
  - *Contras*: Contradiz a solicitação anterior #8 ("este módulo e2e precisa subir com o cucumber com o spring injetado").
- **Complexidade**: Média.

---

## 🛠️ Detalhamento da Execução da Opção 1 (Recomendada)

### 1. Ajuste em `e2e/src/test/java/br/com/fiap/garage/e2e/config/CucumberSpringConfiguration.java`
```java
@CucumberContextConfiguration
@ActiveProfiles("local")
@SpringBootTest(classes = GarageApplication.class, webEnvironment = NONE)
public class CucumberSpringConfiguration {

    @Before
    public void setup() {
        var customUri = System.getProperty("garage.base-uri", System.getenv("GARAGE_BASE_URI"));
        if (customUri != null && !customUri.isBlank()) {
            RestAssured.baseURI = customUri;
        } else {
            RestAssured.baseURI = "http://localhost:8080/api";
        }

        setupMessaging();
    }
```

### 2. Configuração de `JwtDecoder` Local na Aplicação
- Criar `br.com.fiap.commons.config.LocalSecurityConfig` em `application`:
  ```java
  @Configuration
  @Profile("local")
  public class LocalSecurityConfig {
      @Bean
      @Primary
      public JwtDecoder localJwtDecoder() {
          return token -> Jwt.withTokenValue(token)
                  .header("alg", "none")
                  .claim("sub", "local-user")
                  .claim("preferred_username", "90443471010")
                  .claim("email", "admin@garage.com")
                  .claim("realm_access", Map.of("roles", List.of("EMPLOYEE", "ADMIN", "CUSTOMER")))
                  .issuedAt(Instant.now())
                  .expiresAt(Instant.now().plus(1, ChronoUnit.HOURS))
                  .build();
      }
  }
  ```

### 3. Rebuild e Atualização do Contêiner Docker
```powershell
mvn clean package -pl application,domain -DskipTests
docker compose build garage
docker compose up -d garage
```

---

## 🧪 Plano de Verificação
1. Validar que o contêiner `garage` está rodando em `http://localhost:8080/api/actuator/health`.
2. Executar a suíte E2E:
   ```powershell
   mvn test -pl e2e -Pmanual-e2e
   ```
3. Verificar nos logs que **nenhum Tomcat foi inicializado na JVM dos testes** (tempo de inicialização reduzido a ~1-2s) e que todas as requisições são processadas diretamente pelo contêiner Docker na porta 8080 com 100% de sucesso.
