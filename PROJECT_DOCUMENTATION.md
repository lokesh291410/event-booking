# Krashi Event Booking System - Complete Project Documentation

## Table of Contents
1. [Project Overview](#project-overview)
2. [Dependencies](#dependencies)
3. [Project Structure](#project-structure)
4. [Architecture Layers](#architecture-layers)
5. [Detailed Class Documentation](#detailed-class-documentation)

---

## Project Overview

**Project Name**: Krashi Event Booking System  
**Technology Stack**: Spring Boot, PostgreSQL, Redis, JWT Authentication  
**Java Version**: 17  
**Spring Boot Version**: 3.4.1  
**Purpose**: A comprehensive event booking platform with user management, event management, waitlist functionality, and email notifications.

### Key Features
- User Authentication & Authorization (JWT)
- Event Management (CRUD operations)
- Booking System with Waitlist
- Email Notifications
- Admin Panel
- Forgot Password with OTP
- Automatic Waitlist Promotion
- Role-based Access Control

---

## Dependencies

### Core Spring Boot Dependencies
- **spring-boot-starter-web**: REST API development
- **spring-boot-starter-data-jpa**: Database operations with JPA/Hibernate
- **spring-boot-starter-security**: Authentication and authorization
- **spring-boot-starter-validation**: Input validation
- **spring-boot-starter-mail**: Email functionality
- **spring-boot-starter-data-redis**: Redis for caching/sessions
- **spring-boot-starter-actuator**: Application monitoring

### Database & Persistence
- **postgresql**: PostgreSQL database driver
- **spring-boot-starter-data-jpa**: JPA repository pattern

### Security & JWT
- **jjwt-api**: JWT token creation and validation
- **jjwt-impl**: JWT implementation
- **jjwt-jackson**: JWT JSON processing

### Utilities
- **lombok**: Reduce boilerplate code
- **springdoc-openapi**: API documentation (Swagger)
- **spring-boot-devtools**: Development tools

---

## Project Structure

```
krashi.server/
├── configuration/          # Configuration classes
├── controller/             # REST Controllers
├── dto/                   # Data Transfer Objects
├── entity/                # JPA Entities
├── exception/             # Custom Exceptions & Handlers
├── filter/                # Security Filters
├── mapping/               # Entity-DTO Mapping utilities
├── repository/            # JPA Repositories
├── service/               # Service Interfaces
│   └── serviceImpl/       # Service Implementations
└── ServerApplication.java # Main Application Class
```

---

## Architecture Layers

### 1. **Presentation Layer** (`controller/`)
- Handles HTTP requests and responses
- Input validation and error handling
- RESTful API endpoints

### 2. **Business Logic Layer** (`service/`)
- Core business logic implementation
- Transaction management
- Data validation and processing

### 3. **Data Access Layer** (`repository/`)
- Database operations using Spring Data JPA
- Query methods and custom queries

### 4. **Data Transfer Layer** (`dto/`)
- Data transfer between layers
- Request/Response payload structures

### 5. **Security Layer** (`filter/`, `configuration/`)
- Authentication and authorization
- JWT token processing
- Security configurations

---

## Detailed Class Documentation

### 📁 Configuration Layer

#### `SecurityConfig`
**Purpose**: Configures Spring Security for the application  
**Dependencies**: `AuthenticationManager`, `JwtFilter`, `CustomUserDetailsService`  
**Key Methods**:
- `securityFilterChain(HttpSecurity http)`: Input: HttpSecurity configuration | Output: SecurityFilterChain bean | Purpose: Defines security rules, CORS, JWT filter chain
- `authenticationManager(AuthenticationConfiguration config)`: Input: AuthenticationConfiguration | Output: AuthenticationManager | Purpose: Configures authentication manager
- `passwordEncoder()`: Input: None | Output: BCryptPasswordEncoder | Purpose: Password encoding bean
- `corsConfigurationSource()`: Input: None | Output: CorsConfigurationSource | Purpose: CORS policy configuration

**Why Needed**: Protects endpoints, handles authentication, and manages security policies.

#### `RedisConfig`
**Purpose**: Configures Redis connection and templates  
**Dependencies**: Redis connection properties  
**Key Methods**:
- `redisTemplate()`: Input: None | Output: RedisTemplate<String, Object> | Purpose: Redis operations template with serializers
- `connectionFactory()`: Input: None | Output: LettuceConnectionFactory | Purpose: Redis connection factory
- `redisConnectionDetails()`: Input: None | Output: RedisConnectionDetails | Purpose: Connection configuration

**Why Needed**: Manages Redis for caching and session storage.

#### `CustomAccessDeniedHandler`
**Purpose**: Handles access denied exceptions in security layer  
**Dependencies**: `ObjectMapper`, `HttpServletResponse`, `AccessDeniedException`  
**Key Methods**:
- `handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)`: Input: Request, Response, Exception | Output: void | Purpose: Custom JSON error response for access denied

**ObjectMapper Usage**: Used to convert error response Map to JSON string for consistent API error format

**Why Needed**: Provides consistent error responses for authorization failures.

#### `CustomAuthenticationEntryPoint`
**Purpose**: Handles authentication failures  
**Dependencies**: `ObjectMapper`, `HttpServletResponse`, `AuthenticationException`  
**Key Methods**:
- `commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)`: Input: Request, Response, Exception | Output: void | Purpose: Custom JSON error response for authentication failures

**ObjectMapper Usage**: Used to convert error response Map to JSON string for consistent API error format

**Why Needed**: Standardizes authentication error responses.

#### `CustomUserDetails`
**Purpose**: Implements UserDetails for Spring Security integration  
**Dependencies**: `UserInfo` entity, Spring Security interfaces  
**Key Methods**:
- `getAuthorities()`: Input: None | Output: Collection<GrantedAuthority> | Purpose: Returns user roles as authorities
- `getPassword()`: Input: None | Output: String | Purpose: Returns encoded password
- `getUsername()`: Input: None | Output: String | Purpose: Returns username
- `isAccountNonExpired()`: Input: None | Output: boolean | Purpose: Account expiration status
- `isAccountNonLocked()`: Input: None | Output: boolean | Purpose: Account lock status
- `isCredentialsNonExpired()`: Input: None | Output: boolean | Purpose: Credentials expiration status
- `isEnabled()`: Input: None | Output: boolean | Purpose: Account enabled status

**Why Needed**: Bridges application user entity with Spring Security.

#### `CustomUserDetailsService`
**Purpose**: Loads user details for authentication  
**Dependencies**: `UserInfoRepository`  
**Key Methods**:
- `loadUserByUsername(String username)`: Input: String username | Output: UserDetails | Purpose: Loads user for authentication, handles account locking

**Why Needed**: Integrates custom user repository with Spring Security.

#### `AuthenticationConfig`
**Purpose**: Configures authentication components  
**Dependencies**: `CustomUserDetailsService`, `PasswordEncoder`  
**Key Methods**:
- `authenticationManager(AuthenticationConfiguration config)`: Input: AuthenticationConfiguration | Output: AuthenticationManager | Purpose: Authentication manager bean
- `authenticationProvider()`: Input: None | Output: DaoAuthenticationProvider | Purpose: Custom authentication provider
- `passwordEncoder()`: Input: None | Output: BCryptPasswordEncoder | Purpose: Password encoder bean

**Why Needed**: Sets up authentication infrastructure.

---

### 📁 Entity Layer

#### `UserInfo`
**Purpose**: Represents user data in the database  
**Dependencies**: JPA annotations, Lombok  
**Key Fields**: `id` (Long), `userName` (String), `email` (String), `password` (String), `role` (String), `name` (String), `isAccountNonLocked` (Boolean), `failedAttempt` (Integer), `lockTime` (Date)  
**Relationships**: 
- OneToMany with `Booking` (user bookings)
- OneToMany with `Waitlist` (user waitlist entries)
- OneToMany with `EventFeedback` (user feedback)
- OneToMany with `Event` (created events)

**Validation Constraints**:
- `userName`: unique, not null, 3-50 characters
- `email`: unique, not null, valid email format
- `password`: not null, minimum 8 characters (before encoding)
- `role`: default "USER", enum values (USER, ADMIN)

**Why Needed**: Core user entity for authentication and user management.

#### `Event`
**Purpose**: Represents events in the system  
**Dependencies**: JPA annotations, Lombok  
**Key Fields**: `id` (Long), `title` (String), `description` (String), `dateTime` (LocalDateTime), `location` (String), `totalSeats` (Integer), `availableSeats` (Integer), `category` (String), `status` (EventStatus), `price` (BigDecimal), `createdAt` (LocalDateTime), `updatedAt` (LocalDateTime)  
**Relationships**: 
- ManyToOne with `UserInfo` (createdBy)
- OneToMany with `Booking` (event bookings)
- OneToMany with `Waitlist` (event waitlist)
- OneToMany with `EventFeedback` (event feedback)

**Validation Constraints**:
- `title`: not blank, max 200 characters
- `description`: not blank, max 2000 characters
- `dateTime`: future date required
- `totalSeats`: minimum 1, maximum 10000
- `price`: minimum 0, max 2 decimal places

**Why Needed**: Central entity for event management functionality.

#### `Booking`
**Purpose**: Represents user bookings for events  
**Dependencies**: JPA annotations, Lombok  
**Key Fields**: `id` (Long), `numberOfSeats` (Integer), `bookingDateTime` (LocalDateTime), `status` (BookingStatus), `totalAmount` (BigDecimal), `paymentStatus` (String), `cancellationReason` (String)  
**Relationships**: 
- ManyToOne with `UserInfo` (booking user)
- ManyToOne with `Event` (booked event)

**Validation Constraints**:
- `numberOfSeats`: minimum 1, maximum 10
- `status`: enum values (CONFIRMED, CANCELLED, PENDING)
- `totalAmount`: calculated field, non-negative

**Why Needed**: Tracks user bookings and seat allocation.

#### `Waitlist`
**Purpose**: Manages waitlist when events are full  
**Dependencies**: JPA annotations, Lombok  
**Key Fields**: `id` (Long), `requestedSeats` (Integer), `status` (WaitlistStatus), `joinedAt` (LocalDateTime), `notifiedAt` (LocalDateTime), `priority` (Integer), `expiryTime` (LocalDateTime)  
**Relationships**: 
- ManyToOne with `UserInfo` (waitlisted user)
- ManyToOne with `Event` (waitlisted event)

**Validation Constraints**:
- `requestedSeats`: minimum 1, maximum 10
- `status`: enum values (WAITING, PROMOTED, EXPIRED, CANCELLED)
- `joinedAt`: automatically set on creation
- `priority`: calculated based on join time

**Why Needed**: Handles automatic promotion when seats become available.

#### `EventFeedback`
**Purpose**: Stores user feedback for events  
**Dependencies**: JPA annotations, Lombok  
**Key Fields**: `id` (Long), `rating` (Double), `comment` (String), `suggestions` (String), `wouldRecommend` (Boolean), `submittedAt` (LocalDateTime), `isPublic` (Boolean), `helpfulCount` (Integer)  
**Relationships**: 
- ManyToOne with `UserInfo` (feedback author)
- ManyToOne with `Event` (feedback target)

**Validation Constraints**:
- `rating`: required, range 1.0-5.0, one decimal place
- `comment`: max 1000 characters
- `suggestions`: max 500 characters
- `submittedAt`: automatically set
- One feedback per user per event

**Why Needed**: Collects user feedback for event improvement.

---

### 📁 Repository Layer

#### `UserInfoRepository`
**Purpose**: Data access for user operations  
**Dependencies**: `JpaRepository<UserInfo, Long>`  
**Key Methods**:
- `findByUserName(String userName)`: Input: String | Output: Optional<UserInfo> | Purpose: Find user by username for authentication
- `findByEmail(String email)`: Input: String | Output: Optional<UserInfo> | Purpose: Find user by email for password reset
- `existsByUserName(String userName)`: Input: String | Output: boolean | Purpose: Check username availability during registration
- `existsByEmail(String email)`: Input: String | Output: boolean | Purpose: Check email availability during registration
- `findByRole(String role)`: Input: String | Output: List<UserInfo> | Purpose: Get users by role (ADMIN/USER)
- `findByIsAccountNonLocked(Boolean locked)`: Input: Boolean | Output: List<UserInfo> | Purpose: Find locked/unlocked accounts

**Why Needed**: Provides database operations for user management.

#### `EventRepository`
**Purpose**: Data access for event operations  
**Dependencies**: `JpaRepository<Event, Long>`  
**Key Methods**:
- `findByStatus(EventStatus status)`: Input: EventStatus | Output: List<Event> | Purpose: Find events by status (DRAFT, PUBLISHED, CANCELLED)
- `findByCreatedBy_Id(Long creatorId)`: Input: Long | Output: List<Event> | Purpose: Find events created by specific user
- `findUpcomingPublicEvents(@Param("now") LocalDateTime now)`: Input: LocalDateTime | Output: List<Event> | Purpose: Custom query for upcoming published events
- `findByCategoryAndStatus(String category, EventStatus status)`: Input: String, EventStatus | Output: List<Event> | Purpose: Filter events by category and status
- `findByDateTimeBetween(LocalDateTime start, LocalDateTime end)`: Input: LocalDateTime, LocalDateTime | Output: List<Event> | Purpose: Find events in date range
- `findByLocationContainingIgnoreCase(String location)`: Input: String | Output: List<Event> | Purpose: Search events by location
- `findByTitleContainingIgnoreCase(String title)`: Input: String | Output: List<Event> | Purpose: Search events by title

**Why Needed**: Handles complex event queries and filtering.

#### `BookingRepository`
**Purpose**: Data access for booking operations  
**Dependencies**: `JpaRepository<Booking, Long>`  
**Key Methods**:
- `findByUser_Id(Long userId)`: Input: Long | Output: List<Booking> | Purpose: Get all bookings for a user
- `findByEvent_Id(Long eventId)`: Input: Long | Output: List<Booking> | Purpose: Get all bookings for an event
- `existsByUser_IdAndEvent_Id(Long userId, Long eventId)`: Input: Long, Long | Output: boolean | Purpose: Check if user already booked event
- `countConfirmedBookingsByEventId(@Param("eventId") Long eventId)`: Input: Long | Output: Long | Purpose: Count confirmed bookings for seat calculation
- `findByStatusAndEvent_Id(BookingStatus status, Long eventId)`: Input: BookingStatus, Long | Output: List<Booking> | Purpose: Get bookings by status for specific event
- `findByUser_IdAndStatus(Long userId, BookingStatus status)`: Input: Long, BookingStatus | Output: List<Booking> | Purpose: Get user bookings by status
- `findByBookingDateTimeBetween(LocalDateTime start, LocalDateTime end)`: Input: LocalDateTime, LocalDateTime | Output: List<Booking> | Purpose: Get bookings in date range

**Why Needed**: Manages booking data and prevents duplicate bookings.

#### `WaitlistRepository`
**Purpose**: Data access for waitlist operations  
**Dependencies**: `JpaRepository<Waitlist, Long>`  
**Key Methods**:
- `findByEvent_IdAndStatusOrderByJoinedAtAsc(Long eventId, WaitlistStatus status)`: Input: Long, WaitlistStatus | Output: List<Waitlist> | Purpose: Get waitlist in FIFO order for promotions
- `existsByUser_IdAndEvent_Id(Long userId, Long eventId)`: Input: Long, Long | Output: boolean | Purpose: Check if user is already waitlisted
- `countByEvent_IdAndStatus(Long eventId, WaitlistStatus status)`: Input: Long, WaitlistStatus | Output: Long | Purpose: Count waitlist entries
- `findByUser_IdAndStatus(Long userId, WaitlistStatus status)`: Input: Long, WaitlistStatus | Output: List<Waitlist> | Purpose: Get user's waitlist entries by status
- `findByStatusAndJoinedAtBefore(WaitlistStatus status, LocalDateTime time)`: Input: WaitlistStatus, LocalDateTime | Output: List<Waitlist> | Purpose: Find expired waitlist entries
- `deleteByEvent_IdAndUser_Id(Long eventId, Long userId)`: Input: Long, Long | Output: void | Purpose: Remove user from waitlist

**Why Needed**: Manages waitlist functionality and automatic promotions.

#### `EventFeedbackRepository`
**Purpose**: Data access for feedback operations  
**Dependencies**: `JpaRepository<EventFeedback, Long>`  
**Key Methods**:
- `findByEvent_Id(Long eventId)`: Input: Long | Output: List<EventFeedback> | Purpose: Get all feedback for an event
- `getAverageRatingByEventId(@Param("eventId") Long eventId)`: Input: Long | Output: Double | Purpose: Calculate average rating for event
- `existsByUser_IdAndEvent_Id(Long userId, Long eventId)`: Input: Long, Long | Output: boolean | Purpose: Check if user already submitted feedback
- `findByUser_Id(Long userId)`: Input: Long | Output: List<EventFeedback> | Purpose: Get all feedback by user
- `findByEvent_IdAndIsPublic(Long eventId, Boolean isPublic)`: Input: Long, Boolean | Output: List<EventFeedback> | Purpose: Get public feedback for display
- `countByEvent_IdAndRating(Long eventId, Double rating)`: Input: Long, Double | Output: Long | Purpose: Count feedback by rating (for star distribution)
- `findByRatingGreaterThanEqual(Double rating)`: Input: Double | Output: List<EventFeedback> | Purpose: Find high-rated feedback

**Why Needed**: Handles feedback collection and rating calculations.

---

### 📁 Service Layer

#### `AuthenticationService` (Interface) & `AuthenticationServiceImpl`
**Purpose**: Handles user authentication and authorization  
**Dependencies**: `UserInfoRepository`, `SecurityContextHolder`, `Authentication`  
**Key Methods**:
- `getCurrentUser()`: Input: None | Output: UserInfo | Purpose: Get currently authenticated user from security context
- `getCurrentAdmin()`: Input: None | Output: UserInfo | Purpose: Get current user and verify admin role
- `getCurrentUserId()`: Input: None | Output: Long | Purpose: Get current user ID for service operations
- `getCurrentAdminId()`: Input: None | Output: Long | Purpose: Get current admin user ID
- `getCurrentUsername()`: Input: None | Output: String | Purpose: Get username of current authenticated user
- `isCurrentUserAdmin()`: Input: None | Output: boolean | Purpose: Check if current user has admin role
- `isCurrentUser(Long userId)`: Input: Long | Output: boolean | Purpose: Check if given user ID matches current user

**Why Needed**: Centralized authentication logic for security across the application.

#### `UserService` (Interface) & `UserServiceImpl`
**Purpose**: User-related business operations  
**Dependencies**: `EventRepository`, `BookingRepository`, `WaitlistRepository`, `EventFeedbackRepository`, `AuthenticationService`, `WaitlistPromotionService`, `EmailNotificationService`, `BookingToDto`, `EventToDto`, `EventFeedbackToDto`  
**Key Methods**:
- `bookEvent(Long eventId, int numberOfSeats)`: Input: Long, int | Output: ResponseEntity<?> | Purpose: Book event or add to waitlist automatically
- `cancelBooking(Long bookingId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Cancel booking and trigger waitlist promotions
- `getBookingDetails(Long bookingId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get specific booking details
- `getUserBookings()`: Input: None | Output: ResponseEntity<?> | Purpose: Get current user's bookings
- `getUserWaitlist()`: Input: None | Output: ResponseEntity<?> | Purpose: Get user's waitlist entries
- `removeFromWaitlist(Long waitlistId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Remove user from waitlist
- `submitEventFeedback(EventFeedbackDto feedbackDto)`: Input: EventFeedbackDto | Output: ResponseEntity<?> | Purpose: Submit event feedback
- `getUserFeedback()`: Input: None | Output: ResponseEntity<?> | Purpose: Get user's submitted feedback
- `getUpcomingEvents()`: Input: None | Output: ResponseEntity<?> | Purpose: Get published upcoming events
- `getEventsByCategory(String category)`: Input: String | Output: ResponseEntity<?> | Purpose: Get events filtered by category
- `searchEvents(String keyword)`: Input: String | Output: ResponseEntity<?> | Purpose: Search events by keyword

**Constants**: `MAX_SEATS_PER_BOOKING = 5` - Maximum seats allowed per booking

**Why Needed**: Implements core user functionality with automatic waitlist management.

#### `AdminService` (Interface) & `AdminServiceImpl`
**Purpose**: Admin-specific operations  
**Dependencies**: `EventRepository`, `BookingRepository`, `WaitlistRepository`, `AuthenticationService`, `EventFeedbackRepository`  
**Key Methods**:
- `createEvent(EventDto eventDto)`: Input: EventDto | Output: ResponseEntity<?> | Purpose: Create new events
- `updateEvent(Long eventId, EventDto eventDto)`: Input: Long, EventDto | Output: ResponseEntity<?> | Purpose: Update event details
- `deleteEvent(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Delete events
- `publishEvent(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Publish draft events
- `cancelEvent(Long eventId, String reason)`: Input: Long, String | Output: ResponseEntity<?> | Purpose: Cancel events with reason
- `getEventStatistics(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get event analytics
- `getEventBookings(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get event bookings
- `getEventWaitlist(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get event waitlist
- `getEventFeedback(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get event feedback
- `getAdminEvents()`: Input: None | Output: ResponseEntity<?> | Purpose: Get all events for admin
- `getEventDetails(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get event details
- `notifyWaitlistUsers(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Notify waitlist users

**Why Needed**: Provides admin functionality for event management and analytics.

#### `WaitlistPromotionService` (Interface) & `WaitlistPromotionServiceImpl`
**Purpose**: Handles automatic waitlist promotions  
**Dependencies**: `WaitlistRepository`, `BookingRepository`, `EventRepository`  
**Key Methods**:
- `processWaitlistPromotions(Event event, int availableSeats)`: Input: Event, int | Output: PromotionResultDto | Purpose: Promote waitlisted users when seats become available
- `hasWaitlistUsers(Long eventId)`: Input: Long | Output: boolean | Purpose: Check if event has waiting users
- `getWaitlistCount(Long eventId)`: Input: Long | Output: long | Purpose: Count total waitlist entries for event

**Why Needed**: Separates waitlist promotion logic for maintainability and reusability.

#### `EmailNotificationService` (Interface) & `EmailNotificationServiceImpl`
**Purpose**: Handles all email communications  
**Dependencies**: `JavaMailSender`  
**Key Methods**:
- `sendBookingConfirmationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId)`: Input: UserInfo, Event, int, Long | Output: void | Purpose: Send booking confirmation with details
- `sendWaitlistConfirmationEmail(UserInfo user, Event event, int requestedSeats)`: Input: UserInfo, Event, int | Output: void | Purpose: Send waitlist confirmation
- `sendPromotionNotificationEmails(List<PromotedUserDto> promotedUsers)`: Input: List<PromotedUserDto> | Output: void | Purpose: Notify all promoted users
- `sendPromotionNotificationEmail(PromotedUserDto promotedUser)`: Input: PromotedUserDto | Output: void | Purpose: Notify individual promoted user
- `sendBookingCancellationEmail(UserInfo user, Event event, int numberOfSeats, Long bookingId)`: Input: UserInfo, Event, int, Long | Output: void | Purpose: Send cancellation confirmation
- `sendNotificationEmail(String userEmail, String subject, String message)`: Input: String, String, String | Output: void | Purpose: Send general notification emails

**Why Needed**: Centralized email functionality with professional templates.

#### `JwtService` (Interface) & `JwtServiceImpl`
**Purpose**: JWT token management and authentication  
**Dependencies**: JWT libraries, application properties  
**Key Methods**:
- `generateToken(String username)`: Input: String | Output: String | Purpose: Create JWT token for authenticated user
- `login(LoginRequest loginRequest)`: Input: LoginRequest | Output: ResponseEntity<LoginResponse> | Purpose: Authenticate user and return login response
- `extractUsername(String jwtToken)`: Input: String | Output: String | Purpose: Extract username from token claims
- `isTokenValid(String jwtToken, UserDetails userDetails)`: Input: String, UserDetails | Output: boolean | Purpose: Validate token integrity and expiration
- `logout()`: Input: None | Output: ResponseEntity<?> | Purpose: Handle user logout

**Why Needed**: Secure stateless authentication using JWT tokens.

#### `OtpService` (Interface) & `OtpServiceImpl`
**Purpose**: OTP generation and validation  
**Dependencies**: `RedisTemplate`  
**Key Methods**:
- `generateOtp()`: Input: None | Output: String | Purpose: Generate random OTP
- `storeOtp(String email, String otp)`: Input: String, String | Output: void | Purpose: Store OTP in Redis with expiry
- `validateOtp(String email, String otp)`: Input: String, String | Output: boolean | Purpose: Verify OTP against stored value
- `deleteOtp(String email)`: Input: String | Output: void | Purpose: Remove OTP after successful use

**Why Needed**: Secure password reset functionality with time-limited OTPs.

#### `ForgotPasswordService` (Interface) & `ForgotPasswordServiceImpl`
**Purpose**: Password reset workflow  
**Dependencies**: `UserInfoRepository`, `OtpService`, `EmailService`  
**Key Methods**:
- `sendOtp(ForgotPasswordRequestDto request)`: Input: ForgotPasswordRequestDto | Output: ForgotPasswordResponseDto | Purpose: Initiate password reset by sending OTP
- `verifyOtpAndResetPassword(VerifyOtpRequestDto request)`: Input: VerifyOtpRequestDto | Output: ForgotPasswordResponseDto | Purpose: Validate OTP and reset password

**Why Needed**: Secure password recovery process for users.

#### `SignUpService` (Interface) & `SignUpServiceImpl`
**Purpose**: User registration and public event access  
**Dependencies**: `UserInfoRepository`, `PasswordEncoder`, `EventRepository`  
**Key Methods**:
- `signUp(String username, String name, String email, String password, String role)`: Input: String, String, String, String, String | Output: ResponseEntity<?> | Purpose: Create new user accounts
- `getAllEvents()`: Input: None | Output: ResponseEntity<?> | Purpose: Get all events (public access)
- `getPublishedEvents()`: Input: None | Output: ResponseEntity<?> | Purpose: Get published events only
- `getEventDetails(Long eventId)`: Input: Long | Output: ResponseEntity<?> | Purpose: Get event details (public access)
- `getEventCategories()`: Input: None | Output: ResponseEntity<?> | Purpose: Get available event categories

**Why Needed**: Handles user registration and provides public event access.

---

### 📁 Controller Layer

#### `AuthController`
**Purpose**: Authentication endpoints  
**Dependencies**: `JwtService`  
**Endpoints**: 
- `POST /auth/login`: Input: LoginRequest (userName, password) | Output: LoginResponse | Purpose: User authentication
- `POST /auth/logout`: Input: None | Output: ResponseEntity<?> | Purpose: User logout

**Error Handling**: Returns appropriate HTTP status codes based on authentication result

**Why Needed**: Provides authentication API endpoints.

#### `UserController`
**Purpose**: User-specific endpoints  
**Dependencies**: `UserService`  
**Endpoints**:
- `POST /user/book/{eventId}/{numberOfSeats}`: Input: Long eventId, int numberOfSeats | Output: ResponseEntity<?> | Purpose: Book events
- `POST /user/cancel/{bookingId}`: Input: Long bookingId | Output: ResponseEntity<?> | Purpose: Cancel bookings
- `GET /user/details/{bookingId}`: Input: Long bookingId | Output: ResponseEntity<?> | Purpose: Get booking details
- `GET /user/bookings`: Input: None (uses auth) | Output: ResponseEntity<?> | Purpose: Get user's bookings
- `GET /user/waitlist`: Input: None (uses auth) | Output: ResponseEntity<?> | Purpose: Get user's waitlist entries
- `DELETE /user/waitlist/{waitlistId}`: Input: Long waitlistId | Output: ResponseEntity<?> | Purpose: Remove from waitlist
- `POST /user/feedback`: Input: EventFeedbackDto | Output: ResponseEntity<?> | Purpose: Submit event feedback
- `GET /user/feedback`: Input: None (uses auth) | Output: ResponseEntity<?> | Purpose: Get user's feedback
- `GET /user/events/upcoming`: Input: None | Output: ResponseEntity<?> | Purpose: Get upcoming events
- `GET /user/events/category`: Input: String category (query param) | Output: ResponseEntity<?> | Purpose: Get events by category
- `GET /user/events/search`: Input: String keyword (query param) | Output: ResponseEntity<?> | Purpose: Search events by keyword

**Security**: All endpoints require USER role authentication

**Why Needed**: Exposes user functionality through REST API.

#### `AdminController`
**Purpose**: Admin-specific endpoints  
**Dependencies**: `AdminService`  
**Endpoints**:
- `POST /admin/event`: Input: EventDto | Output: ResponseEntity<?> | Purpose: Create new events
- `PUT /admin/event/{eventId}`: Input: Long eventId, EventDto | Output: ResponseEntity<?> | Purpose: Update event details
- `DELETE /admin/event/{eventId}`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Delete events
- `POST /admin/event/{eventId}/publish`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Publish draft events
- `POST /admin/event/{eventId}/cancel`: Input: Long eventId, String reason (query param) | Output: ResponseEntity<?> | Purpose: Cancel events
- `GET /admin/event/{eventId}/statistics`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Get event analytics
- `GET /admin/event/{eventId}/bookings`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Get event bookings
- `GET /admin/event/{eventId}/waitlist`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Get event waitlist
- `GET /admin/event/{eventId}/feedback`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Get event feedback
- `GET /admin/events`: Input: None | Output: ResponseEntity<?> | Purpose: Get all events for admin
- `GET /admin/event/{eventId}`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Get event details
- `POST /admin/event/{eventId}/notify-waitlist`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Notify waitlist users

**Security**: All endpoints require ADMIN role authentication

**Why Needed**: Provides admin functionality through REST API.

#### `ForgotPasswordController`
**Purpose**: Password reset endpoints  
**Dependencies**: `ForgotPasswordService`  
**Endpoints**:
- `POST /api/auth/forgot-password`: Input: ForgotPasswordRequestDto (email) | Output: ResponseEntity<ForgotPasswordResponseDto> | Purpose: Send OTP to email
- `POST /api/auth/verify-otp`: Input: VerifyOtpRequestDto (email, otp, newPassword) | Output: ResponseEntity<ForgotPasswordResponseDto> | Purpose: Verify OTP and reset password

**Security**: Public endpoints (no authentication required)

**Why Needed**: Handles password recovery workflow.

#### `SignupController`
**Purpose**: User registration and public event endpoints  
**Dependencies**: `SignUpService`  
**Endpoints**:
- `POST /signup`: Input: UserInfoDto | Output: ResponseEntity<?> | Purpose: User registration
- `GET /events`: Input: None | Output: ResponseEntity<?> | Purpose: Get all events (public)
- `GET /events/published`: Input: None | Output: ResponseEntity<?> | Purpose: Get published events
- `GET /events/{eventId}`: Input: Long eventId | Output: ResponseEntity<?> | Purpose: Get event details (public)
- `GET /events/categories`: Input: None | Output: ResponseEntity<?> | Purpose: Get event categories

**Security**: Public endpoints (no authentication required)

**Why Needed**: Handles user registration and public event access.

---

### 📁 DTO Layer

#### `PromotionResultDto` & `PromotedUserDto`
**Purpose**: Transfer promotion data between services  
**Fields**: 
- `PromotionResultDto`: promotedUsers (List<PromotedUserDto>), totalPromoted (Integer), remainingWaitlistCount (Integer), eventId (Long)
- `PromotedUserDto`: userId (Long), userName (String), userEmail (String), eventId (Long), eventTitle (String), seatsPromoted (int), newBookingId (Long)

**Validation**: No direct validation (internal DTOs)

**Why Needed**: Clean data transfer for waitlist promotion results.

#### `EventDto` & `EventResponseDto`
**Purpose**: Event data transfer  
**Fields**: 
- `EventDto`: title (@NotBlank @Size(3,100)), description (@Size(max=1000)), date (@NotBlank @Pattern), time (@NotBlank @Pattern), endDate (@Pattern), endTime (@Pattern), location (@NotBlank), totalSeats (@Min(1) @Max(10000)), category (@NotBlank), imageUrl, price (@Min(0)), organizerName (@NotBlank), organizerEmail (@NotBlank @Email)
- `EventResponseDto`: id, title, description, date, time, endDate, endTime, location, totalSeats, availableSeats, category, imageUrl, price, organizerName, organizerEmail, status, createdAt, updatedAt, createdById, createdByName

**Validation**: Comprehensive input validation on EventDto with date/time patterns and size constraints

**Why Needed**: Separates internal entity structure from API contracts.

#### `BookingDto`
**Purpose**: Booking data transfer  
**Fields**: id (Long), status (String), numberOfSeats (int), bookingdate (String), bookingtime (String), eventId (Long), eventTitle (String), eventDate (String), eventTime (String)

**Validation**: No validation (response DTO)

**Why Needed**: Structured booking data for client applications.

#### `EventDetailResponseDto`
**Purpose**: Comprehensive event information with nested DTOs  
**Fields**: All EventResponseDto fields plus totalBookings (int), bookedSeats (int), waitlistCount (int), averageRating (double), totalFeedbacks (int), recentBookings (List<BookingDto>), waitlistUsers (List<WaitlistSummaryDto>), recentFeedbacks (List<FeedbackSummaryDto>)

**Nested DTOs**:
- `WaitlistSummaryDto`: id (Long), userName (String), userEmail (String), requestedSeats (int), status (String), joinedAt (LocalDateTime), notifiedAt (LocalDateTime)
- `FeedbackSummaryDto`: id (Long), userName (String), rating (int), comment (String), wouldRecommend (boolean), submittedAt (LocalDateTime)

**Why Needed**: Rich event details for admin and user interfaces.

#### Request/Response DTOs with Validation

**`LoginRequest` & `LoginResponse`**:
- `LoginRequest`: username (@NotBlank), password (@NotBlank)
- `LoginResponse`: success (boolean), message (String), username (String)

**`UserInfoDto`**:
- **Fields**: username (@NotBlank), name (@NotBlank), email (@NotBlank @Pattern), password (@NotBlank @Size(min=8)), role (@NotBlank)
- **Purpose**: User registration data transfer

**`ForgotPasswordRequestDto`, `VerifyOtpRequestDto` & `ForgotPasswordResponseDto`**:
- `ForgotPasswordRequestDto`: email (@NotBlank @Email)
- `VerifyOtpRequestDto`: email (@NotBlank @Email), otp (@NotBlank @Pattern(regexp="\\d{6}")), newPassword (@NotBlank @Size(8,100))
- `ForgotPasswordResponseDto`: success (Boolean), message (String), otpExpiry (LocalDateTime)

**`EventFeedbackDto` & `UserFeedbackResponseDto`**:
- `EventFeedbackDto`: eventId (Long), rating (int), comment (String), suggestions (String), wouldRecommend (boolean)
- `UserFeedbackResponseDto`: id (Long), eventTitle (String), rating (Double), comment (String), submittedAt (LocalDateTime), canEdit (Boolean)

**Why Needed**: Type-safe API contracts with comprehensive input validation and structured responses.

---

### 📁 Exception Layer

#### `GlobalExceptionHandler`
**Purpose**: Centralized exception handling  
**Dependencies**: Spring's `@RestControllerAdvice`, `ResponseEntity`, `HttpStatus`  
**Key Methods**:
- `handleResourceNotFoundException(ResourceNotFoundException ex)`: Input: ResourceNotFoundException | Output: ResponseEntity<String> | Purpose: Handle 404 errors
- `handleBadRequestException(BadRequestException ex)`: Input: BadRequestException | Output: ResponseEntity<String> | Purpose: Handle 400 validation errors
- `handleAccessDeniedException(AccessDeniedException ex)`: Input: AccessDeniedException | Output: ResponseEntity<String> | Purpose: Handle 403 authorization errors
- `handleInsufficientSeatsException(InsufficientSeatsException ex)`: Input: InsufficientSeatsException | Output: ResponseEntity<String> | Purpose: Handle booking conflicts
- `handleInvalidOtpException(InvalidOtpException ex)`: Input: InvalidOtpException | Output: ResponseEntity<String> | Purpose: Handle OTP validation errors
- `handleOtpExpiredException(OtpExpiredException ex)`: Input: OtpExpiredException | Output: ResponseEntity<String> | Purpose: Handle expired OTP errors
- `handleEmailSendingException(EmailSendingException ex)`: Input: EmailSendingException | Output: ResponseEntity<String> | Purpose: Handle email service failures
- `handleDateTimeParseException(DateTimeParseException ex)`: Input: DateTimeParseException | Output: ResponseEntity<String> | Purpose: Handle date/time format errors
- `handleValidationExceptions(MethodArgumentNotValidException ex)`: Input: MethodArgumentNotValidException | Output: ResponseEntity<String> | Purpose: Handle Bean Validation errors
- `handleIllegalArgumentException(IllegalArgumentException ex)`: Input: IllegalArgumentException | Output: ResponseEntity<String> | Purpose: Handle invalid arguments
- `handleBadCredentialsException(BadCredentialsException ex)`: Input: BadCredentialsException | Output: ResponseEntity<String> | Purpose: Handle authentication failures
- `handleInvalidCredentialsException(InvalidCredentialsException ex)`: Input: InvalidCredentialsException | Output: ResponseEntity<String> | Purpose: Handle custom credential errors
- `handleJwtTokenExpiredException(JwtTokenExpiredException ex)`: Input: JwtTokenExpiredException | Output: ResponseEntity<String> | Purpose: Handle JWT token expiry
- `handleExpiredJwtException(ExpiredJwtException ex)`: Input: ExpiredJwtException | Output: ResponseEntity<String> | Purpose: Handle JWT library expiry
- `handleGlobalException(Exception ex)`: Input: Exception | Output: ResponseEntity<String> | Purpose: Fallback for unexpected errors

**Why Needed**: Consistent error responses across the application.

#### Custom Exception Classes

**`ResourceNotFoundException`**
**Purpose**: Thrown when requested resources don't exist  
**Constructor Methods**:
- `ResourceNotFoundException(String message)`: Input: String | Purpose: Create exception with custom message

**`AccessDeniedException`**
**Purpose**: Thrown for authorization failures  
**Constructor Methods**:
- `AccessDeniedException(String message)`: Input: String | Purpose: Create exception with access denial reason

**`BadRequestException`**
**Purpose**: Thrown for invalid requests and business logic violations  
**Constructor Methods**:
- `BadRequestException(String message)`: Input: String | Purpose: Create exception with validation failure reason

**`InvalidCredentialsException`**
**Purpose**: Thrown for authentication failures  
**Constructor Methods**:
- `InvalidCredentialsException(String message)`: Input: String | Purpose: Create exception with authentication failure reason

**`JwtTokenExpiredException`**
**Purpose**: Thrown for expired JWT tokens  
**Constructor Methods**:
- `JwtTokenExpiredException(String message)`: Input: String | Purpose: Create exception for expired tokens

**`InvalidOtpException`**
**Purpose**: Thrown for invalid OTP validation  
**Constructor Methods**:
- `InvalidOtpException(String message)`: Input: String | Purpose: Create exception for OTP validation failures

**`OtpExpiredException`**
**Purpose**: Thrown for expired OTP scenarios  
**Constructor Methods**:
- `OtpExpiredException(String message)`: Input: String | Purpose: Create exception for expired OTP

**`EmailSendingException`**
**Purpose**: Thrown for email delivery failures  
**Constructor Methods**:
- `EmailSendingException(String message)`: Input: String | Purpose: Create exception for email failures
- `EmailSendingException(String message, Throwable cause)`: Input: String, Throwable | Purpose: Create exception with cause

**`InsufficientSeatsException`**
**Purpose**: Thrown when event booking fails due to seat availability  
**Constructor Methods**:
- `InsufficientSeatsException(String message)`: Input: String | Purpose: Create exception for booking failures

**Why Needed**: Specific error handling for different failure scenarios with detailed context and proper HTTP status codes.

---

### 📁 Security & Filter Layer

#### `JwtFilter`
**Purpose**: JWT token validation for each request  
**Dependencies**: `JwtService`, `CustomUserDetailsService`  
**Key Methods**:
- `doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`: Input: Request, Response, FilterChain | Output: void | Purpose: Validate JWT from Authorization header or cookies and set authentication context

**Implementation Features**:
- **Token Extraction**: Checks Authorization header (Bearer token) and cookies (accessToken)
- **Token Validation**: Uses JwtService for token validation and user extraction
- **Authentication Setup**: Sets SecurityContextHolder with authenticated user
- **Exception Handling**: Basic try-catch with filter chain continuation
- **Cookie Support**: Reads JWT token from accessToken cookie if header is missing

**Why Needed**: Secures API endpoints with token-based authentication from headers or cookies.

#### `CustomUserDetailsService`
**Purpose**: Load user details for Spring Security  
**Dependencies**: `UserInfoRepository`  
**Key Methods**:
- `loadUserByUsername(String username)`: Input: String | Output: UserDetails | Purpose: Load user by username and return CustomUserDetails

**Implementation Features**:
- **User Lookup**: Finds user by username in database
- **Exception Handling**: Throws `UsernameNotFoundException` if user not found
- **UserDetails Creation**: Returns `CustomUserDetails` wrapping the `UserInfo` entity

**Why Needed**: Integrates application users with Spring Security authentication.

### 📁 Mapping Layer

#### `EventToDto`, `BookingToDto`, `EventFeedbackToDto`
**Purpose**: Convert entities to DTOs  
**Key Methods**:

**`EventToDto`**:
**Dependencies**: `DateTimeFormatter`
- `mapToDto(Event event)`: Input: Event | Output: EventDto | Purpose: Convert entity to DTO for requests
- `mapToResponseDto(Event event)`: Input: Event | Output: EventResponseDto | Purpose: Convert entity to response DTO

**`BookingToDto`**:
- `mapToDto(Booking booking)`: Input: Booking | Output: BookingDto | Purpose: Convert booking entity to DTO
- `mapToResponseDto(Booking booking)`: Input: Booking | Output: BookingResponseDto | Purpose: Convert booking to response DTO

**`EventFeedbackToDto`**:
- `mapToDto(EventFeedback feedback)`: Input: EventFeedback | Output: EventFeedbackDto | Purpose: Convert feedback entity to DTO
- `mapToResponseDto(EventFeedback feedback)`: Input: EventFeedback | Output: UserFeedbackResponseDto | Purpose: Convert feedback to response DTO

**DateTimeFormatter Usage**: Used to format LocalDate and LocalTime to String format (yyyy-MM-dd, HH:mm) for consistent date/time representation in DTOs

**Why Needed**: Clean separation between internal entities and API responses with proper date formatting.

---

## Key Feature Implementations

### 🔐 Email Notification System

**Architecture**: Centralized service with JavaMailSender integration

**Implementation Features**:
```java
@Service
public class EmailNotificationServiceImpl implements EmailNotificationService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    // Professional email templates with HTML formatting
    // Automatic error handling and logging
    // Consistent branding across all emails
    // Personalized content for each notification type
}
```

**Email Types**:
- **Booking Confirmation**: Sent when user successfully books an event
- **Waitlist Confirmation**: Sent when user is added to waitlist
- **Promotion Notification**: Sent when user is promoted from waitlist
- **Cancellation Confirmation**: Sent when booking is cancelled
- **OTP Emails**: For password reset functionality

**Error Handling**: Custom `EmailSendingException`

### 🎯 Automatic Waitlist Management

**Architecture**: Separate service with first-come-first-served logic

**Key Algorithm**:
```java
// When seats become available:
1. Query waitlist entries ordered by joinedAt (FIFO)
2. For each waitlist entry:
   - Check if requested seats <= available seats
   - If yes: create booking, mark as promoted, reduce available seats
   - Continue until no more promotions possible
3. Send promotion notifications to all promoted users
4. Update event available seats
```

**Benefits**:
- Automatic promotion without manual intervention
- Fair first-come-first-served basis
- Transactional integrity
- Email notifications for all state changes

### 🔑 JWT Authentication System

**Architecture**: Stateless authentication with secure token handling

**Security Features**:
- **Token Expiration**: Configurable expiry time
- **Signature Validation**: HMAC-SHA256 signing
- **Claims Extraction**: Username and role extraction
- **Request Filtering**: Automatic validation on each request

**Error Handling in Filter**:
- Expired tokens → 401 Unauthorized
- Malformed tokens → 401 Unauthorized  
- Missing tokens → Proceed without authentication
- Filter exceptions → 500 Internal Server Error

### 🔐 OTP-based Password Reset

**Architecture**: Redis-backed temporary storage with time expiration

**Security Features**:
- **6-digit numeric OTP**: Random generation
- **5-minute expiry**: Automatic cleanup via Redis TTL
- **Single-use tokens**: Deleted after successful verification
- **Email delivery**: Secure OTP transmission

### 📊 Role-based Access Control

**Implementation**: Spring Security with custom roles

**Access Matrix**:
```
Public Endpoints: /auth/**, /forgot-password/**
USER Role: /user/** (booking, feedback, profile)
ADMIN Role: /admin/** (event management, analytics)
```

**Authorization Logic**:
```java
// Method-level security
@PreAuthorize("hasRole('ADMIN')")
public void adminOnlyMethod() { }

// Service-level checks
if (!authenticationService.isCurrentUserAdmin()) {
    throw new AccessDeniedException("Admin access required");
}
```

---

## Key Design Patterns Used

1. **Repository Pattern**: Data access abstraction
2. **Service Layer Pattern**: Business logic separation
3. **DTO Pattern**: Data transfer between layers
4. **Dependency Injection**: Loose coupling between components
5. **Interface Segregation**: Service interfaces for testability
6. **Exception Handling**: Centralized error management
7. **Builder Pattern**: DTO construction (via Lombok)

## Security Features

1. **JWT Authentication**: Stateless authentication
2. **Role-based Authorization**: Admin vs User roles
3. **Password Encryption**: BCrypt hashing
4. **Account Locking**: Failed login attempt protection
5. **OTP Verification**: Secure password reset
6. **Input Validation**: Request validation
7. **CORS Configuration**: Cross-origin resource sharing

## Database Design

- **PostgreSQL**: Primary database for persistent data
- **Redis**: Caching and session storage
- **JPA Relationships**: Proper foreign key relationships
- **Indexing**: Optimized queries for performance
- **Transaction Management**: ACID properties maintenance