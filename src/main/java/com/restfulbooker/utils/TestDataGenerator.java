package com.restfulbooker.utils;

import com.github.javafaker.Faker;
import com.restfulbooker.models.Booking;
import com.restfulbooker.models.BookingDates;
import com.restfulbooker.models.AuthRequest;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for generating test data
 */
public class TestDataGenerator {
    
    private static final Faker faker = new Faker();
    
    /**
     * Generates a random booking with valid data
     */
    public static Booking generateRandomBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(1, 30));
        LocalDate checkOut = checkIn.plusDays(ThreadLocalRandom.current().nextInt(1, 14));
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            faker.name().firstName(),
            faker.name().lastName(),
            ThreadLocalRandom.current().nextInt(50, 2000),
            faker.bool().bool(),
            bookingDates,
            faker.options().option("Breakfast", "Lunch", "Dinner", "Late checkout", "Extra towels", null)
        );
    }
    
    /**
     * Generates a booking with specific parameters
     */
    public static Booking generateBooking(String firstName, String lastName, Integer totalPrice, 
                                        Boolean depositPaid, LocalDate checkIn, LocalDate checkOut, 
                                        String additionalNeeds) {
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        return new Booking(firstName, lastName, totalPrice, depositPaid, bookingDates, additionalNeeds);
    }
    
    /**
     * Generates booking with invalid data for negative testing
     */
    public static Booking generateInvalidBooking() {
        // Generate booking with invalid dates (checkout before checkin)
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = checkIn.minusDays(5);
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            "", // Empty first name
            "", // Empty last name
            -100, // Negative price
            null, // Null deposit
            bookingDates,
            "Invalid booking data"
        );
    }
    
    /**
     * Generates authentication request with valid credentials
     */
    public static AuthRequest generateValidAuthRequest() {
        return new AuthRequest("admin", "password123");
    }
    
    /**
     * Generates authentication request with invalid credentials
     */
    public static AuthRequest generateInvalidAuthRequest() {
        return new AuthRequest("invalid_user", "wrong_password");
    }
    
    /**
     * Updates specific fields of a booking for partial update tests
     */
    public static Booking updateBookingFields(Booking original, String field, Object value) {
        Booking updated = new Booking(
            original.getFirstName(),
            original.getLastName(),
            original.getTotalPrice(),
            original.getDepositPaid(),
            original.getBookingDates(),
            original.getAdditionalNeeds()
        );
        
        switch (field.toLowerCase()) {
            case "firstname":
                updated.setFirstName((String) value);
                break;
            case "lastname":
                updated.setLastName((String) value);
                break;
            case "totalprice":
                updated.setTotalPrice((Integer) value);
                break;
            case "depositpaid":
                updated.setDepositPaid((Boolean) value);
                break;
            case "additionalneeds":
                updated.setAdditionalNeeds((String) value);
                break;
        }
        
        return updated;
    }
}
