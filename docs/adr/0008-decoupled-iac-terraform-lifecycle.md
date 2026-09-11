# ADR 0008: Segregação e Ciclo de Vida Desacoplado de Infraestrutura como Código (IaC)

* **Status**: Aceito (Accepted)
* **Data**: 2026-09-10
* **Autores**: Tech Challenge Garage Team
* **Decisores Técnicos**: Especialistas em Cloud / DevOps e Arquitetura de Sistemas

---

## 1. Contexto e Declaração do Problema

O ecossistema do Tech Challenge é composto por múltiplos tipos de recursos de nuvem na AWS:
1. Camada de Persistência de Dados (RDS PostgreSQL / Keycloak PostgreSQL);
2. Camada de Computação e Orquestração (Cluster EKS Kubernetes);
3. Camada Serverless e IAM (Função AWS Lambda `garage-auth-handler`).

Em projetos que mantêm toda a infraestrutura em um único repositório ou arquivo `main.tf` monolítico, surgem riscos catastróficos:
* Um `terraform apply` ou `terraform destroy` executado para recriar um nó do cluster pode acidentalmente destruir o banco de dados e apagar o volume de dados da oficina;
* Acoplamento desnecessário nas pipelines de CI/CD: mudanças no código do Lambda forçam o plano do Terraform a validar todo o cluster Kubernetes;
* Limites de permissão e blast radius (raio de explosão) muito elevados.

---

## 2. Decisão Arquitetural

Decidimos segregar a **Infraestrutura como Código (IaC)** com Terraform em **repositórios e ciclos de vida independentes**:

```
c:/git/fiap/
├── 15-soat-tech-challenge-iac-db   (Ciclo de Vida 1: Persistência / Dados)
├── 15-soat-tech-challenge-iac-k8s  (Ciclo de Vida 2: Computação / EKS)
└── 15-soat-tech-challenge-lamda    (Ciclo de Vida 3: Serverless / IAM Handler)
```

### 2.1. Políticas de Ciclo de Vida por Camada
1. **`15-soat-tech-challenge-iac-db` (Alta Estabilidade)**:
   - Gerencia instâncias RDS PostgreSQL, Subnet Groups e Security Groups de banco.
   - Protegido com flags `deletion_protection = true` e retenção de snapshots para evitar qualquer perda de dados acidental.
2. **`15-soat-tech-challenge-iac-k8s` (Computação Dinâmica)**:
   - Provisiona o cluster AWS EKS, Node Groups, Ingress Controllers e Namespaces.
   - Pode ser atualizado, escalado ou recriado sem afetar os dados persistidos no RDS.
3. **`15-soat-tech-challenge-lamda` (Serverless Edge)**:
   - Provisiona a função Lambda empacotada, Function URL pública e variáveis de ambiente associadas ao APM New Relic.

---

## 3. Consequências

### Positivas (Benefícios):
* **Redução do Raio de Explosão (*Blast Radius*)**: Uma falha de sintaxe ou exclusão na camada de computação jamais atinge a base de dados.
* **Pipelines de Deploy Independentes**: Atualizações no Lambda são implantadas em segundos sem necessidade de rodar o plano do EKS ou do RDS.
* **Princípio do Menor Privilégio (*Least Privilege*)**: Permite atribuir credenciais de automação específicas para cada repositório.

### Negativas / Trade-offs:
* Exige que referências cruzadas de infraestrutura (ex: endpoints de banco de dados e subnets da VPC) sejam compartilhadas via Data Sources do Terraform (`aws_subnets`, `aws_vpc`) ou variáveis explícitas.

---

## 4. Referências
* **HashiCorp Terraform Best Practices**: Structuring Terraform Projects.
* **AWS Well-Architected Framework**: Reliability & Security Pillars.
