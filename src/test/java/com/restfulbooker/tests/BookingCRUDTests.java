package com.restfulbooker.tests;

import com.restfulbooker.base.BaseTest;
import com.restfulbooker.models.Booking;
import com.restfulbooker.models.BookingResponse;
import com.restfulbooker.utils.ApiEndpoints;
import com.restfulbooker.utils.RestAssuredHelper;
import com.restfulbooker.utils.TestDataGenerator;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.hamcrest.Matchers.*;
import static org.testng.Assert.*;

/**
 * Test class for booking CRUD operations
 */
@Epic("Restful Booker API")
@Feature("Booking CRUD Operations")
public class BookingCRUDTests extends BaseTest {
    
    private Integer createdBookingId;
    
    @Test(groups = {"smoke", "regression"}, priority = 1)
    @Story("Create Booking")
    @Description("Verify that a new booking can be created successfully")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBooking() {
        Booking newBooking = TestDataGenerator.generateRandomBooking();
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(newBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("booking.firstname", equalTo(newBooking.getFirstName()))
                .body("booking.lastname", equalTo(newBooking.getLastName()))
                .body("booking.totalprice", equalTo(newBooking.getTotalPrice()))
                .body("booking.depositpaid", equalTo(newBooking.getDepositPaid()))
                .body("booking.additionalneeds", equalTo(newBooking.getAdditionalNeeds()))
                .extract()
                .response();
        
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        createdBookingId = bookingResponse.getBookingId();
        
        assertNotNull(createdBookingId, "Booking ID should not be null");
        assertTrue(createdBookingId > 0, "Booking ID should be positive");
        
        // Verify the booking details match
        Booking createdBooking = bookingResponse.getBooking();
        assertEquals(createdBooking.getFirstName(), newBooking.getFirstName());
        assertEquals(createdBooking.getLastName(), newBooking.getLastName());
        assertEquals(createdBooking.getTotalPrice(), newBooking.getTotalPrice());
        assertEquals(createdBooking.getDepositPaid(), newBooking.getDepositPaid());
        
        logger.info("Successfully created booking with ID: {}", createdBookingId);
    }
    
    @Test(groups = {"regression"}, priority = 2, dependsOnMethods = "testCreateBooking")
    @Story("Update Booking")
    @Description("Verify that an existing booking can be updated completely")
    @Severity(SeverityLevel.CRITICAL)
    public void testUpdateBookingCompletely() {
        Booking updatedBooking = TestDataGenerator.generateRandomBooking();
        
        Response response = RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(updatedBooking)
                .pathParam("id", createdBookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo(updatedBooking.getFirstName()))
                .body("lastname", equalTo(updatedBooking.getLastName()))
                .body("totalprice", equalTo(updatedBooking.getTotalPrice()))
                .body("depositpaid", equalTo(updatedBooking.getDepositPaid()))
                .extract()
                .response();
        
        Booking responseBooking = response.as(Booking.class);
        assertEquals(responseBooking.getFirstName(), updatedBooking.getFirstName());
        assertEquals(responseBooking.getLastName(), updatedBooking.getLastName());
        assertEquals(responseBooking.getTotalPrice(), updatedBooking.getTotalPrice());
        assertEquals(responseBooking.getDepositPaid(), updatedBooking.getDepositPaid());
        
        logger.info("Successfully updated booking with ID: {}", createdBookingId);
    }
    
    @Test(groups = {"regression"}, priority = 3, dependsOnMethods = "testCreateBooking")
    @Story("Update Booking")
    @Description("Verify that an existing booking can be updated partially")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingPartially() {
        // Get current booking details first
        Response getCurrentResponse = RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", createdBookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID);
        
        Booking currentBooking = getCurrentResponse.as(Booking.class);
        
        // Update only the first name
        Booking partialUpdate = TestDataGenerator.updateBookingFields(currentBooking, "firstname", "UpdatedFirstName");
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(partialUpdate)
                .pathParam("id", createdBookingId)
                .when()
                .patch(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("UpdatedFirstName"))
                .body("lastname", equalTo(currentBooking.getLastName())); // Should remain unchanged
        
        logger.info("Successfully partially updated booking with ID: {}", createdBookingId);
    }
    
    @Test(groups = {"regression"}, priority = 4)
    @Story("Update Booking")
    @Description("Verify that updating a booking without authentication fails")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingWithoutAuth() {
        Booking updatedBooking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(updatedBooking)
                .pathParam("id", 1)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403);
        
        logger.info("Correctly returned 403 for unauthenticated update request");
    }
    
    @Test(groups = {"regression"}, priority = 5)
    @Story("Update Booking")
    @Description("Verify error handling when updating non-existent booking")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateNonExistentBooking() {
        Booking updatedBooking = TestDataGenerator.generateRandomBooking();
        int invalidId = 99999;
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(updatedBooking)
                .pathParam("id", invalidId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(405);
        
        logger.info("Correctly handled update request for non-existent booking ID: {}", invalidId);
    }
    
    @Test(groups = {"regression"}, priority = 6, dependsOnMethods = "testCreateBooking")
    @Story("Delete Booking")
    @Description("Verify that an existing booking can be deleted")
    @Severity(SeverityLevel.CRITICAL)
    public void testDeleteBooking() {
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .pathParam("id", createdBookingId)
                .when()
                .delete(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(201);
        
        // Verify booking is deleted by trying to get it
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", createdBookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404);
        
        logger.info("Successfully deleted booking with ID: {}", createdBookingId);
    }
    
    @Test(groups = {"regression"}, priority = 7)
    @Story("Delete Booking")
    @Description("Verify that deleting a booking without authentication fails")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookingWithoutAuth() {
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", 1)
                .when()
                .delete(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403);
        
        logger.info("Correctly returned 403 for unauthenticated delete request");
    }
    
    @Test(groups = {"regression"}, priority = 8)
    @Story("Delete Booking")
    @Description("Verify error handling when deleting non-existent booking")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteNonExistentBooking() {
        int invalidId = 99999;
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .pathParam("id", invalidId)
                .when()
                .delete(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(405);
        
        logger.info("Correctly handled delete request for non-existent booking ID: {}", invalidId);
    }
    
    @Test(groups = {"regression"}, priority = 9)
    @Story("Create Booking")
    @Description("Verify error handling when creating booking with invalid data")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithInvalidData() {
        Booking invalidBooking = TestDataGenerator.generateInvalidBooking();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(invalidBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(400), is(500))); // API might return different status codes for invalid data
        
        logger.info("Correctly handled creation request with invalid booking data");
    }
}
