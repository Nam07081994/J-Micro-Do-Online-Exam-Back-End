# DO ONLINE EXAM 

## SETUP GUIDELINE

### Rules
Gitflow
- Branch name format [type]/[description]
- Type: `feature, fix, dosc, style, refactor, test`
- Commit message format [type]/[description]
- Type: `feature, fix, dosc, style, refactor, test`
- Description: less than 50 characters.
- Example: feature/add exam service

Code styles
- Using `spotless` for reformat source code.

### Libraries & Documents

- Spring Cloud Service Registration (Eureka Server).
- PostgreSQL database
- Spring Data JPA
- Redis cache.
- Message Broker (RabbitMQ).

### Running project
Swagger
- OpenAPI endpoint: http://localhost:8763/swagger-ui.html

Database & Redis (If you are going run with Docker skip the step).
- Run `docker compose up -d postgresDB polar-redis`

Run Application
- Using docker:
  - Run `docker compose up -d`
- Normal:
  - Run all the services in the repository (Eureka server must be start first).
  
### Additional