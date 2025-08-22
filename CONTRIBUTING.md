# Contributing to Restful Booker API Automation

Thank you for your interest in contributing to this project! This document provides guidelines for contributing to the API automation framework.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6+
- Git
- Basic understanding of REST APIs
- Familiarity with Test Automation

### Setting up Development Environment

1. Fork the repository
2. Clone your fork:
   ```bash
   git clone https://github.com/your-username/qa-automation-restassured.git
   cd qa-automation-restassured
   ```
3. Install dependencies:
   ```bash
   mvn clean compile
   ```
4. Run tests to ensure everything works:
   ```bash
   mvn test -Dgroups=smoke
   ```

## 📋 How to Contribute

### Types of Contributions

1. **Bug Reports** - Report issues with existing tests
2. **Feature Requests** - Suggest new test scenarios
3. **Code Contributions** - Add new tests or improve existing ones
4. **Documentation** - Improve project documentation
5. **Performance** - Optimize test execution

### Before Contributing

1. Check existing issues to avoid duplicates
2. Discuss major changes in an issue first
3. Ensure your contribution follows project standards
4. Write tests for new functionality

## 🔧 Development Guidelines

### Code Style

#### Java Code Standards
- Follow standard Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Keep methods small and focused
- Use proper exception handling

#### Test Structure
```java
@Epic("API Feature")
@Feature("Specific Functionality")
public class FeatureTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"})
    @Story("User Story")
    @Description("Detailed test description")
    @Severity(SeverityLevel.CRITICAL)
    public void testMethodName() {
        // Given - Setup test data
        
        // When - Execute the action
        
        // Then - Assert results
    }
}
```

### Test Categories

#### Group Usage
- `smoke` - Critical tests that must pass
- `regression` - Comprehensive test coverage
- Use both groups for critical functionality

#### Severity Levels
- `BLOCKER` - Critical system functionality
- `CRITICAL` - Core features
- `NORMAL` - Standard functionality
- `MINOR` - Edge cases
- `TRIVIAL` - Nice to have features

### Naming Conventions

#### Test Methods
- Use descriptive names: `testCreateBookingWithValidData()`
- Start with `test` prefix
- Use camelCase
- Include expected outcome

#### Variables
- Use meaningful names: `createdBookingId` not `id`
- Avoid abbreviations unless common
- Use camelCase for variables

#### Constants
- Use UPPER_CASE for constants
- Group related constants in utility classes

### File Organization

#### New Test Classes
```
src/test/java/com/restfulbooker/tests/
├── AuthTests.java          # Authentication tests
├── BookingTests.java       # Basic booking operations  
├── BookingCRUDTests.java   # CRUD operations
└── YourNewTests.java       # Your contribution
```

#### Test Data
```
src/test/resources/
├── testdata/
│   ├── booking-testdata.json
│   └── your-test-data.json
├── schemas/
│   └── your-schema.json
```

## 🧪 Testing Guidelines

### Writing Tests

#### Structure
1. **Arrange** - Set up test data and preconditions
2. **Act** - Execute the functionality being tested
3. **Assert** - Verify the expected outcomes

#### Example
```java
@Test(groups = {"smoke", "regression"})
@Description("Verify booking creation with valid data")
public void testCreateBookingWithValidData() {
    // Arrange
    Booking newBooking = TestDataGenerator.generateRandomBooking();
    
    // Act
    Response response = RestAssuredHelper.getBaseRequestSpec()
            .body(newBooking)
            .when()
            .post(ApiEndpoints.BOOKING);
    
    // Assert
    response.then()
            .statusCode(200)
            .body("bookingid", notNullValue())
            .body("booking.firstname", equalTo(newBooking.getFirstName()));
}
```

### Test Data Management

#### Using Test Data Generator
```java
// Generate random valid data
Booking booking = TestDataGenerator.generateRandomBooking();

// Generate specific data
Booking specificBooking = TestDataGenerator.generateBooking(
    "John", "Doe", 100, true, 
    LocalDate.now(), LocalDate.now().plusDays(3), 
    "Breakfast"
);

// Generate invalid data for negative tests
Booking invalidBooking = TestDataGenerator.generateInvalidBooking();
```

#### JSON Test Data
```json
{
  "validBookings": [
    {
      "firstname": "John",
      "lastname": "Doe",
      "totalprice": 111,
      "depositpaid": true,
      "bookingdates": {
        "checkin": "2024-01-01",
        "checkout": "2024-01-05"
      },
      "additionalneeds": "Breakfast"
    }
  ]
}
```

### Assertions

#### Use Descriptive Messages
```java
// Good
assertEquals(response.getStatusCode(), 200, "Expected successful response");

// Better
assertThat(response.getStatusCode())
    .as("Booking creation should return 200 status")
    .isEqualTo(200);
```

#### Multiple Assertions
```java
// Use soft assertions for multiple checks
SoftAssertions soft = new SoftAssertions();
soft.assertThat(booking.getFirstName()).isEqualTo("John");
soft.assertThat(booking.getLastName()).isEqualTo("Doe");
soft.assertThat(booking.getTotalPrice()).isGreaterThan(0);
soft.assertAll();
```

## 📝 Documentation

### Code Documentation

#### JavaDoc Comments
```java
/**
 * Creates a new booking with the provided data
 * 
 * @param booking The booking data to create
 * @return Response containing the created booking with ID
 * @throws RuntimeException if the booking creation fails
 */
public Response createBooking(Booking booking) {
    // Implementation
}
```

#### Inline Comments
- Use for complex logic
- Explain why, not what
- Keep comments up to date

### README Updates
- Update feature lists when adding new functionality
- Add new configuration options
- Update troubleshooting guide if needed

## 🔄 Pull Request Process

### Before Submitting

1. **Run All Tests**
   ```bash
   mvn clean test
   ```

2. **Check Code Quality**
   - No compiler warnings
   - Proper error handling
   - Meaningful test names

3. **Generate Reports**
   ```bash
   mvn allure:report
   ```

4. **Update Documentation**
   - Add/update JavaDoc
   - Update README if needed
   - Add inline comments for complex logic

### Pull Request Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement
- [ ] Refactoring

## Testing
- [ ] Tests pass locally
- [ ] New tests added for new functionality
- [ ] Documentation updated

## Checklist
- [ ] Code follows project standards
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
```

### Review Process

1. **Automated Checks**
   - CI pipeline passes
   - Tests execute successfully
   - No build failures

2. **Code Review**
   - Code quality assessment
   - Test coverage evaluation
   - Documentation review

3. **Approval and Merge**
   - At least one approval required
   - All checks must pass
   - Squash and merge preferred

## 🐛 Reporting Issues

### Bug Reports

Include the following information:

1. **Environment Details**
   - Java version
   - Maven version
   - Operating system

2. **Steps to Reproduce**
   - Detailed steps
   - Expected behavior
   - Actual behavior

3. **Additional Context**
   - Error messages
   - Stack traces
   - Screenshots if applicable

### Feature Requests

Include:

1. **Problem Description**
   - What problem does this solve?
   - Current limitations

2. **Proposed Solution**
   - Detailed description
   - Possible alternatives

3. **Additional Context**
   - Use cases
   - Priority level

## 🎯 Best Practices

### Test Design

1. **Independent Tests**
   - Tests should not depend on each other
   - Clean up test data after execution
   - Use unique test data

2. **Maintainable Tests**
   - Follow DRY principle
   - Use utility methods
   - Keep tests simple and focused

3. **Reliable Tests**
   - Handle timing issues
   - Use explicit waits
   - Implement retry mechanisms

### Code Quality

1. **Error Handling**
   ```java
   try {
       // API call
   } catch (Exception e) {
       logger.error("API call failed", e);
       throw new RuntimeException("Test failed due to API error", e);
   }
   ```

2. **Logging**
   ```java
   logger.info("Creating booking with data: {}", booking);
   logger.debug("Response received: {}", response.asString());
   ```

3. **Resource Management**
   - Clean up resources
   - Close connections
   - Manage test data lifecycle

## 🏆 Recognition

Contributors will be recognized in:
- README contributors section
- Release notes
- GitHub contributors page

Thank you for contributing to make this project better! 🚀


