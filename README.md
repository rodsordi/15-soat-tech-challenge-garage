# Garage API

API responsible for managing the vehicle mechanic workflow. Tech Challenge for the 15SOAT course.

## 🗒️ Information

- [Documentation](https://github.com/rodsordi/15SOAT-TechChallenge/wiki)

## 🏛️ Architecture

**Hexagonal Architecture**

![Hexagonal Architecture](docs/TechChallenge-ArchDesign.png)

### C4Model

```mermaid
C4Container
title Garage (Container Diagram)

    Person(employee, "Organization Employee", "Garage staff member.")
    Person(customer, "System Customer", "Garage customer.")

    System_Boundary(c1, "Garage Applications") {
        Container(web_app, "Web Application", "React / SPA", "Vehicle repair<br> data management interface.")
        Container(mobile_app, "Mobile App", "Flutter", "Mobile vehicle repair<br> data management.")
        Container(api, "API Application", "Java / Spring Boot", "Handles garage business logic<br> via REST API.")
    }

    System_Boundary(c2, "External Systems") {
        System_Ext(email, "E-mail Service", "External SMTP<br> Notification System.")
    }

    System_Boundary(c3, "Storage & Databases") {
        ContainerDb(db, "Database", "PostgreSQL", "Manages work orders, services<br> and authorization data.")
    }

    Rel(employee, web_app, "Uses", "HTTPS")
    Rel(customer, web_app, "Uses", "HTTPS")
    Rel(customer, mobile_app, "Uses", "HTTPS")
    
    Rel(web_app, api, "Consumes", "HTTPS")
    Rel(mobile_app, api, "Consumes", "HTTPS")
    
    Rel(api, db, "Reads from and writes to", "JDBC")
    Rel(api, email, "Sends e-mails via", "HTTP")
```

## 📋 Prerequisites

- [JDK 25](https://jdk.java.net/archive/)
- [IDE 2026.1](https://www.jetbrains.com/idea/download/)
- [Apache Maven 3.9.11](https://maven.apache.org/download.cgi)

## ⚙️ Setup

```sh
export M2_HOME=~/app/apache-maven-3.9.11
export M2=$M2_HOME/bin
export PATH=$PATH:$M2
```

```sh
export JAVA_HOME=~/app/jdk-25.0.2
export PATH=$PATH:$JAVA_HOME/bin
```

### 📂 Cloning repository

```sh
git clone https://github.com/rodsordi/15SOAT-TechChallenge.git
```

### 📦 Package building

```sh
mvn clean install -DskipTests
```

### 🐳 Running the application with Docker

```sh
docker build -t garage:0.0.1-SNAPSHOT .
```

### 🚀 Running the application with Docker Compose

```sh
docker compose up
```

## 📄 Swagger

| Ambiente | Url                                                     | 
|----------|---------------------------------------------------------|
| local    | [link](http://localhost:8080/api/swagger-ui/index.html) |

## 🌐 Curls

- Health check

```sh
curl --location 'http://localhost:8080/api/actuator/health'
```

- Creating Employee

```sh
curl --location 'http://localhost:8080/api/v1/employees' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "john@garage.com",
    "password": "Garage@2026",
    "name": "John",
    "email": "john@garage.com",
    "cpf": "664.260.660-44"
}'
```

- Authenticating

```sh
curl --location 'http://localhost:8080/api/auth/login' \
--header 'Content-Type: application/json' \
--data-raw '{
    "username": "john@garage.com",
    "password": "Garage@2026"
}'
```

- Fetching Employees

```sh
curl --location 'http://localhost:8080/api/v1/employees' \
--header 'Authorization: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqb2huQGdhcmFnZS5jb20iLCJpYXQiOjE3Nzc2MDczOTEsImV4cCI6MTc3NzYxMDk5MX0.Fzwy1Ii8gnpgUtZBRUsZWf8WJgoum-dUNmhNFd6SldgHEW9L6fKLF_xWB6mkVaZ0iQJyZszuhUtNrK64LxUcaQ'
```

## 🚀 CI/CD

**IaC**

- Follow the `iac/README.md` instructions to enable CI/CD pipe-line.

**Gitflow**
```mermaid
---
config:
  logLevel: 'debug'
  theme: 'base'
  gitGraph:
    showBranches: true
    showCommitLabel: true
    mainBranchOrder: 1
---
gitGraph
  commit id: "e3f946 (main)" tag: "v1.0.0"
  
%% hotfix
  branch hotfix/new-fix
  checkout hotfix/new-fix
  commit id: "e3f946 (hotfix)"
  commit id: "commit (hotfix)"
  
%% develop
  checkout main
  branch develop order: 2
  checkout develop
  commit id: "e3f946 (dev)"
  
%% feature1
  checkout develop
  branch feature/new-feature-1 order: 4
  checkout feature/new-feature-1
  commit id: "e3f946 (feature1)"
  commit id: "commit1 (feature1)"
  commit id: "commit2 (feature1)"
  
%% feature2
  checkout develop
  branch feature/new-feature-2 order: 5
  checkout feature/new-feature-2
  commit id: "e3f946 (feature2)"
  commit id: "commit (feature2)"

%% merge features on develop
  checkout develop
  merge feature/new-feature-1 id: "merge (feature1)"
  checkout develop
  merge feature/new-feature-2 id: "merge (feature2)"

%% release
  checkout develop
  branch release/1.0.1-new-release order: 1
  checkout release/1.0.1-new-release
  cherry-pick id: "merge (feature2)" parent: "commit (feature2)"
  commit id: "commit (release)"

%% merge release on main
  checkout main
  merge hotfix/new-fix tag: "v1.0.1a" id: "merge (hotfix)"
  checkout main
  merge release/1.0.1-new-release tag: "v1.0.1" id: "merge (release)"
```

## 📌 Versão

- Using [SemVer](https://semver.org/) for version control.

## ✒ Autores

- [Rodrigo de Sordi - RM372537](https://github.com/rodsordi)

