# Plano de Arquitetura & Integração: Validação de JWT do Keycloak no `api-garage`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-04  
> **Autor**: Engenheiro Especialista em Desenvolvimento & Arquitetura de Software  
> **Repositório Alvo**: `15-soat-tech-challenge-garage`  

---

## 1. Visão Geral do Cenário Atual vs. Estado Desejado

### 1.1 Cenário Atual (`api-garage`)
- A aplicação utiliza autenticação customizada com `TokenService`, `SecurityFilter` e `LoginService`.
- Os tokens são assinados e verificados de forma simétrica utilizando **HMAC-SHA (HS256)** através de uma chave compartilhada (`jwt.secret`).
- A cada requisição HTTP recebida com token Bearer, o filtro `SecurityFilter`:
  1. Decodifica o token com `jwt.secret`.
  2. Executa uma query síncrona no banco de dados (`UserRepository.findByEmail(username)` via `LoginService`) para carregar o `UserDetails`.
- O endpoint `/auth/login` é exposto internamente pelo próprio Spring Boot para emitir esses tokens legados.

### 1.2 Estado Desejado (Integração com Keycloak & Lambda)
- O fluxo de cadastro e login de clientes/funcionários agora é gerenciado pelo **Keycloak** (orquestrado pela AWS Lambda `garage-auth-handler`).
- O Keycloak emite tokens **JWT assinados assimetricamente com chave privada RSA (RS256)** no Realm `garage`.
- O microsserviço `api-garage` deve atuar como um **OAuth2 Resource Server** padrão:
  - Valida a autenticidade e integridade dos tokens JWT através das chaves públicas do Keycloak expostas no endpoint **JWKS** (`/protocol/openid-connect/certs`).
  - Opera de forma **100% Stateless**: elimina a necessidade de consulta a banco de dados em cada requisição para autenticação.
  - Extrai as permissões/roles emitidas pelo Keycloak (`realm_access.roles`) e mapeia para `GrantedAuthority` do Spring Security (ex: `ROLE_CUSTOMER`, `ROLE_EMPLOYEE`).
  - Obtém o identificador do usuário autenticado a partir dos claims padrão do token (`preferred_username` representando o CPF/Documento ou `sub` representando o UUID do usuário no Keycloak).

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

Em estrita observância às melhores práticas de engenharia de software e arquitetura limpa, apresentamos as seguintes opções de implementação:

### 🥇 Opção 1 (Recomendada - Best Practice): Spring Security OAuth2 Resource Server Nativo (JWKS OIDC)

* **Motivação Técnica**:
  Esta é a abordagem recomendada pela documentação oficial do Spring Security 6/7, padrão de mercado para microsserviços modernos baseados em OpenID Connect (OIDC).
* **Como Funciona**:
  1. Adiciona a dependência padrão `org.springframework.boot:spring-boot-starter-oauth2-resource-server` no módulo `application/pom.xml`.
  2. Substitui o filtro customizado `SecurityFilter` e a classe `TokenService` pela configuração nativa do Spring Security:
     ```java
     http.oauth2ResourceServer(oauth2 -> oauth2
         .jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter()))
     );
     ```
  3. Cria o conversor customizado `KeycloakJwtAuthenticationConverter` (implementando `Converter<Jwt, AbstractAuthenticationToken>`):
     - Mapeia o claim `realm_access.roles` (ex: `CUSTOMER`, `EMPLOYEE`) para autoridades do Spring Security prefixadas com `ROLE_` (`ROLE_CUSTOMER`, `ROLE_EMPLOYEE`).
     - Define o Subject principal como o `preferred_username` (CPF) ou `email`.
  4. Configuração em `application.properties`:
     ```properties
     # Keycloak JWKS URI no Cluster EKS / VPC
     spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI:http://keycloak.garage.svc.cluster.local:8080/realms/garage/protocol/openid-connect/certs}
     ```
  5. Estratégia de Testes (TDD):
     - Fornece um `@TestConfiguration` para testes locais e de integração injetando um `JwtDecoder` simulado (mock) ou chave assimétrica de teste, permitindo que toda a suíte de testes unitários (`mvn test`) continue executando de forma rápida, isolada e sem dependência de um Keycloak real rodando na máquina de build/CI.
* **Vantagens**:
  - ✅ **Padrão da Indústria**: Zero código proprietário para validação criptográfica de tokens.
  - ✅ **Rotação Automática de Chaves**: O Spring Security gerencia em memória o cache e a atualização transparente das chaves públicas do Keycloak caso haja renovação de certificados.
  - ✅ **Alta Performance (Zero DB I/O)**: Validação puramente matemática em memória, eliminando a query ao banco de dados em cada request.
  - ✅ **Compatibilidade Nativa com Testes**: Suporte direto do `spring-security-test` (`SecurityMockMvcRequestPostProcessors.jwt()`).
* **Desvantagens**:
  - Requer a adição do starter `spring-boot-starter-oauth2-resource-server` e refatoração da classe `SecurityConfig`.

---

### 🥈 Opção 2 (Alternativa): Validador Híbrido com Suporte Dual (Keycloak RS256 + Fallback HS256)

* **Como Funciona**:
  1. Mantém o `SecurityFilter` existente, mas evolui o `TokenService` para inspecionar o cabeçalho do token (`alg`):
     - Se `alg == "RS256"`: valida a assinatura consultando o endpoint JWKS do Keycloak (utilizando Nimbus JOSE).
     - Se `alg == "HS256"`: mantém a validação legada com `jwt.secret`.
  2. Mantém o endpoint `/auth/login` interno ativo para quem ainda usa autenticação legada.
* **Vantagens**:
  - Permite período de transição onde clientes legados e novos convivem simultaneamente.
* **Desvantagens**:
  - ❌ **Complexidade Adicional**: Manter dois fluxos de validação aumenta a superfície de código e necessidade de testes.
  - ❌ **Risco de Segurança**: Aceitar chaves simétricas e assimétricas no mesmo pipeline requer checagens defensivas estritas para evitar ataques de confusão de algoritmos (CVE-2015-9235 / *alg: none attack*).
  - ❌ **Débito Técnico**: Retarda a migração para a arquitetura centralizada de autenticação.

---

### 🥉 Opção 3 (Minimalista): Validação Manual de Chave Pública Estática (JJWT)

* **Como Funciona**:
  1. Extrai a chave pública RSA do Realm `garage` do Keycloak e a cadastra em uma variável de ambiente (`KEYCLOAK_PUBLIC_KEY`).
  2. Ajusta o método `getSigningKey()` em `TokenService` para construir uma `RSAPublicKey` a partir do formato PEM/DER.
* **Vantagens**:
  - Menor quantidade de linhas de código alteradas.
* **Desvantagens**:
  - ❌ **Quebra perante Rotação de Chaves**: Se o Keycloak rotacionar as chaves criptográficas, a API rejeitará todos os tokens até que a variável de ambiente seja manualmente reconfigurada e o pod reiniciado.
  - ❌ **Anti-pattern**: Desconsidera as capacidades nativas do Spring Security Resource Server.

---

## 3. Detalhamento da Implementação Proposta (Opção 1 Recomendada)

### 3.1 Componentes a Alterar / Criar

#### 1. Módulo `application/pom.xml` [MODIFY]
- Adicionar dependência:
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
  </dependency>
  ```

#### 2. Módulo `application`: `KeycloakJwtAuthenticationConverter.java` [NEW]
- Localização: `br.com.fiap.commons.security.KeycloakJwtAuthenticationConverter`
- Responsabilidade:
  - Implementar `Converter<Jwt, AbstractAuthenticationToken>`.
  - Ler claim `realm_access` (Map) -> `roles` (List<String>).
  - Mapear cada role para `SimpleGrantedAuthority("ROLE_" + role.toUpperCase())`.
  - Definir o principal como `preferred_username` (CPF do usuário) com fallback para `sub`.

#### 3. Módulo `application`: `SecurityConfig.java` [MODIFY]
- Atualizar a configuração:
  - Habilitar `.oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(keycloakJwtAuthenticationConverter)))`.
  - Eliminar injeção e referência de `SecurityFilter`.
  - Definir rotas públicas:
    - `/actuator/**`
    - `/v3/api-docs/**`, `/swagger-ui/**`, `/swagger-ui.html`
    - `/error`
  - Manter `/auth/login` permitido para retrocompatibilidade ou testes legados.

#### 4. Módulo `application`: `application.properties` e `application-local.properties` [MODIFY]
- Adicionar propriedade:
  ```properties
  spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI:http://keycloak.garage.svc.cluster.local:8080/realms/garage/protocol/openid-connect/certs}
  ```

#### 5. Módulo `iandt`: `application-int_test.properties` e Test Configurations [MODIFY]
- Configurar Mock `JwtDecoder` nos testes de integração para geração rápida de tokens sem dependência externa.

---

## 4. Plano de Verificação & TDD (Red-Green-Refactor)

1. **Fase Red (Testes Antes da Implementação)**:
   - Criar `KeycloakJwtAuthenticationConverterTest` em `application/src/test/java/br/com/fiap/commons/security/`:
     - Testar conversão de token com claim `realm_access.roles = ["CUSTOMER"]` -> `ROLE_CUSTOMER`.
     - Testar extração do `preferred_username` como principal.
     - Testar token sem roles (lista vazia defensiva).
   - Executar os testes e confirmar a falha inicial.
2. **Fase Green (Implementação do Código Mínimo)**:
   - Implementar `KeycloakJwtAuthenticationConverter`.
   - Atualizar `SecurityConfig`.
   - Executar os testes e validar que passaram com 100% de sucesso.
3. **Fase Refactor & Suíte Completa**:
   - Executar todos os 69 testes unitários existentes: `mvn test -pl application`.
   - Executar os testes de integração locais: `mvn test -pl iandt`.
   - Executar o teste ponta a ponta na nuvem: `CompleteWorkOrderCreationAwsManualTest`.
4. **Governança Git**:
   - **Nenhum commit ou push será realizado sem autorização explícita do usuário.**
