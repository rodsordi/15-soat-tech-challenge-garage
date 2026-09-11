# Plano de Implementação: Provisionamento de SNS/SQS no Terraform, Integração na Aplicação e Diagramação

Este documento detalha o planejamento arquitetural e técnico para provisionar os recursos gerenciados de mensageria da AWS (**Amazon SNS** e **Amazon SQS**) via Terraform, habilitar o consumo e publicação assíncrona na aplicação Spring Boot (**`api-garage`**) com alta resiliência e tratamento para o ambiente restrito do **AWS Academy**, e atualizar a documentação arquitetural no `README.md` com diagramas de componentes e de sequência em Mermaid.

## User Review Required

> [!IMPORTANT]
> **Aprovação Prévia Requerida**: Conforme as diretrizes do especialista-dev, a implementação só terá início após sua validação e aprovação do plano abaixo.
> 
> **Ambiente AWS Academy**: No AWS Academy Lab, a ServiceAccount não suporta criação de novas roles de IAM para IRSA. A abordagem recomendada (Opção 1) emprega o provisionamento padronizado de SNS Topic + SQS Queue + DLQ + Queue Policy no Terraform do Kubernetes, aliado à **resiliência por contingência na aplicação Java** (`Optional<SnsTemplate>` com `try/catch` defensivo). Dessa forma, a aplicação tenta publicar no SNS e registrar os logs de telemetria, mas **nunca causa HTTP 500** na transação da ordem de serviço caso as credenciais temporárias do laboratório expirem.

---

## 1. Análise de Opções e Melhores Práticas (Ordenadas por Prioridade)

### 🥇 Opção 1 (Recomendada - Best Practice): Módulo Dedicado `modules/messaging` no `15-soat-tech-challenge-iac-k8s` com Injeção Declarativa e Resiliência na Aplicação
* **Motivação Técnica**:
  - **Single Point of Deployment**: O repositório `15-soat-tech-challenge-iac-k8s` gerencia tanto o cluster EKS quanto o deployment Kubernetes da aplicação (`app-garage`).
  - **Injeção Transparente**: O Terraform cria o tópico SNS (`api-garage_notification-creation_topic`), a fila principal SQS (`api-garage_notification-creation_queue`), a fila de mensagens mortas (DLQ - `api-garage_notification-creation_queue_dlq`), a subscrição SNS->SQS e a política `aws_sqs_queue_policy`, conectando os outputs diretamente nas variáveis de ambiente do pod.
  - **Resiliência e Desacoplamento na Aplicação**: No código Java (`NotifyCustomerForApprovalPublisherImpl`), a dependência do `SnsTemplate` é tratada com `Optional<SnsTemplate>` e o envio é protegido por um bloco defensivo com logs estruturados. Se o broker estiver inalcançável ou as credenciais temporárias do AWS Academy expirarem, a operação transacional de diagnóstico da ordem de serviço não é abortada com HTTP 500.
  - **Clareza Arquitetural**: O `README.md` é enriquecido com o desenho de componentes C4 e o diagrama de sequência detalhando o desacoplamento pub/sub do ciclo de vida da OS.
* **Complexidade**: Baixa/Média.
* **Trade-offs**: Centraliza o provisionamento de mensageria junto ao cluster e à aplicação que a consome.

---

### 🥈 Opção 2 (Alternativa): Repositório Isolado de Mensageria (`15-soat-tech-challenge-iac-messaging`)
* **Motivação Técnica**:
  - Separar estritamente recursos gerenciados da AWS de recursos de computação do Kubernetes.
* **Desvantagens / Trade-offs**:
  - Exige criar um 4º repositório Git, um novo pipeline no GitHub Actions e sincronizar estados via `terraform_remote_state` no S3 apenas para 1 tópico e 1 fila.
  - Alto overhead de manutenção para o escopo do Tech Challenge.

---

### 🥉 Opção 3 (Minimalista): Adição Direta dentro de `modules/app-garage` no `iac-k8s`
* **Motivação Técnica**:
  - Colocar os recursos `aws_sns_topic` e `aws_sqs_queue` dentro do arquivo `modules/app-garage/main.tf`.
* **Desvantagens / Trade-offs**:
  - Viola o Princípio da Responsabilidade Única (SRP), misturando recursos de infraestrutura AWS (SNS/SQS) com manifests puros do Kubernetes (Deployment, Service, HPA).
  - Prejudica a manutenibilidade do módulo de aplicação.

---

## 2. Proposed Changes

### Componente 1: Infraestrutura Terraform (`15-soat-tech-challenge-iac-k8s`)

#### [NEW] [main.tf](file:///C:/git/fiap/15-soat-tech-challenge-iac-k8s/modules/messaging/main.tf)
- Provisionar:
  - `aws_sns_topic.notification_creation_topic` (`api-garage_notification-creation_topic`)
  - `aws_sqs_queue.notification_creation_dlq` (`api-garage_notification-creation_queue_dlq`) com retenção estendida (14 dias)
  - `aws_sqs_queue.notification_creation_queue` (`api-garage_notification-creation_queue`) com `redrive_policy` (maxReceiveCount = 3)
  - `aws_sns_topic_subscription.notification_sqs_sub` conectando SNS -> SQS
  - `aws_sqs_queue_policy.notification_queue_policy` permitindo ação `sqs:SendMessage` pelo SNS

#### [NEW] [variables.tf](file:///C:/git/fiap/15-soat-tech-challenge-iac-k8s/modules/messaging/variables.tf)
- Declarar variáveis customizáveis para nomes de tópicos e filas.

#### [NEW] [outputs.tf](file:///C:/git/fiap/15-soat-tech-challenge-iac-k8s/modules/messaging/outputs.tf)
- Exportar: `topic_arn`, `topic_name`, `queue_arn`, `queue_name`, `queue_url`.

#### [MODIFY] [main.tf](file:///C:/git/fiap/15-soat-tech-challenge-iac-k8s/main.tf)
- Instanciar `module "messaging"` e repassar os outputs para `module.app_garage`.

#### [MODIFY] [variables.tf](file:///C:/git/fiap/15-soat-tech-challenge-iac-k8s/modules/app-garage/variables.tf)
- Adicionar variáveis: `sns_enabled`, `sqs_enabled`, `notification_topic`, `notification_queue`.

#### [MODIFY] [main.tf](file:///C:/git/fiap/15-soat-tech-challenge-iac-k8s/modules/app-garage/main.tf)
- Injetar no container `api-garage` as variáveis de ambiente:
  - `SNS_ENABLED`
  - `SQS_ENABLED`
  - `NOTIFICATION_TOPIC`
  - `NOTIFICATION_QUEUE`

---

### Componente 2: Aplicação Backend Java (`15-soat-tech-challenge-garage`)

#### [MODIFY] [NotifyCustomerForApprovalPublisherImpl.java](file:///C:/git/fiap/15-soat-tech-challenge-garage/application/src/main/java/br/com/fiap/garage/application/adapter/publisher/NotifyCustomerForApprovalPublisherImpl.java)
- Injetar `Optional<SnsTemplate> snsTemplate`.
- Envolver publicação em bloco `try/catch` com log estruturado (`Slf4j`), evitando propagar `SdkClientException` para o fluxo de negócio da ordem de serviço.

#### [MODIFY] [NotifyCustomerForApprovalPublisherImplTest.java](file:///C:/git/fiap/15-soat-tech-challenge-garage/application/src/test/java/br/com/fiap/garage/application/adapter/publisher/NotifyCustomerForApprovalPublisherImplTest.java)
- Atualizar e expandir os testes unitários (TDD):
  - Sucesso com SNS ativo.
  - Resiliência quando SNS não estiver presente (`Optional.empty()`).
  - Resiliência quando o envio do SNS lançar exceção.

#### [MODIFY] [application.properties](file:///C:/git/fiap/15-soat-tech-challenge-garage/application/src/main/resources/application.properties)
- Adicionar `spring.cloud.aws.sns.enabled=${SNS_ENABLED:true}` alinhado ao SQS.

#### [MODIFY] [application-prd.properties](file:///C:/git/fiap/15-soat-tech-challenge-garage/application/src/main/resources/application-prd.properties)
- Adicionar `spring.cloud.aws.sns.enabled=${SNS_ENABLED:true}`.

---

### Componente 3: Documentação Arquitetural (`README.md`)

#### [MODIFY] [README.md](file:///C:/git/fiap/15-soat-tech-challenge-garage/README.md)
- Atualizar o **Diagrama de Componentes C4 / Container (Mermaid)** para incluir:
  - `AWS SNS (api-garage_notification-creation_topic)`
  - `AWS SQS (api-garage_notification-creation_queue)`
  - `AWS SQS DLQ (api-garage_notification-creation_queue_dlq)`
  - Conexões de envio (`api-garage` -> SNS) e consumo (SQS -> `api-garage NotificationListener`).
- Adicionar **Diagrama de Sequência (Mermaid)** dedicado ao fluxo de notificação assíncrona no ciclo de vida da OS:
  - Mecânico conclui diagnóstico (`PATCH /v1/orders/{id}/diagnosis`) -> status vira `WAITING_FOR_APPROVAL` -> publica evento no SNS -> SNS entrega na fila SQS -> Listener consome e processa.

---

## 3. Verification Plan

### Automated Tests
- **Unit Tests**:
  ```powershell
  mvn test -pl application -Dtest=NotifyCustomerForApprovalPublisherImplTest
  ```
- **Full Project Build**:
  ```powershell
  mvn clean package -DskipTests
  ```
- **Terraform Validation**:
  ```powershell
  cd C:\git\fiap\15-soat-tech-challenge-iac-k8s
  terraform fmt -check
  terraform validate
  ```

### Manual / E2E Verification
- Executar teste E2E Cucumber com timeout estrito contra o ambiente da AWS:
  ```powershell
  mvn test -pl e2e -Pmanual-e2e -Dtest=RunCucumberTest "-Denv=prd" "-Dgarage.base-uri=https://8sggxeps4j.execute-api.us-east-1.amazonaws.com/api" "-Dgarage.auth.token=Bearer <JWT>"
  ```
