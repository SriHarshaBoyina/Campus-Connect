# CampusConnect – Database Design

## 1. Database

Database Name: campus_connect

Database: MySQL 8

ORM: Hibernate / Spring Data JPA

## 2. Tables

### USERS

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | Primary Key, Auto Increment |
| name | VARCHAR(100) | NOT NULL |
| email | VARCHAR(150) | UNIQUE, NOT NULL |
| password | VARCHAR(255) | NOT NULL |
| role | VARCHAR(20) | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |

Allowed roles:
- STUDENT
- ADMIN

### EVENTS

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | Primary Key, Auto Increment |
| title | VARCHAR(150) | NOT NULL |
| description | TEXT | |
| category | VARCHAR(50) | NOT NULL |
| event_date | DATE | NOT NULL |
| start_time | TIME | NOT NULL |
| end_time | TIME | NOT NULL |
| venue | VARCHAR(150) | NOT NULL |
| capacity | INT | NOT NULL |
| created_at | TIMESTAMP | NOT NULL |

### REGISTRATIONS

| Column | Type | Constraints |
|---|---|---|
| id | BIGINT | Primary Key, Auto Increment |
| user_id | BIGINT | Foreign Key |
| event_id | BIGINT | Foreign Key |
| registered_at | TIMESTAMP | NOT NULL |
| status | VARCHAR(20) | NOT NULL |

Add a unique constraint on:
(user_id, event_id)

Allowed registration statuses:
- REGISTERED
- CANCELLED

## 3. Relationships

### User and Registration

One user can have multiple registrations.

Relationship:
User 1 ---- N Registration

### Event and Registration

One event can have multiple registrations.

Relationship:
Event 1 ---- N Registration

### User and Event

A user can register for multiple events.

An event can have multiple users.

Relationship:
User N ---- N Event

Implement this using the Registration entity as the joining entity.

## 4. Business Constraints

1. User email must be unique.
2. A student cannot register twice for an event.
3. Registration count cannot exceed event capacity.
4. Cancelled registrations should not count toward occupied seats.
5. Only students can register for events.
6. Only administrators can create, update or delete events.
7. Event capacity must be greater than zero.
8. Event end time must be later than start time.
9. Event date cannot be in the past when creating a new event.

## 5. Important Database Queries

Implement queries for:

- Find events by category.
- Find upcoming events.
- Find completed events.
- Search events by title.
- Find registrations by student ID.
- Find students registered for an event.
- Count active registrations for an event.
- Count total events.
- Count total students.
- Count total registrations.

Use Spring Data JPA derived queries and JPQL where appropriate.

## 6. Data Integrity

- Use foreign key constraints.
- Use unique constraints.
- Use NOT NULL constraints where necessary.
- Use appropriate indexes.
- Use transactions for registration and cancellation.
- Handle concurrent registration attempts to avoid exceeding event capacity.

## 7. ER Diagram

USERS
  |
  | 1:N
  |
REGISTRATIONS
  |
  | N:1
  |
EVENTS

Registration connects USERS and EVENTS.

Avoid storing duplicate student or event information in the REGISTRATIONS table.