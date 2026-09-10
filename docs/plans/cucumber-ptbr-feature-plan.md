# Plano de Migração do Gherkin (*.feature) para Português (PT-BR)

## 🎯 Objetivo
Migrar o arquivo de especificação executável BDD `work_order_lifecycle.feature` para Português (PT-BR), permitindo que a especificação atue como documentação viva (Living Documentation) de negócio acessível para os stakeholders, mantendo a implementação técnica em Java estritamente em Inglês.

---

## 📐 Opções de Abordagem

### 🥇 Opção 1 (Recomendada - Best Practice): Gherkin `# language: pt` com Steps Vinculados via `io.cucumber.java.pt.*`
- **Descrição**:
  - No arquivo `.feature`: Incluir o cabeçalho `# language: pt` e traduzir as sentenças e palavras-chave para o padrão oficial do Cucumber em Português (`Funcionalidade`, `Cenário`, `Dado`, `Quando`, `Então`, `E`).
  - No código Java (`WorkOrderLifecycleSteps.java`): Utilizar as anotações nativas de localização do Cucumber (`import io.cucumber.java.pt.*;` com `@Dado`, `@Quando`, `@Entao`, `@E`), mapeando para as frases em português.
  - **Preservação de Padrões Técnicos**: Métodos, variáveis, DTOs, asserts e logs em Java permanecem 100% em **Inglês**, respeitando a Regra de Ouro #6 do especialista-dev.
- **Vantagens**:
  - Padrão oficial recomendado pela comunidade Cucumber e BDD.
  - Alinhamento total com a Linguagem Ubíqua (DDD) do Tech Challenge em português para a camada de especificação de requisitos.
  - Código fonte do teste limpo, sem regex complexas ou gambiarras.
- **Complexidade**: Baixa/Média.

---

### 🥈 Opção 2 (Alternativa): Suporte Bilíngue nos Steps (PT-BR e EN)
- **Descrição**:
  - Traduzir o `.feature` para PT-BR com `# language: pt`.
  - Nos Step Definitions em Java, anotar cada método com ambas as anotações (`@Given("...")` e `@Dado("...")`).
- **Trade-offs**:
  - *Prós*: Permite que cenários futuros sejam escritos tanto em inglês quanto em português.
  - *Contras*: Código dos steps fica mais poluído com anotações duplicadas; maior esforço de manutenção.
- **Complexidade**: Média.

---

### 🥉 Opção 3 (Abordagem Minimalista): Manter Anotações em Inglês e Usar Regex
- **Descrição**:
  - Utilizar `@Given("^que o sistema ...$")` com regex nas anotações em inglês para capturar os passos em português sem importar `io.cucumber.java.pt.*`.
- **Trade-offs**:
  - *Prós*: Não altera os imports de anotações.
  - *Contras*: Menos idiomático, regex desnecessárias para texto fixo, dificulta autocompletar na IDE.
- **Complexidade**: Baixa.

---

## 🛠️ Detalhamento da Opção 1 (Recomendada)

### 1. `e2e/src/test/resources/features/work_order_lifecycle.feature`
```gherkin
# language: pt
Funcionalidade: Ciclo de Vida Completo da Ordem de Serviço
  Como cliente e funcionário da oficina mecânica
  Quero cadastrar veículos, serviços e gerenciar ordens de serviço
  Para que o veículo seja reparado, notificado e liberado com métricas consolidadas

  Cenário: Ciclo de vida completo da ordem de serviço desde a criação até a liberação
    Dado que o sistema da oficina está em execução e operacional
    E um cliente cadastrado com documento e e-mail únicos
    E um veículo cadastrado associado ao cliente
    E um funcionário mecânico cadastrado
    E um material de estoque cadastrado
    E um serviço cadastrado vinculado ao material
    Quando uma nova ordem de serviço é criada para o veículo e mecânico
    Então a ordem de serviço deve ser criada com o status "RECEIVED"
    Quando o mecânico inicia o diagnóstico da ordem de serviço
    Então o status da ordem de serviço deve ser atualizado para "DIAGNOSING"
    Quando o diagnóstico é concluído e aguarda aprovação do cliente
    Então o status da ordem de serviço deve ser atualizado para "WAITING_FOR_APPROVAL"
    E uma notificação deve ser gerada para a ordem de serviço
    Quando o cliente aprova o orçamento e a execução é iniciada
    Então o status da ordem de serviço deve ser atualizado para "EXECUTING"
    Quando o serviço solicitado é marcado como concluído
    E a ordem de serviço é finalizada
    Então o status da ordem de serviço deve ser atualizado para "FINISHED"
    Quando o veículo é liberado para o cliente
    Então o status da ordem de serviço deve ser atualizado para "RELEASED"
    Quando a rotina de tempo médio de execução é acionada
    Então o tempo médio do serviço deve ser calculado e persistido
```

### 2. `WorkOrderLifecycleSteps.java`
- Substituir:
  - `import io.cucumber.java.en.Given;`
  - `import io.cucumber.java.en.When;`
  - `import io.cucumber.java.en.Then;`
  - `import io.cucumber.java.en.And;`
- Por:
  - `import io.cucumber.java.pt.Dado;`
  - `import io.cucumber.java.pt.Quando;`
  - `import io.cucumber.java.pt.Entao;`
  - `import io.cucumber.java.pt.E;`
- Mapear os métodos de step para as sentenças em português correspondentes.

---

## 🧪 Plano de Verificação
1. Execução manual da suíte E2E:
   ```powershell
   mvn test -pl e2e -Pmanual-e2e
   ```
2. Garantir 100% de cenários e passos verdes (passed).
