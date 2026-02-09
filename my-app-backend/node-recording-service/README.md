# Node Recording Service

A high-performance Node.js microservice optimized for real-time recording operations in the my-app-backend monorepo.

## Overview

This service provides:
- RESTful API for recording operations
- WebSocket support for real-time streaming
- PostgreSQL integration for data persistence
- Redis caching for performance
- JWT-based authentication
- Comprehensive error handling and logging

## Prerequisites

- Node.js 18+ (LTS recommended)
- npm 9+
- PostgreSQL 12+
- Redis 6+

## Project Structure

```
src/
├── index.js                    # Application entry point
├── routes/                     # Route definitions
│   ├── health.js              # Health check routes
│   ├── recordings.js          # Recording endpoints
│   └── index.js               # Route aggregator
├── controllers/                # Request handlers
│   ├── recordingController.js
│   └── healthController.js
├── services/                   # Business logic
│   ├── recordingService.js
│   ├── storageService.js
│   └── cacheService.js
├── middlewares/                # Express middleware
│   ├── authMiddleware.js      # JWT authentication
│   ├── errorHandler.js        # Error handling
│   ├── logger.js              # Request logging
│   └── validation.js          # Input validation
├── config/                     # Configuration
│   ├── database.js            # PostgreSQL config
│   ├── redis.js               # Redis config
│   └── constants.js           # Application constants
└── utils/                      # Utility functions
    └── logger.js              # Winston logger setup
```

## Getting Started

### Installation

```bash
npm install
```

### Configuration

Copy `.env.example` to `.env` and update with your values:

```bash
cp .env.example .env
```

### Run Locally

```bash
# Development with auto-reload
npm run dev

# Production
npm start
```

The service will start on `http://localhost:3000`

### Run with Docker

```bash
docker build -t node-recording-service .
docker run -p 3000:3000 \
  -e DATABASE_URL=postgresql://appuser:apppassword@host.docker.internal:5432/myapp_db \
  -e REDIS_URL=redis://host.docker.internal:6379 \
  node-recording-service
```

Or use Docker Compose from the root directory:

```bash
docker-compose up node-recording-service
```

## API Endpoints

### Health Check

```bash
GET /health
```

Response:
```json
{
  "status": "healthy",
  "timestamp": "2024-02-09T10:30:00Z",
  "uptime": 3600
}
```

### Recording Operations

```bash
# Start recording
POST /api/v1/recordings/start
Content-Type: application/json

{
  "sessionId": "uuid",
  "config": {
    "quality": "high",
    "format": "mp4"
  }
}

# Stop recording
POST /api/v1/recordings/:recordingId/stop

# Get recording status
GET /api/v1/recordings/:recordingId/status

# List all recordings
GET /api/v1/recordings?page=1&limit=20
```

## Configuration Files

### config/database.js
PostgreSQL connection pool configuration using `pg` library.

### config/redis.js
Redis client initialization for caching and session management.

### config/constants.js
Application-wide constants and configuration values.

## Middleware

### authMiddleware.js
- JWT token validation
- User context extraction
- Request authentication

### errorHandler.js
- Global error handling
- Status code mapping
- Error response formatting

### logger.js
- HTTP request logging via Morgan
- Detailed operation logging

### validation.js
- Input validation using Joi
- Schema validation for requests

## Services

### recordingService.js
- Start/stop recording operations
- Recording state management
- Metadata handling

### storageService.js
- File system operations
- Temporary storage management
- Cleanup operations

### cacheService.js
- Redis operations
- Cache invalidation
- Session management

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `NODE_ENV` | Environment mode | `development` |
| `PORT` | Server port | `3000` |
| `DATABASE_URL` | PostgreSQL connection string | Required |
| `REDIS_URL` | Redis connection string | Required |
| `JWT_SECRET` | JWT signing secret | Required |
| `LOG_LEVEL` | Logging level | `info` |

## Testing

```bash
# Run all tests
npm test

# Run tests in watch mode
npm run test:watch

# Run with coverage
npm test -- --coverage
```

## Code Quality

```bash
# Lint code
npm run lint

# Fix linting issues
npm run lint:fix

# Format code
npm run format
```

## Performance Features

- **Streaming**: Efficient chunk-based streaming for large files
- **Caching**: Redis-backed caching for frequently accessed data
- **Connection Pooling**: PostgreSQL connection pooling for optimal database access
- **Compression**: Built-in gzip compression for responses
- **Rate Limiting**: Configurable rate limiting per endpoint

## Logging

Logs are handled by Winston and include:
- Console output (development)
- File-based logging (production)
- Structured JSON logging
- Request/response tracking

## Database Schema

The service expects the following PostgreSQL tables:

```sql
CREATE TABLE recordings (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  session_id UUID NOT NULL,
  user_id UUID NOT NULL,
  status VARCHAR(50) NOT NULL,
  duration INTEGER,
  storage_path VARCHAR(500),
  created_at TIMESTAMP DEFAULT NOW(),
  updated_at TIMESTAMP DEFAULT NOW(),
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_recordings_session_id ON recordings(session_id);
CREATE INDEX idx_recordings_user_id ON recordings(user_id);
```

## Deployment

### Production Checklist

1. Set `NODE_ENV=production`
2. Configure strong `JWT_SECRET`
3. Enable HTTPS
4. Set up database backups
5. Configure monitoring/alerting
6. Enable rate limiting
7. Set up log aggregation

### Scaling

- Horizontal scaling: Use Docker/Kubernetes with load balancer
- Vertical scaling: Increase Node.js heap size via `--max-old-space-size`
- Database optimization: Add indexes, enable connection pooling
- Cache optimization: Implement Redis cluster for distributed caching

## Troubleshooting

### Port 3000 already in use
```bash
lsof -i :3000
kill -9 <PID>
```

### Database connection refused
- Verify PostgreSQL is running
- Check `DATABASE_URL` connection string
- Ensure database user has proper permissions

### Redis connection issues
- Verify Redis is running on configured port
- Check `REDIS_URL` is correct
- Monitor Redis memory usage

## Contributing

1. Create a feature branch
2. Follow the project structure
3. Write tests for new functionality
4. Run linting and tests before committing
5. Submit pull request

## Security

- Always use HTTPS in production
- Rotate JWT secrets regularly
- Validate all inputs
- Use environment variables for secrets
- Keep dependencies updated: `npm audit`

## Monitoring

Monitor these key metrics:
- Request latency (p50, p95, p99)
- Error rate
- Database connection pool usage
- Redis memory usage
- Recording success rate

## License

See LICENSE file in the root directory.
