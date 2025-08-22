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
    
    /**
     * Generates booking with invalid date range (checkout before checkin)
     */
    public static Booking generateBookingWithInvalidDates() {
        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = checkIn.minusDays(5); // Invalid: checkout before checkin
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            faker.name().firstName(),
            faker.name().lastName(),
            ThreadLocalRandom.current().nextInt(50, 2000),
            faker.bool().bool(),
            bookingDates,
            faker.lorem().sentence()
        );
    }
    
    /**
     * Generates booking with same checkin and checkout dates
     */
    public static Booking generateBookingWithSameDates() {
        LocalDate sameDate = LocalDate.now().plusDays(5);
        BookingDates bookingDates = new BookingDates(sameDate, sameDate);
        
        return new Booking(
            faker.name().firstName(),
            faker.name().lastName(),
            ThreadLocalRandom.current().nextInt(50, 500),
            faker.bool().bool(),
            bookingDates,
            "Same day booking"
        );
    }
    
    /**
     * Generates booking with past dates
     */
    public static Booking generateBookingWithPastDates() {
        LocalDate checkIn = LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(5, 30));
        LocalDate checkOut = checkIn.plusDays(ThreadLocalRandom.current().nextInt(1, 7));
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            faker.name().firstName(),
            faker.name().lastName(),
            ThreadLocalRandom.current().nextInt(50, 1000),
            faker.bool().bool(),
            bookingDates,
            "Historical booking"
        );
    }
    
    /**
     * Generates booking with far future dates
     */
    public static Booking generateBookingWithFutureDates() {
        LocalDate checkIn = LocalDate.now().plusDays(ThreadLocalRandom.current().nextInt(365, 1095)); // 1-3 years
        LocalDate checkOut = checkIn.plusDays(ThreadLocalRandom.current().nextInt(1, 21));
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            faker.name().firstName(),
            faker.name().lastName(),
            ThreadLocalRandom.current().nextInt(100, 3000),
            faker.bool().bool(),
            bookingDates,
            "Future booking"
        );
    }
    
    /**
     * Generates booking with minimal required fields only
     */
    public static Booking generateMinimalBooking() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(1);
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            "John",
            "Doe",
            100,
            true,
            bookingDates,
            null // No additional needs
        );
    }
    
    /**
     * Generates authentication request with empty credentials
     */
    public static AuthRequest generateEmptyAuthRequest() {
        return new AuthRequest("", "");
    }
    
    /**
     * Generates authentication request with null credentials
     */
    public static AuthRequest generateNullAuthRequest() {
        return new AuthRequest(null, null);
    }
    
    /**
     * Generates booking with extreme values for boundary testing
     */
    public static Booking generateBookingWithExtremeValues() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(1);
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            "A".repeat(100), // Very long first name
            "B".repeat(100), // Very long last name
            Integer.MAX_VALUE, // Maximum price
            true,
            bookingDates,
            "X".repeat(500) // Very long additional needs
        );
    }
    
    /**
     * Generates booking with special characters and Unicode
     */
    public static Booking generateBookingWithSpecialCharacters() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(2);
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            "José-María",
            "O'Connor-Smith",
            150,
            false,
            bookingDates,
            "Special chars: @#$%^&*()_+{}|:<>?[]\\;'\",./"
        );
    }
    
    /**
     * Generates a random price within specified range
     */
    public static Integer generateRandomPrice(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
    
    /**
     * Generates a random date within specified range from today
     */
    public static LocalDate generateRandomFutureDate(int minDays, int maxDays) {
        int days = ThreadLocalRandom.current().nextInt(minDays, maxDays + 1);
        return LocalDate.now().plusDays(days);
    }
    
    /**
     * Generates booking with SQL injection attempt in string fields
     */
    public static Booking generateBookingWithSQLInjection() {
        LocalDate checkIn = LocalDate.now().plusDays(1);
        LocalDate checkOut = checkIn.plusDays(1);
        
        BookingDates bookingDates = new BookingDates(checkIn, checkOut);
        
        return new Booking(
            "'; DROP TABLE bookings; --",
            "1' OR '1'='1",
            100,
            true,
            bookingDates,
            "<script>alert('xss')</script>"
        );
    }
}
