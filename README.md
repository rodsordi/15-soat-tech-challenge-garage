# 🚗 Garage API (`15-soat-tech-challenge-garage`)

Microsserviço central do Tech Challenge (FIAP SOAT) responsável pela gestão e automação do fluxo operacional de oficinas mecânicas, incluindo cadastro de clientes, veículos, controle de peças/inventário, funcionários e ciclo de vida de ordens de serviço.

---

## 🎯 1. Descrição do Propósito

A **Garage API** implementa as regras de negócio essenciais de uma oficina mecânica moderna sob o padrão de **Arquitetura Hexagonal (Ports & Adapters)** e **Domain-Driven Design (DDD)**. 

Principais responsabilidades:
* **Gestão de Clientes e Veículos**: Cadastro, busca por CPF/CNPJ e associação de veículos a proprietários.
* **Ordens de Serviço (Work Orders)**: Abertura, diagnóstico, aprovação de orçamentos, execução de serviços e finalização com pagamento.
* **Inventário e Serviços**: Controle de estoque de peças, precificação e catálogo de serviços mecânicos.
* **Segurança e Controle de Acesso (RBAC)**: Proteção de rotas sensíveis com Spring Security 6 e tokens JWT (diferenciando perfis `CUSTOMER` e `EMPLOYEE`).

---

## 💻 2. Tecnologias Utilizadas

* **Linguagem & Runtime**: Java 25 (OpenJDK / Eclipse Temurin).
* **Framework Principal**: Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Security, Spring Validation, Spring Actuator).
* **Banco de Dados**: PostgreSQL 15 (com migrações automatizadas via Flyway).
* **Segurança**: Criptografia BCrypt, autenticação Stateless via JWT (JSON Web Tokens) e controle de acesso RBAC.
* **Gerenciador de Dependências & Build**: Apache Maven 3.9+.
* **Qualidade & Testes**: JUnit 5, Mockito, Testcontainers, Jacoco (cobertura de código) e SonarQube / SonarCloud.
* **Segurança de Dependências**: OWASP Dependency-Check.
* **Documentação de API**: OpenAPI 3 / SpringDoc Swagger UI.
* **Conteinerização & Deploy**: Docker (multi-stage build), Docker Compose e Helm Charts para Kubernetes (AWS EKS).

---

## 🏛️ 3. Diagrama da Arquitetura do Repositório

```mermaid
graph TD
    subgraph DrivingAdapters [Driving Adapters (Inbound)]
        REST[REST Controllers / Swagger]
        Security[SecurityFilter & JWT Validator]
    end

    subgraph PortsIn [Inbound Ports (Use Cases)]
        CustomerUC[Customer Use Cases]
        OrderUC[Work Order Use Cases]
        VehicleUC[Vehicle Use Cases]
        InventoryUC[Inventory Use Cases]
    end

    subgraph DomainCore [Domain Core (Business Logic)]
        Entities[Entities & Value Objects: Customer, Order, Vehicle, Service]
        DomainServices[Domain Services & Business Rules]
    end

    subgraph PortsOut [Outbound Ports (Gateways)]
        CustomerGateway[Customer Gateway Port]
        OrderGateway[Order Gateway Port]
        VehicleGateway[Vehicle Gateway Port]
    end

    subgraph DrivenAdapters [Driven Adapters (Outbound)]
        JPAAdapter[Spring Data JPA Repositories]
        DB[(PostgreSQL Database)]
    end

    REST --> Security
    Security --> PortsIn
    CustomerUC --> DomainCore
    OrderUC --> DomainCore
    VehicleUC --> DomainCore
    InventoryUC --> DomainCore
    DomainCore --> PortsOut
    CustomerGateway --> JPAAdapter
    OrderGateway --> JPAAdapter
    VehicleGateway --> JPAAdapter
    JPAAdapter --> DB
```

---

## ⚙️ 4. Passos para Execução e Deploy

### 4.1. Execução Local com Docker Compose
Para subir a aplicação rapidamente junto com o banco PostgreSQL local:

```bash
# 1. Compilar o projeto sem rodar testes
mvn clean package -DskipTests

# 2. Iniciar os contêineres da aplicação e banco
docker compose up -d

# 3. Acompanhar os logs
docker compose logs -f api
```

### 4.2. Execução Local via Maven
```bash
# Exportar variáveis de banco se necessário
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/garage_db"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="password"

# Executar a aplicação
mvn spring-boot:run -pl application
```

### 4.3. Deploy no Kubernetes (AWS EKS) via Helm
Com o `kubectl` conectado ao cluster AWS EKS (`techchallenge-cluster`):

```bash
helm upgrade --install api-garage helm \
  --namespace garage \
  --set image.repository=890958457263.dkr.ecr.us-east-1.amazonaws.com/garage-api \
  --set image.tag=latest \
  --wait --timeout 3m
```

### 4.4. Deploy Automatizado (CI/CD via GitHub Actions)
O repositório conta com pipeline automatizada em `.github/workflows/workflow-develop.yml`:
1. **Quality Assurance**: Execução de testes unitários e cobertura com Jacoco.
2. **Security**: Varredura de vulnerabilidades OWASP.
3. **Build Image**: Geração da imagem Docker e push para o **AWS ECR** (`garage-api`).
4. **Deploy**: Instalação e atualização automática no **AWS EKS** via Helm.

---

## 📑 5. Link para o Swagger e Postman das APIs

### 🌐 Swagger UI / OpenAPI 3:
* **Execução Local**: [http://localhost:8080/api/swagger-ui/index.html](http://localhost:8080/api/swagger-ui/index.html)
* **Especificação OpenAPI JSON (Local)**: [http://localhost:8080/api/v3/api-docs](http://localhost:8080/api/v3/api-docs)
* **Ambiente AWS (via AWS API Gateway)**:
  ```
  https://igqc9vtfx9.execute-api.us-east-1.amazonaws.com/api/swagger-ui/index.html
  ```
* **OpenAPI JSON na AWS**:
  ```
  https://igqc9vtfx9.execute-api.us-east-1.amazonaws.com/api/v3/api-docs
  ```

### 📬 Coleção Postman / cURL de Exemplo:
Para importar no Postman ou testar no terminal:

```bash
# 1. Health Check
curl --location 'https://igqc9vtfx9.execute-api.us-east-1.amazonaws.com/api/actuator/health'

# 2. Cadastro de Cliente (Autenticado com Token Bearer)
curl --location 'https://igqc9vtfx9.execute-api.us-east-1.amazonaws.com/api/v1/customers' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer <SEU_TOKEN_JWT>' \
--data-raw '{
    "name": "Maria Oliveira",
    "document": "529.982.247-25",
    "email": "maria@email.com",
    "phone": "11999999999"
}'

# 3. Consulta de Ordem de Serviço
curl --location 'https://igqc9vtfx9.execute-api.us-east-1.amazonaws.com/api/v1/orders' \
--header 'Authorization: Bearer <SEU_TOKEN_JWT>'
```
