# Plano de Refatoração Canônica do Build Nativo GraalVM (Spring Boot 4)

Este documento descreve o plano de refatoração para eliminar configurações e mapeamentos manuais de reflexão, alinhando a aplicação às melhores práticas do **Spring Boot 4 AOT** com **GraalVM Native Image**.

---

## 1. Contexto e Justificativa

Durante as tentativas de execução em modo nativo no Kubernetes, foram adicionadas classes e arquivos auxiliares com listas exaustivas de entidades e classes de domínio:
- @RegisterReflectionForBinding com dezenas de classes em GarageApplication.
- Entradas manuais no eflect-config.json para cada entidade e enum do projeto.
- Lista manual em NativeRuntimeHints.java.
- Bean manual PersistenceManagedTypesScanner em JpaConfig.java.

A causa raiz real do erro foi:
1. **Descentralização dos pacotes do JPA**: O pacote raiz do @SpringBootApplication é r.com.fiap.garage.application, enquanto as entidades residem em r.com.fiap.garage.domain.entity e r.com.fiap.commons.entity. Sem @EntityScan explícito no ponto de entrada da aplicação, o Spring AOT nativo não registrou esses pacotes no LocalContainerEntityManagerFactoryBean.
2. **Ambiguidade de Nomenclatura da Entidade Service**: O nome simples da classe Service causava conflito semântico com a anotação estereótipo @Service do Spring e interfaces internas do Hibernate (org.hibernate.service.Service).

---

## 2. Abordagem Canônica Proposta

- Mover @EntityScan e @EnableJpaRepositories para GarageApplication.java, apontando explicitamente para os pacotes de entidades e repositórios.
- Explicitar @Entity(name = "GarageService") e @Table(name = "service", schema = "garage") em Service.java.
- Limpar eflect-config.json e NativeRuntimeHints.java, mantendo apenas tipos de baixo nível que não sejam gerenciados pelo Spring Data JPA.
- Remover o bean manual PersistenceManagedTypesScanner de JpaConfig.java.
