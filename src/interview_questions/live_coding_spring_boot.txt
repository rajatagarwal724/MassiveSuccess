# Spring Boot REST API Development - Live Coding Preparation

## Overview
This section covers common Spring Boot REST API development tasks that may appear in a live coding round. For each topic, I've included example code, explanations, best practices, and key annotations/concepts.

## Setting Up a Spring Boot REST API

### 1. Basic Project Structure

A typical Spring Boot REST API project structure:

```
src/main/java/com/example/demo/
├── DemoApplication.java                 // Main application class
├── controllers/                         // REST controllers
│   └── UserController.java
├── models/                              // Domain objects
│   └── User.java
├── repositories/                        // Data access layer
│   └── UserRepository.java
├── services/                            // Business logic layer
│   ├── UserService.java                 // Interface
│   └── UserServiceImpl.java             // Implementation
├── exceptions/                          // Custom exceptions and handlers
│   ├── ResourceNotFoundException.java
│   └── GlobalExceptionHandler.java
└── config/                              // Configuration classes
    └── ApplicationConfig.java
```

### 2. Basic Dependencies

Essential dependencies in `pom.xml`:

```xml
<dependencies>
    <!-- Spring Web -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <!-- Spring Data JPA -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <!-- H2 Database (for rapid prototyping) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Validation -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Creating a Complete REST API (CRUD Operations)

### 1. Domain Model

```java
package com.example.demo.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    private boolean active = true;
    
    // Default constructor (required for JPA)
    public User() {
    }
    
    // Constructor with fields
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                '}';
    }
}
```

### 2. Repository Layer

```java
package com.example.demo.repositories;

import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Custom query methods
    Optional<User> findByEmail(String email);
    
    List<User> findByActiveTrue();
    
    boolean existsByEmail(String email);
}
```

### 3. Service Layer

Interface:
```java
package com.example.demo.services;

import com.example.demo.models.User;
import java.util.List;

public interface UserService {
    
    List<User> getAllUsers();
    
    User getUserById(Long id);
    
    User createUser(User user);
    
    User updateUser(Long id, User userDetails);
    
    void deleteUser(Long id);
    
    List<User> getActiveUsers();
    
    User getUserByEmail(String email);
    
    boolean isEmailTaken(String email);
}
```

Implementation:
```java
package com.example.demo.services;

import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    @Override
    public User createUser(User user) {
        // Additional validation logic can be added here
        if (isEmailTaken(user.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(Long id, User userDetails) {
        User user = getUserById(id);
        
        // Update user properties
        user.setName(userDetails.getName());
        
        // If email is changing, check if the new one is already taken
        if (!user.getEmail().equals(userDetails.getEmail()) && isEmailTaken(userDetails.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }
        
        user.setEmail(userDetails.getEmail());
        user.setActive(userDetails.isActive());
        
        return userRepository.save(user);
    }
    
    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
    }
    
    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByActiveTrue();
    }
    
    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }
    
    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}
```

### 4. Controller Layer

```java
package com.example.demo.controllers;

import com.example.demo.models.User;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // Get all users
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
    
    // Get user by ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    
    // Create new user
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }
    
    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @Valid @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails));
    }
    
    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    
    // Get active users only
    @GetMapping("/active")
    public ResponseEntity<List<User>> getActiveUsers() {
        return ResponseEntity.ok(userService.getActiveUsers());
    }
    
    // Get user by email
    @GetMapping("/byEmail")
    public ResponseEntity<User> getUserByEmail(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }
    
    // Check if email is taken
    @GetMapping("/checkEmail")
    public ResponseEntity<Boolean> isEmailTaken(@RequestParam String email) {
        return ResponseEntity.ok(userService.isEmailTaken(email));
    }
}
```

### 5. Exception Handling

Custom exception:
```java
package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

Global exception handler:
```java
package com.example.demo.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    // Handle ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
    
    // Handle general exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(
                new Date(),
                ex.getMessage(),
                request.getDescription(false));
        
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    // Handle validation errors
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
    
    // Error details class
    public static class ErrorDetails {
        private Date timestamp;
        private String message;
        private String details;
        
        public ErrorDetails(Date timestamp, String message, String details) {
            this.timestamp = timestamp;
            this.message = message;
            this.details = details;
        }
        
        // Getters
        public Date getTimestamp() {
            return timestamp;
        }
        
        public String getMessage() {
            return message;
        }
        
        public String getDetails() {
            return details;
        }
    }
}
```

## Key Spring Annotations to Know

### Controller Layer Annotations
- `@RestController`: Combines `@Controller` and `@ResponseBody`, marks the class as a controller where every method returns a domain object instead of a view.
- `@Controller`: Indicates the class is a Spring MVC controller.
- `@RequestMapping`: Maps HTTP requests to handler methods.
- `@GetMapping`, `@PostMapping`, `@PutMapping`, `@DeleteMapping`, `@PatchMapping`: Specialized versions of `@RequestMapping` for specific HTTP methods.
- `@PathVariable`: Extracts values from the URI path.
- `@RequestParam`: Extracts query parameters.
- `@RequestBody`: Binds the HTTP request body to an object.
- `@ResponseStatus`: Specifies the response status code.
- `@ExceptionHandler`: Handles exceptions in specific controller methods.

### Service Layer Annotations
- `@Service`: Indicates the class is a service component.
- `@Transactional`: Defines transaction boundaries and propagation behavior.

### Repository Layer Annotations
- `@Repository`: Indicates the class is a repository component.
- `@Query`: Defines custom queries in Spring Data JPA repositories.

### Component Annotations
- `@Component`: Generic Spring-managed component.
- `@Configuration`: Indicates the class declares bean methods.
- `@Bean`: Indicates the method produces a bean to be managed by Spring.
- `@Autowired`: Marks a constructor, field, or method for automatic dependency injection.

### Validation Annotations
- `@Valid`: Triggers validation of the annotated request body or parameter.
- `@NotNull`, `@NotBlank`, `@Size`, `@Min`, `@Max`, `@Email`: Common validation constraints.

## Common HTTP Status Codes

- **2xx Success**
  - 200 OK: Standard success response
  - 201 Created: Resource successfully created
  - 204 No Content: Successful operation with no body content (e.g., delete)

- **4xx Client Errors**
  - 400 Bad Request: Invalid request format or parameters
  - 401 Unauthorized: Authentication required
  - 403 Forbidden: Authenticated but not authorized
  - 404 Not Found: Resource not found
  - 409 Conflict: Request conflicts with current state of the server

- **5xx Server Errors**
  - 500 Internal Server Error: Generic server error
  - 503 Service Unavailable: Server temporarily unavailable

## Tips for Spring Boot REST API Interviews

1. **Understand the layered architecture:**
   - Controller → Service → Repository
   - Know the responsibility of each layer

2. **Master HTTP response best practices:**
   - Return appropriate status codes
   - Implement proper exception handling
   - Include meaningful error messages

3. **Know Spring Boot annotations:**
   - Understand the purpose of common annotations
   - Know when to use each annotation

4. **Implement validation:**
   - Use Bean Validation (JSR-380)
   - Add custom validation when needed

5. **Security awareness:**
   - Be prepared to discuss authentication/authorization
   - Know how to use Spring Security basics

6. **Testing knowledge:**
   - Understand how to write unit and integration tests
   - Know how to mock dependencies

7. **Documentation:**
   - Be familiar with tools like Swagger/OpenAPI
   - Know how to document your API endpoints

8. **Performance considerations:**
   - Pagination for large data sets
   - Caching strategies
   - Asynchronous processing

By understanding these concepts and practicing building simple REST APIs, you'll be well-prepared for Spring Boot REST API challenges in live coding interviews.
