# FACK Backend

Spring Boot 3.1.5 backend for course management and enrollment. The API is served under the `/api` context path and uses MySQL with JWT-based authentication.

## Prerequisites

- Java 17
- Maven 3.8+
- MySQL 8.0+

## Project Structure

```
src/
├── main/
│   ├── java/com/app/
│   │   ├── main.java                    # Spring Boot entry point
│   │   ├── controller/                  # REST API endpoints
│   │   ├── service/                     # Business logic layer
│   │   ├── repository/                  # Data access layer (JPA)
│   │   ├── entity/                      # JPA entity classes
│   │   ├── dto/                         # Request/response models
│   │   ├── config/                      # Spring configuration
│   │   └── security/                    # JWT auth and filters
│   └── resources/
│       ├── application.properties       # Main configuration
│       └── fack_db.sql                  # Optional SQL seed
└── test/
    └── java/com/app/                    # Tests
```

## Setup

1. Create a MySQL database named `fack_db`.
2. Update [src/main/resources/application.properties](src/main/resources/application.properties) with your local credentials.
3. (Optional) Run the SQL seed in [src/main/resources/fack_db.sql](src/main/resources/fack_db.sql).

## Run Locally

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080/api`.

## Run with Docker Compose

```bash
docker-compose up --build
```

Note: The compose file currently points the app to `myapp_db`. Update the `SPRING_DATASOURCE_URL` to `fack_db` or adjust your database name to match.

## Configuration

Key properties in [src/main/resources/application.properties](src/main/resources/application.properties):

```
server.port=8080
server.servlet.context-path=/api

spring.datasource.url=jdbc:mysql://localhost:3306/fack_db
spring.datasource.username=fack_admin
spring.datasource.password=Admin@123456789

security.jwt.secret=change_me_to_32+_chars_minimum_secret_key
security.jwt.expiration-minutes=15
```

## Health Check

```bash
curl http://localhost:8080/api/actuator/health
```

## Tests

```bash
mvn test
```
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
