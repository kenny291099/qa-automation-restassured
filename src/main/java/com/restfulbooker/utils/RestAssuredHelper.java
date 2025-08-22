package com.restfulbooker.utils;

import io.restassured.RestAssured;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import com.restfulbooker.config.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static io.restassured.RestAssured.given;

/**
 * Helper class for Rest Assured configuration and common operations
 */
public class RestAssuredHelper {
    
    private static final Configuration config = Configuration.getInstance();
    
    static {
        setupRestAssured();
    }
    
    /**
     * Configure Rest Assured with default settings
     */
    private static void setupRestAssured() {
        RestAssured.baseURI = config.getBaseUrl();
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        
        // Configure Jackson ObjectMapper with JSR310 support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Configure object mapper
        RestAssured.config = RestAssuredConfig.config()
                .objectMapperConfig(ObjectMapperConfig.objectMapperConfig()
                        .defaultObjectMapperType(ObjectMapperType.JACKSON_2)
                        .jackson2ObjectMapperFactory((cls, charset) -> objectMapper));
        
        // Add request timeouts with separate connection and socket timeouts
        RestAssured.config = RestAssured.config().httpClient(
                RestAssured.config().getHttpClientConfig()
                        .setParam("http.connection.timeout", config.getConnectionTimeout())
                        .setParam("http.socket.timeout", config.getSocketTimeout())
                        .setParam("http.connection-manager.timeout", config.getRequestTimeout())
        );
    }
    
    /**
     * Get base request specification with common headers
     */
    public static RequestSpecification getBaseRequestSpec() {
        RequestSpecification requestSpec = given()
                .contentType("application/json")
                .accept("application/json");
        
        if (config.isLoggingEnabled()) {
            requestSpec = requestSpec
                    .filter(new RequestLoggingFilter())
                    .filter(new ResponseLoggingFilter());
        }
        
        return requestSpec;
    }
    
    /**
     * Get request specification with authentication token
     */
    public static RequestSpecification getAuthenticatedRequestSpec(String token) {
        return getBaseRequestSpec()
                .cookie("token", token);
    }
    
    /**
     * Get request specification with basic auth
     */
    public static RequestSpecification getBasicAuthRequestSpec(String username, String password) {
        return getBaseRequestSpec()
                .auth().basic(username, password);
    }
    
    /**
     * Perform health check on the API
     */
    public static Response performHealthCheck() {
        return getBaseRequestSpec()
                .when()
                .get(ApiEndpoints.PING)
                .then()
                .extract()
                .response();
    }
}
