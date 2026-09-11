# RFC 0004: Estratégia de Cache Distribuído com Redis para Catálogos e Sessões OIDC

* **Status**: Proposta (Draft)
* **Data de Criação**: 2026-09-10
* **Autor(es)**: Tech Challenge Garage Architecture Guild
* **Alvo de Implementação**: Fase 4 / Performance & Escalabilidade

---

## 1. Resumo Executivo (Summary)

Esta RFC propõe a introdução de uma camada de **Cache Distribuído em Memória utilizando Redis (AWS ElastiCache)** na aplicação `api-garage`. O objetivo principal é aliviar a carga de leitura no banco de dados relacional PostgreSQL para dados de consulta intensiva e baixa volatilidade (como tabela de serviços, inventário de peças e marcas/modelos de veículos), além de prover suporte para *Rate Limiting* distribuído por IP/usuário.

---

## 2. Motivação (Motivation)

À medida que a oficina escala o número de atendentes e mecânicos operando o sistema via totens e tablets:
1. **Sobrecarga de Leituras Repetidas**: A tela de abertura de ordens de serviço lista repetidamente as dezenas de serviços disponíveis (troca de óleo, balanceamento, geometria) e marcas de veículos. Cada renderização bate no banco relacional gerando conexões e IO de disco desnecessários.
2. **Proteção contra Abusos de API**: Endpoints públicos de login e validação de documentos precisam de controle de requisições por segundo (*Rate Limiting*) para mitigar tentativas de força bruta.

---

## 3. Proposta de Design Detalhada (Detailed Design)

### 3.1. Padrão Cache-Aside com Spring Cache e Redisson

```mermaid
flowchart TD
    Req[Requisição HTTP GET /v1/services] --> App[api-garage Service Layer]
    App -->|1. Consulta Chave| Redis[(Redis Cluster)]
    
    Redis -->|Cache Hit (Dado em RAM)| ReturnHit[Retorna em < 2ms]
    ReturnHit --> App
    
    Redis -.->|Cache Miss| DB[(PostgreSQL RDS)]
    DB -->|2. Consulta SQL| App
    App -->|3. Grava no Redis com TTL 1h| Redis
    App --> Resp[Resposta 200 OK]
```

### 3.2. Políticas de Expiração e Invalidação Reativa
* **Time-to-Live (TTL)**: 
  - Catálogo de Serviços e Peças: TTL padrão de 60 minutos.
  - Chaves públicas JWKS: TTL de 24 horas.
* **Invalidação Reativa (`@CacheEvict`)**: Sempre que um administrador cadastrar ou alterar um serviço (`POST /v1/services`, `PUT /v1/services/{id}`), a anotação `@CacheEvict(value = "services", allEntries = true)` limpa imediatamente o cache correspondente.

---

## 4. Desvantagens e Trade-offs

* **Inconsistência Momentânea**: Dados atualizados diretamente no banco sem passar pela API podem ficar desatualizados até a expiração do TTL.
* **Custo Adicional**: Adiciona o custo operacional de um cluster AWS ElastiCache for Redis na arquitetura.
