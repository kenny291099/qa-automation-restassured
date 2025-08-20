package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.models.AuthResponse;
import com.restfulbooker.utils.ApiEndpoints;
import com.restfulbooker.utils.RestAssuredHelper;
import com.restfulbooker.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test class for authentication endpoints
 */
@Epic("Restful Booker API")
@Feature("Authentication")
public class AuthTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, priority = 1)
    @Story("User Authentication")
    @Description("Verify that a user can authenticate with valid credentials")
    @Severity(SeverityLevel.CRITICAL)
    public void testValidAuthentication() {
        AuthRequest authRequest = TestDataGenerator.generateValidAuthRequest();
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(authRequest)
                .when()
                .post(ApiEndpoints.AUTH)
                .then()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(emptyString()))
                .extract()
                .response();
        
        AuthResponse authResponse = response.as(AuthResponse.class);
        assertNotNull(authResponse.getToken(), "Token should not be null");
        assertFalse(authResponse.getToken().isEmpty(), "Token should not be empty");
        
        logger.info("Authentication successful with token: {}", authResponse.getToken());
    }
    
    @Test(groups = {"regression"}, priority = 2)
    @Story("User Authentication")
    @Description("Verify that authentication fails with invalid credentials")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidAuthentication() {
        AuthRequest authRequest = TestDataGenerator.generateInvalidAuthRequest();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(authRequest)
                .when()
                .post(ApiEndpoints.AUTH)
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
        
        logger.info("Authentication correctly failed for invalid credentials");
    }
    
    @Test(groups = {"regression"}, priority = 3)
    @Story("User Authentication")
    @Description("Verify authentication with empty username")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthenticationWithEmptyUsername() {
        AuthRequest authRequest = new AuthRequest("", "password123");
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(authRequest)
                .when()
                .post(ApiEndpoints.AUTH)
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
        
        logger.info("Authentication correctly failed for empty username");
    }
    
    @Test(groups = {"regression"}, priority = 4)
    @Story("User Authentication")
    @Description("Verify authentication with empty password")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthenticationWithEmptyPassword() {
        AuthRequest authRequest = new AuthRequest("admin", "");
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(authRequest)
                .when()
                .post(ApiEndpoints.AUTH)
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
        
        logger.info("Authentication correctly failed for empty password");
    }
    
    @Test(groups = {"regression"}, priority = 5)
    @Story("User Authentication")
    @Description("Verify authentication with null request body")
    @Severity(SeverityLevel.NORMAL)
    public void testAuthenticationWithNullBody() {
        RestAssuredHelper.getBaseRequestSpec()
                .when()
                .post(ApiEndpoints.AUTH)
                .then()
                .statusCode(500);
        
        logger.info("Authentication correctly failed for null request body");
    }
    
    @Test(groups = {"regression"}, priority = 6)
    @Story("User Authentication")
    @Description("Verify authentication endpoint with wrong HTTP method")
    @Severity(SeverityLevel.MINOR)
    public void testAuthenticationWithWrongMethod() {
        AuthRequest authRequest = TestDataGenerator.generateValidAuthRequest();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(authRequest)
                .when()
                .get(ApiEndpoints.AUTH)
                .then()
                .statusCode(405);
        
        logger.info("Authentication correctly returned 405 for GET method");
    }
}
