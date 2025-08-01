# Event Management System

A robust Spring Boot application for comprehensive event management and booking system. Built with modern technologies and best practices, featuring admin-specific event control, advanced error handling, and comprehensive validation.

---

## 🚀 Key Features

### 🎯 Event Management
- **Multiple Event Types**: WORKSHOP, CONFERENCE, HACKATHON, MEETUP, WEBINAR, SEMINAR
- **Event Lifecycle**: DRAFT → PUBLISHED → ONGOING → COMPLETED → CANCELLED
- **Admin-Specific Control**: Admins can only manage events they created
- **Rich Event Details**: Title, description, date/time, location, pricing, organizer info
- **Seat Management**: Total seats, available seats, automatic availability tracking

### 📅 Booking System
- **Smart Booking**: Real-time seat availability checks with conflict prevention
- **Booking Limits**: Maximum 5 seats per booking (configurable)
- **Status Tracking**: Confirmed, Cancelled booking statuses
- **User Authorization**: Users can only access their own bookings

### ⏳ Waitlist Management
- **Intelligent Waitlist**: Automatic enrollment when events are full
- **Waitlist Notifications**: Admin can notify waitlisted users
- **Position Tracking**: WAITING → NOTIFIED status progression

### ⭐ Feedback System
- **Star Ratings**: 1-5 star rating system with average calculation
- **Detailed Reviews**: Comments, suggestions, and recommendations
- **Feedback Analytics**: Admin access to all event feedback

### 🔒 Security & Validation
- **Role-Based Access**: ADMIN and USER roles with endpoint protection
- **Input Validation**: Comprehensive validation using Bean Validation API
- **Error Handling**: Global exception handler with structured error responses
- **Client-Side ID Validation**: Simplified authentication approach

---

## 🛠️ Tech Stack

- **Java 17** with Spring Boot 3.x
- **Spring Data JPA** with Hibernate
- **Spring Security** (HTTP Basic Authentication)
- **PostgreSQL** Database
- **Bean Validation API** (jakarta.validation)
- **Lombok** for boilerplate reduction
- **Swagger/OpenAPI 3** for API documentation

---

## 🌐 API Endpoints

### 👤 User Endpoints

#### Event Discovery
```http
GET /user/events/upcoming                      # Get upcoming published events
GET /user/events/category?category={type}      # Filter events by category  
GET /user/events/search?keyword={term}         # Search events by title
```

#### Booking Management
```http
POST /user/book/{eventId}/{userId}/{seats}     # Book event seats
POST /user/cancel/{bookingId}/{userId}         # Cancel existing booking
GET  /user/details/{bookingId}/{userId}        # Get booking details
GET  /user/bookings/{userId}                   # List all user bookings
```

#### Waitlist Operations
```http
POST   /user/waitlist/{eventId}/{userId}/{seats}  # Join event waitlist
GET    /user/waitlist/{userId}                     # Get user waitlist entries
DELETE /user/waitlist/{waitlistId}/{userId}       # Remove from waitlist
```

#### Feedback System
```http
POST /user/feedback/{userId}                   # Submit event feedback
GET  /user/feedback/{userId}                   # Get user's feedback history
```

### 🔧 Admin Endpoints

#### Event Management
```http
POST   /admin/event?adminId={id}               # Create new event
PUT    /admin/event/{eventId}?adminId={id}     # Update event (creator only)
DELETE /admin/event/{eventId}?adminId={id}     # Delete event (creator only)
POST   /admin/event/{eventId}/publish?adminId={id}  # Publish draft event
POST   /admin/event/{eventId}/cancel?adminId={id}   # Cancel published event
```

#### Analytics & Monitoring
```http
GET /admin/event/{eventId}/statistics?adminId={id}     # Event analytics
GET /admin/event/{eventId}/bookings?adminId={id}       # View event bookings
GET /admin/event/{eventId}/waitlist?adminId={id}       # View event waitlist
GET /admin/event/{eventId}/feedback?adminId={id}       # View event feedback
GET /admin/events?adminId={id}                         # List admin's events
```

#### Waitlist Management
```http
POST /admin/event/{eventId}/notify-waitlist?adminId={id}  # Notify waitlist users
```

---

## 🏗️ Project Structure

```
src/main/java/krashi/server/
├── controller/
│   ├── AdminController.java       # Admin API endpoints
│   └── UserController.java        # User API endpoints
├── dto/
│   ├── EventDto.java             # Event data transfer object (with validation)
│   ├── BookingDto.java           # Booking data transfer object
│   └── EventFeedbackDto.java     # Feedback data transfer object
├── entity/
│   ├── Event.java                # Event entity with JPA annotations
│   ├── UserInfo.java             # User entity
│   ├── Booking.java              # Booking entity
│   ├── Waitlist.java             # Waitlist entity
│   └── EventFeedback.java        # Feedback entity
├── exception/
│   ├── GlobalExceptionHandler.java    # Centralized error handling
│   ├── ResourceNotFoundException.java  # Custom exception for 404s
│   ├── BadRequestException.java       # Custom exception for 400s
│   ├── AccessDeniedException.java     # Custom exception for 403s
│   └── InsufficientSeatsException.java # Custom exception for booking conflicts
├── repository/
│   ├── EventRepository.java      # Event data access
│   ├── BookingRepository.java    # Booking data access
│   ├── UserInfoRepository.java   # User data access
│   ├── WaitlistRepository.java   # Waitlist data access
│   └── EventFeedbackRepository.java # Feedback data access
├── service/
│   ├── AdminService.java         # Admin business logic interface
│   ├── UserService.java          # User business logic interface
│   └── serviceImpl/
│       ├── AdminServiceImpl.java # Admin business logic implementation
│       └── UserServiceImpl.java  # User business logic implementation
├── configuration/
│   ├── SecurityConfig.java       # Spring Security configuration
│   ├── CustomUserDetails.java    # User details for authentication
│   └── CustomUserDetailsService.java # User details service
└── mapping/
    └── BookingToDto.java         # Entity to DTO mapping utility
```

---

## 🚀 Getting Started

### 1. Prerequisites
- Java 17 or higher
- Maven 3.6+
- PostgreSQL 12+

### 2. Clone Repository
```bash
git clone <repository-url>
cd server
```

### 3. Database Configuration
Update `src/main/resources/application.properties`:
```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/event_management
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Security Configuration
spring.security.user.name=admin
spring.security.user.password=admin123
spring.security.user.roles=ADMIN
```

### 4. Build and Run
```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

### 5. Access Points
- **Application**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui/index.html

---

## 📋 Validation Rules

### Event Creation/Update
```java
@NotBlank(message = "Event title is required")
@Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
private String title;

@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in format yyyy-MM-dd")
private String date;

@Pattern(regexp = "\\d{2}:\\d{2}", message = "Time must be in format HH:mm")
private String time;

@Min(value = 1, message = "Total seats must be at least 1")
@Max(value = 10000, message = "Total seats cannot exceed 10000")
private int totalSeats;

@Email(message = "Please provide a valid email address")
private String organizerEmail;
```

### Business Rules
- Event date/time must be in the future
- End date/time must be after start date/time
- Only draft events can be published
- Only event creators can modify their events
- Maximum 5 seats per booking
- Cannot delete events with existing bookings

---

## ⚡ Error Handling

### HTTP Status Codes
- **200 OK**: Successful operations
- **400 Bad Request**: Validation errors, business rule violations
- **403 Forbidden**: Authorization failures
- **404 Not Found**: Resource not found
- **409 Conflict**: Insufficient seats available
- **500 Internal Server Error**: Unexpected server errors

---

## 📱 Usage Examples

### 1. Create Event (Admin)
```json
POST /admin/event?adminId=1
Content-Type: application/json

{
  "title": "Spring Boot Advanced Workshop",
  "description": "Deep dive into Spring Boot microservices",
  "date": "2025-09-15",
  "time": "09:00",
  "endDate": "2025-09-15", 
  "endTime": "17:00",
  "location": "Tech Park, Bangalore",
  "totalSeats": 30,
  "category": "WORKSHOP",
  "price": 2999.0,
  "organizerName": "Spring Academy",
  "organizerEmail": "workshop@springacademy.com"
}
```

### 2. Book Event (User)
```http
POST /user/book/1/123/2
Authorization: Basic dXNlcjpwYXNzd29yZA==
```

### 3. Submit Feedback (User)
```json
POST /user/feedback/123
Content-Type: application/json

{
  "eventId": 1,
  "rating": 5,
  "comment": "Outstanding workshop with practical examples!",
  "suggestions": "Include more real-world use cases",
  "wouldRecommend": true
}
```

### 4. Get Event Statistics (Admin)
```http
GET /admin/event/1/statistics?adminId=1
Authorization: Basic YWRtaW46YWRtaW4xMjM=
```

**Response:**
```json
{
  "event": { /* event details */ },
  "totalBookings": 15,
  "bookedSeats": 28,
  "availableSeats": 2,
  "waitlistCount": 5,
  "averageRating": 4.6
}
```

---

## 🔐 Security Features

### Authentication
- **HTTP Basic Authentication** for API access
- **Form-based login** for web interface
- **Role-based authorization** (ADMIN, USER)

### Authorization Matrix
| Endpoint | Admin | User | Anonymous |
|----------|-------|------|-----------|
| POST /admin/event | ✅ | ❌ | ❌ |
| GET /user/events/upcoming | ✅ | ✅ | ❌ |
| POST /user/book/* | ❌ | ✅ | ❌ |
| GET /admin/events | ✅ | ❌ | ❌ |

### Data Protection
- **Input validation** on all endpoints
- **SQL injection prevention** via JPA/Hibernate
- **Authorization checks** for resource access

---

## 🧪 Testing

### Test with cURL
```bash
# Get upcoming events
curl -u user:password http://localhost:8080/user/events/upcoming

# Create event (admin)
curl -u admin:admin123 -X POST \
  -H "Content-Type: application/json" \
  -d '{"title":"Test Event","date":"2025-12-01","time":"10:00","location":"Test Location","totalSeats":50,"category":"WORKSHOP","organizerName":"Test Org","organizerEmail":"test@example.com"}' \
  http://localhost:8080/admin/event?adminId=1

# Book event (user)
curl -u user:password -X POST \
  http://localhost:8080/user/book/1/1/2
```

---

## 🎯 Key Improvements Made

### 1. **Enhanced Error Handling**
- Global exception handler for consistent error responses
- Custom exceptions for different error types
- Proper HTTP status codes and error messages

### 2. **Comprehensive Validation**
- Bean Validation API integration
- Input validation on DTOs
- Business rule validation in services

### 3. **Admin-Specific Access Control**
- Events are tied to their creators
- Admins can only manage events they created
- Enhanced security with authorization checks

### 4. **Simplified Architecture**
- Removed complex error response objects
- Eliminated unnecessary try-catch blocks
- Clean separation of concerns

### 5. **Client-Side ID Approach**
- Simplified authentication without SecurityContextHolder
- Client provides user/admin IDs in requests
- Maintains security through Spring Security roles

---

## � Future Enhancements

- **Email Notifications**: Automated emails for bookings and waitlist updates
- **Payment Integration**: Support for paid events with payment gateways
- **Event Analytics**: Advanced reporting and analytics dashboard
- **Mobile API**: RESTful APIs optimized for mobile applications
- **Real-time Updates**: WebSocket integration for live updates
- **Multi-tenant Support**: Support for multiple organizations

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 📧 Support

For support and questions:
- Create an issue in the repository
- Contact: [your-email@example.com]

---

**Built with ❤️ using Spring Boot and modern Java technologies**
