# Plano de Configuração & Arquitetura: `application-prd.properties` e `application-local.properties`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-10  
> **Autor**: Engenheiro Especialista em Desenvolvimento & Arquitetura de Software  
> **Repositório Alvo**: `15-soat-tech-challenge-garage`  

---

## 1. Contexto e Motivação

O projeto `api-garage` utiliza o Spring Boot com suporte a perfis de execução (`Spring Profiles`). Para garantir a separação de responsabilidades entre o ambiente de desenvolvimento local e o ambiente de produção na nuvem AWS, é necessário estruturar e padronizar os arquivos de propriedades no diretório `application/src/main/resources/`:
* **`application.properties`**: Configurações padrão e genéricas compartilhadas entre todos os perfis.
* **`application-local.properties`**: Perfil ativado localmente (`local`), voltado para o desenvolvedor e execução com Docker Compose (PostgreSQL local, Floci/LocalStack para SQS/SNS, logs detalhados).
* **`application-prd.properties`**: Perfil ativado em produção (`prd` / `prod`), voltado para execução no Kubernetes (AWS EKS), banco gerenciado Amazon RDS, mensageria nativa AWS, observabilidade com New Relic (OpenTelemetry OTLP) e conformidade estrita com as diretrizes de DevSecOps (zero segredos em código-fonte).

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): Perfis Explícitos com Injeção de Variáveis de Ambiente e DevSecOps Rigoroso

* **Motivação Técnica**:
  Alinhada com o manifesto *The Twelve-Factor App* (fator III: Configurações armazenadas no ambiente) e com as diretrizes mandatárias de DevSecOps do projeto. O arquivo `application-prd.properties` não contém nenhum valor sensível gravado em texto plano; todas as senhas e chaves são referenciadas via variáveis de ambiente (`${SPRING_DATASOURCE_PASSWORD}`, `${NEWRELIC_LICENSE_KEY}`, etc.), permitindo que a infraestrutura (Kubernetes Secrets / Terraform) controle os valores dinamicamente.
* **Estrutura Proposta**:
  1. **`application-prd.properties`**:
     - **Database**: `spring.datasource.url=${SPRING_DATASOURCE_URL}`, `username=${SPRING_DATASOURCE_USERNAME}`, `password=${SPRING_DATASOURCE_PASSWORD}`.
     - **Pool HikariCP**: Otimizado para produção (`maximum-pool-size=20`, `minimum-idle=5`, `connection-timeout=30000`).
     - **Hibernate**: `ddl-auto=validate`, `show-sql=false`, `format_sql=false` para máxima performance.
     - **Security/JWT**: `spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI}`.
     - **AWS**: Região `us-east-1`, credenciais via IRSA (sem access-keys mockadas), sem endpoint estático (usa a AWS real).
     - **Observabilidade**: OpenTelemetry/OTLP ativo para o New Relic (`https://otlp.nr-data.net:4318`), logs estruturados ECS em JSON e nível `INFO`.
  2. **`application-local.properties`**:
     - **Database**: PostgreSQL local (`jdbc:postgresql://localhost:5432/postgres`, usuário `postgres`, senha `postgres`).
     - **AWS Local**: Floci / LocalStack em `http://localhost:4566`.
     - **Hibernate**: `show-sql=true`, `format_sql=true` para facilitar debug de consultas.
     - **Logs**: Nível `DEBUG` para `br.com.fiap` e `org.springframework.web`.
     - **APM**: Desativar exportador de métricas para evitar logs de erro (401/403) quando não houver chave New Relic configurada na máquina do desenvolvedor.
* **Vantagens**:
  - ✅ **Segurança Máxima**: Nenhuma credencial exposta no repositório Git.
  - ✅ **Alta Performance em Produção**: Pool de conexões ajustado, sem logs excessivos de SQL.
  - ✅ **Excelente Experiência do Desenvolvedor (DX)**: Subida local imediata via Docker Compose sem necessidade de configurar variáveis externas complexas.

---

### 🥈 Opção 2 (Alternativa): Perfis com Valores Padrão (Fallbacks) em Todas as Propriedades

* **Motivação Técnica**:
  Fornecer valores padrão (*fallback*) para todas as variáveis mesmo em produção (ex: `${SPRING_DATASOURCE_URL:jdbc:postgresql://...}`).
* **Trade-offs**:
  - ⚠️ **Risco de Segurança**: Pode mascarar falhas de configuração em produção ou tentar conectar a endpoints inexistentes se uma variável de ambiente for esquecida na pipeline.

---

### 🥉 Opção 3 (Minimalista): Arquivo Único `application-prd.properties` com Conteúdo Enxuto

* **Motivação Técnica**:
  Apenas criar o arquivo `application-prd.properties` com as 5 propriedades fundamentais de banco e AWS, mantendo o `application-local.properties` inalterado.
* **Trade-offs**:
  - ⚠️ Mantém o erro de métricas 401/403 nos logs do contêiner local.
  - ⚠️ Não otimiza o pool de conexões nem a observabilidade para o cluster EKS.

---

## 3. Conteúdo Detalhado dos Arquivos (Opção 1 Recomendada)

### `application-prd.properties`
```properties
# ---------------------------------------------------------
# PRODUCTION PROFILE (prd)
# ---------------------------------------------------------

# Spring Profile
spring.config.activate.on-profile=prd,prod

# Datasource & Pool (AWS RDS PostgreSQL)
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=${SPRING_DATASOURCE_URL}
spring.datasource.username=${SPRING_DATASOURCE_USERNAME}
spring.datasource.password=${SPRING_DATASOURCE_PASSWORD}
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000

# JPA & Hibernate
spring.jpa.show-sql=false
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.default_schema=garage

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.createSchemas=true
spring.flyway.schemas=garage
spring.flyway.locations=classpath:/db/migrations

# Security (Keycloak OAuth2 Resource Server via JWKS)
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI}

# AWS Messaging (SNS & SQS via EKS IRSA)
spring.cloud.aws.region.static=${AWS_REGION:us-east-1}
spring.cloud.aws.sqs.enabled=true
message.notification-creation.topic=${NOTIFICATION_TOPIC:api-garage_notification-creation_topic}
message.notification-creation.queue=${NOTIFICATION_QUEUE:api-garage_notification-creation_queue}

# Observability, APM & OpenTelemetry (New Relic)
management.tracing.enabled=true
management.tracing.sampling.probability=${MANAGEMENT_TRACING_SAMPLING_PROBABILITY:1.0}
management.otlp.tracing.endpoint=${MANAGEMENT_OTLP_TRACING_ENDPOINT:https://otlp.nr-data.net:4318/v1/traces}
management.otlp.tracing.headers.api-key=${NEWRELIC_LICENSE_KEY}
management.otlp.metrics.export.url=${MANAGEMENT_OTLP_METRICS_EXPORT_URL:https://otlp.nr-data.net:4318/v1/metrics}
management.otlp.metrics.export.headers.api-key=${NEWRELIC_LICENSE_KEY}

# Logging
logging.structured.format.console=ecs
logging.include-application-name=true
logging.level.root=INFO
logging.level.br.com.fiap=INFO
logging.level.org.springframework=WARN
logging.level.org.hibernate=WARN
```

### `application-local.properties`
```properties
# ---------------------------------------------------------
# LOCAL DEVELOPMENT PROFILE (local)
# ---------------------------------------------------------

# Spring Profile
spring.config.activate.on-profile=local

# AWS Local (Floci / LocalStack)
spring.cloud.aws.region.static=sa-east-1
spring.cloud.aws.credentials.access-key=test
spring.cloud.aws.credentials.secret-key=test
spring.cloud.aws.sqs.enabled=true
spring.cloud.aws.sqs.endpoint=http://localhost:4566
spring.cloud.aws.sqs.account=000000000000
spring.cloud.aws.sqs.queue-not-found-strategy=CREATE

# Datasource (Docker Compose PostgreSQL)
spring.datasource.driverClassName=org.postgresql.Driver
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.sql.init.mode=always

# JPA & Hibernate
spring.jpa.show-sql=true
spring.jpa.generate-ddl=false
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.default_schema=garage
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.createSchemas=true
spring.flyway.schemas=garage
spring.flyway.locations=classpath:/db/migrations

# Security (Keycloak Local / Test)
spring.security.oauth2.resourceserver.jwt.jwk-set-uri=${KEYCLOAK_JWK_SET_URI:http://localhost:8080/realms/garage/protocol/openid-connect/certs}

# Logging (Verbose para Desenvolvimento)
logging.level.br.com.fiap=DEBUG
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.validation=DEBUG

# Messaging
message.notification-creation.topic=api-garage_notification-creation_topic
message.notification-creation.queue=api-garage_notification-creation_queue

# Email & Web Defaults
email.garage-management-email-recipient=management@garage.com
email.estimate-customer-approval-email-subject=Estimate Approval Request
email.body-template-file-name=/estimate-customer-approval-email-message.html
web.garage-web-page-url=http://localhost:8080/garage
```
