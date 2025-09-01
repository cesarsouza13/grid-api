# Grid - Projeto Spring Boot com PostgreSQL e Flyway

Este é um projeto inicial em **Java Spring Boot** chamado `Grid`, com **PostgreSQL** em Docker e **migrações de banco com Flyway**.

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado em sua máquina:

- **Java 17**
- **Maven 3.x**
- **Docker** e **Docker Compose**
- (Opcional) **Git**

---

## Estrutura do projeto

grid/
├─ src/
│ ├─ main/
│ │ ├─ java/com/example/grid/
│ │ │ └─ GridApplication.java
│ │ └─ resources/
│ │ ├─ application.yaml
│ │ └─ db/migration/
│ │ └─ V1__create_table_example.sql
├─ .env
├─ docker-compose.yml
├─ pom.xml
└─ README.md


---

## Configuração das variáveis de ambiente

Crie um arquivo `.env` na raiz do projeto com o seguinte conteúdo:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=grid
DB_USERNAME=postgres
DB_PASSWORD= # sua senha
API_SECRET= # sua chave secreta
GRID_UI=http://localhost:5173
SPRING_PROFILES_ACTIVE=dev
````
## Executando o PostgreSQL com Docker

Na raiz do projeto, execute:
```bash
docker-compose up -d
````

## Executando o projeto

Compile e rode a aplicação com Maven:
```bash
./mvnw clean package
# Usa o profile 'dev'
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
mvn spring-boot:run
````

ou apenas:
```bash
# Usa o profile 'dev'
SPRING_PROFILES_ACTIVE=dev mvn spring-boot:run
mvn spring-boot:run
````

Ao iniciar, o Spring Boot irá:

Conectar ao PostgreSQL definido no .env

Executar todas as migrations do Flyway automaticamente

A aplicação estará rodando por padrão em:
http://localhost:8081


