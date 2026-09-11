# Catálogo de RFCs (Requests for Comments)

As **RFCs (Requests for Comments)** registram propostas técnicas formais de grandes mudanças, novos subsistemas e evoluções de arquitetura que estão em fase de concepção, debate e avaliação pela equipe antes de serem implementadas.

---

## 📑 Índice de Propostas Arquiteturais

| ID | Título | Status | Área Técnica |
| :--- | :--- | :---: | :--- |
| **[RFC 0001](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/rfc/0001-event-driven-architecture-sqs-sns.md)** | Arquitetura Orientada a Eventos (EDA) com AWS SQS/SNS para Ordens de Serviço e Notificações | `Em Revisão` | Microsserviços & Resiliência |
| **[RFC 0002](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/rfc/0002-transactional-outbox-pattern.md)** | Padrão Outbox Transacional (Transactional Outbox) para Garantia de Entrega de Eventos | `Proposta` | Consistência Distribuída |
| **[RFC 0003](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/rfc/0003-payment-gateway-pix-integration.md)** | Integração com Gateway de Pagamentos e Pix Dinâmico com Webhooks Idempotentes | `Proposta` | Negócio & Pagamentos |
| **[RFC 0004](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/rfc/0004-distributed-cache-redis.md)** | Estratégia de Cache Distribuído com Redis para Catálogos e Sessões OIDC | `Proposta` | Performance & Escalabilidade |
| **[RFC 0005](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/rfc/0005-graalvm-native-image-compilation.md)** | Compilação Nativa com GraalVM Native Image (Spring Boot 4 AOT) | `Proposta` | Cloud-Native & Otimização |

---

## 🔄 Fluxo de Ciclo de Vida de uma RFC
```
[ Proposta (Draft) ] ──> [ Em Revisão (In Review) ] ──> [ Aprovada (Approved) ] ──> [ Transforma-se em ADR + Código ]
                                     │
                                     └──> [ Rejeitada / Adiada (Rejected) ]
```
