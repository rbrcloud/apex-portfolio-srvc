# Apex Portfolio Service

Manages user portfolios and CRUD operations for stocks in a portfolio. Consumes order-execution events from Kafka to update owned positions. Built with Spring Boot 3.4 (3.4.2) and Java 21.

## Tech Stack

- **Java 21** · **Spring Boot 3.4** · **Spring Data JPA** · **PostgreSQL** · **Flyway** · **Kafka** · **Lombok**

## Prerequisites

- **Java 21+**
- **GitHub Packages access** (this service depends on `com.rbrcloud.apex:apex-shared-lib`, resolved from GitHub Packages)
- **PostgreSQL** with schema `portfolio` (schema must exist; Flyway uses `portfolio` with `baseline-on-migrate`)
- **Kafka** (optional for run; required for consuming `order.executed.event` and updating owned stocks)

## Configuration

Configuration is driven by `application.yml` and can be overridden via environment variables or an optional `.env` file (loaded as `.properties`).

### Environment variables

| Variable | Description | Default |
|----------|-------------|--------|
| `DB_HOST` | PostgreSQL host | — |
| `DB_NAME` | Database name | — |
| `DB_USERNAME` | Database user | — |
| `DB_PASSWORD` | Database password | — |
| `KAFKA_BOOTSTRAP_SERVERS` | Kafka broker list | `localhost:9092` |

### Notes on `.env`

- `.env` is loaded automatically at startup via `spring.config.import=optional:file:.env[.properties]`.
- Do not commit real credentials in `.env`. Prefer a local-only `.env` file (gitignored) or environment variables in your runtime/CI.

### Application defaults

- **Server port:** `8080`
- **Datasource:** `jdbc:postgresql://${DB_HOST}/${DB_NAME}?currentSchema=portfolio&sslmode=require&channel_binding=require`
- **HikariCP:** max pool size `5`, connection timeout `30000` ms
- **JPA:** schema `portfolio`, `ddl-auto: validate`, PostgreSQL dialect
- **Flyway:** schema `portfolio`, `baseline-on-migrate: true`, `create-schemas: false`
- **Kafka consumer group:** `execution-group` (also referenced from shared `KafkaConstants`)

### OpenAPI / Swagger UI

- **Swagger UI:** `GET /swagger-ui.html`
- **OpenAPI spec:** `GET /v3/api-docs`

## Running the Service

**Local (Maven):**

```bash
./mvnw spring-boot:run
```

If Maven dependency resolution fails for `apex-shared-lib`, configure GitHub Packages auth for Maven (commonly via `~/.m2/settings.xml`) or use the Docker build (below) which passes credentials as build args.

**Docker (build + run):**

This repository’s `Dockerfile` is a multi-stage build that:

- builds using `ghcr.io/rbrcloud/apex-build-base`
- requires GitHub Packages credentials to download internal dependencies

```bash
docker build \
  --build-arg GITHUB_USERNAME="$GITHUB_USERNAME" \
  --build-arg GITHUB_TOKEN="$GITHUB_TOKEN" \
  -t apex-portfolio-srvc:local .

docker run --rm -p 8080:8080 \
  -e DB_HOST="$DB_HOST" \
  -e DB_NAME="$DB_NAME" \
  -e DB_USERNAME="$DB_USERNAME" \
  -e DB_PASSWORD="$DB_PASSWORD" \
  -e KAFKA_BOOTSTRAP_SERVERS="${KAFKA_BOOTSTRAP_SERVERS:-localhost:9092}" \
  apex-portfolio-srvc:local
```

**Docker Compose (with Kafka):**

This repository does not include a `docker-compose.yml`. In the broader Apex workspace, the portfolio service is typically started from the top-level compose (where Kafka runs) and `KAFKA_BOOTSTRAP_SERVERS` is pointed at the compose broker (for example `apex-kafka:9092`).

## CI/CD (Jenkins)

`Jenkinsfile` uses the shared Jenkins library (`apex-shared-library`) and delegates to `apexPipeline(serviceName: "apex-portfolio-srvc")`. Any Jenkins build agent must provide whatever credentials that shared pipeline expects (including access to GHCR and GitHub Packages for internal dependencies).

## API

Base path for portfolios: **`/api/v1/portfolios`**

### POST – Create portfolio

Creates a new portfolio for a user. If it is the user’s first portfolio, it is set as the default regardless of `isDefault`.

**Endpoint:** `POST /api/v1/portfolios`

**Request headers:** `Content-Type: application/json`

**Request body:**

```json
{
  "userId": 1001,
  "name": "My Growth Portfolio",
  "isDefault": false
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `userId` | number | Yes | Owner of the portfolio |
| `name` | string | Yes | Portfolio name (max 50 chars) |
| `isDefault` | boolean | No | Whether this is the default portfolio (default: `false`; first portfolio for user is forced to `true`) |

**Example – minimal payload (only required fields):**

```json
{
  "userId": 1001,
  "name": "Retirement"
}
```

**Example – full payload:**

```json
{
  "userId": 42,
  "name": "Tech Stocks",
  "isDefault": true
}
```

**Success response:** `201 Created`

```json
{
  "id": 1,
  "userId": 1001,
  "name": "My Growth Portfolio",
  "isDefault": false
}
```

**cURL example:**

```bash
curl -X POST http://localhost:8080/api/v1/portfolios \
  -H "Content-Type: application/json" \
  -d '{"userId": 1001, "name": "My Growth Portfolio", "isDefault": false}'
```

---

### GET – List all portfolios

**Endpoint:** `GET /api/v1/portfolios`

**Success response:** `200 OK` – array of all portfolios.

---

### GET – List portfolios by user

**Endpoint:** `GET /api/v1/portfolios/{user_id}`

**Path parameter:** `user_id` (number) – user ID.

**Success response:** `200 OK` – array of portfolios for that user.

---

## Health / DB check

**Endpoint:** `GET /api/v1/portfolio/dbcheck/connection-info`

Returns database connection info: database name, user, current schema, PostgreSQL version, and client IP. Useful for verifying connectivity and environment.

---

## Kafka integration

The service consumes **order execution events** to keep owned stocks in sync with executed orders.

- **Topic:** `order.executed.event` (via shared `com.rbrcloud.apex.common.constants.KafkaConstants.ORDER_EXECUTED_TOPIC`)
- **Consumer group:** `execution-group` (via shared `com.rbrcloud.apex.common.constants.KafkaConstants.EXECUTION_GROUP`, defaulted in `application.yml`)
- **Payload type:** `OrderExecutedEvent` (from `com.rbrcloud.orderexecution.dto`)

On each event, the service:

1. Resolves the user’s **default portfolio**.
2. Looks up existing **owned stock** for that user and ticker (if any).
3. **BUY:** adds quantity or creates a new `OwnedStock` row; **SELL:** subtracts quantity.
4. Persists/updates the record in the `portfolio.owned_stocks` table.

Owned stocks are stored per user, portfolio, and ticker with `quantity` and `price`. Without Kafka (e.g. in tests), the service still runs; only this event-driven update is skipped.

## Data model (reference)

- **Portfolio:** `id`, `user_id`, `name`, `is_default` (table `portfolio.portfolio`).
- **OwnedStock:** `id`, `user_id`, `portfolio_id`, `ticker` (max 8 chars), `quantity`, `price` (table `portfolio.owned_stocks`).

## Testing

Unit and integration tests use **H2** in-memory with PostgreSQL compatibility and an initial schema from `src/test/resources/init-schema.sql`. Kafka is disabled in tests via `KafkaAutoConfiguration` exclusion.

```bash
./mvnw test
```

Tests include: `PortfolioControllerTest`, `PortfolioServiceTest`, `PortfolioServiceIT`, and `PortfolioSrvcApplicationTests`.
