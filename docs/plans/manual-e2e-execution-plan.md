# Plano de Configuração Maven: Execução Estritamente Manual dos Testes `e2e`

> **Status**: Proposta para Revisão & Aprovação  
> **Data**: 2026-09-10  
> **Autor**: Engenheiro Especialista em Desenvolvimento & QA Architect  
> **Repositório Alvo**: `15-soat-tech-challenge-garage`  

---

## 1. Visão Geral & Motivação

Por padrão em projetos multi-módulo Maven, ao rodar fases de ciclo de vida como `mvn test`, `mvn package` ou `mvn verify`, o plugin Maven Surefire varre todos os submódulos e executa os testes localizados em `src/test/java`.

Para testes de ponta a ponta (E2E), essa execução automática é indesejada por:
1. **Dependência de Ambiente**: Testes E2E demandam que os serviços (banco, mensageria, API) estejam em execução prévia (seja via Docker Compose ou ambiente de nuvem).
2. **Tempo de Build**: Execuções automáticas em pipelines de CI/CD não devem ser travadas por testes E2E sem orquestração explícita.
3. **Controle Estrito do Operador**: O teste deve rodar apenas sob comando explícito e intencional do desenvolvedor ou operador.

---

## 2. Opções Técnicas Ordenadas por Prioridade e Recomendação

### 🥇 Opção 1 (Recomendada - Best Practice): `skipTests=true` Incondicional + Profile Manual Dedicado (`manual-e2e`)

* **Motivação Técnica**:
  Esta é a abordagem mais segura e idiomática no Maven. Remove qualquer ativação automática (`<activation>`) do profile, garantindo que o `maven-surefire-plugin` esteja configurado com `<skipTests>true</skipTests>` de forma absoluta no build padrão do módulo `e2e`. A execução só ocorrerá quando o profile explícito for invocado na linha de comando (`-Pmanual-e2e` ou `-Pe2eTests`).
* **Como Funciona**:
  1. No `e2e/pom.xml`:
     ```xml
     <build>
         <plugins>
             <plugin>
                 <groupId>org.apache.maven.plugins</groupId>
                 <artifactId>maven-surefire-plugin</artifactId>
                 <configuration>
                     <skipTests>true</skipTests>
                 </configuration>
             </plugin>
         </plugins>
     </build>

     <profiles>
         <profile>
             <id>manual-e2e</id>
             <!-- SEM BLOCO DE ATIVAÇÃO AUTOMÁTICA -->
             <build>
                 <plugins>
                     <plugin>
                         <groupId>org.apache.maven.plugins</groupId>
                         <artifactId>maven-surefire-plugin</artifactId>
                         <configuration>
                             <skipTests>false</skipTests>
                         </configuration>
                     </plugin>
                 </plugins>
             </build>
         </profile>
     </profiles>
     ```
  2. No `pom.xml` raiz:
     - O profile correspondente garante que os módulos de negócio (`domain`, `application`, `iandt`, `stress`) pulem seus testes quando o operador desejar rodar apenas o E2E manual.
* **Vantagens**:
  - ✅ **Zero Execução Acidental**: Qualquer comando comum (`mvn test`, `mvn clean install`, `mvn verify`) pula automaticamente o `e2e`.
  - ✅ **Segurança em CI/CD**: Pipelines de PR e merge no master nunca falharão por causa do `e2e`.
  - ✅ **Execução Manual Simples e Explícita**: `mvn test -pl e2e -Pmanual-e2e`.

---

### 🥈 Opção 2 (Alternativa): Exclusão Padrão de Padrões de Teste (`<excludes>`)

* **Motivação Técnica**:
  Utilizar tags `<excludes>` no Maven Surefire excluindo todos os testes (`**/*Test.java`), exigindo que o desenvolvedor informe `-Dtest=NomeDoTeste` na linha de comando.
* **Trade-offs**:
  - ⚠️ Exige lembrar o nome exato da classe de teste a ser executada manualmente, em vez de rodar toda a suíte E2E com uma única flag.

---

### 🥉 Opção 3 (Minimalista): Apenas Flag `-DskipTests=true` Documentada

* **Motivação Técnica**:
  Não alterar o POM e depender de documentação para que os desenvolvedores sempre passem `-DskipTests` ou `-DskipITs`.
* **Trade-offs**:
  - ⚠️ Alto risco de erro humano e quebra de builds automáticos.

---

## 3. Comandos de Operação

| Ação | Comando Maven | Comportamento no `e2e` |
| :--- | :--- | :--- |
| **Build padrão / CI** | `mvn test` ou `mvn install` | ⏭️ **Pulado** (`Tests are skipped`) |
| **Build direcionado sem flag** | `mvn test -pl e2e` | ⏭️ **Pulado** (`Tests are skipped`) |
| **Execução Manual Intencional** | `mvn test -pl e2e -Pmanual-e2e` | ▶️ **Executado** |
