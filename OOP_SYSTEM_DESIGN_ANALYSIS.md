# OOP and System Design Analysis - Event Booking System

## Project Overview
This is a Spring Boot-based Event Booking System that demonstrates comprehensive use of Object-Oriented Programming principles and various System Design patterns in a layered architecture.

---

## 1. ENTITY LAYER - Domain Objects

### 1.1 UserInfo Entity
**OOP Principles Used:**
- **Encapsulation**: Private fields with getter/setter methods via Lombok
- **Abstraction**: JPA annotations abstract database operations

**System Design Patterns:**
- **Active Record Pattern**: Entity contains both data and behavior
- **Proxy Pattern**: JPA lazy loading creates proxies for relationships

**SOLID Principles:**
- **Single Responsibility**: Manages user data only

```java
@Entity
@Table(name = "user_info")
@Data
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String name;
    private String email;
    
    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Event> createdEvents;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;
}
```

### 1.2 Event Entity
**OOP Principles Used:**
- **Encapsulation**: Data hiding with controlled access
- **Polymorphism**: Different event categories handled uniformly

**System Design Patterns:**
- **Builder Pattern**: Lombok @Setter enables builder-like construction
- **Composite Pattern**: Event aggregates multiple related entities

```java
@Entity
@Getter
@Setter
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    
    @ManyToOne
    @JoinColumn(name = "created_by")
    private UserInfo createdBy;
    
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> booking;
}
```

### 1.3 Booking Entity
**OOP Principles Used:**
- **Association**: Strong relationship(Composition) with User and Event
- **Encapsulation**: Booking state management

**System Design Patterns:**
- **State Pattern**: Status field manages booking states
- **Bridge Pattern**: Links User and Event entities

```java
@Entity
@Getter
@Setter
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;
    
    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;
    
    private String status;
}
```

### 1.4 Waitlist Entity
**OOP Principles Used:**
- **Encapsulation**: Waitlist management logic encapsulated
- **Association**: Connects users to fully booked events

**System Design Patterns:**
- **Observer Pattern**: Notifies users when seats become available
- **Queue Pattern**: FIFO processing of waitlist entries

```java
@Entity
@Getter
@Setter
public class Waitlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;
    
    private String status;
    private LocalDateTime joinedAt;
}
```

### 1.5 EventFeedback Entity
**OOP Principles Used:**
- **Encapsulation**: Feedback data and validation rules
- **Association**: Links user feedback to specific events

```java
@Entity
@Getter
@Setter
public class EventFeedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserInfo user;
    
    private int rating;
    private String comment;
    private boolean wouldRecommend;
}
```

---

## 2. REPOSITORY LAYER - Data Access Objects

### 2.1 UserInfoRepository
**OOP Principles Used:**
- **Abstraction**: Hides complex database operations behind simple method calls
- **Interface Segregation**: Specific methods for user operations only

**System Design Patterns:**
- **Repository Pattern**: Encapsulates data access logic
- **Proxy Pattern**: Spring Data JPA creates implementation at runtime

**SOLID Principles:**
- **Interface Segregation**: Only user-related data operations
- **Dependency Inversion**: Depends on JpaRepository abstraction

```java
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Long> {
    Optional<UserInfo> findByUserName(String userName);
    Optional<UserInfo> findByEmail(String email);
}
```

### 2.2 EventRepository
**OOP Principles Used:**
- **Abstraction**: Complex queries abstracted behind method names
- **Polymorphism**: Different query methods for various search criteria

**System Design Patterns:**
- **Query Object Pattern**: Custom queries using @Query annotation
- **Specification Pattern**: Method names specify query criteria

```java
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    List<Event> findByCategory(String category);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED' AND e.dateTime > :now ORDER BY e.dateTime ASC")
    List<Event> findUpcomingPublicEvents(LocalDateTime now);
}
```

### 2.3 BookingRepository
**System Design Patterns:**
- **Repository Pattern**: Centralized booking data access
- **Specification Pattern**: Method naming convention for queries

```java
@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByEvent_Id(Long eventId);
    boolean existsByUserIdAndEventId(Long userId, Long eventId);
}
```

---

## 3. DTO LAYER - Data Transfer Objects

### 3.1 LoginRequest DTO
**OOP Principles Used:**
- **Encapsulation**: Request data validation and structure
- **Data Integrity**: Validation annotations ensure data quality

**System Design Patterns:**
- **DTO Pattern**: Transfers data between layers without exposing entity structure
- **Validator Pattern**: Bean validation annotations

```java
@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required") 
    private String password;
}
```

### 3.2 EventDto
**OOP Principles Used:**
- **Encapsulation**: Event data with validation rules
- **Data Abstraction**: Simplified event representation for external use

**System Design Patterns:**
- **DTO Pattern**: Clean data transfer without entity complexity
- **Builder Pattern**: Setter methods enable flexible object construction

```java
@Getter
@Setter
public class EventDto {
    @NotBlank(message = "Event title is required")
    @Size(min = 3, max = 100)
    private String title;
    
    @Min(value = 1, message = "Total seats must be at least 1")
    private int totalSeats;
    
    @Email(message = "Please provide a valid email address")
    private String organizerEmail;
}
```

---

## 4. SERVICE LAYER - Business Logic

### 4.1 UserService Interface
**OOP Principles Used:**
- **Abstraction**: Interface defines contract without implementation details
- **Polymorphism**: Multiple implementations possible for same interface

**System Design Patterns:**
- **Strategy Pattern**: Different implementations can be swapped
- **Facade Pattern**: Simplifies complex booking operations

**SOLID Principles:**
- **Interface Segregation**: User-specific operations only
- **Dependency Inversion**: Depends on abstractions, not concrete classes

```java
public interface UserService {
    ResponseEntity<?> bookEvent(Long eventId, int numberOfSeats);
    ResponseEntity<?> cancelBooking(Long bookingId);
    ResponseEntity<?> getUserBookings();
    ResponseEntity<?> submitEventFeedback(EventFeedbackDto feedbackDto);
}
```

### 4.2 UserServiceImpl
**OOP Principles Used:**
- **Encapsulation**: Business logic encapsulated in service methods
- **Inheritance**: Implements UserService interface
- **Polymorphism**: Multiple validation methods with different parameters

**System Design Patterns:**
- **Template Method Pattern**: Common validation steps in booking process
- **Command Pattern**: Each method represents a specific user action
- **Observer Pattern**: Email notifications for booking events
- **Strategy Pattern**: Different handling for booking vs waitlist

**SOLID Principles:**
- **Single Responsibility**: Handles only user-related business operations
- **Open/Closed**: Can be extended without modification
- **Dependency Inversion**: Depends on repository abstractions

```java
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final AuthenticationService authenticationService;
    
    private void verifyBookingOwnership(Booking booking, UserInfo user) {
        // Validation logic
    }
    
    @Override
    public ResponseEntity<?> bookEvent(Long eventId, int numberOfSeats) {
        // Business logic implementation
    }
    
    @Override
    public ResponseEntity<?> cancelBooking(Long bookingId) {
        // Cancellation logic with waitlist promotion
    }
}
```

### 4.3 EmailService Interface & Implementation
**OOP Principles Used:**
- **Abstraction**: Interface hides email implementation complexity from clients
- **Encapsulation**: Email sending logic encapsulated within service
- **Single Responsibility**: Focused only on OTP email functionality

**System Design Patterns:**
- **Template Method Pattern**: Common email structure with customizable content
- **Strategy Pattern**: Different email implementations possible
- **Factory Pattern**: Creates email message objects

**SOLID Principles:**
- **Single Responsibility**: Handles only OTP email operations
- **Dependency Inversion**: Depends on JavaMailSender abstraction

```java
public interface EmailService {
    void sendOtpEmail(String toEmail, String otp);
}

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender mailSender;
    
    @Override
    public void sendOtpEmail(String toEmail, String otp) {
        // Email sending implementation
    }
    
    private String buildOtpEmailContent(String otp) {
        // Content building logic
    }
}
```

### 4.4 EmailNotificationService Interface & Implementation
**OOP Principles Used:**
- **Abstraction**: Complex email notification operations abstracted
- **Encapsulation**: Email template generation and sending logic encapsulated
- **Polymorphism**: Multiple notification methods for different scenarios

**System Design Patterns:**
- **Observer Pattern**: Notifies users about booking events via email
- **Template Method Pattern**: Common email structure with different content
- **Command Pattern**: Each notification method represents a specific email action
- **Builder Pattern**: Email content construction using string formatting

**SOLID Principles:**
- **Single Responsibility**: Handles only email notifications for events
- **Open/Closed**: New notification types can be added without modifying existing code
- **Dependency Inversion**: Depends on JavaMailSender abstraction

```java
public interface EmailNotificationService {
    void sendBookingConfirmationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId);
    void sendWaitlistConfirmationEmail(UserInfo user, Event event, int requestedSeats);
    void sendPromotionNotificationEmails(List<PromotedUserDto> promotedUsers);
    void sendBookingCancellationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId);
}

@Service
@AllArgsConstructor
public class EmailNotificationServiceImpl implements EmailNotificationService {
    private final JavaMailSender javaMailSender;
    
    @Override
    public void sendBookingConfirmationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId) {
        // Booking confirmation email logic
    }
    
    private String buildBookingConfirmationMessage(UserInfo user, Event event, int numberOfSeats, Long bookingId) {
        // Template method for building email content
    }
}
```

---

## 5. CONTROLLER LAYER - Presentation/API Layer

### 5.1 AuthController
**OOP Principles Used:**
- **Encapsulation**: Request/response handling logic
- **Abstraction**: Hides service layer complexity from clients

**System Design Patterns:**
- **MVC Pattern**: Controller handles HTTP requests and responses
- **Facade Pattern**: Simplifies authentication operations for clients
- **Command Pattern**: Each endpoint represents a specific action

**SOLID Principles:**
- **Single Responsibility**: Handles only authentication-related HTTP requests
- **Dependency Inversion**: Depends on service abstractions

```java
@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return jwtService.login(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return jwtService.logout();
    }
}
```

### 5.2 UserController
**OOP Principles Used:**
- **Polymorphism**: Multiple endpoint methods handling different HTTP operations
- **Encapsulation**: Request validation and response formatting

**System Design Patterns:**
- **RESTful API Pattern**: Resource-based URL design
- **Command Pattern**: Each endpoint executes specific user commands
- **Proxy Pattern**: Controller acts as proxy to service layer

```java
@RestController
@RequestMapping("/user")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/book/{eventId}/{numberOfSeats}")
    public ResponseEntity<?> bookEvent(@PathVariable Long eventId, @PathVariable int numberOfSeats) {
        return userService.bookEvent(eventId, numberOfSeats);
    }

    @GetMapping("/bookings")
    public ResponseEntity<?> getUserBookings() {
        return userService.getUserBookings();
    }
}
```

---

## 6. CONFIGURATION LAYER - System Configuration

### 6.1 SecurityConfig
**OOP Principles Used:**
- **Abstraction**: Complex security configuration abstracted into methods
- **Composition**: Combines multiple security components

**System Design Patterns:**
- **Builder Pattern**: HttpSecurity configuration using method chaining
- **Chain of Responsibility**: Security filter chain
- **Factory Pattern**: Bean creation methods

**SOLID Principles:**
- **Single Responsibility**: Handles only security configuration
- **Open/Closed**: Security rules can be extended without modifying existing code

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtFilter jwtFilter;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasRole("USER")
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
```

### 6.2 CustomUserDetailsService
**OOP Principles Used:**
- **Inheritance**: Implements UserDetailsService interface
- **Encapsulation**: User loading logic encapsulated

**System Design Patterns:**
- **Adapter Pattern**: Adapts UserInfo entity to Spring Security UserDetails
- **Factory Pattern**: Creates UserDetails objects

```java
@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Implementation
    }
}
```

---

## 7. FILTER LAYER - Request Processing

### 7.1 JwtFilter
**OOP Principles Used:**
- **Inheritance**: Extends OncePerRequestFilter
- **Encapsulation**: JWT processing logic contained within filter

**System Design Patterns:**
- **Chain of Responsibility**: Part of Spring Security filter chain
- **Template Method**: Follows OncePerRequestFilter template
- **Strategy Pattern**: Different token extraction strategies (header vs cookie)

```java
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) {
        // JWT processing logic
    }
    
    private String getJwtFromCookie(HttpServletRequest request) {
        // Cookie extraction logic
    }
}
```

---

## 8. EXCEPTION LAYER - Error Handling

### 8.1 GlobalExceptionHandler
**OOP Principles Used:**
- **Polymorphism**: Different exception types handled by appropriate methods
- **Encapsulation**: Exception handling logic centralized

**System Design Patterns:**
- **Observer Pattern**: Observes and handles exceptions globally
- **Strategy Pattern**: Different handling strategies for different exception types
- **Template Method**: Common exception response structure

**SOLID Principles:**
- **Single Responsibility**: Handles only exception processing
- **Open/Closed**: New exception handlers can be added without modification

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
```

---

## 9. MAPPING LAYER - Object Transformation

### 9.1 EventToDto Mapper
**OOP Principles Used:**
- **Abstraction**: Hides complex mapping logic behind static methods
- **Encapsulation**: Mapping logic contained in dedicated class

**System Design Patterns:**
- **Mapper Pattern**: Converts between entity and DTO representations
- **Factory Pattern**: Static methods create DTO objects
- **Builder Pattern**: Step-by-step DTO construction

**SOLID Principles:**
- **Single Responsibility**: Handles only Event to DTO mapping
- **Open/Closed**: New mapping methods can be added

```java
public class EventToDto {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    public static EventDto mapToDto(Event event) {
        // Mapping logic
    }
    
    public static EventResponseDto mapToResponseDto(Event event) {
        // Response DTO mapping
    }
}
```

---

## 10. MAIN APPLICATION CLASS

### 10.1 ServerApplication
**System Design Patterns:**
- **Singleton Pattern**: Spring Boot ensures single application instance
- **Factory Pattern**: SpringApplication.run() creates application context
- **Front Controller Pattern**: Handles all incoming requests through DispatcherServlet

```java
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
```

---

## ARCHITECTURAL PATTERNS SUMMARY

### 1. **Layered Architecture Pattern**
- **Presentation Layer**: Controllers handle HTTP requests/responses
- **Business Layer**: Services contain business logic
- **Data Access Layer**: Repositories handle data operations
- **Domain Layer**: Entities represent business objects

### 2. **MVC (Model-View-Controller) Pattern**
- **Model**: Entities and DTOs represent data
- **View**: REST API responses (JSON)
- **Controller**: Request handlers and routing

### 3. **Dependency Injection Pattern**
- Constructor injection used throughout (@AllArgsConstructor, @RequiredArgsConstructor)
- Spring manages object lifecycle and dependencies

### 4. **Repository Pattern**
- Data access abstracted through repository interfaces
- JPA provides concrete implementations

### 5. **DTO (Data Transfer Object) Pattern**
- Clean separation between internal entities and external API
- Validation and data transformation handled at DTO level

---

## SOLID PRINCIPLES IMPLEMENTATION

1. **Single Responsibility Principle (SRP)**: Each class has one reason to change
   - Controllers handle HTTP concerns only
   - Services handle business logic only
   - Repositories handle data access only

2. **Open/Closed Principle (OCP)**: Classes open for extension, closed for modification
   - Interface-based design allows new implementations
   - New exception handlers can be added without changing existing ones

3. **Liskov Substitution Principle (LSP)**: Subtypes must be substitutable for base types
   - Service implementations can replace interfaces
   - Custom UserDetails implements Spring Security's UserDetails

4. **Interface Segregation Principle (ISP)**: Clients shouldn't depend on unused methods
   - Specific service interfaces for different functionalities
   - Repository interfaces contain only relevant methods

5. **Dependency Inversion Principle (DIP)**: Depend on abstractions, not concretions
   - Services depend on repository interfaces, not implementations
   - Controllers depend on service interfaces, not implementations

This architecture demonstrates a well-structured application following OOP principles and established design patterns for maintainability, scalability, and testability.