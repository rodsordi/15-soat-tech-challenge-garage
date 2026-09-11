# ADR 0004: Validação Stateless de Tokens JWT com Assinatura Assimétrica RS256 e JWKS

* **Status**: Aceito (Accepted)
* **Data**: 2026-09-10
* **Autores**: Tech Challenge Garage Team
* **Decisores Técnicos**: Especialistas em Arquitetura de Software e Segurança da Informação

---

## 1. Contexto e Declaração do Problema

A aplicação `api-garage` atua como um *Resource Server* protegido que recebe requisições de clientes web, mobile e serviços internos. Para autorizar acessos a endpoints sensíveis (ex: criação de ordens de serviço, cadastro de veículos e manipulação de estoque), era necessário definir o mecanismo de validação das credenciais do usuário.

Modelos tradicionais apresentavam limitações críticas:
1. **Sessões HTTP em Memória / Stateful**: Inviabilizam escalabilidade horizontal em contêineres no Kubernetes (exigiriam sticky sessions ou Redis compartilhado para sessões).
2. **Introspecção Remota de Tokens (RFC 7662)**: Obriga o Resource Server a fazer uma chamada HTTP síncrona para o IdP (Keycloak) a cada requisição que chega. Isso cria um gargalo de latência e transforma o Keycloak em um Ponto Único de Falha (*Single Point of Failure - SPOF*).
3. **Assinatura Simétrica (HS256 com Secret Compartilhado)**: Obriga a distribuir a mesma chave secreta entre o emissor do token e todos os serviços consumidores, aumentando drasticamente o risco de vazamento de credencial.

---

## 2. Decisão Arquitetural

Decidimos adotar a **Validação Stateless de Tokens JWT com Criptografia Assimétrica RS256 via JWKS (JSON Web Key Set)**.

### 2.1. Como Funciona a Solução
1. **Emissão no Keycloak**: O Keycloak assina os Access Tokens JWT utilizando sua chave privada RSA (algoritmo RS256).
2. **Publicação via JWKS**: O Keycloak disponibiliza publicamente (ou na rede interna) sua chave pública em formato padrão no endpoint OIDC:
   ```
   GET /realms/garage/protocol/openid-connect/certs
   ```
3. **Validação Local no Spring Security**: O Resource Server `api-garage` utiliza o módulo `spring-boot-starter-oauth2-resource-server` configurado com o `issuer-uri` ou `jwk-set-uri`.
4. **Cache Inteligente de Chaves Públicas**: O framework busca o conjunto de chaves públicas na inicialização e as mantém em cache em memória. Cada requisição com cabeçalho `Authorization: Bearer <token>` é validada matematicamente em microssegundos sem nenhuma chamada de rede externa ao IdP.

---

## 3. Consequências

### Positivas (Benefícios):
* **Desempenho Extremo e Baixa Latência**: A validação da assinatura criptográfica e das claims de expiração (`exp`), emissor (`iss`) e papéis (`roles`) é executada 100% em memória local.
* **Escalabilidade Horizontal Infinita**: Novos pods da `api-garage` podem ser provisionados pelo HPA (Horizontal Pod Autoscaler) no Kubernetes sem sobrecarregar o Keycloak.
* **Resiliência a Quedas do IdP**: Se o Keycloak sofrer uma reinicialização momentânea, os usuários portadores de tokens JWT válidos continuam acessando a `api-garage` normalmente.
* **Segurança Baseada em Chaves Assimétricas**: O Resource Server precisa apenas da chave pública para validar, nunca da chave privada usada para assinar.

### Negativas / Trade-offs:
* **Revogação Imediata de Tokens**: Tokens JWT stateless não podem ser revogados individualmente antes do seu tempo de expiração (`exp`) sem a implementação de listas de revogação distribuídas (blacklist). Esse trade-off é mitigado mantendo o tempo de vida do Access Token curto (ex: 5 a 15 minutos).

---

## 4. Alternativas Consideradas e Rejeitadas

| Alternativa | Veredito | Motivo da Rejeição |
| :--- | :--- | :--- |
| **1. Token Introspection (RFC 7662)** | **Rejeitada** | Sobrecarga de rede severa (1 chamada externa para cada request à API) e dependência estrita de disponibilidade contínua do IdP. |
| **2. Segredo Compartilhado Simétrico (HS256)** | **Rejeitada** | Menor nível de segurança; risco de comprometimento da chave secreta distribuída em múltiplos microsserviços. |
| **3. Sessões HTTP em Memória / Cookie de Sessão** | **Rejeitada** | Não atende à arquitetura nativa em nuvem (Cloud-Native) e padrões RESTful modernos. |

---

## 5. Referências
* **RFC 7519** - JSON Web Token (JWT)
* **RFC 7517** - JSON Web Key (JWK)
* **Spring Security Reference**: OAuth 2.0 Resource Server JWT
