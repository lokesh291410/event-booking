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
**Dependencies**: `HttpServletResponse`  
**Key Methods**:
- `handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex)`: Input: Request, Response, Exception | Output: void | Purpose: Custom JSON error response for access denied

**Why Needed**: Provides consistent error responses for authorization failures.

#### `CustomAuthenticationEntryPoint`
**Purpose**: Handles authentication failures  
**Dependencies**: `HttpServletResponse`  
**Key Methods**:
- `commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)`: Input: Request, Response, Exception | Output: void | Purpose: Custom JSON error response for authentication failures

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
**Dependencies**: `UserInfoRepository`, `SecurityContextHolder`  
**Key Methods**:
- `getCurrentUser()`: Input: None | Output: UserInfo | Purpose: Get currently authenticated user from security context
- `getCurrentAdmin()`: Input: None | Output: UserInfo | Purpose: Get current user and verify admin role
- `isCurrentUserAdmin()`: Input: None | Output: boolean | Purpose: Check if current user has admin role
- `getCurrentUserId()`: Input: None | Output: Long | Purpose: Get current user ID for service operations
- `validateUserAccess(Long userId)`: Input: Long | Output: void | Purpose: Verify user can access their own data
- `getUserByUsername(String username)`: Input: String | Output: UserInfo | Purpose: Load user by username for authentication

**Why Needed**: Centralized authentication logic for security across the application.

#### `UserService` (Interface) & `UserServiceImpl`
**Purpose**: User-related business operations  
**Dependencies**: `EventRepository`, `BookingRepository`, `WaitlistRepository`, `AuthenticationService`, `WaitlistPromotionService`, `EmailNotificationService`  
**Key Methods**:
- `bookEvent(Long eventId, Integer numberOfSeats)`: Input: Long, Integer | Output: String | Purpose: Book event or add to waitlist automatically
- `cancelBooking(Long bookingId)`: Input: Long | Output: void | Purpose: Cancel booking and trigger waitlist promotions
- `getUserBookings()`: Input: None | Output: List<BookingDto> | Purpose: Get current user's bookings
- `getUserBookings(Long userId)`: Input: Long | Output: List<BookingDto> | Purpose: Get specific user's bookings (admin)
- `submitEventFeedback(Long eventId, EventFeedbackDto feedbackDto)`: Input: Long, EventFeedbackDto | Output: void | Purpose: Submit event feedback
- `getUpcomingEvents()`: Input: None | Output: List<EventResponseDto> | Purpose: Get published upcoming events
- `getEventDetails(Long eventId)`: Input: Long | Output: EventDetailResponseDto | Purpose: Get detailed event information
- `getUserWaitlist()`: Input: None | Output: List<WaitlistDto> | Purpose: Get user's waitlist entries
- `removeFromWaitlist(Long waitlistId)`: Input: Long | Output: void | Purpose: Remove user from waitlist

**Why Needed**: Implements core user functionality with automatic waitlist management.

#### `AdminService` (Interface) & `AdminServiceImpl`
**Purpose**: Admin-specific operations  
**Dependencies**: `EventRepository`, `BookingRepository`, `WaitlistRepository`, `AuthenticationService`, `EventFeedbackRepository`  
**Key Methods**:
- `createEvent(EventDto eventDto)`: Input: EventDto | Output: EventResponseDto | Purpose: Create new event in draft status
- `updateEvent(Long eventId, EventDto eventDto)`: Input: Long, EventDto | Output: EventResponseDto | Purpose: Update event details
- `publishEvent(Long eventId)`: Input: Long | Output: void | Purpose: Publish draft event to make it bookable
- `cancelEvent(Long eventId, String reason)`: Input: Long, String | Output: void | Purpose: Cancel event and notify users
- `getEventStatistics(Long eventId)`: Input: Long | Output: EventStatisticsDto | Purpose: Get comprehensive event analytics
- `getEventDetails(Long eventId)`: Input: Long | Output: EventDetailResponseDto | Purpose: Get detailed event info with admin data
- `getAllEvents()`: Input: None | Output: List<EventResponseDto> | Purpose: Get all events for admin panel
- `getEventBookings(Long eventId)`: Input: Long | Output: List<BookingDto> | Purpose: Get all bookings for specific event
- `getEventWaitlist(Long eventId)`: Input: Long | Output: List<WaitlistDto> | Purpose: Get waitlist for specific event
- `getUserAnalytics()`: Input: None | Output: UserAnalyticsDto | Purpose: Get user registration and activity analytics

**Why Needed**: Provides admin functionality for event management and analytics.

#### `WaitlistPromotionService` (Interface) & `WaitlistPromotionServiceImpl`
**Purpose**: Handles automatic waitlist promotions  
**Dependencies**: `WaitlistRepository`, `BookingRepository`, `EventRepository`  
**Key Methods**:
- `processWaitlistPromotions(Long eventId, Integer availableSeats)`: Input: Long, Integer | Output: PromotionResultDto | Purpose: Promote waitlisted users when seats available
- `hasWaitlistUsers(Long eventId)`: Input: Long | Output: boolean | Purpose: Check if event has waiting users
- `getWaitlistCount(Long eventId)`: Input: Long | Output: Integer | Purpose: Count total waitlist entries
- `getNextWaitlistUsers(Long eventId, Integer maxUsers)`: Input: Long, Integer | Output: List<Waitlist> | Purpose: Get next users for promotion in FIFO order
- `promoteUser(Waitlist waitlistEntry)`: Input: Waitlist | Output: Booking | Purpose: Convert waitlist entry to confirmed booking
- `calculateAvailableSeats(Long eventId)`: Input: Long | Output: Integer | Purpose: Calculate actual available seats

**Why Needed**: Separates waitlist promotion logic for maintainability and reusability.

#### `EmailNotificationService` (Interface) & `EmailNotificationServiceImpl`
**Purpose**: Handles all email communications  
**Dependencies**: `JavaMailSender`  
**Key Methods**:
- `sendBookingConfirmationEmail(UserInfo user, Event event, Integer seats)`: Input: UserInfo, Event, Integer | Output: void | Purpose: Send booking confirmation with event details
- `sendWaitlistConfirmationEmail(UserInfo user, Event event, Integer seats)`: Input: UserInfo, Event, Integer | Output: void | Purpose: Send waitlist confirmation with position info
- `sendPromotionNotificationEmails(List<PromotedUserDto> promotedUsers)`: Input: List<PromotedUserDto> | Output: void | Purpose: Notify promoted users about booking confirmation
- `sendBookingCancellationEmail(UserInfo user, Event event, Integer seats)`: Input: UserInfo, Event, Integer | Output: void | Purpose: Send cancellation confirmation
- `sendEventCancellationEmail(List<UserInfo> affectedUsers, Event event)`: Input: List<UserInfo>, Event | Output: void | Purpose: Notify users about event cancellation
- `sendEventUpdateEmail(List<UserInfo> affectedUsers, Event event, String changes)`: Input: List<UserInfo>, Event, String | Output: void | Purpose: Notify about event changes
- `sendReminderEmail(UserInfo user, Event event, Integer daysBefore)`: Input: UserInfo, Event, Integer | Output: void | Purpose: Send event reminder emails

**Why Needed**: Centralized email functionality with professional templates.

#### `JwtService` (Interface) & `JwtServiceImpl`
**Purpose**: JWT token management  
**Dependencies**: JWT libraries, application properties  
**Key Methods**:
- `generateToken(String username)`: Input: String | Output: String | Purpose: Create JWT token for authenticated user
- `generateTokenWithClaims(String username, Map<String, Object> claims)`: Input: String, Map | Output: String | Purpose: Create JWT with custom claims
- `validateToken(String token, UserDetails userDetails)`: Input: String, UserDetails | Output: Boolean | Purpose: Validate token integrity and expiration
- `extractUsername(String token)`: Input: String | Output: String | Purpose: Extract username from token claims
- `extractExpiration(String token)`: Input: String | Output: Date | Purpose: Get token expiration date
- `isTokenExpired(String token)`: Input: String | Output: Boolean | Purpose: Check if token is expired
- `refreshToken(String token)`: Input: String | Output: String | Purpose: Generate new token from valid existing token

**Why Needed**: Secure stateless authentication using JWT tokens.

#### `OtpService` (Interface) & `OtpServiceImpl`
**Purpose**: OTP generation and validation  
**Dependencies**: `RedisTemplate`  
**Key Methods**:
- `generateOtp()`: Input: None | Output: String | Purpose: Generate 6-digit random OTP
- `storeOtp(String email, String otp)`: Input: String, String | Output: void | Purpose: Store OTP in Redis with 5-minute expiry
- `validateOtp(String email, String otp)`: Input: String, String | Output: boolean | Purpose: Verify OTP against stored value
- `deleteOtp(String email)`: Input: String | Output: void | Purpose: Remove OTP after successful use
- `getOtpExpiry(String email)`: Input: String | Output: Duration | Purpose: Get remaining OTP validity time
- `resendOtp(String email)`: Input: String | Output: String | Purpose: Generate and store new OTP for email

**Why Needed**: Secure password reset functionality with time-limited OTPs.

#### `ForgotPasswordService` (Interface) & `ForgotPasswordServiceImpl`
**Purpose**: Password reset workflow  
**Dependencies**: `UserInfoRepository`, `OtpService`, `EmailService`  
**Key Methods**:
- `sendOtp()`: Initiate password reset
- `verifyOtp()`: Validate OTP
- `resetPassword()`: Update password

**Why Needed**: Secure password recovery process for users.

#### `SignUpService` (Interface) & `SignUpServiceImpl`
**Purpose**: User registration functionality  
**Dependencies**: `UserInfoRepository`, `PasswordEncoder`  
**Key Methods**:
- `registerUser()`: Create new user accounts
- `checkUsernameAvailability()`: Validate unique usernames
- `checkEmailAvailability()`: Validate unique emails

**Why Needed**: Handles user registration with validation and security.

---

### 📁 Controller Layer

#### `AuthController`
**Purpose**: Authentication endpoints  
**Dependencies**: `AuthenticationManager`, `JwtService`, `UserInfoRepository`, `SignUpService`  
**Endpoints**: 
- `POST /auth/login`: Input: LoginRequest (userName, password) | Output: LoginResponse (token, userInfo) | Purpose: User authentication
- `POST /auth/signup`: Input: SignUpRequestDto (userName, email, password, name) | Output: SignUpResponseDto (success message) | Purpose: User registration
- `POST /auth/refresh`: Input: RefreshTokenRequest (token) | Output: LoginResponse (new token) | Purpose: Token refresh
- `GET /auth/validate`: Input: Authorization header | Output: UserValidationResponse | Purpose: Validate token and return user info

**Error Handling**: Returns 401 for invalid credentials, 400 for validation errors, 409 for duplicate users

**Why Needed**: Provides authentication API endpoints.

#### `UserController`
**Purpose**: User-specific endpoints  
**Dependencies**: `UserService`, `AuthenticationService`  
**Endpoints**:
- `POST /user/book/{eventId}/{numberOfSeats}`: Input: Long eventId, Integer numberOfSeats | Output: BookingResponseDto | Purpose: Book events or add to waitlist
- `POST /user/cancel/{bookingId}`: Input: Long bookingId | Output: CancellationResponseDto | Purpose: Cancel bookings
- `GET /user/bookings`: Input: None (uses auth) | Output: List<BookingDto> | Purpose: Get user's bookings
- `GET /user/events/upcoming`: Input: Optional query params (category, location) | Output: List<EventResponseDto> | Purpose: List upcoming events
- `GET /user/events/{eventId}`: Input: Long eventId | Output: EventDetailResponseDto | Purpose: Get event details
- `POST /user/events/{eventId}/feedback`: Input: Long eventId, EventFeedbackDto | Output: FeedbackResponseDto | Purpose: Submit event feedback
- `GET /user/waitlist`: Input: None (uses auth) | Output: List<WaitlistDto> | Purpose: Get user's waitlist entries
- `DELETE /user/waitlist/{waitlistId}`: Input: Long waitlistId | Output: void | Purpose: Remove from waitlist
- `GET /user/profile`: Input: None (uses auth) | Output: UserProfileDto | Purpose: Get user profile

**Security**: All endpoints require USER role authentication

**Why Needed**: Exposes user functionality through REST API.

#### `AdminController`
**Purpose**: Admin-specific endpoints  
**Dependencies**: `AdminService`, `AuthenticationService`  
**Endpoints**:
- `POST /admin/events`: Input: EventDto | Output: EventResponseDto | Purpose: Create new events
- `PUT /admin/events/{eventId}`: Input: Long eventId, EventDto | Output: EventResponseDto | Purpose: Update event details
- `DELETE /admin/events/{eventId}`: Input: Long eventId | Output: void | Purpose: Delete events
- `POST /admin/events/{eventId}/publish`: Input: Long eventId | Output: void | Purpose: Publish draft events
- `POST /admin/events/{eventId}/cancel`: Input: Long eventId, CancellationRequestDto | Output: void | Purpose: Cancel events
- `GET /admin/events`: Input: Optional filters (status, category) | Output: List<EventResponseDto> | Purpose: Get all events for admin
- `GET /admin/events/{eventId}/statistics`: Input: Long eventId | Output: EventStatisticsDto | Purpose: Get event analytics
- `GET /admin/events/{eventId}/bookings`: Input: Long eventId | Output: List<BookingDto> | Purpose: Get event bookings
- `GET /admin/events/{eventId}/waitlist`: Input: Long eventId | Output: List<WaitlistDto> | Purpose: Get event waitlist
- `GET /admin/users`: Input: Optional filters | Output: List<UserDto> | Purpose: Get user list
- `GET /admin/analytics/dashboard`: Input: None | Output: DashboardAnalyticsDto | Purpose: Get admin dashboard data

**Security**: All endpoints require ADMIN role authentication

**Why Needed**: Provides admin functionality through REST API.

#### `ForgotPasswordController`
**Purpose**: Password reset endpoints  
**Dependencies**: `ForgotPasswordService`  
**Endpoints**:
- `POST /forgot-password/send-otp`: Input: ForgotPasswordRequestDto (email) | Output: ForgotPasswordResponseDto | Purpose: Send OTP to email
- `POST /forgot-password/verify-otp`: Input: VerifyOtpRequestDto (email, otp, newPassword) | Output: ForgotPasswordResponseDto | Purpose: Verify OTP and reset password
- `POST /forgot-password/resend-otp`: Input: ResendOtpRequestDto (email) | Output: ForgotPasswordResponseDto | Purpose: Resend OTP if expired

**Security**: Public endpoints (no authentication required)

**Why Needed**: Handles password recovery workflow.

#### `SignupController`
**Purpose**: User registration endpoints  
**Dependencies**: `SignUpService`  
**Endpoints**:
- `POST /signup/register`: Input: SignUpRequestDto | Output: SignUpResponseDto | Purpose: User registration
- `GET /signup/check-username/{username}`: Input: String username | Output: AvailabilityResponseDto | Purpose: Check username availability
- `GET /signup/check-email/{email}`: Input: String email | Output: AvailabilityResponseDto | Purpose: Check email availability
- `POST /signup/activate`: Input: ActivationRequestDto (token) | Output: ActivationResponseDto | Purpose: Account activation if required

**Security**: Public endpoints (no authentication required)

**Why Needed**: Handles user registration process.

---

### 📁 DTO Layer

#### `PromotionResultDto` & `PromotedUserDto`
**Purpose**: Transfer promotion data between services  
**Fields**: 
- `PromotionResultDto`: promotedUsers (List<PromotedUserDto>), totalPromoted (Integer), remainingWaitlistCount (Integer), eventId (Long)
- `PromotedUserDto`: userId (Long), email (String), name (String), seats (Integer), bookingId (Long), eventTitle (String), promotionTime (LocalDateTime)

**Validation**: No direct validation (internal DTOs)

**Why Needed**: Clean data transfer for waitlist promotion results.

#### `EventDto` & `EventResponseDto`
**Purpose**: Event data transfer  
**Fields**: 
- `EventDto`: title (@NotBlank), description (@NotBlank), dateTime (@Future), location (@NotBlank), totalSeats (@Min(1)), category, price (@DecimalMin("0.0"))
- `EventResponseDto`: id, title, description, dateTime, location, totalSeats, availableSeats, category, status, price, createdBy, averageRating, totalBookings, waitlistCount, createdAt

**Validation**: Input validation on EventDto, no validation on response DTO

**Why Needed**: Separates internal entity structure from API contracts.

#### `BookingDto`
**Purpose**: Booking data transfer  
**Fields**: id (Long), eventTitle (String), eventDateTime (LocalDateTime), eventLocation (String), numberOfSeats (Integer), bookingDateTime (LocalDateTime), status (String), totalAmount (BigDecimal), canCancel (Boolean), daysUntilEvent (Long)

**Validation**: No validation (response DTO)

**Why Needed**: Structured booking data for client applications.

#### `EventDetailResponseDto`
**Purpose**: Comprehensive event information with nested DTOs  
**Fields**: All EventResponseDto fields plus feedbackSummary (FeedbackSummaryDto), waitlistSummary (WaitlistSummaryDto), recentFeedbacks (List<PublicFeedbackDto>), isUserBookmarked (Boolean), similarEvents (List<EventResponseDto>)

**Nested DTOs**:
- `FeedbackSummaryDto`: averageRating (Double), totalFeedbacks (Integer), fiveStarCount (Integer), fourStarCount (Integer), threeStarCount (Integer), twoStarCount (Integer), oneStarCount (Integer), recommendationPercentage (Double)
- `WaitlistSummaryDto`: totalWaiting (Integer), totalSeatsRequested (Integer), oldestWaitlistEntry (LocalDateTime), userPosition (Integer)
- `PublicFeedbackDto`: rating (Double), comment (String), submittedAt (LocalDateTime), userName (String), wouldRecommend (Boolean)

**Why Needed**: Rich event details for admin and user interfaces.

#### Request/Response DTOs with Validation

**`LoginRequest` & `LoginResponse`**:
- `LoginRequest`: userName (@NotBlank), password (@NotBlank)
- `LoginResponse`: token (String), userName (String), role (String), name (String), email (String), expiresAt (LocalDateTime)

**`SignUpRequestDto` & `SignUpResponseDto`**:
- `SignUpRequestDto`: userName (@NotBlank @Size(3,50)), email (@NotBlank @Email), password (@NotBlank @Size(8,100)), name (@NotBlank @Size(2,100)), confirmPassword (@NotBlank)
- `SignUpResponseDto`: success (Boolean), message (String), userId (Long)

**`ForgotPasswordRequestDto`, `VerifyOtpRequestDto` & `ForgotPasswordResponseDto`**:
- `ForgotPasswordRequestDto`: email (@NotBlank @Email)
- `VerifyOtpRequestDto`: email (@NotBlank @Email), otp (@NotBlank @Pattern(regexp="\\d{6}")), newPassword (@NotBlank @Size(8,100))
- `ForgotPasswordResponseDto`: success (Boolean), message (String), otpExpiry (LocalDateTime)

**`EventFeedbackDto` & `UserFeedbackResponseDto`**:
- `EventFeedbackDto`: rating (@NotNull @DecimalMin("1.0") @DecimalMax("5.0")), comment (@Size(max=1000)), suggestions (@Size(max=500)), wouldRecommend (Boolean)
- `UserFeedbackResponseDto`: id (Long), eventTitle (String), rating (Double), comment (String), submittedAt (LocalDateTime), canEdit (Boolean)

**`WaitlistDto`**:
**Fields**: id (Long), eventTitle (String), eventDateTime (LocalDateTime), requestedSeats (Integer), position (Integer), joinedAt (LocalDateTime), status (String), estimatedWaitTime (String)

**`UserInfoDto`**:
**Fields**: id (Long), userName (String), email (String), name (String), role (String), registrationDate (LocalDateTime), totalBookings (Integer), totalFeedbacks (Integer), isActive (Boolean)

**`EventStatisticsDto`**:
**Fields**: eventId (Long), totalBookings (Integer), totalSeats (Integer), availableSeats (Integer), waitlistCount (Integer), totalRevenue (BigDecimal), averageRating (Double), totalFeedbacks (Integer), bookingsByDate (Map<String, Integer>), topRatedFeedbacks (List<PublicFeedbackDto>)

**Error Response DTOs**:
- `ErrorResponse`: timestamp (LocalDateTime), status (Integer), error (String), message (String), path (String)
- `ValidationErrorResponse`: extends ErrorResponse, fieldErrors (Map<String, String>)

**Why Needed**: Type-safe API contracts with comprehensive input validation and structured error responses.

---

### 📁 Exception Layer

#### `GlobalExceptionHandler`
**Purpose**: Centralized exception handling  
**Dependencies**: Spring's `@ControllerAdvice`  
**Key Methods**:
- `handleResourceNotFound(ResourceNotFoundException ex)`: Input: ResourceNotFoundException | Output: ResponseEntity<ErrorResponse> | Purpose: Handle 404 errors with resource details
- `handleAccessDenied(AccessDeniedException ex)`: Input: AccessDeniedException | Output: ResponseEntity<ErrorResponse> | Purpose: Handle 403 authorization errors
- `handleBadRequest(BadRequestException ex)`: Input: BadRequestException | Output: ResponseEntity<ErrorResponse> | Purpose: Handle 400 validation and business logic errors
- `handleValidationErrors(MethodArgumentNotValidException ex)`: Input: MethodArgumentNotValidException | Output: ResponseEntity<ValidationErrorResponse> | Purpose: Handle Bean Validation errors with field details
- `handleEmailSending(EmailSendingException ex)`: Input: EmailSendingException | Output: ResponseEntity<ErrorResponse> | Purpose: Handle email service failures
- `handleJwtErrors(JwtException ex)`: Input: JwtException | Output: ResponseEntity<ErrorResponse> | Purpose: Handle JWT token related errors
- `handleDatabaseErrors(DataAccessException ex)`: Input: DataAccessException | Output: ResponseEntity<ErrorResponse> | Purpose: Handle database operation failures
- `handleGeneral(Exception ex)`: Input: Exception | Output: ResponseEntity<ErrorResponse> | Purpose: Fallback for unexpected errors

**Response Structure**: All methods return structured JSON with timestamp, status, error, message, and optional path/field details

**Why Needed**: Consistent error responses across the application.

#### Custom Exception Classes

**`ResourceNotFoundException`**
**Purpose**: Thrown when requested resources don't exist  
**Constructor Methods**:
- `ResourceNotFoundException(String resource, String field, Object value)`: Input: String, String, Object | Purpose: Create exception with resource details
- `ResourceNotFoundException(String message)`: Input: String | Purpose: Create exception with custom message

**`AccessDeniedException`**
**Purpose**: Thrown for authorization failures  
**Constructor Methods**:
- `AccessDeniedException(String message)`: Input: String | Purpose: Create exception with access denial reason
- `AccessDeniedException(String message, Throwable cause)`: Input: String, Throwable | Purpose: Create exception with cause

**`BadRequestException`**
**Purpose**: Thrown for invalid requests and business logic violations  
**Constructor Methods**:
- `BadRequestException(String message)`: Input: String | Purpose: Create exception with validation failure reason
- `BadRequestException(String field, Object value, String reason)`: Input: String, Object, String | Purpose: Create exception with field-specific details

**`InvalidCredentialsException`**
**Purpose**: Thrown for authentication failures  
**Constructor Methods**:
- `InvalidCredentialsException(String message)`: Input: String | Purpose: Create exception with authentication failure reason

**JWT-related Exceptions**:
- `InvalidJwtTokenException`: Input: String message, Throwable cause | Purpose: JWT token validation failures
- `JwtTokenExpiredException`: Input: String message | Purpose: Expired token scenarios
- `JwtTokenMalformedException`: Input: String message | Purpose: Malformed token format

**OTP-related Exceptions**:
- `OtpValidationException`: Input: String message | Purpose: OTP verification failures
- `OtpStorageException`: Input: String message, Throwable cause | Purpose: Redis storage failures
- `OtpExpiredException`: Input: String message | Purpose: Expired OTP scenarios

**Email-related Exceptions**:
- `EmailSendingException`: Input: String message, Throwable cause | Purpose: Email delivery failures
- `EmailTemplateException`: Input: String message | Purpose: Email template processing errors

**Booking-related Exceptions**:
- `BookingException`: Input: String message | Purpose: Booking operation failures
- `EventFullException`: Input: String eventTitle | Purpose: No available seats
- `DuplicateBookingException`: Input: Long userId, Long eventId | Purpose: User already booked event
- `WaitlistException`: Input: String message | Purpose: Waitlist operation failures

**Why Needed**: Specific error handling for different failure scenarios with detailed context and proper HTTP status codes.

---

### 📁 Security & Filter Layer

#### `JwtFilter`
**Purpose**: JWT token validation for each request  
**Dependencies**: `JwtService`, `CustomUserDetailsService`  
**Key Methods**:
- `doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)`: Input: Request, Response, FilterChain | Output: void | Purpose: Validate JWT and set authentication context
- `handleJwtError(HttpServletResponse response, String message, HttpStatus status)`: Input: Response, String, HttpStatus | Output: void | Purpose: Send structured error response for JWT failures
- `handleFilterException(HttpServletResponse response, Exception e)`: Input: Response, Exception | Output: void | Purpose: Handle unexpected filter errors
- `extractTokenFromHeader(String authHeader)`: Input: String | Output: String | Purpose: Extract JWT token from Authorization header
- `setAuthenticationInContext(UserDetails userDetails, HttpServletRequest request)`: Input: UserDetails, Request | Output: void | Purpose: Set Spring Security authentication

**Error Handling Features**:
- **Expired JWT Tokens**: Returns 401 with "JWT token has expired" message
- **Malformed Tokens**: Returns 401 with "Invalid JWT token format" message  
- **Missing Authorization**: Continues filter chain without authentication
- **Filter Exceptions**: Returns 500 with "Authentication filter failed" message
- **Structured Responses**: All errors return JSON with timestamp, status, error, message

**Why Needed**: Secures API endpoints with token-based authentication and comprehensive error handling.

#### `CustomUserDetailsService`
**Purpose**: Load user details for Spring Security  
**Dependencies**: `UserInfoRepository`  
**Key Methods**:
- `loadUserByUsername(String username)`: Input: String | Output: UserDetails | Purpose: Load user for authentication with account status checks
- `handleAccountLocking(UserInfo user)`: Input: UserInfo | Output: void | Purpose: Check and handle account locking logic
- `incrementFailedAttempts(UserInfo user)`: Input: UserInfo | Output: void | Purpose: Track failed login attempts
- `resetFailedAttempts(UserInfo user)`: Input: UserInfo | Output: void | Purpose: Reset failed attempts on successful login
- `lockAccount(UserInfo user)`: Input: UserInfo | Output: void | Purpose: Lock account after multiple failed attempts

**Error Handling Features**:
- **User Not Found**: Throws `UsernameNotFoundException` with specific message
- **Account Locked**: Throws `AccountLockedException` with lock reason
- **Database Errors**: Wraps in `InternalAuthenticationServiceException`
- **Account Status**: Validates account expiry, credentials expiry, enabled status

**Why Needed**: Integrates application users with Spring Security with comprehensive account management.

### 📁 Mapping Layer

#### `EventToDto`, `BookingToDto`, `EventFeedbackToDto`
**Purpose**: Convert entities to DTOs  
**Key Methods**:

**`EventToDto`**:
- `toEventResponseDto(Event event)`: Input: Event | Output: EventResponseDto | Purpose: Convert entity to response DTO with calculated fields
- `toEventDetailResponseDto(Event event, List<EventFeedback> feedbacks)`: Input: Event, List<EventFeedback> | Output: EventDetailResponseDto | Purpose: Create detailed response with feedback summary
- `createFeedbackSummary(List<EventFeedback> feedbacks)`: Input: List<EventFeedback> | Output: FeedbackSummaryDto | Purpose: Calculate rating distribution and averages
- `createWaitlistSummary(Event event)`: Input: Event | Output: WaitlistSummaryDto | Purpose: Generate waitlist statistics
- `mapToPublicFeedbacks(List<EventFeedback> feedbacks)`: Input: List<EventFeedback> | Output: List<PublicFeedbackDto> | Purpose: Convert feedback to public display format

**`BookingToDto`**:
- `toBookingDto(Booking booking)`: Input: Booking | Output: BookingDto | Purpose: Convert booking entity to DTO
- `toBookingDtoWithDetails(Booking booking)`: Input: Booking | Output: BookingDto | Purpose: Include additional details like cancellation ability
- `calculateTotalAmount(Booking booking)`: Input: Booking | Output: BigDecimal | Purpose: Calculate total booking amount
- `calculateDaysUntilEvent(Booking booking)`: Input: Booking | Output: Long | Purpose: Calculate days until event
- `canCancelBooking(Booking booking)`: Input: Booking | Output: Boolean | Purpose: Determine if booking can be cancelled

**`EventFeedbackToDto`**:
- `toUserFeedbackResponseDto(EventFeedback feedback)`: Input: EventFeedback | Output: UserFeedbackResponseDto | Purpose: Convert feedback for user view
- `toPublicFeedbackDto(EventFeedback feedback)`: Input: EventFeedback | Output: PublicFeedbackDto | Purpose: Convert feedback for public display
- `canEditFeedback(EventFeedback feedback, Long userId)`: Input: EventFeedback, Long | Output: Boolean | Purpose: Check if user can edit feedback

**`UserInfoToDto`**:
- `toUserInfoDto(UserInfo user)`: Input: UserInfo | Output: UserInfoDto | Purpose: Convert user entity to DTO (excluding sensitive data)
- `toUserProfileDto(UserInfo user)`: Input: UserInfo | Output: UserProfileDto | Purpose: Create user profile response
- `calculateUserStatistics(UserInfo user)`: Input: UserInfo | Output: UserStatisticsDto | Purpose: Generate user activity statistics

**`WaitlistToDto`**:
- `toWaitlistDto(Waitlist waitlist)`: Input: Waitlist | Output: WaitlistDto | Purpose: Convert waitlist entity to DTO
- `calculateWaitlistPosition(Waitlist waitlist)`: Input: Waitlist | Output: Integer | Purpose: Calculate user position in waitlist
- `estimateWaitTime(Waitlist waitlist)`: Input: Waitlist | Output: String | Purpose: Estimate waiting time based on promotion patterns

**Why Needed**: Clean separation between internal entities and API responses with calculated fields.

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
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
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

**Error Handling**: Custom `EmailSendingException` with retry logic and fallback mechanisms.

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

## Error Handling Architecture

### 📍 Multi-Level Error Handling

#### **1. Filter Level Error Handling**
```java
// JwtFilter handles authentication errors before reaching controllers
try {
    // JWT validation logic
} catch (ExpiredJwtException e) {
    handleJwtError(response, "JWT token has expired", HttpStatus.UNAUTHORIZED);
    return; // Stop filter chain
} catch (MalformedJwtException e) {
    handleJwtError(response, "Invalid JWT token format", HttpStatus.UNAUTHORIZED);
    return;
} catch (Exception e) {
    handleFilterException(response, e);
    return;
}
```

#### **2. Controller Level Error Handling**
```java
// Controllers throw specific exceptions
if (eventRepository.findById(eventId).isEmpty()) {
    throw new ResourceNotFoundException("Event", "id", eventId);
}

if (!authenticationService.isCurrentUserAdmin()) {
    throw new AccessDeniedException("Admin access required");
}
```

#### **3. Service Level Error Handling**
```java
// Services handle business logic errors
@Transactional
public void bookEvent(Long eventId, Integer seats) {
    try {
        // Business logic
    } catch (DataAccessException e) {
        throw new BookingException("Database error during booking", e);
    } catch (EmailSendingException e) {
        // Log error but don't fail booking
        log.error("Failed to send booking confirmation email", e);
    }
}
```

#### **4. Global Exception Handler**
```java
// Centralized error response formatting
@ExceptionHandler(ResourceNotFoundException.class)
public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
    // Structured error response with timestamp, status, message
}
```

### 📋 Error Response Structure
```json
{
    "timestamp": "2025-09-21T10:30:00",
    "status": 404,
    "error": "Resource Not Found",
    "message": "Event not found with id: 123",
    "path": "/user/book/123/2"
}
```

### 🚨 Validation Error Structure
```json
{
    "timestamp": "2025-09-21T10:30:00",
    "status": 400,
    "error": "Validation Failed",
    "message": "Request validation failed",
    "fieldErrors": {
        "title": "Title is required",
        "dateTime": "Event date must be in the future",
        "totalSeats": "Total seats must be at least 1"
    }
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

This documentation provides a complete overview of the Krashi Event Booking System, detailing every component's purpose, dependencies, and role in the overall architecture.