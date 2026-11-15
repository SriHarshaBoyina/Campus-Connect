# CampusConnect API

Base URL: `http://localhost:8080`

The API returns JSON. Protected requests use the authenticated server session.
State-changing requests require a CSRF token because CSRF protection is
enabled. The interactive OpenAPI UI is available at `/swagger-ui.html`.

## Authentication

| Method | Path | Access | Success |
|---|---|---|---|
| POST | `/api/auth/register` | Public | 201 |
| POST | `/api/auth/login` | Public | 200 |
| POST | `/api/auth/logout` | Authenticated/session | 204 |

Registration always assigns `STUDENT`.

## Events

| Method | Path | Access | Success |
|---|---|---|---|
| GET | `/api/events` | Public | 200 |
| GET | `/api/events/{id}` | Public | 200 |
| GET | `/api/events/upcoming` | Public | 200 |
| GET | `/api/events/completed` | Public | 200 |

`GET /api/events` supports `page`, `size`, `title` and `category`.

## Student

| Method | Path | Access | Success |
|---|---|---|---|
| GET | `/api/student/dashboard` | STUDENT | 200 |
| GET | `/api/student/registrations` | STUDENT | 200 |
| POST | `/api/registrations` | STUDENT | 201 |
| DELETE | `/api/registrations/{eventId}` | STUDENT | 200 |

Registration request:

```json
{"eventId": 1}
```

## Administrator

| Method | Path | Access | Success |
|---|---|---|---|
| GET | `/api/admin/dashboard` | ADMIN | 200 |
| POST | `/api/admin/events` | ADMIN | 201 |
| PUT | `/api/admin/events/{id}` | ADMIN | 200 |
| DELETE | `/api/admin/events/{id}` | ADMIN | 204 |
| GET | `/api/admin/events/{id}/registrations` | ADMIN | 200 |

Event payload fields are `title`, `description`, `category`, `eventDate`,
`startTime`, `endTime`, `venue` and `capacity`.

## Error statuses

- `400`: malformed or invalid request
- `401`: no authenticated session
- `403`: authenticated user lacks the required role or CSRF token
- `404`: resource does not exist
- `409`: duplicate or business-rule conflict
- `500`: unexpected server error
