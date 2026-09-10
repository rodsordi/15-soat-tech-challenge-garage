# Plano de Arquitetura de Configuração: Discriminação de Ambientes Local e PRD no `resources`

> **Status**: Proposta para Revisão e Aprovação  
> **Data**: 10/09/2026  
> **Especialista**: Engenheiro de Software Lead / Arquiteto de Soluções  
> **Módulo Alvo**: `application/src/main/resources`  

---

## 1. Diagnóstico do Cenário Atual

Atualmente, o módulo `application/src/main/resources` possui arquivos de propriedades com sobreposições e configurações que impactam o comportamento da aplicação em diferentes ambientes:

1. **Vazamento de Configurações de Nuvem no Perfil Base (`application.properties`)**:
   - Parâmetros do New Relic / OpenTelemetry (`management.otlp.*` e `management.opentelemetry.*`) estão definidos no arquivo base. Quando o contêiner sobe no Docker Compose local sem a chave `NEWRELIC_LICENSE_KEY`, o Spring Boot inicializa o `OtlpMeterRegistry` e tenta conectar na URL pública da New Relic a cada 60 segundos, poluindo os logs com advertências de autenticação.
   - A URL do Keycloak (`keycloak.garage.svc.cluster.local`) de Kubernetes está definida como fallback no arquivo base, gerando ambiguidade com o ambiente local.
2. **Separação de Responsabilidades (12-Factor App - Fator III)**:
   - Configurações comuns (estrutura de pacotes, rotas de actuator, flyway base) devem residir no arquivo base `application.properties`.
   - Configurações efêmeras/de desenvolvimento (Floci/LocalStack, logs em `DEBUG`, `show-sql=true`, banco local Docker) pertencem exclusivamente ao `application-local.properties`.
   - Configurações corporativas/produtivas (Amazon RDS, pooling de conexões Hikari dimensionado, métricas OTLP via New Relic com Secrets do EKS, IAM IRSA, logs ECS em `INFO`) devem pertencer ao `application-prd.properties`.

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Segregação Estrita em 3 Camadas (`application.properties`, `application-local.properties`, `application-prd.properties`) com Fail-Fast e 12-Factor App

* **Motivação Técnica**:
  Esta é a convenção padrão e mais robusta do Spring Boot 3.x para aplicações corporativas em contêineres e Kubernetes.
  - `application.properties`: Contém estritamente propriedades estruturais e neutras a ambiente (nome da aplicação, context-path `/api`, configurações do Jackson, Flyway baseline/schemas, rotas base do Actuator).
  - `application-local.properties` (`spring.config.activate.on-profile=local`):
    - Conecta no PostgreSQL local (`jdbc:postgresql://localhost:5432/postgres`) com credenciais padrão de dev (`postgres`/`postgres`).
    - Desativa o exportador OTLP de métricas (`management.otlp.metrics.export.enabled=false`) para eliminar logs desnecessários de erro New Relic localmente.
    - Habilita `spring.jpa.show-sql=true` e formatação SQL para máxima produtividade do desenvolvedor.
    - Aponta SQS/SNS para o Floci/LocalStack (`http://localhost:4566`).
    - Habilita logging em `DEBUG` nos pacotes do projeto `br.com.fiap`.
  - `application-prd.properties` (`spring.config.activate.on-profile=prd,prod`):
    - **DevSecOps Zero-Trust**: Nenhuma senha, token ou URL de produção com fallback em texto plano. Exige injeção estrita via Kubernetes Secrets / Environment Variables (`${SPRING_DATASOURCE_URL}`, `${SPRING_DATASOURCE_PASSWORD}`, `${NEWRELIC_LICENSE_KEY}`). Se a variável não for fornecida, a aplicação aplica **Fail-Fast** na inicialização.
    - **Pool HikariCP Otimizado**: Dimensionamento para carga de produção (`maximum-pool-size=20`, `minimum-idle=5`, timeouts de leak e conexão).
    - **Observabilidade Total**: OpenTelemetry OTLP ativo apontando para o New Relic com logs em formato ECS JSON em nível `INFO`.
    - **JPA Otimizado**: `show-sql=false`, `format_sql=false` e `ddl-auto=validate`.

* **Vantagens**:
  - ✅ **Segurança**: Conformidade total com DevSecOps e 12-Factor App.
  - ✅ **Isolamento**: Desenvolvedor roda `docker compose up` sem depender de chaves da AWS ou New Relic.
  - ✅ **Performance**: Produção opera com pool de conexões e logging otimizados para alta volumetria.
* **Trade-offs**: Requer que o manifesto do Kubernetes passe a variável `SPRING_PROFILES_ACTIVE=prd`.

---

### 🥈 Opção 2 (Alternativa): Migração Completa para YAML Hierárquico (`application.yml`, `application-local.yml`, `application-prd.yml`)

* **Motivação Técnica**:
  Migrar a sintaxe de `.properties` para `.yml`, reduzindo a repetição de prefixos longos (como `management.opentelemetry.tracing.export.otlp.*` e `spring.datasource.*`).
* **Vantagens**:
  - ✅ Sintaxe limpa, estruturada em árvore e mais compacta.
* **Trade-offs**:
  - ⚠️ Quebra a homogeneidade com outros projetos do repositório `15-soat-tech-challenge` que utilizam `.properties`.
  - ⚠️ Maior risco de erros de indentação (espaços em branco) em alterações manuais.

---

### 🥉 Opção 3 (Minimalista): Arquivo Único Multi-Documento com Separadores `#---`

* **Motivação Técnica**:
  Consolidar tudo em um único arquivo `application.properties` utilizando a especificação do Spring Boot 2.4+ com blocos `#---` e `spring.config.activate.on-profile`.
* **Vantagens**:
  - ✅ Menor número de arquivos no diretório `resources`.
* **Trade-offs**:
  - ⚠️ Arquivo extenso (>150 linhas) com mistura de regras locais e de produção no mesmo arquivo.
  - ⚠️ Risco de desenvolvedores alterarem acidentalmente configurações de produção ao ajustar configurações locais.

---

## 3. Estrutura e Conteúdo Detalhado Proposto (Opção 1)

### 3.1. `application.properties` (Configurações Base / Agnósticas)
```properties
# ===================================================================
# BASE APPLICATION CONFIGURATION (COMMON TO ALL PROFILES)
# ===================================================================

# Application Information
server.servlet.context-path=/api
spring.application.name=@project.parent.artifactId@

info.app.title=@project.parent.name@
info.app.name=@project.parent.artifactId@
info.app.version=@project.parent.version@
info.app.description=@project.parent.description@

# Actuator Endpoints
management.info.env.enabled=true
management.endpoints.web.exposure.include=health,info,metrics,prometheus
management.endpoints.web.base-path=/actuator
management.server.base-path=/

# Web & Error Handling
spring.mvc.problemdetails.enabled=true
spring.jackson.mapper.sort_properties_alphabetically=false

# Flyway Migrations (Schema-First)
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.createSchemas=true
spring.flyway.locations=classpath:/db/migrations
spring.flyway.schemas=garage

# JPA / Hibernate Base Defaults
spring.datasource.driverClassName=org.postgresql.Driver
spring.jpa.properties.hibernate.default_schema=garage
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
spring.jpa.properties.hibernate.cache.use_query_cache=false
spring.jpa.properties.hibernate.cache.use_second_level_cache=false

# Email Template Defaults
email.garage-management-email-recipient=${GARAGE_MGMT_EMAIL:management@garage.com}
email.estimate-customer-approval-email-subject=${ESTIMATE_SUBJECT:Estimate Approval Request}
email.body-template-file-name=/estimate-customer-approval-email-message.html

# AWS Messaging Identifiers
message.notification-creation.topic=api-garage_notification-creation_topic
message.notification-creation.queue=api-garage_notification-creation_queue
```

---

### 3.2. `application-local.properties` (Perfil `local`)
```properties
# ===================================================================
# LOCAL DEVELOPMENT PROFILE (local)
# ===================================================================
spring.config.activate.on-profile=local

# Datasource (Docker Compose PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.sql.init.mode=always

# JPA & Hibernate Debug
spring.jpa.show-sql=true
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.format_sql=true

# AWS Local (Floci / LocalStack)
spring.cloud.aws.region.static=sa-east-1
spring.cloud.aws.credentials.access-key=test
spring.cloud.aws.credentials.secret-key=test
spring.cloud.aws.endpoint=http://localhost:4566
spring.cloud.aws.sqs.enabled=true
spring.cloud.aws.sqs.endpoint=http://localhost:4566
spring.cloud.aws.sqs.account=000000000000
spring.cloud.aws.sqs.queue-not-found-strategy=CREATE
spring.cloud.aws.sns.enabled=true
spring.cloud.aws.sns.endpoint=http://localhost:4566
spring.cloud.aws.sns.account=000000000000

# Security (Keycloak Local / Token Decoder Local)
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI:http://localhost:8080/realms/garage/protocol/openid-connect/certs}

# Logging (Verbose para Desenvolvimento)
logging.structured.format.console=
logging.level.root=INFO
logging.level.br.com.fiap=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.validation=DEBUG

# Observability (Desativa exportação externa no ambiente local para evitar 401/403)
management.tracing.enabled=false
management.otlp.metrics.export.enabled=false
management.otlp.tracing.endpoint=
management.otlp.metrics.export.url=

# Web
web.garage-web-page-url=http://localhost:8080/garage
```

---

### 3.3. `application-prd.properties` (Perfil `prd` / `prod`)
```properties
# ===================================================================
# PRODUCTION PROFILE (prd)
# ===================================================================
spring.config.activate.on-profile=prd,prod

# Datasource (AWS RDS PostgreSQL - 12-Factor Fail-Fast)
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.sql.init.mode=never

# Hikari Connection Pool Sizing
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA & Hibernate Production Settings
spring.jpa.show-sql=false
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.format_sql=false

# Security & Keycloak OIDC via EKS
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI}

# AWS Cloud Messaging (IAM Roles for Service Accounts - IRSA)
spring.cloud.aws.region.static=${AWS_REGION:us-east-1}
spring.cloud.aws.sqs.enabled=true

# Observability & Tracing (New Relic / OpenTelemetry)
management.tracing.enabled=true
management.tracing.sampling.probability=${MANAGEMENT_TRACING_SAMPLING_PROBABILITY:1.0}
management.otlp.tracing.endpoint=${MANAGEMENT_OTLP_TRACING_ENDPOINT:https://otlp.nr-data.net:4318/v1/traces}
management.otlp.tracing.headers.api-key=${NEWRELIC_LICENSE_KEY}
management.otlp.metrics.export.enabled=true
management.otlp.metrics.export.url=${MANAGEMENT_OTLP_METRICS_EXPORT_URL:https://otlp.nr-data.net:4318/v1/metrics}
management.otlp.metrics.export.headers.api-key=${NEWRELIC_LICENSE_KEY}

# Structured Logging (ECS JSON para CloudWatch / Log Ingestion)
logging.structured.format.console=ecs
logging.include-application-name=true
logging.level.root=INFO
logging.level.br.com.fiap=INFO
logging.level.org.springframework.WARN=WARN
logging.level.org.hibernate=WARN

# Web
web.garage-web-page-url=${GARAGE_WEB_URL}
```

---

## 4. Plano de Verificação e Testes

1. **Build & Compilação**:
   - `mvn clean package -pl domain,application -DskipTests` para garantir que os arquivos são empacotados corretamente no jar.
2. **Execução de Testes Unitários e de Integração**:
   - `mvn test -pl application,domain`
3. **Execução de Testes E2E contra Contêiner Local**:
   - `mvn test -pl e2e -Pmanual-e2e` para assegurar que a execução local (`profile=local`) continua 100% verde sem nenhum impacto.
4. **Verificação dos Logs do Contêiner Local**:
   - Confirmar a ausência de mensagens de erro ou advertência de conexão OTLP com o New Relic (`https://otlp.nr-data.net:4318/v1/metrics`).

---

## 5. Próximos Passos (Aguardando Aprovação)

Conforme a **Regra de Ouro #1 do Especialista**, nenhuma alteração de código ou arquivos de configuração foi executada ainda. Aguardamos sua confirmação sobre qual das opções seguir (recomendamos a **Opção 1**).
