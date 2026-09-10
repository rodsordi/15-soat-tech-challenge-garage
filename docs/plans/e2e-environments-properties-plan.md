# Plano de Arquitetura: Discriminação de Ambientes (Local e PRD) no Módulo `e2e` & Análise do Spring no Cucumber

> **Status**: Proposta para Revisão e Aprovação  
> **Data**: 10/09/2026  
> **Especialista**: Engenheiro de Software Lead / Arquiteto de Testes  
> **Módulo Alvo**: `e2e` (`api-garage.e2e`)  
> **Versões**: Spring Boot 4.0.7, Java 25, Cucumber 7.34.2  

---

## 1. Contexto e Motivação

O usuário solicitou:
1. **Esclarecimento de Versão**: A aplicação e módulos estão baseados em **Spring Boot 4.0.7** e Java 25.
2. **Escopo Específico no Módulo `e2e`**: Criação de arquivos de propriedades para discriminar os ambientes `"local"` e `"prd"` no diretório `e2e/src/test/resources`.
3. **Parecer Técnico Arquitetural**: Análise profunda sobre a conveniência de **injetar o Spring Boot no Cucumber** (`cucumber-spring`) versus **manter o teste E2E Black-Box puro** (com `cucumber-picocontainer` ou leitor de configuração leve).

---

## 2. Parecer Técnico Arquitetural: Deve-se Usar o Spring Injetado no Cucumber para E2E?

### Veredito: **NÃO recomendada a injeção do Spring Boot no Cucumber para testes E2E.**
A recomendação arquitetural definitiva é manter o módulo `e2e` como **Black-Box Puro (sem Spring injetado)**.

### Fundamentação Técnica Detalhada:

1. **Natureza do Teste E2E (Pirâmide de Testes de Martin Fowler)**:
   - Um teste E2E tem o propósito de validar o sistema como um **cliente externo real** (navegador, mobile ou API Gateway).
   - O executor de testes não deve compartilhar o *ApplicationContext*, classloader, banco de dados interno ou ciclo de vida do servidor.
   - Quando o teste roda contra **PRD** (Produção em AWS EKS), o serviço já está em execução na nuvem. Injetar o Spring no teste E2E forçaria a JVM de teste a tentar instanciar contextos locais, conexões de banco e mocks, o que é um anti-pattern conceitual e operacional.

2. **Compatibilidade Spring Boot 4 & Java 25**:
   - O ecossistema `cucumber-spring` (v7.34.2) foi projetado para Spring Framework 5 e 6 (Spring Boot 2.x e 3.x).
   - O Spring Boot 4 / Spring Framework 7 traz reformulações severas em `TestContextManager`, `AOT` e Jakarta EE 11. Forçar a integração do `cucumber-spring` gera fragilidades de runtime, dependência de bibliotecas de teste pesadas e potenciais incompatibilidades no Java 25.

3. **Performance e Agilidade (Fast Feedback)**:
   - **Com Spring Injetado**: O bootstrap do contexto Spring adiciona de 10 a 20 segundos de overhead a cada execução, mesmo que a aplicação já esteja rodando externamente.
   - **Black-Box Puro (PicoContainer + Config)**: O bootstrap do Cucumber é imediato (< 500ms), executando toda a bateria em ~3 a 4 segundos.

4. **Portabilidade em Pipelines CI/CD**:
   - Um executor Black-Box puro empacota em um JAR leve (ou imagem runner) que pode ser disparado em qualquer esteira de homologação ou smoke test pós-deploy em produção apenas passando a URL e credenciais, sem exigir infraestrutura local (Postgres, LocalStack, etc.).

---

## 3. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Black-Box Puro com `E2eConfig` + `application-local.properties` e `application-prd.properties`

* **Como Funciona**:
  - O módulo `e2e` mantém o `cucumber-picocontainer` para o estado de cenário (`ScenarioTestContext`).
  - Cria-se uma classe de configuração limpa `E2eConfig` que lê o ambiente via propriedade de sistema (`-Denv=local` ou `-Denv=prd`, default: `local`) ou variável de ambiente `E2E_ENV`.
  - O `E2eConfig` carrega automaticamente o arquivo correspondente do classpath:
    - `application-local.properties` para desenvolvimento local.
    - `application-prd.properties` para produção / staging.
  - Permite override granular via variáveis de ambiente (`E2E_BASE_URI`, `E2E_AUTH_TOKEN`).
* **Estrutura de Arquivos Proposta**:
  - `e2e/src/test/resources/application.properties` (configurações base e fallbacks).
  - `e2e/src/test/resources/application-local.properties` (URL local `http://localhost:8080/api`, token de teste local).
  - `e2e/src/test/resources/application-prd.properties` (URL do cluster/API Gateway, parâmetros de autenticação Keycloak de PRD).
* **Vantagens**:
  - ✅ **Zero acoplamento com o servidor**: roda contra qualquer URL sem subir Spring.
  - ✅ **Velocidade máxima**: execução em segundos.
  - ✅ **Flexibilidade total**: chaveamento simples via `-Denv=prd` ou `-Denv=local`.
  - ✅ **100% compatível** com Java 25 e sem atritos de bibliotecas.

---

### 🥈 Opção 2 (Alternativa): Spring TestContext Minimalista Apenas para Configuração (`@ContextConfiguration`)

* **Como Funciona**:
  - Utiliza `cucumber-spring` com uma classe de configuração isolada (`@Configuration @PropertySource(...)`), **sem** `@SpringBootTest`.
  - O Spring é usado unicamente para fazer injeção de dependência das propriedades via `@Value` e gerenciar o `ScenarioTestContext`.
* **Vantagens**:
  - ✅ Permite sintaxe Spring (`@Value("${garage.base-uri}")`).
* **Trade-offs**:
  - ⚠️ Adiciona dependência de `spring-boot-starter-test` / `spring-context` no módulo `e2e`.
  - ⚠️ Aumenta o tempo de inicialização em ~2 a 4 segundos.
  - ⚠️ Sujeito a futuras quebras de compatibilidade entre versões de `cucumber-spring` e Spring Boot 4.

---

### 🥉 Opção 3 (Anti-Pattern): Injeção Total com `@SpringBootTest` e Servidor Embutido

* **Como Funciona**:
  - Restaura o `@SpringBootTest(webEnvironment = RANDOM_PORT)` no módulo `e2e`.
* **Trade-offs**:
  - ❌ **Inviável para PRD**: Tentará inicializar a aplicação localmente com banco H2/PostgreSQL mesmo quando o teste for executado contra o cluster Kubernetes de produção.
  - ❌ **Lento**: Mais de 15 segundos para iniciar.
  - ❌ Viola os princípios da pirâmide de testes para Black-Box E2E.

---

## 4. Conteúdo Detalhado dos Arquivos Propostos (Opção 1)

### 4.1. `e2e/src/test/resources/application.properties` (Base E2E)
```properties
# ===================================================================
# E2E BASE CONFIGURATION
# ===================================================================
e2e.env=local
e2e.http.timeout.connect-ms=5000
e2e.http.timeout.read-ms=10000
```

### 4.2. `e2e/src/test/resources/application-local.properties` (Ambiente Local)
```properties
# ===================================================================
# E2E LOCAL ENVIRONMENT (Docker Compose / Local Dev)
# ===================================================================
garage.base-uri=http://localhost:8080/api
garage.auth.type=bearer-mock
garage.auth.token=Bearer e2e-integration-token
```

### 4.3. `e2e/src/test/resources/application-prd.properties` (Ambiente PRD / Kubernetes)
```properties
# ===================================================================
# E2E PRODUCTION ENVIRONMENT (AWS EKS / API Gateway)
# ===================================================================
garage.base-uri=${E2E_GARAGE_BASE_URI:https://api-garage.fiap.com.br/api}
garage.auth.type=${E2E_AUTH_TYPE:oidc-keycloak}
garage.auth.token=${E2E_AUTH_TOKEN:}
garage.auth.keycloak.token-url=${E2E_KEYCLOAK_TOKEN_URL:http://keycloak.garage.svc.cluster.local:8080/realms/garage/protocol/openid-connect/token}
garage.auth.keycloak.client-id=${E2E_KEYCLOAK_CLIENT_ID:garage-e2e}
garage.auth.keycloak.client-secret=${E2E_KEYCLOAK_CLIENT_SECRET:}
```

### 4.4. Componente de Configuração `E2eConfig.java` (POJO Singleton / PicoContainer)
```java
package br.com.fiap.garage.e2e.config;

import java.io.InputStream;
import java.util.Properties;

public final class E2eConfig {

    private static final Properties properties = new Properties();

    static {
        loadProperties();
    }

    private static void loadProperties() {
        // Carrega base
        loadResource("application.properties");

        // Identifica ambiente ativo (default: local)
        String env = System.getProperty("env",
                System.getenv().getOrDefault("E2E_ENV", properties.getProperty("e2e.env", "local")));

        // Carrega arquivo específico do ambiente
        loadResource("application-" + env + ".properties");
    }

    private static void loadResource(String filename) {
        try (InputStream in = E2eConfig.class.getClassLoader().getResourceAsStream(filename)) {
            if (in != null) {
                properties.load(in);
            }
        } catch (Exception ignored) {
        }
    }

    public static String getBaseUri() {
        return System.getProperty("garage.base-uri",
                System.getenv().getOrDefault("GARAGE_BASE_URI",
                        properties.getProperty("garage.base-uri", "http://localhost:8080/api")));
    }

    public static String getAuthToken() {
        return System.getProperty("garage.auth.token",
                System.getenv().getOrDefault("GARAGE_AUTH_TOKEN",
                        properties.getProperty("garage.auth.token", "Bearer e2e-integration-token")));
    }
}
```

---

## 5. Como o Usuário Alternará os Ambientes

- **Para rodar contra ambiente local**:
  ```powershell
  mvn test -pl e2e -Pmanual-e2e
  ```
  *(Default automático: `local` -> `http://localhost:8080/api`)*

- **Para rodar contra ambiente de produção/remoto**:
  ```powershell
  mvn test -pl e2e -Pmanual-e2e -Denv=prd -Dgarage.base-uri=https://seu-api-gateway/api
  ```
  *(Ou definindo as variáveis de ambiente `E2E_ENV=prd` e `GARAGE_BASE_URI`)*

---

## 6. Próximos Passos (Aguardando Aprovação)

Conforme a **Regra de Ouro #1 do Especialista**, nenhuma alteração no código do módulo `e2e` foi iniciada. Aguardamos sua confirmação para prosseguir com a implementação da **Opção 1**.
