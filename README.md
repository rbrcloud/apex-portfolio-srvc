# Apex Portfolio Service

Manages user portfolios and CRUD operations for stocks in a portfolio. Built with Spring Boot 3.4 and Java 21.

## Tech Stack

- **Java 21** · **Spring Boot 3.4** · **Spring Data JPA** · **PostgreSQL** · **Flyway** · **Kafka**

## Prerequisites

- Java 21+
- PostgreSQL (schema: `portfolio`)
- Kafka (optional, for events)

## Configuration

Environment variables:

| Variable    | Description                |
|------------|----------------------------|
| `DB_HOST`  | PostgreSQL host            |
| `DB_NAME`  | Database name              |
| `DB_USERNAME` | Database user          |
| `DB_PASSWORD` | Database password      |

Default server port: **8080**.

## Running the Service

```bash
./mvnw spring-boot:run
```

## API

Base path: `/api/v1/portfolios`

### POST – Create portfolio

Creates a new portfolio for a user. If it is the user’s first portfolio, it is set as the default.

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

| Field      | Type    | Required | Description                                      |
|-----------|--------|----------|--------------------------------------------------|
| `userId`  | number | Yes      | Owner of the portfolio                           |
| `name`    | string | Yes      | Portfolio name (max 50 chars)                    |
| `isDefault` | boolean | No    | Whether this is the default portfolio (default: `false`; first portfolio for user is forced to `true`) |

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

**Success response:** `200 OK` – array of portfolios.

---

### GET – List portfolios by user

**Endpoint:** `GET /api/v1/portfolios/{user_id}`

**Path parameter:** `user_id` (number) – user ID.

**Success response:** `200 OK` – array of portfolios for that user.

---

## Health / DB check

**Endpoint:** `GET /api/v1/portfolio/dbcheck/connection-info`

Returns database connection info (database name, user, schema, version, client IP).
