# Implementation Notes

## Scope

- Implemented the approved local-only Spring Boot calculator service.
- Kept the API limited to add, subtract, and health.
- No database, no cloud integration, and no extra infrastructure were added.

## Validation Behavior

- Inputs use Bean Validation to require both numbers.
- Malformed or missing input returns a sanitized `400` response.
- Unexpected failures return a sanitized `500` response.

## Local Runtime

- The app binds to `127.0.0.1`.
- Actuator health is exposed at `/actuator/health`.

## Config Changes

- Added `backend/src/main/resources/application.properties` to set the local bind address, default port, and actuator exposure.

## Dependency Additions

- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-actuator`
- `spring-boot-starter-test`