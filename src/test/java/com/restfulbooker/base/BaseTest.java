package com.restfulbooker.base;

import com.restfulbooker.config.Configuration;
import com.restfulbooker.models.AuthRequest;
import com.restfulbooker.models.AuthResponse;
import com.restfulbooker.utils.ApiEndpoints;
import com.restfulbooker.utils.RestAssuredHelper;
import com.restfulbooker.utils.TestDataGenerator;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Base test class with common setup and utilities
 */
public class BaseTest {
    
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected static final Configuration config = Configuration.getInstance();
    protected String authToken;
    
    @BeforeClass(alwaysRun = true)
    public void setUpClass() {
        logger.info("Setting up test class: {}", this.getClass().getSimpleName());
        performHealthCheck();
        authenticateUser();
    }
    
    @BeforeMethod(alwaysRun = true) 
    public void setUpMethod() {
        logger.info("Starting test method");
    }
    
    @Step("Perform API health check")
    protected void performHealthCheck() {
        logger.info("Performing health check on API: {}", config.getBaseUrl());
        
        Response response = RestAssuredHelper.performHealthCheck();
        
        response.then()
                .statusCode(201)
                .body(equalTo("Created"));
        
        logger.info("Health check passed - API is responding");
    }
    
    @Step("Authenticate user and get token")
    protected void authenticateUser() {
        logger.info("Authenticating user to get auth token");
        
        AuthRequest authRequest = TestDataGenerator.generateValidAuthRequest();
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(authRequest)
                .when()
                .post(ApiEndpoints.AUTH)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        AuthResponse authResponse = response.as(AuthResponse.class);
        this.authToken = authResponse.getToken();
        
        logger.info("Authentication successful - Token obtained");
    }
    
    @Step("Get authentication token")
    protected String getAuthToken() {
        if (authToken == null || authToken.isEmpty()) {
            authenticateUser();
        }
        return authToken;
    }
    
    /**
     * Wait for a specified amount of time
     */
    protected void waitFor(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("Thread interrupted during wait", e);
        }
    }
}
