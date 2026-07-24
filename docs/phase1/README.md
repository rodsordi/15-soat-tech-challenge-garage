# Garage API

API responsible for managing the vehicle mechanic workflow. Tech Challenge for the 15SOAT course.

## 🗒️ Information

- [Documentation](https://github.com/rodsordi/15SOAT-TechChallenge/wiki)

## 📋 Prerequisites

- [JDK 25](https://jdk.java.net/archive/)
- [Apache Maven 3.9.11](https://maven.apache.org/download.cgi)

## ⚙️ Setup

**Maven**
```sh
export M2_HOME=~/app/apache-maven-3.9.11
export M2=$M2_HOME/bin
export PATH=$PATH:$M2
```

**jdk**
```sh
export JAVA_HOME=~/app/jdk-25.0.2
export PATH=$PATH:$JAVA_HOME/bin
```

### 📂 Cloning repository

```sh
git clone https://github.com/rodsordi/15SOAT-TechChallenge.git
```

### 👌 Running unit tests

```sh
mvn test
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

## ✨ Quality

### 🧪 Integration tests:

```sh
mvn test -DintegrationTests
```

### 🛡️ Vulnerabilities

- Request an api-key on https://nvd.nist.gov/developers/request-an-api-key
- Obs: This sptep is optional, but it will provide more accurate results and a higher rate limit for vulnerability
  checks.

```sh
NVD_API_KEY=${confirmed_api_key_on_email}
echo $NVD_API_KEY
```

```sh
mvn clean verify -DskipTests -Dowasp
```

### 🧹 Coverage

```sh
docker compose -f docker-compose-devops.yml up -d
```

- Change password and create token
- Obs: To change new tokens, you should change the name query param.

```sh
curl -u admin:admin -X POST "http://localhost:9000/api/users/change_password?login=admin&previousPassword=admin&password=Sonarqube@2026"
SONAR_TOKEN=$(curl -u admin:Sonarqube@2026 -X POST "http://localhost:9000/api/user_tokens/generate?name=setup-token" | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo $SONAR_TOKEN
```

- Run report

```sh
mvn clean verify sonar:sonar -Dsonar.host.url=http://localhost:9000 -Dsonar.qualitygate.wait=true -Dsonar.token=$SONAR_TOKEN
```

- Browse [sonar](http://localhost:9000/)

| User  | Pass           | 
|-------|----------------|
| admin | Sonarqube@2026 |


## 📌 Versão

- Using [SemVer](https://semver.org/) for version control.

## ✒ Autores

- [Rodrigo de Sordi - RM372537](https://github.com/rodsordi)
