# CampusConnect

CampusConnect is a Spring Boot college event and student activity management
system. Students discover and register for events; administrators manage events
and monitor registrations.

## Technology

- Java 21
- Spring Boot 3.5
- Maven
- Spring Web, Spring Data JPA and Hibernate
- Spring Security with BCrypt and server-side sessions
- MySQL 8
- Thymeleaf, Bootstrap 5, HTML, CSS and JavaScript
- JUnit 5, Mockito, MockMvc and H2 test database

## Features

- Student account registration and form login
- Role-based student/admin authorization
- Event search, category filtering, sorting and pagination
- Event CRUD for administrators
- Registration, cancellation, duplicate prevention and capacity protection
- Student and administrator dashboards
- REST API and responsive Thymeleaf frontend
- CSRF-protected browser forms

## Quick start

1. Install Java 21, Maven and MySQL 8.
2. Create the database:

   ```sql
   CREATE DATABASE campus_connect
       CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. Set environment variables:

   ```powershell
   $env:DB_PASSWORD="replace-with-a-local-secret"
   $env:DDL_AUTO="update"
   ```

4. Start the application:

   ```powershell
   mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
   ```

5. Open <http://localhost:8080/login>.

Public student registration creates only `STUDENT` accounts. To create the
first administrator locally, set `ADMIN_EMAIL` and `ADMIN_PASSWORD` before
startup; never expose an admin role in the public registration form.

For production, use `DDL_AUTO=validate` with reviewed migrations and secrets
from an environment/secret manager.

## Tests

Run the complete suite:

```powershell
mvn test
```

Tests use an isolated H2 database through the `test` profile. The suite covers
application startup, JPA persistence and constraints, service business rules,
REST endpoints, security, validation and Thymeleaf workflows.

## Documentation

- [Project setup](docs/setup.md)
- [API documentation](docs/api-documentation.md)
- [API testing guide](docs/api-testing-guide.md)
- [Database ER diagram](docs/er-diagram.md)
- [Institute demonstration guide](docs/institute-demonstration-guide.md)
- [Requirements](docs/requirements.md)
- [Database design](docs/database-design.md)

## Architecture

The project is a layered modular monolith:

`Controller -> Service -> Repository -> MySQL`

DTOs separate HTTP payloads from entities. Thymeleaf page controllers and REST
controllers reuse the same services. See [claude.md](claude.md) for the
approved development rules.
