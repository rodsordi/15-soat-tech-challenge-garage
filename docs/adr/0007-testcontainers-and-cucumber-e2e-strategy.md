# ADR 0007: Estratégia de Testes Automatizados com Testcontainers e Cucumber BDD

* **Status**: Aceito (Accepted)
* **Data**: 2026-09-10
* **Autores**: Tech Challenge Garage Team
* **Decisores Técnicos**: Especialistas em Qualidade de Software (QA/SDET) e Arquitetura

---

## 1. Contexto e Declaração do Problema

A confiabilidade das operações de uma oficina (cálculo de orçamentos, concorrência de estoque e transições de ordens de serviço) exige garantias sólidas de qualidade de software.

Práticas antigas frequentemente introduzem falsos positivos ou falsos negativos:
* **Bancos em memória (ex: H2)**: Ocultam incompatibilidades de dialeto SQL, funções JSONB nativas e comportamentos de transação e concorrência específicos do PostgreSQL.
* **Testes E2E acoplados ao código-fonte**: Dificultam a validação da aplicação exatamente como ela é entregue nos contêineres do Kubernetes.

---

## 2. Decisão Arquitetural

Decidimos estruturar uma **Pirâmide de Testes Automatizados Moderna**, dividida em três níveis complementares:

```
          / \
         /   \      Módulo E2E (Cucumber BDD + REST Assured)
        / E2E \     - Testes Caixa-Preta contra a API em execução
       /-------\
      / Integr. \   Testes de Integração com Testcontainers
     /  (JPA)    \  - PostgreSQL Real efêmero em contêiner Docker
    /-------------\
   /   Unitários   \ Testes Unitários F.I.R.S.T. (JUnit 5 + AssertJ)
  / (Domain/Appl.)  \ - Estrutura aninhada @Nested em 3 níveis (When/Then/Given)
 /-------------------\
```

### 2.1. Princípios Implementados
1. **Testes Unitários F.I.R.S.T.** (Fast, Independent, Repeatable, Self-Validating, Timely):
   - Módulos `domain` e `application` utilizam mocks estritos (Mockito) e checagens com AssertJ.
   - Padrão estruturado de testes em 3 níveis aninhados:
     - `@DisplayName("When [ação]")` -> `@DisplayName("Then [resultado]")` -> `@DisplayName("Given [cenário]")`.
2. **Testes de Integração com Testcontainers**:
   - Valida repositórios Spring Data JPA contra uma instância real de PostgreSQL inicializada dinamicamente em contêiner Docker.
3. **Módulo `e2e` com BDD / Cucumber**:
   - Testes comportamentais caixa-preta escritos em Gherkin com especificação viva em Português (`Dado`, `Quando`, `Então`).
   - Comunicação estritamente via protocolo HTTP com REST Assured.

---

## 3. Consequências

### Positivas (Benefícios):
* **Fidelidade de Produção**: 100% de confiança de que queries, índices e constraints relacionais funcionam identicamente no PostgreSQL do RDS e nos testes.
* **Documentação Viva e Rastreabilidade**: Histórias de usuário e critérios de aceite expressos de forma clara nos arquivos `.feature`.
* **Detecção Precoce de Regressões**: Pipeline de CI executa a suíte e garante que nenhuma alteração quebre contratos de API ou regras de domínio.

### Negativas / Trade-offs:
* Testcontainers exige que o ambiente de execução (máquina local ou GitHub Actions runner) tenha o daemon do Docker disponível.

---

## 4. Referências
* **Testcontainers for Java**: https://testcontainers.com
* **Cucumber BDD**: https://cucumber.io
* **Martin Fowler**: The Practical Test Pyramid
