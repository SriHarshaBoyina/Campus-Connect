# CampusConnect – Project Requirements

## 1. Project Overview

Project Name: CampusConnect

Project Type: College Event and Student Activity Management System.

Objective:
Develop a web application that allows students to discover college events, register for them, and manage their participation. Administrators can create and manage events and monitor student registrations.

Target Users:
- College students
- College administrators

## 2. Technology Stack

- Java 21
- Spring Boot 3.x
- Maven
- Spring Web
- Spring Data JPA
- Hibernate
- Spring Security
- MySQL 8
- Thymeleaf
- HTML, CSS, JavaScript
- Bootstrap 5
- JUnit 5
- Mockito
- Postman

## 3. User Roles

### Student
- Register an account.
- Log in and log out.
- View available events.
- Search and filter events.
- View event details.
- Register for events.
- Cancel registrations.
- View personal registrations.

### Administrator
- Log in securely.
- Create events.
- Update events.
- Delete events.
- View all events.
- View registered students.
- Monitor registration statistics.
- Manage event availability.

## 4. Event Management

Each event should contain:

- Event ID
- Title
- Description
- Category
- Event date
- Start time
- End time
- Venue
- Maximum capacity
- Created timestamp

Categories:
- Technical
- Cultural
- Sports
- Workshop
- Seminar
- Hackathon

Functionalities:
- Create, read, update and delete events.
- Search by title.
- Filter by category.
- Sort by event date.
- View upcoming and completed events.

## 5. Registration Management

Students can register for available events.

Business rules:
- A student cannot register twice for the same event.
- Registration is rejected when event capacity is full.
- Students cannot register for past or completed events.
- Students can cancel their registrations.
- Available seats must update correctly.
- Registration and cancellation must be transactional.
- Database constraints must prevent duplicate registrations.

## 6. Dashboards

### Student Dashboard
- Welcome message.
- Upcoming events.
- Registered events.
- Recently added events.
- Registration history.

### Admin Dashboard
- Total students.
- Total events.
- Upcoming events.
- Completed events.
- Total registrations.
- Event-wise registration statistics.

## 7. Security Requirements

- BCrypt password hashing.
- Spring Security authentication.
- Role-based authorization.
- STUDENT and ADMIN roles.
- Secure session management.
- Students cannot access administrative functionality.
- Public registration must only create STUDENT accounts.
- Default administrator credentials must be configurable and not committed to GitHub.

Use session-based authentication with Spring Security and Thymeleaf.

## 8. Application Requirements

- Follow layered architecture.
- Use Controller, Service and Repository layers.
- Use constructor-based dependency injection.
- Use DTOs for request and response objects.
- Implement Bean Validation.
- Implement centralized exception handling.
- Use Spring Data JPA.
- Use proper entity relationships.
- Implement pagination and sorting.
- Use meaningful HTTP status codes.
- Write unit and integration tests.

## 9. Frontend Requirements

Pages:
1. Login
2. Student registration
3. Student dashboard
4. Event listing
5. Event details
6. My registrations
7. Admin dashboard
8. Admin event management

Design:
- Responsive layout.
- Clean dashboard.
- Sidebar navigation.
- Event cards.
- Search and filter options.
- Registration buttons.
- Success and error messages.

## 10. Project Scope

This is a medium-level academic project.

Do not introduce:
- Microservices
- Kafka
- Kubernetes
- Complex distributed systems
- Unnecessary AI integration
- Unnecessary cloud infrastructure

Prioritize working functionality, clean code, understandable architecture and Spring Boot fundamentals.

## 11. Expected Deliverables

- Complete Spring Boot application.
- MySQL database integration.
- Student and admin authentication.
- Event management.
- Registration management.
- Responsive frontend.
- REST APIs.
- API documentation.
- Unit and integration tests.
- Database ER diagram.
- README and setup instructions.