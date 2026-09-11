# ADR 0006: Validação Algorítmica de Documentos Brasileiros (Módulo 11) no Perímetro (Edge)

* **Status**: Aceito (Accepted)
* **Data**: 2026-09-10
* **Autores**: Tech Challenge Garage Team
* **Decisores Técnicos**: Especialistas em Arquitetura de Software e Segurança

---

## 1. Contexto e Declaração do Problema

No ecossistema da oficina, a identificação dos usuários e emissão de notas ou ordens de serviço depende da validação estrita de documentos oficiais brasileiros: **CPF** (Pessoa Física - 11 dígitos) e **CNPJ** (Pessoa Jurídica - 14 dígitos).

Receber documentos formatados incorretamente ou com dígitos verificadores inválidos causa:
* Contas corrompidas no Keycloak que não conseguem ser faturadas;
* Falhas tardias no banco de dados após gasto desnecessário de computação e IO;
* Vulnerabilidade a ataques de enumeração ou injeção de dados lixo na esteira de autenticação.

---

## 2. Decisão Arquitetural

Decidimos implementar a **Validação Algorítmica Estrita de CPF e CNPJ (Módulo 11 da Receita Federal) no Perímetro de Entrada (*Edge / Gateway*)**, executada dentro da função AWS Lambda (`garage-auth-handler`):

### 2.1. Regras Executadas no Perímetro
1. **Sanitização**: Remoção de máscaras e pontuações (`.`, `-`, `/`).
2. **Rejeição de Sequências Conhecidas**: Rejeição imediata de padrões óbvios inválidos (ex: `111.111.111-11`, `000.000.000-00`).
3. **Cálculo dos Dois Dígitos Verificadores (DV)**:
   - Para CPF: Algoritmo de Módulo 11 com pesos de 10 a 2 para o primeiro dígito, e de 11 a 2 para o segundo dígito.
   - Para CNPJ: Algoritmo de Módulo 11 com pesos cíclicos de 5 a 2 e 9 a 2.
4. **Rejeição Rápida (Fail-Fast)**: Se o cálculo dos dígitos verificadores falhar, a requisição é rejeitada com `HTTP 400 Bad Request` antes de qualquer consulta ao Keycloak ou à `api-garage`.

---

## 3. Consequências

### Positivas (Benefícios):
* **Economia de Recursos e Custos**: Bloqueia requisições inválidas no primeiro milissegundo de execução, sem consumir conexões de banco de dados nem chamadas à API REST do Keycloak.
* **Dados Limpos e Confiáveis**: Garante que 100% dos usuários cadastrados no Keycloak e na base da oficina possuem documentos matematicamente legítimos.
* **Proteção de Perímetro (*Defense in Depth*)**: Impede a propagação de payloads corrompidos para o cluster interno do Kubernetes.

### Negativas / Trade-offs:
* A validação garante a higidez matemática do documento, mas não checa se o CPF/CNPJ está ativo na Receita Federal (o que exigiria integração com serviços governamentais externos).

---

## 4. Referências e Códigos Impactados
* [`documentValidator.js`](file:///c:/git/fiap/15-soat-tech-challenge-lamda/src/documentValidator.js)
* [`documentValidator.test.js`](file:///c:/git/fiap/15-soat-tech-challenge-lamda/test/documentValidator.test.js)
