# CampusConnect Setup

## Required software

- Java 21 (`java -version`)
- Maven 3.9+ (`mvn -version`)
- MySQL 8
- VS Code with Extension Pack for Java and Spring Boot Extension Pack

The project compiles with Java 24 in the current environment, but Java 21 is
the supported runtime target.

## MySQL setup

```sql
CREATE DATABASE campus_connect
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

The configured MySQL profile connects as the existing `root` user. It reads
only the password from the `DB_PASSWORD` environment variable.

Configure the MySQL profile without committing credentials:

```powershell
$env:DB_PASSWORD="replace-with-a-local-secret"
$env:DDL_AUTO="update"
```

`update` is convenient for local development. Use reviewed migrations and
`DDL_AUTO=validate` in production.

## Run and verify

```powershell
mvn clean test
mvn spring-boot:run "-Dspring-boot.run.profiles=mysql"
```

Verify:

- `GET http://localhost:8080/login` displays the form.
- `GET http://localhost:8080/events` displays public events.
- `GET http://localhost:8080/swagger-ui.html` displays API documentation.

If MySQL is unavailable, the application cannot authenticate against the
database. Tests remain runnable because they use H2 under the `test` profile.

## Create an administrator

Public registration intentionally cannot create administrators. For a local
bootstrap, configure these environment variables before starting:

```powershell
$env:ADMIN_EMAIL="sriharshaboyina07@gmail.com"
$env:ADMIN_PASSWORD="choose-a-local-admin-password"
```

The application creates the administrator only when both values are present
and the email does not already exist. It stores only the BCrypt hash. Never
commit these variables or log the password.
