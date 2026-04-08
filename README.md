# Zairo — Financial Workspace API

> A backend REST API for teams that need clarity — not chaos — around money.

Most finance tools either lock everyone out or let everyone in. Zairo does both — granularly. A viewer can see the numbers. An analyst can pull reports. Only admins touch the data.

Built with Spring Boot 3, JWT auth, and PostgreSQL on Neon.

---

## Why it's built this way

**Roles over flags** — 4 roles map to real team structures, not arbitrary booleans.

**JWT, not sessions** — Stateless auth scales horizontally without sticky sessions.

**PDF export as a first-class feature** — Finance teams live in reports. Export isn't an afterthought.

**Neon for the database** — Serverless Postgres so you get a real DB without managing infra.

---

## Tech Stack

| Layer     | Technology                  |
|-----------|-----------------------------|
| Language  | Java 21                     |
| Framework | Spring Boot 3.x             |
| Security  | Spring Security + JWT       |
| Database  | PostgreSQL (hosted on Neon) |
| ORM       | Spring Data JPA / Hibernate |
| Docs      | Swagger / OpenAPI 3         |
| Build     | Maven                       |

---

## Who can do what

| Action                    | Viewer | Analyst | Admin | Organizer |
|---------------------------|:------:|:-------:|:-----:|:---------:|
| View & filter transactions | ✓      | ✓       | ✓     | ✓         |
| View net position          | ✓      | ✓       | ✓     | ✓         |
| Export PDF report          |        | ✓       | ✓     | ✓         |
| Create / edit / delete     |        |         | ✓     | ✓         |
| Manage workspace users     |        |         | ✓     | ✓         |

---

## Get running in 3 steps

### 1. Clone & configure

```bash
git clone https://github.com/Aakashch-code/zairo-api
cd zairo
```

Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://<your-db-host>/neondb
spring.datasource.username=<your-db-username>
spring.datasource.password=<your-db-password>

application.security.jwt.secret-key=<your-secret-key-at-least-32-chars>
application.security.jwt.expiration=86400000

server.port=8085
```

### 2. Start the server

```bash
mvn spring-boot:run
```

Runs on `http://localhost:8085`. Hibernate auto-creates the schema on first run.

### 3. Explore the API

Open Swagger UI: `http://localhost:8085/swagger-ui/index.html`

Login → grab the JWT → authenticate every subsequent request:

```
Authorization: Bearer <your-token>
```

---

## Core endpoints

```
POST   /api/auth/register              Register a new user
POST   /api/auth/login                 Login → receive JWT

GET    /api/transactions               Paginated list (all roles)
GET    /api/transactions/net           Income vs expenses (all roles)
POST   /api/transactions/filter        Filter by date, category, amount (all roles)
GET    /api/transactions/pdf           Export report (analyst+)
POST   /api/transactions               Create transaction (admin/organizer)
PUT    /api/transactions/{id}          Update transaction (admin/organizer)
DELETE /api/transactions/{id}          Delete transaction (admin/organizer)

GET    /api/auth/workspace/users       List workspace users (admin/organizer)
PUT    /api/auth/{userId}              Update user credentials (admin/organizer)
DELETE /api/auth/{userId}              Remove user (admin/organizer)
```

### Example: Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "yourpassword"
}
```

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6..."
}
```

### Example: Create a transaction

```http
POST /api/transactions
Authorization: Bearer <your-token>
Content-Type: application/json

{
  "description": "Office rent",
  "amount": 1500.00,
  "type": "EXPENSE",
  "categoryId": 3,
  "date": "2025-04-01"
}
```

---

## Project structure

```
src/
├── main/
│   ├── java/org/example/zairo/
│   │   ├── authentication/
│   │   │   ├── api/            # Auth controllers
│   │   │   ├── application/    # DTOs, services
│   │   │   └── domain/         # User model
│   │   └── transaction/
│   │       ├── api/            # Transaction controllers
│   │       ├── application/    # DTOs, services, filters
│   │       └── infrastructure/ # PDF export
│   └── resources/
│       └── application.properties
```

---

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL database — local or cloud ([Neon](https://neon.tech) recommended)

---

## License

Open for learning and portfolio purposes.

*Built by [Aakash Chauhan](https://github.com/Aakashch-code)*