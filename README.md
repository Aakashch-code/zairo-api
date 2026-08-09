# Zairo — Financial Workspace API

A backend REST API for teams that need clarity around financial data. Zairo provides granular role-based access control, allowing viewers to see numbers, analysts to pull reports, and admins to manage data.

Built with Spring Boot 3, JWT authentication, and PostgreSQL on Neon.

---

## Why This Design

**Role-based access** — Four roles map to real team structures, not arbitrary permission flags.

**Stateless authentication** — JWT tokens enable horizontal scaling without sticky sessions.

**PDF exports by default** — Finance teams work with reports. Export is a first-class feature, not an afterthought.

**Serverless database** — Neon PostgreSQL eliminates infrastructure management while maintaining a production-grade relational database.

---

## Tech Stack

| Layer | Technology |
|-------|------------|
| Language | Java 21 |
| Framework | Spring Boot 3.x |
| Security | Spring Security + JWT |
| Database | PostgreSQL (Neon) |
| ORM | Spring Data JPA / Hibernate |
| Documentation | Swagger / OpenAPI 3 |
| Build Tool | Maven |

---

## Role Permissions

| Action | Viewer | Analyst | Admin | Organizer |
|--------|:------:|:-------:|:-----:|:---------:|
| View and filter transactions | X | X | X | X |
| View net position | X | X | X | X |
| Export PDF reports | | X | X | X |
| Create, edit, delete records | | | X | X |
| Manage workspace users | | | X | X |

---

## Quick Start

### 1. Clone and configure

```bash
git clone https://github.com/Aakashch-code/zairo-api
cd zairo
```

Update `src/main/resources/application.properties`:

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

The server runs on `http://localhost:8085`. Hibernate auto-creates the database schema on first run.

### 3. Explore the API

Open Swagger UI at `http://localhost:8085/swagger-ui/index.html`

Log in, retrieve your JWT token, and include it in subsequent requests:

```
Authorization: Bearer <your-token>
```

---

## API Endpoints

### Authentication

```
POST   /api/auth/register              Register a new user
POST   /api/auth/login                 Login and receive JWT token
```

### Transactions

```
GET    /api/transactions               List transactions (paginated, all roles)
GET    /api/transactions/net           View net income vs expenses (all roles)
POST   /api/transactions/filter        Filter by date, category, amount (all roles)
GET    /api/transactions/pdf           Export financial report (analyst+)
POST   /api/transactions               Create transaction (admin/organizer)
PUT    /api/transactions/{id}          Update transaction (admin/organizer)
DELETE /api/transactions/{id}          Delete transaction (admin/organizer)
```

### User Management

```
GET    /api/auth/workspace/users       List workspace members (admin/organizer)
PUT    /api/auth/{userId}              Update user credentials (admin/organizer)
DELETE /api/auth/{userId}              Remove user (admin/organizer)
```

---

## Request Examples

### Login

```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "yourpassword"
}
```

Response:

```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### Create a transaction

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

## Project Structure

```
src/
├── main/
│   ├── java/org/example/zairo/
│   │   ├── authentication/
│   │   │   ├── api/                # Auth controllers
│   │   │   ├── application/        # DTOs, services
│   │   │   └── domain/             # User entity
│   │   └── transaction/
│   │       ├── api/                # Transaction controllers
│   │       ├── application/        # DTOs, services, filtering logic
│   │       └── infrastructure/     # PDF export
│   └── resources/
│       └── application.properties
```

---

## Prerequisites

- Java 21 or later
- Maven 3.8 or later
- PostgreSQL database (local or cloud instance via Neon)

---

## Configuration

All configuration is managed via `application.properties`. Key settings:

- `spring.datasource.url` — PostgreSQL connection string
- `application.security.jwt.secret-key` — JWT signing key (minimum 32 characters)
- `application.security.jwt.expiration` — Token expiration in milliseconds
- `server.port` — Server port (default 8085)

---

## License

Open for learning and portfolio use.

Built by Aakash Chauhan — [GitHub](https://github.com/Aakashch-code)