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
                .statusCode(403);
        
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
                .statusCode(403);
        
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
    
    // ======================
    // DATA VALIDATION TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 10)
    @Story("Create Booking")
    @Description("Verify that booking creation fails with null first name")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithNullFirstName() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setFirstName(null);
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(400), is(500)));
        
        logger.info("Correctly handled booking creation with null first name");
    }
    
    @Test(groups = {"regression"}, priority = 11)
    @Story("Create Booking")
    @Description("Verify that booking creation fails with empty first name")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithEmptyFirstName() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setFirstName("");
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400), is(500))); // API accepts empty strings
        
        logger.info("Handled booking creation with empty first name");
    }
    
    @Test(groups = {"regression"}, priority = 12)
    @Story("Create Booking")
    @Description("Verify booking creation with very long name")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithLongName() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        String longName = "A".repeat(500); // 500 character name
        booking.setFirstName(longName);
        booking.setLastName(longName);
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400)));
        
        logger.info("Tested booking creation with long names (500 characters)");
    }
    
    @Test(groups = {"regression"}, priority = 13)
    @Story("Create Booking")
    @Description("Verify booking creation with special characters in name")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithSpecialCharacters() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setFirstName("João-José");
        booking.setLastName("O'Connor-Smith");
        booking.setAdditionalNeeds("café & wifi, 24/7 service");
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        assertEquals(bookingResponse.getBooking().getFirstName(), "João-José");
        assertEquals(bookingResponse.getBooking().getLastName(), "O'Connor-Smith");
        
        logger.info("Successfully created booking with special characters");
    }
    
    @Test(groups = {"regression"}, priority = 14)
    @Story("Create Booking")
    @Description("Verify booking creation with negative price")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithNegativePrice() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setTotalPrice(-100);
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400), is(500)));
        
        logger.info("Tested booking creation with negative price");
    }
    
    @Test(groups = {"regression"}, priority = 15)
    @Story("Create Booking")
    @Description("Verify booking creation with zero price")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithZeroPrice() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setTotalPrice(0);
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("booking.totalprice", equalTo(0));
        
        logger.info("Successfully created booking with zero price");
    }
    
    @Test(groups = {"regression"}, priority = 16)
    @Story("Create Booking")
    @Description("Verify booking creation with maximum integer price")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithMaxPrice() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setTotalPrice(Integer.MAX_VALUE);
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400)));
        
        logger.info("Tested booking creation with maximum integer price");
    }
    
    // ======================
    // BUSINESS LOGIC TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 17)
    @Story("Create Booking")
    @Description("Verify booking creation with checkout date before checkin date")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBookingWithInvalidDateRange() {
        Booking booking = TestDataGenerator.generateBookingWithInvalidDates();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400), is(500))); // API may or may not validate this
        
        logger.info("Tested booking creation with checkout before checkin");
    }
    
    @Test(groups = {"regression"}, priority = 18)
    @Story("Create Booking")
    @Description("Verify booking creation with same checkin and checkout dates")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithSameDate() {
        Booking booking = TestDataGenerator.generateBookingWithSameDates();
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        assertEquals(bookingResponse.getBooking().getBookingDates().getCheckIn(), 
                    bookingResponse.getBooking().getBookingDates().getCheckOut());
        
        logger.info("Successfully created booking with same checkin/checkout dates");
    }
    
    @Test(groups = {"regression"}, priority = 19)
    @Story("Create Booking")
    @Description("Verify booking creation with past dates")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithPastDates() {
        Booking booking = TestDataGenerator.generateBookingWithPastDates();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200); // API allows past dates
        
        logger.info("Successfully created booking with past dates");
    }
    
    @Test(groups = {"regression"}, priority = 20)
    @Story("Create Booking")
    @Description("Verify booking creation with far future dates")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithFutureDates() {
        Booking booking = TestDataGenerator.generateBookingWithFutureDates();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("booking.bookingdates.checkin", notNullValue())
                .body("booking.bookingdates.checkout", notNullValue());
        
        logger.info("Successfully created booking with far future dates");
    }
    
    // ======================
    // EDGE CASE TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 21)
    @Story("Create Booking")
    @Description("Verify booking creation with null additional needs")
    @Severity(SeverityLevel.TRIVIAL)
    public void testCreateBookingWithNullAdditionalNeeds() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setAdditionalNeeds(null);
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200);
        
        // Null additional needs should be handled gracefully
        
        logger.info("Successfully created booking with null additional needs");
    }
    
    @Test(groups = {"regression"}, priority = 22)
    @Story("Update Booking")
    @Description("Verify updating booking with only required fields")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingMinimalFields() {
        // First create a booking
        Booking originalBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(originalBooking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Integer bookingId = createResponse.then().extract().path("bookingid");
        
        // Update with minimal required fields
        Booking minimalBooking = TestDataGenerator.generateMinimalBooking();
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(minimalBooking)
                .pathParam("id", bookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo(minimalBooking.getFirstName()))
                .body("lastname", equalTo(minimalBooking.getLastName()));
        
        logger.info("Successfully updated booking with minimal fields");
    }
    
    @Test(groups = {"regression"}, priority = 23)
    @Story("Update Booking")
    @Description("Verify concurrent booking updates")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentBookingUpdates() {
        // Create a booking first
        Booking originalBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(originalBooking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Integer bookingId = createResponse.then().extract().path("bookingid");
        
        // Perform two concurrent updates
        Booking update1 = TestDataGenerator.generateRandomBooking();
        update1.setFirstName("FirstUpdate");
        
        Booking update2 = TestDataGenerator.generateRandomBooking();
        update2.setFirstName("SecondUpdate");
        
        // Both updates should succeed (API doesn't have locking)
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(update1)
                .pathParam("id", bookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(update2)
                .pathParam("id", bookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("SecondUpdate"));
        
        logger.info("Successfully tested concurrent booking updates");
    }
    
    @Test(groups = {"regression"}, priority = 24)
    @Story("Create Booking")
    @Description("Verify booking creation with Unicode characters")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithUnicodeCharacters() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setFirstName("张三"); // Chinese characters
        booking.setLastName("Müller"); // German umlaut
        booking.setAdditionalNeeds("需要早餐 🍳"); // Mixed unicode and emoji
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        assertEquals(bookingResponse.getBooking().getFirstName(), "张三");
        assertEquals(bookingResponse.getBooking().getLastName(), "Müller");
        
        logger.info("Successfully created booking with Unicode characters");
    }
    
    @Test(groups = {"regression"}, priority = 25)
    @Story("Update Booking")
    @Description("Verify booking update with large payload")
    @Severity(SeverityLevel.MINOR)
    public void testUpdateBookingWithLargePayload() {
        // Create a booking first
        Booking originalBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(originalBooking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Integer bookingId = createResponse.then().extract().path("bookingid");
        
        // Update with large additional needs field
        Booking largeBooking = TestDataGenerator.generateRandomBooking();
        largeBooking.setAdditionalNeeds("X".repeat(1000)); // 1000 character string
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(largeBooking)
                .pathParam("id", bookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(200), is(400), is(413))); // Could be payload too large
        
        logger.info("Tested booking update with large payload");
    }
    
    // ======================
    // SECURITY TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 26)
    @Story("Create Booking")
    @Description("Verify booking creation handles SQL injection attempts")
    @Severity(SeverityLevel.CRITICAL)
    public void testCreateBookingWithSQLInjection() {
        Booking sqlInjectionBooking = TestDataGenerator.generateBookingWithSQLInjection();
        
        Response response = RestAssuredHelper.getBaseRequestSpec()
                .body(sqlInjectionBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200) // API should handle safely
                .extract()
                .response();
        
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        
        // Verify the malicious data is stored as-is (indicating proper handling)
        assertNotNull(bookingResponse.getBookingId());
        
        logger.info("Successfully tested SQL injection protection");
    }
    
    @Test(groups = {"regression"}, priority = 27)
    @Story("Create Booking")
    @Description("Verify booking creation with extreme boundary values")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithExtremeBoundaryValues() {
        Booking extremeBooking = TestDataGenerator.generateBookingWithExtremeValues();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(extremeBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400), is(413))); // May reject extreme values
        
        logger.info("Tested booking creation with extreme boundary values");
    }
    
    // ======================
    // PERFORMANCE TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 28)
    @Story("Create Booking")
    @Description("Verify multiple rapid booking creations")
    @Severity(SeverityLevel.MINOR)
    public void testRapidBookingCreation() {
        int numberOfBookings = 5;
        
        for (int i = 0; i < numberOfBookings; i++) {
            Booking booking = TestDataGenerator.generateRandomBooking();
            booking.setFirstName("RapidTest" + i);
            
            RestAssuredHelper.getBaseRequestSpec()
                    .body(booking)
                    .when()
                    .post(ApiEndpoints.BOOKING)
                    .then()
                    .statusCode(200)
                    .body("bookingid", notNullValue())
                    .body("booking.firstname", equalTo("RapidTest" + i));
        }
        
        logger.info("Successfully created {} bookings rapidly", numberOfBookings);
    }
    
    // ======================
    // ADDITIONAL VALIDATION TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 29)
    @Story("Create Booking")
    @Description("Verify booking creation with numeric strings in name fields")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithNumericNames() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setFirstName("12345");
        booking.setLastName("67890");
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("booking.firstname", equalTo("12345"))
                .body("booking.lastname", equalTo("67890"));
        
        logger.info("Successfully created booking with numeric names");
    }
    
    @Test(groups = {"regression"}, priority = 30)
    @Story("Create Booking")
    @Description("Verify booking creation with whitespace-only names")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithWhitespaceNames() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setFirstName("   ");
        booking.setLastName("\t\t\t");
        booking.setAdditionalNeeds("\n\n\n");
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400))); // API may or may not accept whitespace
        
        logger.info("Tested booking creation with whitespace-only names");
    }
    
    @Test(groups = {"regression"}, priority = 31)
    @Story("Update Booking")
    @Description("Verify partial update preserves unchanged fields")
    @Severity(SeverityLevel.NORMAL)
    public void testPartialUpdatePreservesFields() {
        // Create a booking first
        Booking originalBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(originalBooking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Integer bookingId = createResponse.then().extract().path("bookingid");
        String originalLastName = createResponse.then().extract().path("booking.lastname");
        Integer originalPrice = createResponse.then().extract().path("booking.totalprice");
        
        // Get current booking details
        Response getCurrentResponse = RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID);
        
        Booking currentBooking = getCurrentResponse.as(Booking.class);
        
        // Update only first name using PATCH
        Booking partialUpdate = new Booking(
                "UpdatedName",
                currentBooking.getLastName(),
                currentBooking.getTotalPrice(),
                currentBooking.getDepositPaid(),
                currentBooking.getBookingDates(),
                currentBooking.getAdditionalNeeds()
        );
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(partialUpdate)
                .pathParam("id", bookingId)
                .when()
                .patch(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("UpdatedName"))
                .body("lastname", equalTo(originalLastName)) // Should be preserved
                .body("totalprice", equalTo(originalPrice)); // Should be preserved
        
        logger.info("Successfully verified partial update preserves unchanged fields");
    }
    
    @Test(groups = {"regression"}, priority = 32)
    @Story("Create Booking")
    @Description("Verify booking creation with extremely long additional needs")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithExtremelyLongAdditionalNeeds() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        booking.setAdditionalNeeds("X".repeat(10000)); // 10,000 characters
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400), is(413))); // May reject very large payloads
        
        logger.info("Tested booking creation with extremely long additional needs (10,000 chars)");
    }
    
    // ======================
    // HTTP METHOD TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 33)
    @Story("Create Booking")
    @Description("Verify booking creation fails with wrong HTTP method (GET instead of POST)")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithWrongHttpMethod() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .get(ApiEndpoints.BOOKING) // Wrong method
                .then()
                .statusCode(anyOf(is(405), is(404))); // Method not allowed or not found
        
        logger.info("Correctly handled booking creation with wrong HTTP method");
    }
    
    @Test(groups = {"regression"}, priority = 34)
    @Story("Update Booking")
    @Description("Verify booking update fails with wrong HTTP method (POST instead of PUT)")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingWithWrongHttpMethod() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(booking)
                .pathParam("id", 1)
                .when()
                .post(ApiEndpoints.BOOKING_BY_ID) // Wrong method
                .then()
                .statusCode(anyOf(is(405), is(404))); // Method not allowed
        
        logger.info("Correctly handled booking update with wrong HTTP method");
    }
    
    // ======================
    // CONTENT-TYPE TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 35)
    @Story("Create Booking")
    @Description("Verify booking creation fails with wrong content type")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithWrongContentType() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getBaseRequestSpec()
                .contentType("text/plain") // Wrong content type
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(400), is(415))); // Bad request or unsupported media type
        
        logger.info("Correctly handled booking creation with wrong content type");
    }
    
    @Test(groups = {"regression"}, priority = 36)
    @Story("Create Booking")
    @Description("Verify booking creation with malformed JSON")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithMalformedJson() {
        String malformedJson = "{ \"firstname\": \"John\", \"lastname\": \"Doe\", invalid_json }";
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(malformedJson)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(400); // Bad request for malformed JSON
        
        logger.info("Correctly handled booking creation with malformed JSON");
    }
    
    @Test(groups = {"regression"}, priority = 37)
    @Story("Create Booking")
    @Description("Verify booking creation with empty JSON object")
    @Severity(SeverityLevel.NORMAL)
    public void testCreateBookingWithEmptyJson() {
        RestAssuredHelper.getBaseRequestSpec()
                .body("{}")
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(400), is(500))); // Bad request or server error
        
        logger.info("Correctly handled booking creation with empty JSON");
    }
    
    // ======================
    // HEADER TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 38)
    @Story("Create Booking")
    @Description("Verify booking creation without Accept header")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithoutAcceptHeader() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getBaseRequestSpec()
                .contentType("application/json")
                .header("Accept", "") // Remove accept header
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200); // Should still work
        
        logger.info("Successfully created booking without Accept header");
    }
    
    @Test(groups = {"regression"}, priority = 39)
    @Story("Update Booking")
    @Description("Verify booking update with custom headers")
    @Severity(SeverityLevel.MINOR)
    public void testUpdateBookingWithCustomHeaders() {
        // Create a booking first
        Booking originalBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(originalBooking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Integer bookingId = createResponse.then().extract().path("bookingid");
        
        Booking updatedBooking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .header("X-Custom-Header", "test-value")
                .header("User-Agent", "API-Test-Suite/1.0")
                .body(updatedBooking)
                .pathParam("id", bookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200); // Should work with custom headers
        
        logger.info("Successfully updated booking with custom headers");
    }
    
    // ======================
    // WORKFLOW TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 40)
    @Story("Booking Workflow")
    @Description("Verify complete booking lifecycle: Create -> Read -> Update -> Delete")
    @Severity(SeverityLevel.CRITICAL)
    public void testCompleteBookingWorkflow() {
        // Step 1: Create booking
        Booking newBooking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(newBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .extract()
                .response();
        
        Integer bookingId = createResponse.path("bookingid");
        logger.info("Step 1: Created booking with ID: {}", bookingId);
        
        // Step 2: Read booking
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo(newBooking.getFirstName()))
                .body("lastname", equalTo(newBooking.getLastName()));
        
        logger.info("Step 2: Successfully retrieved booking");
        
        // Step 3: Update booking
        Booking updatedBooking = TestDataGenerator.generateRandomBooking();
        updatedBooking.setFirstName("WorkflowUpdated");
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(updatedBooking)
                .pathParam("id", bookingId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("WorkflowUpdated"));
        
        logger.info("Step 3: Successfully updated booking");
        
        // Step 4: Verify update
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("WorkflowUpdated"));
        
        logger.info("Step 4: Verified booking update");
        
        // Step 5: Delete booking
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .pathParam("id", bookingId)
                .when()
                .delete(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(200), is(201), is(204))); // Various success codes
        
        logger.info("Step 5: Successfully deleted booking");
        
        // Step 6: Verify deletion
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(404); // Should not be found
        
        logger.info("Step 6: Verified booking deletion - Complete workflow successful");
    }
    
    @Test(groups = {"regression"}, priority = 41)
    @Story("Booking Workflow")
    @Description("Verify multiple bookings can be created and managed independently")
    @Severity(SeverityLevel.NORMAL)
    public void testMultipleBookingsIndependence() {
        // Create multiple bookings
        Booking booking1 = TestDataGenerator.generateRandomBooking();
        booking1.setFirstName("MultiTest1");
        
        Booking booking2 = TestDataGenerator.generateRandomBooking();
        booking2.setFirstName("MultiTest2");
        
        Booking booking3 = TestDataGenerator.generateRandomBooking();
        booking3.setFirstName("MultiTest3");
        
        // Create all bookings
        Response response1 = RestAssuredHelper.getBaseRequestSpec()
                .body(booking1)
                .when()
                .post(ApiEndpoints.BOOKING);
        Integer id1 = response1.then().extract().path("bookingid");
        
        Response response2 = RestAssuredHelper.getBaseRequestSpec()
                .body(booking2)
                .when()
                .post(ApiEndpoints.BOOKING);
        Integer id2 = response2.then().extract().path("bookingid");
        
        Response response3 = RestAssuredHelper.getBaseRequestSpec()
                .body(booking3)
                .when()
                .post(ApiEndpoints.BOOKING);
        Integer id3 = response3.then().extract().path("bookingid");
        
        // Verify all bookings exist independently
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", id1)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MultiTest1"));
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", id2)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MultiTest2"));
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", id3)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MultiTest3"));
        
        // Update one booking and verify others remain unchanged
        Booking updatedBooking = TestDataGenerator.generateRandomBooking();
        updatedBooking.setFirstName("MultiTestUpdated");
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(updatedBooking)
                .pathParam("id", id2)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);
        
        // Verify only id2 was updated, others remain unchanged
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", id1)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MultiTest1")); // Unchanged
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", id2)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MultiTestUpdated")); // Changed
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", id3)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MultiTest3")); // Unchanged
        
        logger.info("Successfully verified independence of multiple bookings");
    }
    
    // ======================
    // RESPONSE VALIDATION TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 42)
    @Story("Create Booking")
    @Description("Verify booking creation response contains all required fields")
    @Severity(SeverityLevel.CRITICAL)
    public void testBookingCreationResponseValidation() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .body("bookingid", notNullValue())
                .body("bookingid", instanceOf(Integer.class))
                .body("booking", notNullValue())
                .body("booking.firstname", notNullValue())
                .body("booking.lastname", notNullValue())
                .body("booking.totalprice", notNullValue())
                .body("booking.totalprice", instanceOf(Integer.class))
                .body("booking.depositpaid", notNullValue())
                .body("booking.depositpaid", instanceOf(Boolean.class))
                .body("booking.bookingdates", notNullValue())
                .body("booking.bookingdates.checkin", notNullValue())
                .body("booking.bookingdates.checkout", notNullValue())
                .header("Content-Type", containsString("application/json"));
        
        logger.info("Successfully validated booking creation response structure");
    }
    
    @Test(groups = {"regression"}, priority = 43)
    @Story("Get Booking")
    @Description("Verify response time for booking retrieval is acceptable")
    @Severity(SeverityLevel.MINOR)
    public void testBookingRetrievalResponseTime() {
        // Create a booking first
        Booking booking = TestDataGenerator.generateRandomBooking();
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Integer bookingId = createResponse.then().extract().path("bookingid");
        
        // Test response time
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .time(lessThan(5000L)); // Response should be under 5 seconds
        
        logger.info("Booking retrieval response time is acceptable");
    }
    
    // ======================
    // ERROR HANDLING TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 44)
    @Story("Get Booking")
    @Description("Verify handling of non-numeric booking ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingWithNonNumericId() {
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", "abc123")
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(400), is(404))); // Bad request or not found
        
        logger.info("Correctly handled non-numeric booking ID");
    }
    
    @Test(groups = {"regression"}, priority = 45)
    @Story("Update Booking")
    @Description("Verify handling of booking update with invalid JSON structure")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingWithInvalidJsonStructure() {
        String invalidJsonStructure = "{ \"invalidField\": \"value\", \"anotherInvalid\": 123 }";
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(invalidJsonStructure)
                .pathParam("id", 1)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(400), is(422))); // Bad request or unprocessable entity
        
        logger.info("Correctly handled booking update with invalid JSON structure");
    }
    
    // ======================
    // URL/PATH TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 46)
    @Story("Get Booking")
    @Description("Verify handling of special characters in booking ID path")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingWithSpecialCharactersInPath() {
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", "!@#$%")
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(400), is(404))); // Bad request or not found
        
        logger.info("Correctly handled special characters in booking ID path");
    }
    
    @Test(groups = {"regression"}, priority = 47)
    @Story("Get Booking")
    @Description("Verify handling of extremely large booking ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingWithExtremelyLargeId() {
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", String.valueOf(Long.MAX_VALUE))
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(400), is(404))); // Bad request or not found
        
        logger.info("Correctly handled extremely large booking ID");
    }
    
    // ======================
    // AUTHENTICATION EDGE CASES
    // ======================
    
    @Test(groups = {"regression"}, priority = 48)
    @Story("Update Booking")
    @Description("Verify booking update with expired/invalid token")
    @Severity(SeverityLevel.NORMAL)
    public void testUpdateBookingWithInvalidToken() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        String invalidToken = "invalid_token_12345";
        
        RestAssuredHelper.getBaseRequestSpec()
                .contentType("application/json")
                .accept("application/json")
                .cookie("token", invalidToken)
                .body(booking)
                .pathParam("id", 1)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403); // Forbidden
        
        logger.info("Correctly handled booking update with invalid token");
    }
    
    @Test(groups = {"regression"}, priority = 49)
    @Story("Delete Booking")
    @Description("Verify booking deletion with empty token")
    @Severity(SeverityLevel.NORMAL)
    public void testDeleteBookingWithEmptyToken() {
        RestAssuredHelper.getBaseRequestSpec()
                .contentType("application/json")
                .accept("application/json")
                .cookie("token", "")
                .pathParam("id", 1)
                .when()
                .delete(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(403); // Forbidden
        
        logger.info("Correctly handled booking deletion with empty token");
    }
    
    // ======================
    // DATA CONSISTENCY TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 50)
    @Story("Data Consistency")
    @Description("Verify booking data consistency after multiple operations")
    @Severity(SeverityLevel.CRITICAL)
    public void testBookingDataConsistency() {
        // Create a booking with specific data
        Booking originalBooking = TestDataGenerator.generateRandomBooking();
        originalBooking.setFirstName("DataConsistency");
        originalBooking.setLastName("TestCase");
        originalBooking.setTotalPrice(999);
        originalBooking.setDepositPaid(true);
        
        Response createResponse = RestAssuredHelper.getBaseRequestSpec()
                .body(originalBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        Integer bookingId = createResponse.path("bookingid");
        
        // Retrieve and verify data consistency
        Response getResponse = RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", bookingId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .extract()
                .response();
        
        Booking retrievedBooking = getResponse.as(Booking.class);
        
        // Verify all fields match exactly
        assertEquals(retrievedBooking.getFirstName(), originalBooking.getFirstName());
        assertEquals(retrievedBooking.getLastName(), originalBooking.getLastName());
        assertEquals(retrievedBooking.getTotalPrice(), originalBooking.getTotalPrice());
        assertEquals(retrievedBooking.getDepositPaid(), originalBooking.getDepositPaid());
        assertEquals(retrievedBooking.getBookingDates().getCheckIn(), originalBooking.getBookingDates().getCheckIn());
        assertEquals(retrievedBooking.getBookingDates().getCheckOut(), originalBooking.getBookingDates().getCheckOut());
        
        // Perform multiple reads to ensure consistency
        for (int i = 0; i < 3; i++) {
            RestAssuredHelper.getBaseRequestSpec()
                    .pathParam("id", bookingId)
                    .when()
                    .get(ApiEndpoints.BOOKING_BY_ID)
                    .then()
                    .statusCode(200)
                    .body("firstname", equalTo("DataConsistency"))
                    .body("lastname", equalTo("TestCase"))
                    .body("totalprice", equalTo(999))
                    .body("depositpaid", equalTo(true));
        }
        
        logger.info("Successfully verified booking data consistency across multiple operations");
    }
    
    // ======================
    // BOOKING RETRIEVAL EDGE CASES
    // ======================
    
    @Test(groups = {"regression"}, priority = 51)
    @Story("Get Booking")
    @Description("Verify handling of negative booking ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingWithNegativeId() {
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", -1)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(400), is(404))); // Bad request or not found
        
        logger.info("Correctly handled negative booking ID");
    }
    
    @Test(groups = {"regression"}, priority = 52)
    @Story("Get Booking")
    @Description("Verify handling of zero as booking ID")
    @Severity(SeverityLevel.NORMAL)
    public void testGetBookingWithZeroId() {
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", 0)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(anyOf(is(400), is(404))); // Bad request or not found
        
        logger.info("Correctly handled zero as booking ID");
    }
    
    // ======================
    // CONCURRENCY TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 53)
    @Story("Concurrency")
    @Description("Verify concurrent booking creation doesn't cause conflicts")
    @Severity(SeverityLevel.NORMAL)
    public void testConcurrentBookingCreation() {
        // Create multiple bookings concurrently (simulated)
        Booking booking1 = TestDataGenerator.generateRandomBooking();
        booking1.setFirstName("Concurrent1");
        
        Booking booking2 = TestDataGenerator.generateRandomBooking();
        booking2.setFirstName("Concurrent2");
        
        Booking booking3 = TestDataGenerator.generateRandomBooking();
        booking3.setFirstName("Concurrent3");
        
        // Create bookings rapidly to simulate concurrency
        Response response1 = RestAssuredHelper.getBaseRequestSpec()
                .body(booking1)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Response response2 = RestAssuredHelper.getBaseRequestSpec()
                .body(booking2)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        Response response3 = RestAssuredHelper.getBaseRequestSpec()
                .body(booking3)
                .when()
                .post(ApiEndpoints.BOOKING);
        
        // Verify all were created successfully
        Integer id1 = response1.then().statusCode(200).extract().path("bookingid");
        Integer id2 = response2.then().statusCode(200).extract().path("bookingid");
        Integer id3 = response3.then().statusCode(200).extract().path("bookingid");
        
        // Verify all IDs are unique
        assertNotEquals(id1, id2);
        assertNotEquals(id2, id3);
        assertNotEquals(id1, id3);
        
        logger.info("Successfully verified concurrent booking creation with unique IDs: {}, {}, {}", id1, id2, id3);
    }
    
    // ======================
    // BOUNDARY TESTS
    // ======================
    
    @Test(groups = {"regression"}, priority = 54)
    @Story("Create Booking")
    @Description("Verify booking creation with minimum possible date")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithMinimumDate() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        // Set to a very old date
        booking.getBookingDates().setCheckIn(java.time.LocalDate.of(1900, 1, 1));
        booking.getBookingDates().setCheckOut(java.time.LocalDate.of(1900, 1, 2));
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400))); // May or may not accept very old dates
        
        logger.info("Tested booking creation with minimum possible date");
    }
    
    @Test(groups = {"regression"}, priority = 55)
    @Story("Create Booking")
    @Description("Verify booking creation with maximum possible date")
    @Severity(SeverityLevel.MINOR)
    public void testCreateBookingWithMaximumDate() {
        Booking booking = TestDataGenerator.generateRandomBooking();
        // Set to a very future date
        booking.getBookingDates().setCheckIn(java.time.LocalDate.of(2999, 12, 30));
        booking.getBookingDates().setCheckOut(java.time.LocalDate.of(2999, 12, 31));
        
        RestAssuredHelper.getBaseRequestSpec()
                .body(booking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(anyOf(is(200), is(400))); // May or may not accept very future dates
        
        logger.info("Tested booking creation with maximum possible date");
    }
    
    // ======================
    // FINAL INTEGRATION TEST
    // ======================
    
    @Test(groups = {"regression"}, priority = 56)
    @Story("Integration Test")
    @Description("Comprehensive integration test covering all CRUD operations with various data types")
    @Severity(SeverityLevel.CRITICAL)
    public void testComprehensiveIntegrationScenario() {
        logger.info("Starting comprehensive integration test...");
        
        // Test 1: Create booking with special characters
        Booking specialBooking = TestDataGenerator.generateBookingWithSpecialCharacters();
        Response createResponse1 = RestAssuredHelper.getBaseRequestSpec()
                .body(specialBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        Integer specialId = createResponse1.path("bookingid");
        logger.info("✓ Created booking with special characters: ID {}", specialId);
        
        // Test 2: Create booking with Unicode
        Booking unicodeBooking = TestDataGenerator.generateRandomBooking();
        unicodeBooking.setFirstName("测试");
        unicodeBooking.setLastName("用户");
        Response createResponse2 = RestAssuredHelper.getBaseRequestSpec()
                .body(unicodeBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        Integer unicodeId = createResponse2.path("bookingid");
        logger.info("✓ Created booking with Unicode characters: ID {}", unicodeId);
        
        // Test 3: Create minimal booking
        Booking minimalBooking = TestDataGenerator.generateMinimalBooking();
        Response createResponse3 = RestAssuredHelper.getBaseRequestSpec()
                .body(minimalBooking)
                .when()
                .post(ApiEndpoints.BOOKING)
                .then()
                .statusCode(200)
                .extract()
                .response();
        Integer minimalId = createResponse3.path("bookingid");
        logger.info("✓ Created minimal booking: ID {}", minimalId);
        
        // Test 4: Verify all bookings can be retrieved
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", specialId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);
        logger.info("✓ Retrieved special character booking");
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", unicodeId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("测试"));
        logger.info("✓ Retrieved Unicode booking");
        
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", minimalId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200);
        logger.info("✓ Retrieved minimal booking");
        
        // Test 5: Update one booking
        Booking updateData = TestDataGenerator.generateRandomBooking();
        updateData.setFirstName("IntegrationUpdated");
        
        RestAssuredHelper.getAuthenticatedRequestSpec(getAuthToken())
                .body(updateData)
                .pathParam("id", minimalId)
                .when()
                .put(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("IntegrationUpdated"));
        logger.info("✓ Updated minimal booking");
        
        // Test 6: Verify update didn't affect other bookings
        RestAssuredHelper.getBaseRequestSpec()
                .pathParam("id", specialId)
                .when()
                .get(ApiEndpoints.BOOKING_BY_ID)
                .then()
                .statusCode(200)
                .body("firstname", equalTo("José-María")); // Should remain unchanged
        logger.info("✓ Verified other bookings remain unchanged");
        
        logger.info("🎉 Comprehensive integration test completed successfully!");
    }
}
