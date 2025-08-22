# Restful Booker API Automation Project

[![API Tests](https://github.com/your-org/qa-automation-restassured/actions/workflows/api-tests.yml/badge.svg)](https://github.com/your-org/qa-automation-restassured/actions/workflows/api-tests.yml)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/projects/jdk/17/)
[![Rest Assured](https://img.shields.io/badge/Rest%20Assured-5.4.0-green.svg)](https://rest-assured.io/)
[![Allure](https://img.shields.io/badge/Allure-2.24.0-yellow.svg)](https://docs.qameta.io/allure/)

A comprehensive API automation testing framework built with Rest Assured and Java 17 for testing the [Restful Booker API](https://restful-booker.herokuapp.com).

## 🚀 Features

- **Java 17** - Latest LTS version with modern language features
- **Rest Assured** - Powerful library for API testing
- **TestNG** - Flexible testing framework with advanced annotations
- **Allure Reports** - Beautiful and detailed test reporting
- **CI/CD Integration** - GitHub Actions workflows for automated testing
- **Page Object Pattern** - Well-structured and maintainable test architecture
- **Data-Driven Testing** - JSON-based test data management
- **Schema Validation** - JSON schema validation for API responses
- **Logging** - Comprehensive logging with Logback
- **Retry Mechanism** - Automatic retry for flaky tests

## 📋 API Coverage

This project covers comprehensive testing of the Restful Booker API:

### Authentication
- ✅ Valid user authentication
- ✅ Invalid credential handling
- ✅ Empty/null credential validation
- ✅ HTTP method validation

### Booking Management
- ✅ Get all bookings
- ✅ Get booking by ID
- ✅ Search bookings by parameters (firstname, lastname, dates)
- ✅ Create new bookings
- ✅ Update bookings (complete & partial)
- ✅ Delete bookings
- ✅ Error handling for invalid operations

### Validation
- ✅ JSON schema validation
- ✅ Response structure validation
- ✅ Data type validation
- ✅ Business logic validation

## 🛠️ Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **Git**
- Internet connection (for API access)

## ⚡ Quick Start

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/qa-automation-restassured.git
cd qa-automation-restassured
```

### 2. Install Dependencies
```bash
mvn clean compile
```

### 3. Run Tests
```bash
# Run all tests
mvn test

# Run smoke tests only
mvn test -Dgroups=smoke

# Run regression tests only
mvn test -Dgroups=regression
```

### 4. Generate Allure Report
```bash
# Generate and open report
mvn allure:serve

# Generate report only
mvn allure:report
```

## 📁 Project Structure

```
qa-automation-restassured/
├── src/
│   ├── main/java/com/restfulbooker/
│   │   ├── config/           # Configuration management
│   │   ├── models/           # API model classes
│   │   └── utils/            # Utility classes
│   └── test/
│       ├── java/com/restfulbooker/
│       │   ├── base/         # Base test classes
│       │   └── tests/        # Test classes
│       └── resources/
│           ├── schemas/      # JSON schemas
│           ├── testdata/     # Test data files
│           ├── allure.properties
│           ├── config.properties
│           ├── logback-test.xml
│           └── testng.xml
├── .github/workflows/        # CI/CD workflows
├── target/                   # Build artifacts
├── pom.xml                  # Maven configuration
└── README.md
```

## 🧪 Test Categories

### Smoke Tests
Critical functionality tests that run quickly:
- Authentication with valid credentials
- Get all bookings
- Get booking by ID
- Create booking

```bash
mvn test -Dgroups=smoke
```

### Regression Tests
Comprehensive test suite covering all functionality:
- All smoke tests
- Error handling scenarios
- Edge cases
- Negative testing

```bash
mvn test -Dgroups=regression
```

## 📊 Reporting

### Allure Reports
The project generates beautiful Allure reports with:
- Test execution results
- Step-by-step details
- Request/response logs
- Screenshots and attachments
- Historical trends
- Test categorization

#### Generating Reports
```bash
# Serve report in browser
mvn allure:serve

# Generate static report
mvn allure:report
```

#### Report Features
- 📈 **Dashboards** - Overall test statistics
- 🔍 **Test Details** - Step-by-step execution
- 📝 **Logs** - Request/response details
- 📊 **Trends** - Historical test results
- 🏷️ **Categories** - Test organization by features

### TestNG Reports
Standard TestNG reports are available in `target/surefire-reports/`

## ⚙️ Configuration

### Environment Configuration
Modify `src/test/resources/config.properties`:

```properties
# API Configuration
base.url=https://restful-booker.herokuapp.com
request.timeout=30000

# Test Configuration
logging.enabled=true
retry.count=3

# Authentication
auth.username=admin
auth.password=password123
```

### System Properties
Override configuration at runtime:

```bash
mvn test -Dbase.url=https://your-api.com -Dlogging.enabled=false
```

## 🔄 CI/CD Integration

### GitHub Actions
The project includes automated workflows:

#### API Tests Workflow (`.github/workflows/api-tests.yml`)
- Triggers: Push, PR, Schedule, Manual
- Runs on Ubuntu with Java 17
- Executes smoke and regression tests
- Generates and publishes Allure reports
- Uploads test artifacts

#### Dependency Check Workflow
- Weekly dependency updates check
- Security vulnerability scanning

### Running in CI
Tests run automatically on:
- Push to main/develop branches
- Pull requests
- Daily schedule (2 AM UTC)
- Manual trigger

## 🔧 Development Guidelines

### Adding New Tests
1. Create test class in appropriate package
2. Extend `BaseTest` class
3. Use appropriate TestNG groups (`smoke`, `regression`)
4. Add Allure annotations for reporting
5. Follow naming conventions

### Test Class Example
```java
@Epic("Restful Booker API")
@Feature("Booking Management")
public class NewFeatureTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"})
    @Story("Feature Story")
    @Description("Test description")
    @Severity(SeverityLevel.CRITICAL)
    public void testNewFeature() {
        // Test implementation
    }
}
```

### Best Practices
- ✅ Use meaningful test names
- ✅ Add proper documentation
- ✅ Include assertions with messages
- ✅ Use data providers for parameterized tests
- ✅ Implement proper error handling
- ✅ Add appropriate test groups

## 🐛 Troubleshooting

### Common Issues

#### API Connection Issues
```bash
# Check API health
curl https://restful-booker.herokuapp.com/ping
```

#### Maven Issues
```bash
# Clean and reinstall
mvn clean install

# Skip tests during build
mvn clean install -DskipTests
```

#### Java Version Issues
```bash
# Check Java version
java -version

# Set JAVA_HOME
export JAVA_HOME=/path/to/java17
```

### Debug Mode
Enable debug logging:
```bash
mvn test -Dlogging.enabled=true -X
```

## 📚 Resources

- [Restful Booker API Documentation](https://restful-booker.herokuapp.com/apidoc/index.html)
- [Rest Assured Documentation](https://rest-assured.io/)
- [TestNG Documentation](https://testng.org/doc/)
- [Allure Documentation](https://docs.qameta.io/allure/)
- [Maven Documentation](https://maven.apache.org/guides/)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add/update tests
5. Run the test suite
6. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 📞 Support

For questions or issues:
- Create an issue on GitHub
- Check existing documentation
- Review troubleshooting guide

---

**Happy Testing! 🚀**


