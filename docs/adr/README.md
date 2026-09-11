# Catálogo de ADRs (Architecture Decision Records)

Este repositório adota o padrão **Architecture Decision Records (ADR)** para documentar decisões técnicas estruturantes, seus contextos de negócio, trade-offs e alternativas avaliadas.

Os ADRs são mantidos como **Docs-as-Code**, evoluindo de forma versionada junto à base de código.

---

## 📑 Índice de Decisões Arquiteturais

| ID | Título | Status | Data | Categoria |
| :--- | :--- | :---: | :---: | :--- |
| **[ADR 0001](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0001-relational-database-choice-and-er-model.md)** | Justificativa Formal da Escolha do Banco de Dados Relacional (PostgreSQL) e Modelo de Entidade-Relacionamento (ER) | `Aceito` | 2026-09-10 | Engenharia de Dados & Persistência |
| **[ADR 0002](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0002-centralized-onboarding-saga-orchestration.md)** | Orquestração Centralizada no Backend com Compensação Saga para Onboarding de Usuários | `Aceito` | 2026-09-10 | Resiliência & Microsserviços |
| **[ADR 0003](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0003-unified-identity-keycloak-primary-key.md)** | Padrão de Identidade Unificada (Keycloak UUID como Chave Primária Relacional) | `Aceito` | 2026-09-10 | Identidade & Dados |
| **[ADR 0004](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0004-stateless-jwt-validation-rs256-jwks.md)** | Validação Stateless de Tokens JWT com Assinatura Assimétrica RS256 e JWKS | `Aceito` | 2026-09-10 | Segurança & IAM |
| **[ADR 0005](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0005-multi-module-clean-architecture-domain-isolation.md)** | Isolamento de Domínio e Clean Architecture em Estrutura Multi-Módulos Maven | `Aceito` | 2026-09-10 | Arquitetura de Software |
| **[ADR 0006](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0006-algorithmic-document-validation-edge.md)** | Validação Algorítmica de Documentos Brasileiros (Módulo 11) no Perímetro (Edge) | `Aceito` | 2026-09-10 | Segurança & Performance |
| **[ADR 0007](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0007-testcontainers-and-cucumber-e2e-strategy.md)** | Estratégia de Testes Automatizados com Testcontainers e Cucumber BDD | `Aceito` | 2026-09-10 | Engenharia de Testes |
| **[ADR 0008](file:///c:/git/fiap/15-soat-tech-challenge-garage/docs/adr/0008-decoupled-iac-terraform-lifecycle.md)** | Segregação e Ciclo de Vida Desacoplado de Infraestrutura como Código (IaC) | `Aceito` | 2026-09-10 | Cloud & DevOps |

---

## 🛠️ Padrão Utilizado
* Formato baseado no **MADR (Markdown Architectural Decision Records)** e especificações de Michael Nygard.
