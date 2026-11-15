# CLAUDE.md – CampusConnect Development Instructions

## Project Context

You are helping develop CampusConnect, a medium-level College Event and Student Activity Management System.

Read docs/requirements.md and docs/database-design.md before making architectural decisions.

## Approved Architecture

CampusConnect is a single Spring Boot MVC application using Java 21, Maven,
MySQL 8, Spring Data JPA, Spring Security, Thymeleaf and Bootstrap 5.
It follows a layered architecture:

- **Web layer**: Thymeleaf page controllers and REST controllers handle HTTP
  requests, validation results, view/API responses and redirects only.
- **Service layer**: application and business rules, authorization checks,
  transactions and dashboard calculations.
- **Repository layer**: Spring Data JPA repositories, derived queries and
  JPQL for persistence.
- **Domain layer**: JPA entities, enums and relationship mappings.
- **DTO layer**: validated request objects and response/view models; entities
  are never exposed directly to web clients.
- **Configuration and exception handling**: security configuration, application
  properties, centralized MVC/API error handling and shared validation support.

The application is a modular monolith with these functional modules:

1. **Authentication and user management**: student registration, login/logout,
   password hashing, roles and current-user access.
2. **Event management**: event CRUD, category validation, search, filters,
   sorting, pagination and upcoming/completed classifications.
3. **Registration management**: register, cancel, list registrations,
   capacity enforcement, duplicate prevention and transactional consistency.
4. **Student dashboard**: personalized upcoming events, registrations,
   recently added events and registration history.
5. **Administrator dashboard**: aggregate counts and event-wise registration
   statistics.
6. **Shared web/API infrastructure**: DTO mapping, validation, error
   responses, flash messages, pagination models and common security rules.

### Database Relationships

- `User` has a one-to-many relationship with `Registration`.
- `Event` has a one-to-many relationship with `Registration`.
- `User` and `Event` are logically many-to-many through `Registration`.
- `Registration` owns the foreign keys to `User` and `Event` and has a unique
  `(user_id, event_id)` constraint.
- A cancelled registration remains the same relationship row and can be
  reactivated as `REGISTERED`; cancelled rows do not consume capacity.
- Foreign keys, not-null constraints, unique constraints and indexes enforce
  database integrity. Business checks remain in services as well.

Concurrent registration attempts must lock or atomically update the event
capacity state so active registrations cannot exceed capacity. Registration
and cancellation are transactional.

### Required Dependencies

The Maven project should use only the dependencies required by the approved
scope:

- `spring-boot-starter-web`
- `spring-boot-starter-thymeleaf`
- `spring-boot-starter-data-jpa`
- `spring-boot-starter-security`
- `spring-boot-starter-validation`
- MySQL Connector/J runtime
- `spring-boot-starter-test` (JUnit 5, Mockito and Spring test support)
- `spring-security-test`
- Optional documentation dependency: `springdoc-openapi-starter-webmvc-ui`

Bootstrap assets may be managed as static resources or through a documented
frontend asset approach; no frontend framework is required.

### Planned Folder Structure

```text
src/
  main/
    java/com/campusconnect/
      CampusConnectApplication.java
      config/
      controller/
      dto/
        auth/
        user/
        event/
        registration/
        dashboard/
      entity/
      enums/
      exception/
      mapper/
      repository/
      service/
        impl/
      security/
    resources/
      application.properties
      static/css/
      static/js/
      templates/
        auth/
        student/
        admin/
        fragments/
  test/
    java/com/campusconnect/
      controller/
      service/
      repository/
      integration/
```

### Implementation Sequence

Work in approved phases and stop for approval between phases:

1. Bootstrap the Maven/Spring Boot project and configuration.
2. Implement the domain model, enums, constraints and repositories.
3. Implement authentication, users, roles and secure session configuration.
4. Implement event CRUD and event discovery queries.
5. Implement transactional registration and cancellation with concurrency
   protection.
6. Implement student and administrator dashboards.
7. Build Thymeleaf pages, fragments, Bootstrap layout and messages.
8. Add REST endpoints and API documentation.
9. Add unit, repository and integration tests.
10. Complete README, setup instructions, ER diagram and progress tracking.

### Anticipated Technical Challenges

- Preventing over-capacity during concurrent registrations.
- Handling cancelled registrations while preserving the unique pair constraint.
- Keeping authorization consistent between page controllers, REST endpoints
  and service methods.
- Validating event date/time rules, including past events and end-before-start.
- Avoiding entity serialization or lazy-loading problems by using DTOs.
- Keeping dashboard aggregate queries efficient and correct.
- Configuring database credentials and administrator credentials without
  committing secrets.
- Testing security, transactions, validation and MySQL-specific behavior
  without relying only on a running local database.

## Developer Background

The developer:
- Knows Core Java fundamentals.
- Is learning Spring Boot.
- Has basic knowledge of SQL and databases.
- Uses VS Code.
- Wants to understand the implementation rather than blindly generate code.

## Development Principles

1. Use Java 21 and Spring Boot 3.x.
2. Follow layered architecture.
3. Use constructor-based dependency injection.
4. Keep controllers focused on HTTP handling.
5. Keep business logic inside service classes.
6. Use repositories for database operations.
7. Use DTOs instead of exposing entities directly.
8. Use meaningful names and Java conventions.
9. Avoid duplicate code.
10. Keep the implementation simple and readable.
11. Do not introduce unnecessary dependencies.
12. Do not implement advanced architecture unless requested.

## Teaching Instructions

For every implementation:

1. Explain the concept first.
2. Explain why it is required.
3. Explain the class structure.
4. Explain important Spring annotations.
5. Generate complete code.
6. Explain how the code works.
7. Provide commands to run it.
8. Provide testing instructions.
9. Explain common errors.

Do not assume the developer understands Spring Boot internals.

## Code Quality

- Use proper exception handling.
- Follow SOLID principles where appropriate.
- Validate user input.
- Never store plain-text passwords.
- Use BCrypt.
- Avoid hardcoded credentials.
- Use application.properties for configuration.
- Keep secrets in environment variables.
- Use transactions for operations that require atomicity.
- Prevent duplicate database records.
- Avoid exposing sensitive information.

## Database Rules

- Use MySQL.
- Use Spring Data JPA.
- Use appropriate entity relationships.
- Use foreign keys and unique constraints.
- Avoid unnecessary bidirectional mappings.
- Use DTOs to prevent serialization problems.
- Do not use destructive schema operations without approval.

## Security Rules

- Use Spring Security.
- Use session-based authentication.
- Protect administrator endpoints.
- Enforce role-based access.
- Preserve CSRF protection for form-based requests.
- Never expose passwords or secrets.

## Testing Rules

After every module:

- Run Maven compilation.
- Run relevant JUnit tests.
- Fix compilation errors.
- Test business logic.
- Verify API responses.
- Do not claim tests passed without executing them.

## Development Workflow

Work in small phases.

Do not implement the entire application at once.

Follow the sequence in docs/requirements.md.

Update docs/development-progress.md after every completed phase.

Do not proceed to another phase until the developer approves.

Before changing existing code:
- Inspect the existing implementation.
- Identify affected files.
- Explain the proposed changes.
- Avoid rewriting working functionality unnecessarily.

## Git Rules

- Keep changes focused.
- Recommend meaningful commits after completed phases.
- Never commit .env files, passwords, API keys or database credentials.
- Maintain a .gitignore file.

## Final Goal

Deliver a working, understandable, maintainable CampusConnect application that demonstrates practical Spring Boot knowledge.

The developer must be able to explain the architecture, database relationships, business logic, authentication and important annotations during an institute demonstration.