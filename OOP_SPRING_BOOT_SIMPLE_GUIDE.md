# Object-Oriented Programming (OOP) Concepts in Spring Boot

## Overview
Object-Oriented Programming (OOP) concepts are fundamental to Java development, and they are extensively applied in Spring Boot applications to structure code, manage dependencies, and promote reusability and maintainability.

---

## 1. ENCAPSULATION

Encapsulation bundles data (attributes) and methods (functions) that operate on the data within a single unit, known as a class. It hides the internal implementation details of an object and exposes only what is necessary for interaction.

### Example 1: User Entity with Data Protection

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    private String password; // Hidden internally
    
    @Column(nullable = false)
    private String email;
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    // Getters and setters for id and username
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    // No public setter for password, demonstrating encapsulation
}
```

**Key Points:**
- Private fields protect internal data from direct access
- Public methods provide controlled access to data
- Password field has no public setter for security
- **Proxy Pattern**: JPA creates proxy objects for lazy loading

### Example 2: Service Layer Encapsulation

```java
@Service
@Transactional
public class BankAccountService {
    
    private final AccountRepository accountRepository;
    
    public void transferMoney(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        validateTransfer(amount);
        performTransfer(fromAccountId, toAccountId, amount);
    }
    
    // Private methods encapsulate complex business logic
    private void validateTransfer(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
    }
    
    private void performTransfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        // Transfer logic hidden from external classes
    }
}
```

**Key Points:**
- Business logic is encapsulated within service methods
- Private helper methods hide implementation details
- Public interface exposes only necessary operations
- **Template Method Pattern**: Common validation steps before transfer

### Example 3: Configuration Properties Encapsulation

```java
@Component
@ConfigurationProperties(prefix = "app.security")
@Data
public class SecurityProperties {
    
    private String jwtSecret;
    private long jwtExpiration = 86400000;
    
    @PostConstruct
    private void validateProperties() {
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalStateException("JWT secret must be at least 32 characters");
        }
    }
    
    public boolean isJwtConfigured() {
        return jwtSecret != null && jwtExpiration > 0;
    }
}
```

**Key Points:**
- Configuration properties are encapsulated in dedicated classes
- Validation logic is hidden within the configuration class
- Public methods provide safe access to configuration state
- **Factory Pattern**: Spring creates and configures these objects

---

## 2. ABSTRACTION

Abstraction focuses on showing only the essential information and hiding the complex implementation details. It allows you to define a blueprint or a contract without specifying the full implementation.

### Example 1: Service Interface Abstraction

```java
public interface UserService {
    User createUser(User user);
    User getUserById(Long id);
    void deleteUser(Long id);
}

@Service
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Override
    public User createUser(User user) {
        // Implementation details for creating a user
        return userRepository.save(user);
    }
    
    @Override
    public User getUserById(Long id) {
        // Implementation details for fetching a user
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
    
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
```

**Key Points:**
- Interface defines contract without implementation details
- Multiple implementations can exist for same abstraction
- Clients depend on abstraction, not concrete classes
- **Strategy Pattern**: Different implementations can be swapped

### Example 2: Payment Processing Abstraction

```java
public interface PaymentProcessor {
    PaymentResult processPayment(PaymentRequest request);
    boolean isPaymentMethodSupported(PaymentMethod method);
}

@Service
public class StripePaymentProcessor implements PaymentProcessor {
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // Stripe-specific implementation
        return PaymentResult.success("stripe-123", request.getAmount());
    }
    
    @Override
    public boolean isPaymentMethodSupported(PaymentMethod method) {
        return method == PaymentMethod.CREDIT_CARD;
    }
}

@Service
public class PayPalPaymentProcessor implements PaymentProcessor {
    
    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        // PayPal-specific implementation
        return PaymentResult.success("paypal-456", request.getAmount());
    }
    
    @Override
    public boolean isPaymentMethodSupported(PaymentMethod method) {
        return method == PaymentMethod.PAYPAL;
    }
}
```

**Key Points:**
- Abstract payment interface hides implementation complexity
- Different payment systems can be swapped transparently
- Each implementation handles specific payment methods
- **Factory Pattern**: Spring can inject appropriate implementation

### Example 3: Data Access Abstraction

```java
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(String status);
    List<Event> findByCategory(String category);
    
    @Query("SELECT e FROM Event e WHERE e.status = 'PUBLISHED'")
    List<Event> findPublishedEvents();
}

@Service
public class EventService {
    
    private final EventRepository eventRepository;
    
    public List<Event> getAvailableEvents() {
        return eventRepository.findByStatus("AVAILABLE");
    }
    
    public List<Event> getEventsByCategory(String category) {
        return eventRepository.findByCategory(category);
    }
}
```

**Key Points:**
- Repository abstraction hides database complexity
- JPA provides concrete implementation at runtime
- Service layer abstracts business operations from controllers
- **Repository Pattern**: Encapsulates data access logic

---

## 3. INHERITANCE

Inheritance allows a class (subclass/child class) to inherit properties and behaviors from another class (superclass/parent class), promoting code reusability.

### Example 1: Entity Inheritance

```java
@MappedSuperclass
public class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    private LocalDateTime updatedAt;
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

@Entity
public class Event extends BaseEntity {
    private String title;
    private String description;
    private LocalDateTime dateTime;
    
    // Getters and setters for event-specific fields
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}

@Entity
public class User extends BaseEntity {
    private String username;
    private String email;
    
    // Getters and setters for user-specific fields
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
```

**Key Points:**
- BaseEntity provides common fields to all entities
- Child entities inherit id, createdAt, and updatedAt automatically
- Reduces code duplication across entity classes
- **Template Method Pattern**: JPA lifecycle callbacks in base class

### Example 2: Exception Hierarchy

```java
public abstract class BusinessException extends RuntimeException {
    private final String errorCode;
    
    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() { return errorCode; }
    public abstract HttpStatus getHttpStatus();
}

public class ValidationException extends BusinessException {
    
    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
    }
    
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}

public class ResourceNotFoundException extends BusinessException {
    
    public ResourceNotFoundException(String message) {
        super("RESOURCE_NOT_FOUND", message);
    }
    
    @Override
    public HttpStatus getHttpStatus() {
        return HttpStatus.NOT_FOUND;
    }
}
```

**Key Points:**
- Common exception handling logic inherited from base class
- Each exception type defines specific HTTP status code
- Consistent error structure across application
- **Template Method Pattern**: Common error handling with specific implementations

### Example 3: Service Layer Inheritance

```java
public abstract class CrudService<T, ID> {
    
    protected abstract JpaRepository<T, ID> getRepository();
    
    public List<T> findAll() {
        return getRepository().findAll();
    }
    
    public Optional<T> findById(ID id) {
        return getRepository().findById(id);
    }
    
    public T save(T entity) {
        validateEntity(entity);
        return getRepository().save(entity);
    }
    
    protected void validateEntity(T entity) {
        if (entity == null) {
            throw new ValidationException("Entity cannot be null");
        }
    }
}

@Service
public class UserService extends CrudService<User, Long> {
    
    private final UserRepository userRepository;
    
    @Override
    protected JpaRepository<User, Long> getRepository() {
        return userRepository;
    }
    
    @Override
    protected void validateEntity(User user) {
        super.validateEntity(user);
        if (user.getUsername() == null) {
            throw new ValidationException("Username is required");
        }
    }
}
```

**Key Points:**
- Common CRUD operations inherited from base service
- Template method pattern allows customization
- Eliminates code duplication across service classes
- **Strategy Pattern**: Different validation strategies in subclasses

---

## 4. POLYMORPHISM

Polymorphism means "many forms," allowing objects of different classes to be treated as objects of a common type. This can be achieved through method overloading (compile-time) and method overriding (runtime).

### Example 1: Notification System

```java
public interface NotificationService {
    void sendNotification(String recipient, String message);
}

@Service
public class EmailNotificationService implements NotificationService {
    
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("Sending email to " + recipient + ": " + message);
    }
}

@Service
public class SmsNotificationService implements NotificationService {
    
    @Override
    public void sendNotification(String recipient, String message) {
        System.out.println("Sending SMS to " + recipient + ": " + message);
    }
}

@Service
public class NotificationManager {
    
    private final List<NotificationService> notificationServices;
    
    public void notifyUser(String type, String recipient, String message) {
        NotificationService service = getServiceByType(type);
        service.sendNotification(recipient, message); // Polymorphic call
    }
    
    private NotificationService getServiceByType(String type) {
        // Return appropriate service based on type
        return notificationServices.get(0); // Simplified
    }
}
```

**Key Points:**
- Different notification implementations treated uniformly
- Runtime polymorphism through interface implementation
- Service can be changed without modifying client code
- **Observer Pattern**: Notify multiple services of events

### Example 2: Data Processing with Method Overloading

```java
@Service
public class DataProcessor {
    
    // Method overloading - compile-time polymorphism
    public void processData(String data) {
        System.out.println("Processing string data: " + data);
    }
    
    public void processData(List<String> dataList) {
        System.out.println("Processing list of " + dataList.size() + " items");
    }
    
    public void processData(Map<String, Object> dataMap) {
        System.out.println("Processing map with " + dataMap.size() + " entries");
    }
    
    public ProcessingResult processData(String data, String format) {
        if ("JSON".equals(format)) {
            return processJsonData(data);
        } else if ("XML".equals(format)) {
            return processXmlData(data);
        }
        return processDefaultData(data);
    }
    
    private ProcessingResult processJsonData(String data) {
        return new ProcessingResult("JSON", "Processed JSON data");
    }
    
    private ProcessingResult processXmlData(String data) {
        return new ProcessingResult("XML", "Processed XML data");
    }
    
    private ProcessingResult processDefaultData(String data) {
        return new ProcessingResult("DEFAULT", "Processed default data");
    }
}
```

**Key Points:**
- Multiple methods with same name but different parameters
- Compile-time polymorphism through method overloading
- Different processing strategies based on data type
- **Strategy Pattern**: Different processing algorithms

### Example 3: Payment Strategy Pattern

```java
public interface PaymentStrategy {
    PaymentResult processPayment(PaymentContext context);
}

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {
    
    @Override
    public PaymentResult processPayment(PaymentContext context) {
        return new PaymentResult("CREDIT_CARD", "Success", context.getAmount());
    }
}

@Component
public class BankTransferPaymentStrategy implements PaymentStrategy {
    
    @Override
    public PaymentResult processPayment(PaymentContext context) {
        return new PaymentResult("BANK_TRANSFER", "Pending", context.getAmount());
    }
}

@Service
public class PaymentService {
    
    private final Map<PaymentMethod, PaymentStrategy> strategies;
    
    public PaymentResult processPayment(PaymentRequest request) {
        PaymentStrategy strategy = strategies.get(request.getPaymentMethod());
        return strategy.processPayment(request.getContext()); // Polymorphic call
    }
    
    // Method overloading for different payment scenarios
    public PaymentResult processPayment(PaymentRequest request, boolean validateOnly) {
        if (validateOnly) {
            return validatePayment(request);
        }
        return processPayment(request);
    }
    
    public List<PaymentResult> processPayment(List<PaymentRequest> requests) {
        return requests.stream()
            .map(this::processPayment)
            .collect(Collectors.toList());
    }
    
    private PaymentResult validatePayment(PaymentRequest request) {
        return new PaymentResult("VALIDATION", "Valid", BigDecimal.ZERO);
    }
}
```

**Key Points:**
- Runtime polymorphism with different payment strategies
- Method overloading for different processing scenarios
- Strategy pattern allows dynamic algorithm selection
- **Command Pattern**: Each payment request as a command object

---

## INTEGRATED DESIGN PATTERNS SUMMARY

The examples above demonstrate various design patterns integrated with OOP principles:

### **Creational Patterns**
- **Factory Pattern**: Spring creates beans and manages dependencies
- **Singleton Pattern**: Spring beans are singleton by default
- **Builder Pattern**: Used in configuration and DTO construction

### **Structural Patterns**
- **Proxy Pattern**: JPA lazy loading, AOP proxies, transaction management
- **Adapter Pattern**: Converting between different interfaces
- **Repository Pattern**: Abstracting data access layer

### **Behavioral Patterns**
- **Strategy Pattern**: Multiple implementations of same interface
- **Observer Pattern**: Event-driven architecture with notifications
- **Template Method Pattern**: Base classes with customizable steps
- **Command Pattern**: Encapsulating requests as objects

### **Architectural Patterns**
- **MVC Pattern**: Controllers, Services, Repositories separation
- **Dependency Injection**: Constructor injection throughout examples
- **Layered Architecture**: Clear separation of concerns

---

## CONCLUSION

Object-Oriented Programming principles are fundamental to building maintainable Spring Boot applications. They provide:

- **Encapsulation**: Protects data and hides implementation details
- **Abstraction**: Simplifies complex systems through interfaces
- **Inheritance**: Promotes code reuse and establishes relationships
- **Polymorphism**: Enables flexible and extensible designs

Combined with proven design patterns, these principles create robust, scalable applications that can evolve with changing business requirements.