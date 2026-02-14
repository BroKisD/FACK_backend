# Spring Main Service

A robust Spring Boot microservice for handling core business logic, data management, and API orchestration in the my-app-backend monorepo.

## Overview

This service is built with Spring Boot 3.1.5 and provides:
- RESTful API endpoints for business operations
- JPA/Hibernate ORM for database operations
- Spring Security with JWT authentication
- Health checks and monitoring via Spring Actuator

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+

## Project Structure

```
src/
├── main/
│   ├── java/com/app/
│   │   ├── Application.java              # Spring Boot entry point
│   │   ├── controller/                   # REST API endpoints
│   │   ├── service/                      # Business logic layer
│   │   ├── repository/                   # Data access layer (JPA)
│   │   ├── entity/                       # JPA entity classes
│   │   ├── dto/                          # Data Transfer Objects
│   │   ├── config/                       # Spring configuration beans
│   │   └── security/                     # Authentication & authorization
│   └── resources/
│       ├── application.yml               # Main configuration
│       ├── application-docker.yml        # Docker environment config
│       ├── application-dev.yml           # Development config
│       └── application-prod.yml          # Production config
└── test/
    └── java/com/app/                     # Unit and integration tests
```

## Getting Started

### Build

```bash
mvn clean install
```

### Run Locally

1. Ensure MySQL is running:
```bash
docker run --name mysql -e MYSQL_ROOT_PASSWORD=password -e MYSQL_DATABASE=myapp_db -d mysql:8.0
```

2. Run the application:
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080`

### Run with Docker

```bash
docker build -t spring-main-service .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:mysql://host.docker.internal:3306/myapp_db \
  -e SPRING_DATASOURCE_USERNAME=root \
  -e SPRING_DATASOURCE_PASSWORD=password \
  spring-main-service
```

Or use Docker Compose from the root directory:
```bash
docker-compose up spring-main-service
```

## Configuration

Configuration files are located in `src/main/resources/`:

- **application.yml**: Default configuration
- **application-docker.yml**: Docker environment overrides
- **application-dev.yml**: Development-specific settings
- **application-prod.yml**: Production-specific settings

### Key Environment Variables

```
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/myapp_db
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=password
SPRING_PROFILES_ACTIVE=dev|docker|prod
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

## API Documentation

### Health Check Endpoint

```bash
GET /actuator/health
```

### Application Info

```bash
GET /actuator/info
```

## Architecture

### Controller Layer (`controller/`)
- Handles HTTP requests and responses
- Input validation
- Exception handling

### Service Layer (`service/`)
- Business logic implementation
- Transaction management
- Caching logic

### Repository Layer (`repository/`)
- Database operations via Spring Data JPA
- Custom query methods
- Database abstraction

### Entity Layer (`entity/`)
- JPA entity models
- Database table mappings
- Relationships and constraints

### DTO Layer (`dto/`)
- Request/Response objects
- Data validation annotations
- API contract definitions

### Config Layer (`config/`)
- Spring beans configuration
- Database configuration
- Caching configuration
- Security configuration

### Security Layer (`security/`)
- JWT token generation and validation
- Authentication filters
- Authorization configurations

## Testing

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=UserServiceTest
```

## Monitoring

View application metrics and health:
```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/metrics
```

## Logging

Logs are output to console and can be configured via `application.yml`:

```yaml
logging:
  level:
    root: INFO
    com.app: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
```

## Database Migrations

Migrations can be managed using Flyway or Liquibase. Add the starter:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-flyway</artifactId>
</dependency>
```

Place migration scripts in `src/main/resources/db/migration/`

## Performance Optimization

- **Connection Pooling**: HikariCP for optimized database connections
- **Async Processing**: Spring async support for non-blocking operations

## Development Best Practices

1. Keep controllers thin - move logic to services
2. Use DTOs for external API contracts
3. Implement proper exception handling
4. Write unit tests for business logic
5. Use Spring transactions for data consistency
6. Log important operations for debugging

## Troubleshooting

### Port 8080 already in use
```bash
lsof -i :8080
kill -9 <PID>
```

### Database connection issues
- Verify MySQL is running on port 3306
- Check connection string in configuration
- Ensure database exists

## Contributing

1. Create a feature branch
2. Make changes following Spring Boot best practices
3. Write tests for new functionality
4. Ensure all tests pass: `mvn test`
5. Submit pull request

## License

See LICENSE file in the root directory.
