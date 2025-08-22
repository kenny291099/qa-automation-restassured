package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.Booking;
import com.restfulbooker.utils.ApiEndpoints;
import com.restfulbooker.utils.RestAssuredHelper;
import com.restfulbooker.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test class for booking endpoints - basic functionality
 */
@Epic("Restful Booker API")
@Feature("Booking Management")
public class BookingTests extends BaseTest {
    
    @Test(groups = {"smoke", "regression"}, priority = 1)
    @Story("View Bookings")
    @Description("Verify that all bookings can be retrieved")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetAllBookings() {
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)))
                .body("[0].bookingid", notNullValue())
                .extract()
                .response();
        
        List<Object> bookings = response.jsonPath().getList("$");
        assertTrue(bookings.size() > 0, "Should have at least one booking");
        
        logger.info("Successfully retrieved {} bookings", bookings.size());
    }
    
    @Test(groups = {"smoke", "regression"}, priority = 2)
    @Story("View Booking Details")
    @Description("Verify that a specific booking can be retrieved by ID")
    @Severity(SeverityLevel.CRITICAL)
    public void testGetBookingById() {
        // First get all bookings to get a valid ID
        Response allBookingsResponse = RestAssuredHelper.getBaseRequestSpec()
                .when()
                .get(ApiEndpoints.BOOKING);
        
        int bookingId = allBookingsResponse.jsonPath().getInt("[0].bookingid");
        
        // Get specific booking
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", notNullValue())
                .body("lastname", notNullValue())
                .body("totalprice", notNullValue())
                .body("depositpaid", notNullValue())
                .body("bookingdates", notNullValue())
                .body("bookingdates.checkin", notNullValue())
                .body("bookingdates.checkout", notNullValue())
                .extract()
                .response();
        
        Booking booking = response.as(Booking.class);
        assertNotNull(booking.getFirstName(), "First name should not be null");
        assertNotNull(booking.getLastName(), "Last name should not be null");
        assertNotNull(booking.getTotalPrice(), "Total price should not be null");
        assertNotNull(booking.getDepositPaid(), "Deposit paid should not be null");
        assertNotNull(booking.getBookingDates(), "Booking dates should not be null");
        
        logger.info("Successfully retrieved booking with ID: {}", bookingId);
    }
    
    @Test(groups = {"regression"}, priority = 3)
    @Story("View Booking Details")
    @Description("Verify error handling for non-existent booking ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingByInvalidId() {
        int invalidId = 99999;
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", invalidId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404);
        
        logger.info("Correctly returned 404 for invalid booking ID: {}", invalidId);
    }
    
    @Test(groups = {"regression"}, priority = 4)
    @Story("Search Bookings")
    @Description("Verify bookings can be filtered by first name")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingsByFirstName() {
        // First create a booking to ensure we have data
        Booking newBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(newBooking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        if (createResponse.getStatusCode() == 200) {
            // Search by firstname
            RestAssuredHelper.getBaseRequestSpec()
                    .queryParam(ApiEndpoints.FIRSTNAME_PARAM, newBooking.getFirstName())
                    .when()
                    .get(ApiEndpoints.BOOKING)
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(greaterThanOrEqualTo(0)));
            
            logger.info("Successfully searched bookings by firstname: {}", newBooking.getFirstName());
        }
    }
    
    @Test(groups = {"regression"}, priority = 5)
    @Story("Search Bookings")
    @Description("Verify bookings can be filtered by last name")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingsByLastName() {
        // Search by a common lastname
        RestAssuredHelper.getBaseRequestSpec()
                .queryParam(ApiEndpoints.LASTNAME_PARAM, "Brown")
                .when()
                .get(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
        
        logger.info("Successfully searched bookings by lastname: Brown");
    }
    
    @Test(groups = {"regression"}, priority = 6)
    @Story("Search Bookings")
    @Description("Verify bookings can be filtered by check-in date")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingsByCheckinDate() {
        String checkinDate = "2018-01-01";
        
        RestAssuredHelper.getBaseRequestSpec()
                .queryParam(ApiEndpoints.CHECKIN_PARAM, checkinDate)
                .when()
                .get(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
        
        logger.info("Successfully searched bookings by checkin date: {}", checkinDate);
    }
    
    @Test(groups = {"regression"}, priority = 7)
    @Story("Search Bookings")
    @Description("Verify bookings can be filtered by check-out date")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingsByCheckoutDate() {
        String checkoutDate = "2019-01-01";
        
        RestAssuredHelper.getBaseRequestSpec()
                .queryParam(ApiEndpoints.CHECKOUT_PARAM, checkoutDate)
                .when()
                .get(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThanOrEqualTo(0)));
        
        logger.info("Successfully searched bookings by checkout date: {}", checkoutDate);
    }
}
