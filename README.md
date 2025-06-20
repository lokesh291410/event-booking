# Event Booking System

A Spring Boot application for managing events and bookings with role-based authentication and Swagger API documentation.

---

## Features

- **User Roles:** Admin and User (role-based access)
- **Event Management (Admin):**
  - Create, update, and delete events
- **Booking System (User):**
  - Book tickets for events (with seat limits)
  - View and cancel own bookings
  - Prevents overbooking
- **Authentication:** Spring Security (form login & HTTP Basic)
- **API Documentation:** Swagger UI (`springdoc-openapi`)

---

## Tech Stack

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- PostgreSQL (or any supported DB)
- Lombok
- Swagger (springdoc-openapi)

---

## Getting Started

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd server
```

### 2. Configure Database

Edit `src/main/resources/application.properties`:

```
spring.datasource.url=jdbc:postgresql://localhost:5432/your_db
spring.datasource.username=your_user
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

### 3. Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

### 4. Access the Application

- **Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **API Endpoints:** See Swagger UI for full documentation

---

## API Overview

### Authentication

- `/signup` — Register a new user (user or admin role)
- `/login` — Login (form or HTTP Basic)

### Admin Endpoints (`/admin/**`)

- `POST /admin/event` — Create event
- `PUT /admin/event/{eventId}` — Update event
- `DELETE /admin/event/{eventId}` — Delete event

### User Endpoints (`/user/**`)

- `POST /user/book/{eventId}/{userId}/{numberOfSeats}` — Book event
- `POST /user/cancel/{bookingId}/{userId}` — Cancel booking
- `GET /user/details/{bookingId}/{userId}` — Get booking details
- `GET /user/bookings/{userId}` — List user's bookings

### Public Endpoints

- `/events` — List all events

---

## Security

- **Admin endpoints** require `ADMIN` role
- **User endpoints** require `USER` role
- **Swagger UI** and `/signup` are public

---

## Notes

- Max 5 seats per booking (configurable)
- Booking prevents overbooking and updates available seats
- Uses DTOs to avoid exposing entities directly

---

