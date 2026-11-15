# CampusConnect ER Diagram

```mermaid
erDiagram
    USERS ||--o{ REGISTRATIONS : creates
    EVENTS ||--o{ REGISTRATIONS : receives

    USERS {
        BIGINT id PK
        VARCHAR name
        VARCHAR email UK
        VARCHAR password
        VARCHAR role
        TIMESTAMP created_at
    }

    EVENTS {
        BIGINT id PK
        VARCHAR title
        TEXT description
        VARCHAR category
        DATE event_date
        TIME start_time
        TIME end_time
        VARCHAR venue
        INT capacity
        TIMESTAMP created_at
    }

    REGISTRATIONS {
        BIGINT id PK
        BIGINT user_id FK
        BIGINT event_id FK
        TIMESTAMP registered_at
        VARCHAR status
    }
```

`REGISTRATIONS` is the joining entity for the logical many-to-many
relationship between users and events. The database enforces unique
`(user_id, event_id)`, foreign keys and non-null required values.
