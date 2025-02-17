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
- H2 DB

## Getting Started

### About Database

- We use in memory H2 database. database will work when you run app
- ```Address: localhost:8080/h2-console```

Simply run app:

```bash
make local-run
```

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

| Method | Endpoint                            | Description                                                                               |
|--------|-------------------------------------|-------------------------------------------------------------------------------------------|
| POST   | `/v1/facts`                         | Fetches a random fact from the Useless Facts API, stores it, and returns a shortened URL. |
| GET    | `/v1/facts/{shortenedUrl}`          | Returns the cached fact and increments the access count.                                  |
| GET    | `/v1/facts/{shortenedUrl}/redirect` | Redirects to the original fact and increments the access count.                           |
| GET    | `/v1/facts/`                        | Returns all cached facts and does not increment the access count.                         |

Example requests:

1. Create new fact:

```bash
curl -X POST http://localhost:8080/facts \
  -H "Content-Type: application/json"
```

2. Get fact by shortened URL:

```bash
curl -X GET http://localhost:8080/facts/ABC123 \
  -H "Content-Type: application/json"
```

3. Get fact by shortened URL and redirect(try on browser):

```bash
curl -X GET http://localhost:8080/facts/ABC123/redirect \
  -H "Content-Type: application/json"
```

4. Get all facts:

```bash
curl -X GET http://localhost:8080/facts \
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

| Method | Endpoint                  | Description                                        |
|--------|---------------------------|----------------------------------------------------|
| GET    | `/admin/statistics/cache` | Retrieve system cache statistics                   |
| GET    | `/admin/statistics`       | Provides access statistics for all shortened URLs. |

Example requests:

1. Get cache statistics:

```bash
curl -X GET http://localhost:8080/admin/statistics/cache \
  -H "Content-Type: application/json" \
  -H "X-Client-Secret: admin-secret-1"
```

2. Get access statistics:

```bash
curl -X GET http://localhost:8080/admin/statistics \
  -H "Content-Type: application/json" \
  -H "X-Client-Secret: admin-secret-1"
```

## Development

### Running Tests

To run all tests:

```bash
make test
```