# Institute Demonstration Guide

## 1. Explain the architecture

Show the layered flow:

`Thymeleaf/REST Controller -> Service -> Spring Data Repository -> MySQL`

Explain that DTOs protect entities, services own business rules, and Spring
Security stores authentication in a server-side session.

## 2. Demonstrate student flow

1. Open `/register`.
2. Create a student account.
3. Log in through `/login`.
4. Browse `/events`, search and filter.
5. Open an event and register.
6. Open `/student/registrations`.
7. Cancel the registration.
8. Explain that duplicate registrations and full events are rejected.

## 3. Demonstrate administrator flow

1. Set `ADMIN_EMAIL` and `ADMIN_PASSWORD`, start the application once, and log
   in with the resulting ADMIN account.
2. Open `/admin/dashboard`.
3. Create an event.
4. Edit the event.
5. Open registered students.
6. Delete the event.
7. Explain that a student request to `/admin/**` returns `403`.

## 4. Demonstrate security

- Passwords are stored with BCrypt.
- Public registration cannot select a role.
- Browser forms contain CSRF tokens.
- Logout invalidates the session and deletes `JSESSIONID`.
- Missing authentication returns a login redirect for pages and `401` for API
  requests.

## 5. Demonstrate quality

Run `mvn test` and explain the test groups:

- H2 JPA schema and relationship tests
- Mockito service tests
- MockMvc REST/security tests
- MockMvc Thymeleaf workflow tests

Do not claim MySQL connectivity unless the configured MySQL instance has been
started and the application has successfully completed a real database-backed
login/event/registration flow.
