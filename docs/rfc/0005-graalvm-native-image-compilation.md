# RFC 0005: Compilação Nativa com GraalVM Native Image (Spring Boot 4 AOT)

* **Status**: Proposta (Draft)
* **Data de Criação**: 2026-09-10
* **Autor(es)**: Tech Challenge Garage Architecture Guild
* **Alvo de Implementação**: Fase 4 / Otimização Cloud-Native

---

## 1. Resumo Executivo (Summary)

Propõe-se a migração do empacotamento da aplicação `api-garage` (atualmente executada como fat-JAR sobre uma JVM HotSpot tradicional) para um **executável binário nativo em Linux ELF compilado Ahead-Of-Time (AOT) com GraalVM e Spring Boot 4**. 

O objetivo é viabilizar inicialização instantânea (*sub-second startup* < 100ms) e redução de 70% no consumo de memória RAM por réplica, permitindo ao Kubernetes escalar novos pods (HPA) quase em tempo real sob picos repentinos de requisições.

---

## 2. Motivação e Benchmarks (Motivation)

A JVM padrão oferece excelente performance após o aquecimento do compilador JIT (Just-In-Time), mas impõe penalidades severas para arquiteturas de contêineres e auto-scaling:

| Métrica | JVM Tradicional (OpenJDK 21 HotSpot) | GraalVM Native Image (AOT) | Ganho Estimado |
| :--- | :---: | :---: | :---: |
| **Tempo de Startup (Cold-Start)** | ~8 a 15 segundos | **< 100 milissegundos** | **99% mais rápido** ⚡ |
| **Consumo de Memória Base (RSS)** | ~280MB a 400MB por pod | **~40MB a 65MB por pod** | **75% de economia** 💰 |
| **Tamanho da Imagem de Contêiner** | ~350MB (Alpine/Ubuntu + JRE) | **~50MB a 70MB (Scratch/Distroless)** | **80% menor** 📦 |

### Problema Prático no Kubernetes:
Quando o HPA (Horizontal Pod Autoscaler) detecta um pico de CPU aos 80% e ordena a criação de 3 novos pods, na JVM tradicional esses pods levam até 15 segundos para passar no `readinessProbe` e começar a aceitar tráfego. Durante essa janela, o pod original pode sofrer estrangulamento ou derrubar requisições com timeout. Com GraalVM nativo, o pod entra em estado `Ready` em menos de 1 segundo.

---

## 3. Desafios Técnicos e Adequações de Código

A compilação AOT (Ahead-Of-Time) analisa o código em tempo de build sob a hipótese de mundo fechado (*Closed-World Assumption*). Qualquer uso de reflexão dinâmica, proxies dinâmicos ou serialização não declarada quebra em runtime nativo.

### Ajustes Necessários:
1. **Spring AOT Plugin**: Configurar `native-maven-plugin` no `pom.xml`.
2. **Registro de DTOs e Reflection**: O Spring Framework 7 / Spring Boot 4 já suporta automaticamente reflexão para controllers e repositórios JPA, mas mappers ou classes utilitárias de reflexão profunda (como `ReflectionUtil.hasNoEmptyFields()`) precisarão de `@RegisterReflectionForBinding`.
3. **Driver JDBC PostgreSQL**: O driver oficial do PostgreSQL já é 100% compatível com GraalVM Native Image no Spring Boot 4.

---

## 4. Estratégia de Build na Pipeline CI/CD

Como a compilação nativa consome bastante memória e tempo (de 3 a 6 minutos no pipeline de CI):
* O ambiente de desenvolvimento local continuará utilizando a JVM tradicional rápida (`mvn spring-boot:run`).
* O build nativo será executado apenas nas tags de release para produção via GitHub Actions com `Paketo Buildpacks` (`mvn -Pnative spring-boot:build-image`).
