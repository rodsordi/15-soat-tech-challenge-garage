# Plano de Implementação: Logs em Texto Padrão (Não-JSON) nos Módulos `e2e` e `iandt`

Este documento estabelece a solução técnica para desativar o formato de logs JSON (Structured Logging / ECS) e appenders de observabilidade (OpenTelemetry / OTLP) exclusivamente nos módulos de testes automatizados `e2e` e `iandt`, garantindo saída em texto legível e colorizado no console sem interferir na configuração de produção.

---

## 🎯 Contexto e Motivação

Atualmente, o módulo `application` possui em `application/src/main/resources/`:
1. `logback-spring.xml` que inclui `structured-console-appender.xml` e appender `OpenTelemetryAppender` (OTLP);
2. `application.properties` configurado com `logging.structured.format.console=ecs`.

Como os módulos de teste `iandt` e `e2e` possuem dependência do módulo `application` em escopo de teste (`test`), eles herdam essas configurações no classpath de execução. Consequentemente:
- As mensagens de log são serializadas em linhas de JSON (formato ECS);
- São instanciados appenders de envio OTLP desnecessários para execuções de testes locais ou em pipeline;
- Os logs de testes não são exportados para serviços externos de observabilidade (como New Relic ou coletores OpenTelemetry), tornando o formato JSON redundante e prejudicial à legibilidade humana nos consoles de desenvolvimento e pipelines de CI/CD.

---

## 🏛️ Opções de Arquitetura

### 🥇 Opção 1 (Recomendada - Best Practice): Arquivos dedicados `logback-test.xml` em `src/test/resources` de `e2e` e `iandt` + desativação de structured logging
- **Como funciona**:
  - Criar `logback-test.xml` em:
    - `iandt/src/test/resources/logback-test.xml`
    - `e2e/src/test/resources/logback-test.xml`
  - Utilizar o appender padrão de texto do Spring Boot:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <configuration>
        <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
        <include resource="org/springframework/boot/logging/logback/console-appender.xml"/>

        <root level="INFO">
            <appender-ref ref="CONSOLE"/>
        </root>
    </configuration>
    ```
  - Em `iandt/src/test/resources/application-int_test.properties` e `e2e/src/test/resources/application.properties`:
    - Definir `logging.structured.format.console=` explicitamente para anular o formato ECS herdado.
- **Vantagens**:
  - **Prioridade Nativa do Logback**: O Logback prioriza `logback-test.xml` sobre `logback-spring.xml` ou `logback.xml` da biblioteca empacotada.
  - **Zero Poluição em Produção**: O módulo `application` permanece intacto, garantindo que o runtime de produção continue emitindo logs JSON para a observabilidade.
  - **Legibilidade Imediata**: Saída em texto padrão, colorizado e com formatação legível (timestamp, thread, classe, mensagem).
  - **Desativação de OTLP**: Evita chamadas e overhead do appender OpenTelemetry em suítes de teste.

### 🥈 Opção 2 (Alternativa): Apenas Sobrescrita de Propriedades Spring Boot (`logging.structured.format.console=`)
- **Como funciona**:
  - Apenas anular a propriedade `logging.structured.format.console=` nos arquivos `.properties` de teste de `e2e` e `iandt`.
- **Trade-offs**:
  - O `logback-spring.xml` da dependência `application` ainda é avaliado pelo Spring Boot, podendo tentar registrar o appender OTLP (OpenTelemetry) mesmo que sem endpoint ativo.

### 🥉 Opção 3 (Abordagem via Perfis no `logback-spring.xml` central de `application`)
- **Como funciona**:
  - Adicionar tags `<springProfile>` condicionais dentro de `application/src/main/resources/logback-spring.xml`.
- **Trade-offs**:
  - Viola o isolamento entre módulos de produção e de teste. Acopla a configuração de produção a nomes de perfis de teste (`test`, `int_test`, `e2e`).

---

## 🛠️ Detalhamento das Alterações Propostas (Opção 1)

### 1. Módulo `iandt`
- **[NOVO] `iandt/src/test/resources/logback-test.xml`**:
  - Configura `console-appender.xml` em texto e define `<root level="INFO">`.
- **[MODIFY] `iandt/src/test/resources/application-int_test.properties`**:
  - Adiciona `logging.structured.format.console=` para anular structured logging herdado de `application`.

### 2. Módulo `e2e`
- **[NOVO] `e2e/src/test/resources/logback-test.xml`**:
  - Configura `console-appender.xml` em texto e define `<root level="INFO">`.
- **[MODIFY] `e2e/src/test/resources/application.properties`**:
  - Adiciona `logging.structured.format.console=` para anular structured logging herdado de `application`.

---

## 🧪 Plano de Verificação

1. **Compilação**:
   - `mvn test-compile -pl iandt,e2e`
2. **Execução de Testes Unitários de E2E**:
   - `mvn test -pl e2e -Pmanual-e2e -Dtest="E2eConfigTest,AwsSigV4FilterTest" --no-transfer-progress`
   - Validar que a saída do console exibe formato de texto legível (não-JSON).
