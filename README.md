# Apex Portfolio Service

Manages user portfolios and CRUD operations for stocks in a portfolio. Consumes order-execution events from Kafka to update owned positions. Built with Spring Boot 3.4 and Java 21.

## Tech Stack

- **Java 21** · **Spring Boot 3.4** · **Spring Data JPA** · **PostgreSQL** · **Flyway** · **Kafka** · **Lombok**

## Prerequisites

- **Java 21+**
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

### Application defaults

- **Server port:** `8080`
- **Datasource:** `jdbc:postgresql://${DB_HOST}/${DB_NAME}?currentSchema=portfolio&sslmode=require&channel_binding=require`
- **HikariCP:** max pool size `5`, connection timeout `30000` ms
- **JPA:** schema `portfolio`, `ddl-auto: validate`, PostgreSQL dialect
- **Flyway:** schema `portfolio`, `baseline-on-migrate: true`, `create-schemas: false`
- **Kafka consumer group:** `execution-group`

## Running the Service

**Local (Maven):**

```bash
./mvnw spring-boot:run
```

**Docker Compose (with Kafka):**

The repo includes a `docker-compose.yml` that runs Kafka and this service (and other Apex services). The portfolio service is exposed on host port **8081** (mapped to container 8080).

```bash
docker-compose up -d
```

Ensure a `.env` file exists with `DB_HOST`, `DB_NAME`, `DB_USERNAME`, and `DB_PASSWORD`. For Compose, Kafka is set via `SPRING_KAFKA_BOOTSTRAP_SERVERS=apex-kafka:9092`.

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

- **Topic:** `order.executed.event`
- **Consumer group:** `execution-group`
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
