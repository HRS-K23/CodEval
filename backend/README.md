# Calculator Service

Minimal Spring Boot calculator service for local-only testing.

## Endpoints

- `POST /api/v1/calculator/add`
- `POST /api/v1/calculator/subtract`
- `GET /actuator/health`

## Run locally

```bash
mvn spring-boot:run
```

The service binds to `127.0.0.1` on port `8080` by default.

## Test

```bash
mvn test
```