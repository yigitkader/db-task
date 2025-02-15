# Deutsche Bank Work

A Spring Boot application for managing and retrieving facts with URL shortening capabilities.

## Table of Contents
- [Prerequisites](#prerequisites)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
  - [Local Development](#local-development)
  - [Docker Deployment](#docker-deployment)
  - [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
  - [Public APIs](#public-apis)
  - [Admin APIs](#admin-apis)
- [Development](#development)
- [Contributing](#contributing)

## Prerequisites

- JDK 17 or higher
- Gradle 7.x or higher
- Docker (optional)
- Make

## Technology Stack

- Kotlin
- Spring Boot 3.x
- Gradle
- Docker
- Make (for build automation)

## Getting Started

### Local Development

For local development without Docker, use these commands:

```bash
# Build the application
make build

# Run tests
make test

# Run in development mode with hot reload
make local-dev

# Run in production mode
make local-run

# Stop the application
make local-stop

# Clean build artifacts
make clean
```

### Docker Deployment

For running with Docker:

```bash
# Build Docker image
make docker-build

# Run with Docker
make docker-run

# Stop Docker container
make docker-stop
```

### Environment Variables

The following environment variables can be configured:

```bash
ENV            # Environment profile (default: dev)
JAVA_OPTS     # JVM options (default: -Xmx512m -Xms256m)
SERVER_PORT   # Server port (default: 8080)
```

Example usage with environment variables:
```bash
ENV=prod SERVER_PORT=9000 make local-run
# or
ENV=prod SERVER_PORT=9000 make docker-run
```

## API Documentation

### Public APIs

#### Facts Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/v1/facts` | Create new fact with shortened URL |
| GET | `/v1/facts/{shortenedUrl}` | Retrieve fact by shortened URL |
| GET | `/v1/facts/` | List all facts |

Example request for creating a fact:
```bash
curl -X POST http://localhost:8080/v1/facts \
  -H "Content-Type: application/json"
```

### Admin APIs

Admin APIs require authentication using the `X-Client-Secret` header.

#### Authentication

Add the following header to all admin API requests:
```
X-Client-Secret: admin-secret-1
```

#### Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/v1/admin/statistics/cache` | Retrieve system cache statistics |
| GET | `/v1/admin/statistics` | Get access statistics |

Example admin API request:
```bash
curl -X GET http://localhost:8080/v1/admin/statistics \
  -H "X-Client-Secret: admin-secret-1"
```

## Development

### Running Tests

To run all tests:
```bash
make test
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request
