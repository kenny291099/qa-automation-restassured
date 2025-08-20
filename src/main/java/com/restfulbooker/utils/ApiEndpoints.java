package com.restfulbooker.utils;

/**
 * Constants class for API endpoints
 */
public class ApiEndpoints {
    
    public static final String PING = "/ping";
    public static final String AUTH = "/auth";
    public static final String BOOKING = "/booking";
    public static final String BOOKING_BY_ID = "/booking/{id}";
    
    // Query parameters
    public static final String FIRSTNAME_PARAM = "firstname";
    public static final String LASTNAME_PARAM = "lastname";
    public static final String CHECKIN_PARAM = "checkin";
    public static final String CHECKOUT_PARAM = "checkout";
    
    private ApiEndpoints() {
        // Utility class - prevent instantiation
    }
}
