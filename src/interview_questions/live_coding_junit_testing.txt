# JUnit Testing for Java - Live Coding Preparation

## Overview
This section covers essential JUnit testing concepts, strategies, and patterns that may appear in a live coding round. I've included example code for unit testing different components including mocking dependencies and handling edge cases.

## Setting Up JUnit 5 in a Spring Boot Project

### Essential Dependencies

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
    <!-- Excludes JUnit 4 if you want to use JUnit 5 exclusively -->
    <exclusions>
        <exclusion>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

The `spring-boot-starter-test` includes:
- JUnit 5
- Mockito
- Spring Test
- AssertJ
- Hamcrest

## Basic Unit Testing Examples

### Testing a Simple Utility Class

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest {

    private StringUtils stringUtils;
    
    @BeforeEach
    void setUp() {
        stringUtils = new StringUtils();
    }
    
    @Test
    @DisplayName("Should reverse a string correctly")
    void shouldReverseString() {
        assertEquals("olleh", stringUtils.reverse("hello"));
        assertEquals("", stringUtils.reverse(""));
        assertEquals("a", stringUtils.reverse("a"));
        assertEquals("321", stringUtils.reverse("123"));
    }
    
    @Test
    @DisplayName("Should handle null input in reverse method")
    void shouldHandleNullInputInReverse() {
        assertThrows(IllegalArgumentException.class, () -> {
            stringUtils.reverse(null);
        });
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"radar", "level", "madam", "a", ""})
    @DisplayName("Should identify palindromes correctly")
    void shouldIdentifyPalindrome(String input) {
        assertTrue(stringUtils.isPalindrome(input));
    }
    
    @ParameterizedTest
    @ValueSource(strings = {"hello", "world", "junit"})
    @DisplayName("Should identify non-palindromes correctly")
    void shouldIdentifyNonPalindrome(String input) {
        assertFalse(stringUtils.isPalindrome(input));
    }
    
    @ParameterizedTest
    @CsvSource({
        "hello, HELLO",
        "JUnit, JUNIT",
        "Java, JAVA",
        ", ''",
    })
    @DisplayName("Should convert strings to uppercase")
    void shouldConvertToUppercase(String input, String expected) {
        assertEquals(expected, stringUtils.toUpperCase(input));
    }
}
```

## Testing Spring Boot Service Layer with Mocking

### Example Service to Test

```java
public class UserServiceImpl implements UserService {
    
    private final UserRepository userRepository;
    
    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @Override
    public User createUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new DuplicateResourceException("User with email already exists");
        }
        return userRepository.save(user);
    }
    
    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);
        
        // If email is changing, check if new email is taken
        if (!existingUser.getEmail().equals(userDetails.getEmail()) && 
            userRepository.existsByEmail(userDetails.getEmail())) {
            throw new DuplicateResourceException("Email already taken");
        }
        
        existingUser.setName(userDetails.getName());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setActive(userDetails.isActive());
        
        return userRepository.save(existingUser);
    }
    
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}
```

### Test Class for UserService

```java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserServiceImpl userService;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        testUser.setId(1L);
    }
    
    @Test
    void shouldGetUserById() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        
        // When
        User result = userService.getUserById(1L);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        assertEquals("john@example.com", result.getEmail());
        
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });
        
        verify(userRepository, times(1)).findById(999L);
    }
    
    @Test
    void shouldGetAllUsers() {
        // Given
        List<User> users = Arrays.asList(
            testUser,
            new User("Jane Doe", "jane@example.com")
        );
        when(userRepository.findAll()).thenReturn(users);
        
        // When
        List<User> result = userService.getAllUsers();
        
        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }
    
    @Test
    void shouldCreateUser() {
        // Given
        User newUser = new User("New User", "new@example.com");
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(newUser);
        
        // When
        User result = userService.createUser(newUser);
        
        // Then
        assertNotNull(result);
        assertEquals("New User", result.getName());
        verify(userRepository, times(1)).existsByEmail("new@example.com");
        verify(userRepository, times(1)).save(newUser);
    }
    
    @Test
    void shouldThrowExceptionWhenCreatingUserWithExistingEmail() {
        // Given
        User newUser = new User("Duplicate User", "john@example.com");
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userService.createUser(newUser);
        });
        
        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldUpdateUser() {
        // Given
        User updatedDetails = new User("John Updated", "john@example.com");
        updatedDetails.setActive(false);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        
        // When
        User result = userService.updateUser(1L, updatedDetails);
        
        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Updated", result.getName());
        assertEquals("john@example.com", result.getEmail());
        assertFalse(result.isActive());
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(testUser);
    }
    
    @Test
    void shouldThrowExceptionWhenUpdatingWithTakenEmail() {
        // Given
        User updatedDetails = new User("John Doe", "taken@example.com");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("taken@example.com")).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateResourceException.class, () -> {
            userService.updateUser(1L, updatedDetails);
        });
        
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("taken@example.com");
        verify(userRepository, never()).save(any(User.class));
    }
    
    @Test
    void shouldDeleteUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);
        
        // When
        userService.deleteUser(1L);
        
        // Then
        verify(userRepository, times(1)).existsById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }
    
    @Test
    void shouldThrowExceptionWhenDeletingNonExistentUser() {
        // Given
        when(userRepository.existsById(999L)).thenReturn(false);
        
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.deleteUser(999L);
        });
        
        verify(userRepository, times(1)).existsById(999L);
        verify(userRepository, never()).deleteById(any());
    }
}
```

## Testing Spring REST Controller

```java
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private User testUser;
    
    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com");
        testUser.setId(1L);
    }
    
    @Test
    void shouldGetUserById() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
        
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    void shouldReturn404WhenUserNotFound() throws Exception {
        // Given
        when(userService.getUserById(999L))
                .thenThrow(new ResourceNotFoundException("User not found with id: 999"));
        
        // When & Then
        mockMvc.perform(get("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(userService, times(1)).getUserById(999L);
    }
    
    @Test
    void shouldCreateUser() throws Exception {
        // Given
        User newUser = new User("New User", "new@example.com");
        when(userService.createUser(any(User.class))).thenReturn(testUser);
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"));
        
        verify(userService, times(1)).createUser(any(User.class));
    }
    
    @Test
    void shouldReturnBadRequestWhenInvalidInput() throws Exception {
        // Given
        User invalidUser = new User("", "invalid-email");
        
        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());
        
        verify(userService, never()).createUser(any(User.class));
    }
    
    @Test
    void shouldUpdateUser() throws Exception {
        // Given
        User updatedUser = new User("Updated Name", "john@example.com");
        when(userService.updateUser(eq(1L), any(User.class))).thenReturn(updatedUser);
        
        // When & Then
        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
        
        verify(userService, times(1)).updateUser(eq(1L), any(User.class));
    }
    
    @Test
    void shouldDeleteUser() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);
        
        // When & Then
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
        
        verify(userService, times(1)).deleteUser(1L);
    }
}
```

## Testing a Service with External Dependencies (REST Client)

```java
@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;
    
    @Mock
    private WeatherApiConfig apiConfig;
    
    @InjectMocks
    private WeatherServiceImpl weatherService;
    
    @BeforeEach
    void setUp() {
        when(apiConfig.getUrl()).thenReturn("http://api.weather.com");
        when(apiConfig.getKey()).thenReturn("test-api-key");
    }
    
    @Test
    void shouldGetCurrentWeather() {
        // Given
        String city = "London";
        String url = "http://api.weather.com/current?city=London&apiKey=test-api-key";
        
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setTemperature(20.5);
        mockResponse.setCondition("Sunny");
        
        when(restTemplate.getForObject(url, WeatherResponse.class))
                .thenReturn(mockResponse);
        
        // When
        WeatherInfo result = weatherService.getCurrentWeather(city);
        
        // Then
        assertNotNull(result);
        assertEquals(20.5, result.getTemperature());
        assertEquals("Sunny", result.getCondition());
        verify(restTemplate, times(1))
                .getForObject(url, WeatherResponse.class);
    }
    
    @Test
    void shouldHandleApiError() {
        // Given
        String city = "Unknown";
        String url = "http://api.weather.com/current?city=Unknown&apiKey=test-api-key";
        
        when(restTemplate.getForObject(url, WeatherResponse.class))
                .thenThrow(new RestClientException("API Error"));
        
        // When & Then
        assertThrows(WeatherServiceException.class, () -> {
            weatherService.getCurrentWeather(city);
        });
        
        verify(restTemplate, times(1))
                .getForObject(url, WeatherResponse.class);
    }
}
```

## JUnit Testing Best Practices

1. **Follow the AAA Pattern**
   - Arrange: Set up the test conditions
   - Act: Execute the code under test
   - Assert: Verify the result

2. **Test Isolation**
   - Each test should be independent
   - Use @BeforeEach and @AfterEach for setup and cleanup
   - Avoid shared state between tests

3. **Edge Case Testing**
   - Test null inputs
   - Test empty collections
   - Test boundary conditions
   - Test invalid inputs

4. **Mock External Dependencies**
   - Use Mockito to isolate the code under test
   - Mock repositories, services, and external APIs
   - Verify mock interactions using verify()

5. **Coverage Considerations**
   - Test all logical branches
   - Test error handling
   - Aim for high coverage but focus on critical paths

6. **Testing Exceptions**
   - Use assertThrows() to verify exceptions
   - Verify exception messages when needed
   - Test both positive and negative scenarios

7. **Naming Convention**
   - Use descriptive test method names
   - Follow the pattern: shouldDoSomethingWhenSomethingHappens()
   - Use @DisplayName for additional clarity

## Common Interview Test Scenarios

1. **Discount Calculator Service**
   - Calculate discounts based on purchase amount, customer tier, etc.
   - Test different discount rules and edge cases
   - Verify discount calculations and thresholds

2. **Order Processing Service**
   - Test order validation, status changes, and business rules
   - Mock inventory and payment services
   - Test various order scenarios and error conditions

3. **Data Transformation Service**
   - Test mapping between DTOs and domain objects
   - Verify correct transformation of data structures
   - Test edge cases in data conversion

## Key JUnit 5 Features to Know

1. **Annotations**
   - @Test: Marks a method as a test
   - @DisplayName: Provides custom display name
   - @BeforeEach, @AfterEach: Setup/cleanup for each test
   - @BeforeAll, @AfterAll: Setup/cleanup once for all tests
   - @Disabled: Skip a test
   - @Tag: Categorize tests for selective execution

2. **Assertions**
   - assertEquals, assertNotEquals
   - assertTrue, assertFalse
   - assertNull, assertNotNull
   - assertThrows
   - assertAll (group multiple assertions)
   - assertTimeout

3. **Parameterized Tests**
   - @ParameterizedTest
   - @ValueSource: Array of literal values
   - @CsvSource: Comma-separated values
   - @MethodSource: Values from a method
   - @EnumSource: Values from an enum

4. **Advanced Features**
   - Test Lifecycle: Per-method, per-class
   - Conditional Test Execution
   - Test Instance Lifecycle
   - Nested Tests with @Nested
   - Test Interfaces and Default Methods

By understanding these concepts and practicing writing various types of tests, you'll be well-prepared for JUnit testing challenges in live coding interviews.
