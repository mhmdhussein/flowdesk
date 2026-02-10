# FlowDesk – Project & Ticket Management API
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.x-brightgreen)
![Database](https://img.shields.io/badge/Database-PostgreSQL-blue)
![API](https://img.shields.io/badge/API-REST-success)
![Docs](https://img.shields.io/badge/Docs-Swagger-informational)

FlowDesk is a **backend REST API** built with **Spring Boot and PostgreSQL**, focused on **project-based authorization**, **ticket workflows**, and **real-world backend patterns**.

The project emphasizes **clarity, correctness, and maintainability** over framework magic or shortcuts.

---

## ✨ Key Features

- JWT-based authentication (register / login)
- Role-ready security model (`ADMIN`, `MANAGER`, `MEMBER`)
- Project-based authorization (membership-driven access)
- Ticket lifecycle workflow:
    - `OPEN → IN_PROGRESS → DONE`
- Filtering and pagination for ticket listing
- Soft deletes for projects and tickets
- Flyway-managed database schema
- Docker Compose for local development
- Swagger / OpenAPI documentation with JWT support

---

## 🧱 Architecture Overview

FlowDesk follows a **layered, feature-oriented structure**, keeping responsibilities explicit and easy to reason about.

```
src/main/java/com/flowdesk/flowdesk
├── api        → API utilities, error handling
├── auth       → Authentication endpoints and DTOs
├── config     → Jackson and OpenAPI configuration
├── project    → Project domain, membership, authorization
├── ticket     → Ticket domain, workflow, pagination
├── security   → JWT, filters, security configuration
├── user       → User and role model
```

---

### Design principles

- Controllers expose DTOs only
- Authorization is enforced at the domain boundary
- No hard deletes, data is preserved via soft deletes
- Database schema is owned by Flyway, not Hibernate
- Explicit logic preferred over implicit framework behavior

---

## 🔐 Authentication & Authorization

FlowDesk uses **JWT Bearer authentication**.

Authorization is **project-based**, not just role-based:

- A user must be a **member of a project** to access it
- Ticket access is always scoped to its parent project
- Roles are present and ready, but project membership is the primary rule

This mirrors real-world collaboration systems where access depends on *context*, not only global roles.

---

## 🧾 Ticket Workflow

Tickets follow a strict workflow:

OPEN → IN_PROGRESS → DONE

Invalid transitions are rejected at the API level.
This ensures consistent state transitions and avoids accidental misuse.

---

## 🗑️ Soft Deletes

Projects and tickets are **soft-deleted** using a `deleted_at` timestamp.

- Deleted records are excluded from queries by default
- Data remains in the database for audit or recovery
- No hard deletes are performed in v0.1.0

---

## 🚀 Running the Project Locally

### Prerequisites

- Java 17+
- Docker
- Docker Compose

### Build

```bash
./mvnw package -DskipTests
```

### Run

```bash
docker compose up --build
```

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

---

## 📖 API Documentation (Swagger)

Once the application is running, Swagger UI is available at:

http://localhost:8080/swagger-ui.html

The documentation supports JWT authentication directly from the UI.

---

## 📦 Version

**v0.1.0** – Initial MVP release

This version focuses on:
- Core domain correctness
- Authorization rules
- Workflow enforcement
- API clarity

---

## 🎯 Why This Project Exists

FlowDesk was built as a learning and demonstration project to showcase:

- Realistic backend authorization patterns
- Clean Spring Boot API design
- Practical tradeoffs in backend engineering
- A complete MVP lifecycle from schema to deployment

---

## 📄 License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT). You are free to use, modify, and distribute this project with proper attribution.

