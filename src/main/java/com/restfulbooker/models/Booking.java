package com.restfulbooker.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Model class representing a booking
 */
public class Booking {
    
    @JsonProperty("firstname")
    private String firstName;
    
    @JsonProperty("lastname")
    private String lastName;
    
    @JsonProperty("totalprice")
    private Integer totalPrice;
    
    @JsonProperty("depositpaid")
    private Boolean depositPaid;
    
    @JsonProperty("bookingdates")
    private BookingDates bookingDates;
    
    @JsonProperty("additionalneeds")
    private String additionalNeeds;
    
    // Constructors
    public Booking() {}
    
    public Booking(String firstName, String lastName, Integer totalPrice, 
                   Boolean depositPaid, BookingDates bookingDates, String additionalNeeds) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.totalPrice = totalPrice;
        this.depositPaid = depositPaid;
        this.bookingDates = bookingDates;
        this.additionalNeeds = additionalNeeds;
    }
    
    // Getters and Setters
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public Integer getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public Boolean getDepositPaid() {
        return depositPaid;
    }
    
    public void setDepositPaid(Boolean depositPaid) {
        this.depositPaid = depositPaid;
    }
    
    public BookingDates getBookingDates() {
        return bookingDates;
    }
    
    public void setBookingDates(BookingDates bookingDates) {
        this.bookingDates = bookingDates;
    }
    
    public String getAdditionalNeeds() {
        return additionalNeeds;
    }
    
    public void setAdditionalNeeds(String additionalNeeds) {
        this.additionalNeeds = additionalNeeds;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(firstName, booking.firstName) &&
               Objects.equals(lastName, booking.lastName) &&
               Objects.equals(totalPrice, booking.totalPrice) &&
               Objects.equals(depositPaid, booking.depositPaid) &&
               Objects.equals(bookingDates, booking.bookingDates) &&
               Objects.equals(additionalNeeds, booking.additionalNeeds);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, totalPrice, depositPaid, bookingDates, additionalNeeds);
    }
    
    @Override
    public String toString() {
        return "Booking{" +
               "firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", totalPrice=" + totalPrice +
               ", depositPaid=" + depositPaid +
               ", bookingDates=" + bookingDates +
               ", additionalNeeds='" + additionalNeeds + '\'' +
               '}';
    }
}

