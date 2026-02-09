# My App Backend

A professional polyglot monorepo architecture for high-traffic web applications featuring Java Spring Boot and Node.js microservices.

## Architecture

This monorepo contains multiple backend services optimized for specific workloads:

- **spring-main-service**: Java Spring Boot application handling core business logic and API orchestration
- **node-recording-service**: Node.js service optimized for high-performance recording operations

## Prerequisites

- Docker & Docker Compose
- Java 17+ (for Spring Boot service)
- Node.js 18+ (for Node.js service)
- Maven 3.8+ (for Java service)

## Quick Start

### Using Docker Compose

```bash
docker-compose up -d
```

This will:
- Build and start the Spring Boot service on port 8080
- Build and start the Node.js recording service on port 3000
- Initialize required databases and dependencies

### Local Development

#### Spring Boot Service

```bash
cd spring-main-service
mvn clean install
mvn spring-boot:run
```

#### Node.js Service

```bash
cd node-recording-service
npm install
npm start
```

## Service Documentation

### Spring Main Service

- **Port**: 8080
- **Framework**: Spring Boot
- **Build Tool**: Maven
- **Database**: PostgreSQL (configured in docker-compose)

Key packages:
- `controller`: REST API endpoints
- `service`: Business logic and operations
- `repository`: Data access layer
- `entity`: JPA entities
- `dto`: Data transfer objects
- `config`: Spring configuration beans
- `security`: Authentication and authorization

See [spring-main-service/README.md](spring-main-service/README.md) for detailed documentation.

### Node Recording Service

- **Port**: 3000
- **Framework**: Express.js
- **Node Version**: 18+
- **Use Case**: High-performance recording operations

Key directories:
- `routes`: API route definitions
- `controllers`: Request handlers
- `services`: Business logic
- `middlewares`: Express middleware
- `config`: Configuration management

See [node-recording-service/README.md](node-recording-service/README.md) for detailed documentation.

## CI/CD Pipeline

GitHub Actions workflows are configured for independent service deployment:

- **Deploy Spring Service**: Triggered on changes to `spring-main-service/**`
- **Deploy Node Service**: Triggered on changes to `node-recording-service/**`

Workflows are located in `.github/workflows/`.

## Project Structure

```
my-app-backend/
├── spring-main-service/          # Java Spring Boot microservice
│   ├── src/
│   │   ├── main/java/com/app/
│   │   │   ├── controller/       # REST controllers
│   │   │   ├── service/          # Business logic
│   │   │   ├── repository/       # Data access
│   │   │   ├── entity/           # JPA entities
│   │   │   ├── dto/              # DTOs
│   │   │   ├── config/           # Spring config
│   │   │   └── security/         # Auth & security
│   │   ├── main/resources/       # Configuration files
│   │   └── test/java/com/app/    # Unit tests
│   ├── pom.xml                   # Maven configuration
│   ├── Dockerfile                # Container image
│   └── README.md
├── node-recording-service/       # Node.js microservice
│   ├── src/
│   │   ├── routes/               # Route definitions
│   │   ├── controllers/          # Request handlers
│   │   ├── services/             # Business logic
│   │   ├── middlewares/          # Middleware
│   │   ├── config/               # Configuration
│   │   └── index.js              # Entry point
│   ├── package.json              # NPM dependencies
│   ├── .env.example              # Environment template
│   ├── Dockerfile                # Container image
│   └── README.md
├── .github/workflows/            # CI/CD workflows
│   ├── deploy-spring-service.yml
│   └── deploy-node-service.yml
├── docker-compose.yml            # Local development orchestration
├── .gitignore                    # Git ignore rules
└── README.md                     # This file
```

## Environment Configuration

Each service has its own environment configuration:

- **Spring Service**: `spring-main-service/src/main/resources/application.yml`
- **Node Service**: `node-recording-service/.env`

## Deployment

### Docker Compose (Development)

```bash
docker-compose up -d
```

### Kubernetes (Production)

Each service contains a `Dockerfile` suitable for Kubernetes deployments. Helm charts or kustomize manifests can be added as needed.

## Monitoring & Logging

- Spring Boot: Actuator endpoints available at `/actuator`
- Node.js: Winston/Morgan logging configured
- Docker Compose: View logs with `docker-compose logs -f [service-name]`

## Contributing

1. Create a feature branch for changes to a specific service
2. Ensure CI/CD pipeline passes
3. Submit pull request with changes isolated to the relevant service

## License

See LICENSE file for details.

## Support

For issues or questions:
1. Check service-specific documentation
2. Review GitHub Actions logs for deployment issues
3. Contact the development team
