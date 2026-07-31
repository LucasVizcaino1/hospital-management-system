# 🏥 Hospital Management System — REST API

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.7-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21%2B-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?logo=swagger&logoColor=black)](https://swagger.io/)
[![Docker](https://img.shields.io/badge/Docker-PostgreSQL-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](#)

A REST API for the comprehensive management of a hospital system: people, patients, employees/physicians, medical specialties, and appointments. Built with **Spring Boot 4**, **Spring Security + JWT**, **PostgreSQL**, and documented with **OpenAPI / Swagger**.

> Developed as a backend portfolio project, with a strong focus on best practices: layered architecture, DTOs, centralized error handling, stateless JWT authentication, role-based authorization, and secrets kept out of the repository.


---

## 📑 Table of contents

- [Features](#-features)
- [Tech stack](#-tech-stack)
- [Domain model](#-domain-model)
- [Architecture & structure](#-architecture--structure)
- [Endpoints](#-endpoints)
- [Security (JWT + Roles)](#-security-jwt--roles)
- [Prerequisites](#-prerequisites)
- [Installation & run](#-installation--run)
- [Interactive documentation (Swagger)](#-interactive-documentation-swagger)
- [Testing the API](#-testing-the-api)
- [Loading sample data (seed)](#-loading-sample-data-seed)
- [Design decisions](#-design-decisions)
- [Roadmap](#-roadmap)
- [Author](#-author)

---

## ✨ Features

- 🔐 **Stateless authentication with JWT** (JJWT) and **BCrypt**-hashed passwords.
- 🛡️ **Role-based authorization** (`ADMIN`, `MEDIC`, `PATIENT`) using `@PreAuthorize`.
- 🧩 **Full CRUD** for 6 entities, with pagination, sorting, and filtering.
- 🔎 **Search & filters**: by state, by patient, by employee, by date range, and by free text.
- 📄 **OpenAPI 3 documentation** generated automatically (Swagger UI).
- 🐳 **Database in Docker** for a reproducible environment.
- 🗂️ **Manual entity ↔ DTO mapping** (separate request/response objects, including nested objects).
- ⚠️ **Centralized exception handling** (`@RestControllerAdvice`).
- 🔒 **Secrets kept out of Git** (environment variables / non-versioned local file).

---

## 🧰 Tech stack

| Category | Technology |
|---|---|
| Language | Java 21+ |
| Framework | Spring Boot 4.0.7 / Spring Framework 7 |
| Security | Spring Security 7 + JWT (JJWT 0.12.7) + BCrypt |
| Persistence | Spring Data JPA + Hibernate 7.2 |
| Database | PostgreSQL 17 (Docker) |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Documentation | Springdoc OpenAPI 2.8 (Swagger UI) |
| Utilities | Lombok |
| Build | Maven (Wrapper) |
| Containers | Docker |

---

## 🗃️ Domain model

```mermaid
erDiagram
    PERSON ||--|| USERS : "has credentials"
    PERSON ||--o| PATIENT : "is patient"
    PERSON ||--o| EMPLOYEE : "is employee"
    EMPLOYEE ||--o{ MEDICAL_SPECIALTY : "treats"
    SPECIALTY ||--o{ MEDICAL_SPECIALTY : "treated by"
    PATIENT ||--o{ APPOINTMENT : "requests"
    EMPLOYEE ||--o{ APPOINTMENT : "attends"

    PERSON {
        Long id PK
        String name
        String lastname
        String email
        State state
    }
    USERS {
        Long id PK
        String username
        String password
        Long person_id FK
    }
    PATIENT {
        Long id PK
        Rol rol
        State state
        Long person_id FK
    }
    EMPLOYEE {
        Long id PK
        Rol rol
        State state
        Long person_id FK
    }
    SPECIALTY {
        Long id PK
        String name
        State state
    }
    MEDICAL_SPECIALTY {
        Long id PK
        Long employee_id FK
        Long specialty_id FK
    }
    APPOINTMENT {
        Long id PK
        LocalDateTime date
        String reason
        State state
        Long patient_id FK
        Long employee_id FK
    }
```

**Enums**

- `State` → `ACTIVE`, `INACTIVE`, `PENDING`, `COMPLETED`, `CANCELLED`
- `Rol` → `PATIENT`, `MEDIC`, `ADMIN`

---

## 🏗️ Architecture & structure

Layered pattern **Controller → Service → Repository**, with input/output DTOs and manual mapping.

```
src/main/java/com/hospital_management_system/demo
├── config/          # Configuration classes (Web, Swagger, etc.)
├── controller/      # REST endpoints (@RestController)
├── dto/
│   ├── request/     # Input DTOs (validated)
│   └── response/    # Output DTOs
├── exception/       # Business exceptions + @RestControllerAdvice
├── model/           # JPA entities and enums
├── repository/      # Spring Data JPA repositories
├── security/        # JWT: JwtUtil, JwtAuthenticationFilter,
│                    #        CustomUserDetailsService, SecurityConfig
└── service/
    └── impl/        # Business logic
```

---

## 🔌 Endpoints

> Base URL: `http://localhost:8080`

| Resource | Method | Path | Description |
|---|---|---|---|
| **Auth** | POST | `/api/auth/login` | Login (returns JWT) |
| | POST | `/api/auth/register` | User registration |
| **Persons** | GET/POST | `/api/persons` | List (paginated) / create |
| | GET/PUT/DELETE | `/api/persons/{id}` | Get / update / delete |
| | GET | `/api/persons/state/{state}` | Filter by state |
| | GET | `/api/persons/search?name=` | Search by name |
| **Patients** | GET/POST | `/api/patients` | List / create |
| | GET/PUT/DELETE | `/api/patients/{id}` | Get / update / delete |
| | GET | `/api/patients/active` | Active patients |
| **Employees** | GET/POST | `/api/employees` | List / create |
| | GET/PUT/DELETE | `/api/employees/{id}` | Get / update / delete |
| | GET | `/api/employees/status/{state}` | Filter by state |
| **Specialties** | GET/POST | `/api/specialties` | List / create |
| | GET/PUT/DELETE | `/api/specialties/{id}` | Get / update / delete |
| | GET | `/api/specialties/name?name=` | Search by name |
| **Appointments** | GET/POST | `/api/appointments` | List / create |
| | GET/PUT/DELETE | `/api/appointments/{id}` | Get / update / delete |
| | GET | `/api/appointments/patient/{id}` | Appointments of a patient |
| | GET | `/api/appointments/employee/{id}` | Appointments of an employee |
| | GET | `/api/appointments/status?status=` | Filter by state |
| | GET | `/api/appointments/dates?startDate=&endDate=` | Date range |
| | GET | `/api/appointments/search?reason=` | Search by reason |

---

## 🔐 Security (JWT + Roles)

- **Authentication**: `POST /api/auth/login` validates credentials with `BCrypt` and returns a JWT signed with **HS256**.
- **JWT filter** (`JwtAuthenticationFilter`): intercepts every request, reads the `Authorization: Bearer <token>` header, validates the token, and loads the user into the `SecurityContext`.
- **Role resolution**: `CustomUserDetailsService` determines the role by checking whether the person is a patient or an employee.
- **Public routes**: `/api/auth/**` and the Swagger documentation.
- **Authorization**: by role using `@PreAuthorize` (`hasRole('ADMIN')`, `hasAnyRole('ADMIN','MEDIC')`, etc.).

**Permission matrix (example)**

| Action | PATIENT | MEDIC | ADMIN |
|---|:---:|:---:|:---:|
| Login | ✅ | ✅ | ✅ |
| Manage employees | ❌ | ❌ | ✅ |
| Create patient | ❌ | ✅ | ✅ |
| Create / view appointments | ✅ | ✅ | ✅ |
| View only their own appointments | ✅ | — | — |

---

## 📋 Prerequisites

- **JDK 21+**
- **Maven 3.9+** (or use the included `mvnw` wrapper)
- **Docker** (to run PostgreSQL)
- **Git**

---

## 🚀 Installation & run

### 1. Clone the repository

```bash
git clone https://github.com/LucasVizcaino1/hospital-management-system.git
cd hospital-management-system
```

### 2. Start PostgreSQL with Docker

```bash
docker run --name hospital-db \
  -e POSTGRES_DB=hospital_db \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=postgres \
  -p 5432:5432 \
  -d postgres:17
```

> If you prefer `docker-compose`, the project includes a ready-to-use `docker-compose.yml`: `docker compose up -d`.

### 3. Configure the JWT secret (best practice)

The secret is **not** versioned. Create the file `src/main/resources/application-local.properties` (already ignored by `.gitignore`):

```properties
jwt.secret=YOUR_BASE64_256_BIT_SECRET_HERE
jwt.expirationMs=86400000
```

> 💡 Generate a key using the `KeyGenerator` included in the project, or any 32-byte Base64 generator. In production, the secret is injected via the `JWT_SECRET` environment variable (Spring Boot relaxed binding).

### 4. Run the application

```bash
# With the wrapper (recommended)
./mvnw spring-boot:run

# Or with Maven installed
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## 📖 Interactive documentation (Swagger)

With the application running, open in your browser:

```
http://localhost:8080/swagger-ui.html
```

There you can explore all endpoints, inspect the request/response schemas, and try them out using the **"Try it out"** button.

> For protected endpoints, use the **Authorize** 🔒 button in Swagger and paste your token as `Bearer <token>`.

---

## 🧪 Testing the API

### Login (get a token)

`POST http://localhost:8080/api/auth/login`

```json
{
  "username": "jperez",
  "password": "123456"
}
```

Response:

```json
{ "token": "eyJhbGciOiJIUzI1NiJ9..." }
```

### Use the token on protected requests

Add the header in Postman / curl:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

Example with `curl`:

```bash
curl -H "Authorization: Bearer YOUR_TOKEN" http://localhost:8080/api/appointments?page=0&size=10
```

### Recommended order to create data

Entities have dependencies — respect this order:

```
Person → Patient / Employee → Specialty → Appointment
```

Example — create a person (`POST /api/persons`):

```json
{
  "name": "Juan",
  "lastname": "Pérez",
  "email": "juan@example.com",
  "state": "ACTIVE"
}
```


## 👤 Author

**Lucas Vizcaino** — [GitHub](https://github.com/LucasVizcaino1)

Built with ☕ and Spring Boot.
