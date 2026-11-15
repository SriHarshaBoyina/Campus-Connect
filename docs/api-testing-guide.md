# CampusConnect API Testing Guide

## Prerequisites

1. Start MySQL and create the `campus_connect` database.
2. Configure the datasource and credentials in `application.properties` or
   environment variables.
3. Start the application with `mvn spring-boot:run`.
4. Use Postman or curl with `http://localhost:8080`.

The application uses a server-side session cookie. Browser users authenticate
through the form at `GET /login`, which submits email as `username` and the
password to `POST /login`. Save the `JSESSIONID` returned by login and send it
with protected requests. CSRF protection is enabled; browser forms include the
generated token automatically. For Postman, first obtain the CSRF cookie from
the application and send its token in the `X-XSRF-TOKEN` header on state-changing
requests.

## Authentication

### Register a student

`POST /api/auth/register`

```json
{
  "name": "Asha Student",
  "email": "asha@example.com",
  "password": "password123"
}
```

Expected status: `201 Created`.

### JSON compatibility login

`POST /api/auth/login`

```json
{
  "email": "asha@example.com",
  "password": "password123"
}
```

Expected status: `200 OK`. Store the `JSESSIONID` cookie. Browser-based
authentication should use `/login` instead.

### Logout

Browser logout uses `POST /logout` and redirects to `/login?logout`.
The compatibility endpoint is:

`POST /api/auth/logout`

Expected status: `204 No Content`.

## Public event endpoints

- `GET /api/events`
- `GET /api/events?title=java`
- `GET /api/events?category=Workshop`
- `GET /api/events/{eventId}`
- `GET /api/events/upcoming`
- `GET /api/events/completed`

`GET /api/events` supports `page`, `size`, `title`, and `category`.

## Student endpoints

Send the student session cookie.

- `GET /api/student/dashboard`
- `GET /api/student/registrations`
- `POST /api/registrations`

```json
{
  "eventId": 1
}
```

- `DELETE /api/registrations/{eventId}`

Expected registration status: `201 Created`. Expected cancellation status:
`200 OK`.

All POST, PUT and DELETE requests require a valid CSRF token.

## Administrator endpoints

Login with an administrator account and send its session cookie.

### Create event

`POST /api/admin/events`

```json
{
  "title": "Java Workshop",
  "description": "Spring Boot fundamentals",
  "category": "Workshop",
  "eventDate": "2027-01-15",
  "startTime": "10:00:00",
  "endTime": "12:00:00",
  "venue": "Lab 1",
  "capacity": 50
}
```

- `PUT /api/admin/events/{eventId}` updates an event.
- `DELETE /api/admin/events/{eventId}` deletes an event.
- `GET /api/admin/dashboard` returns aggregate statistics.
- `GET /api/admin/events/{eventId}/registrations` lists active registered
  students for an event.

## Expected error responses

- `400 Bad Request`: invalid JSON or validation failure
- `401 Unauthorized`: no authenticated session
- `403 Forbidden`: authenticated user lacks the required role
- `404 Not Found`: requested user, event, or registration does not exist
- `409 Conflict`: duplicate registration, full event, or business-rule failure

Validation errors have this shape:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Request validation failed",
  "validationErrors": {
    "capacity": "must be greater than or equal to 1"
  }
}
```
