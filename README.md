# Payment Auth Gateway

Java 21 / Spring Boot 3 card-payment authorization API with PostgreSQL, Redis, OAuth2 JWT resource-server security, idempotency, distributed rate limiting, Resilience4j, Flyway, Micrometer and Testcontainers.

## Architecture

`HTTP -> Security -> validation/rate limit -> AuthorizationService -> PostgreSQL + append-only events`

Redis is used for idempotency responses and Bucket4j distributed token buckets. The issuer adapter is deliberately simulated and wrapped with Resilience4j circuit breaker, retry and timeout policies.

## API

- `POST /v1/authorizations` — create an authorization. Requires `Idempotency-Key` and a JWT with `ROLE_PAYMENT_CLIENT`.
- `POST /v1/authorizations/{id}/capture` — capture an authorized payment.
- `POST /v1/authorizations/{id}/void` — void an authorized payment.
- `GET /v1/authorizations/{id}` — retrieve an authorization.
- `/swagger-ui.html` — OpenAPI UI.
- `/actuator/health`, `/actuator/prometheus` — operational endpoints.

Request PAN is validated but only its final four digits are retained. Full PAN is never logged or persisted. The API response is masked.

## Security decisions

OAuth2 resource-server JWT validation and method-level role checks protect payment endpoints. CSRF is disabled because this is a stateless bearer-token API. Security headers include CSP, frame denial and a no-referrer policy. Error responses intentionally avoid exception details.

For a real deployment, set `JWT_ISSUER_URI` to the organization's trusted identity provider. No issuer credentials, PANs, or other secrets are committed.

## Idempotency

`Idempotency-Key` is namespaced by authenticated client and stored in Redis for 24 hours. A replay returns the exact previously serialized authorization response without calling the issuer again.

## Rate limiting

Bucket4j uses its Lettuce Redis integration so buckets are shared across application instances. The default limit is 100 tokens per minute per authenticated client; configure `rate-limit.capacity` and `rate-limit.refill-tokens` as required.

## Resilience

The simulated issuer is decorated with Resilience4j retry, timeout and circuit-breaker policies. Amount `99999` intentionally simulates issuer failure for deterministic tests and local fault-injection.

## Local run

Prerequisites: JDK 21, Maven 3.9+, Docker.

```bash
docker compose up --build
```

Configure `JWT_ISSUER_URI` in the environment before starting the app. For tests:

```bash
mvn test
```

Integration tests use Testcontainers and therefore require a Docker daemon.

## CI

GitHub Actions runs the Maven build/test suite with PostgreSQL and Redis containers and runs OWASP Dependency-Check. The Dockerfile is a multi-stage build that runs the final JVM as a non-root user.

## Data model

`authorizations` contains payment state and only `pan_last4`. `authorization_events` is append-only from application code and records state transitions with JSON metadata. Flyway owns schema creation; Hibernate is configured with `ddl-auto=validate`.

## Scope / non-goals

This repository intentionally does not claim live issuer connectivity, real card-network processing, PCI certification, production traffic, user counts, or performance metrics. The issuer is a simulation used to exercise failure-handling behavior.
