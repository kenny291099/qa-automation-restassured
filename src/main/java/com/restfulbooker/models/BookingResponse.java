package com.restfulbooker.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * Model class representing a booking response with ID
 */
public class BookingResponse {
    
    @JsonProperty("bookingid")
    private Integer bookingId;
    
    @JsonProperty("booking")
    private Booking booking;
    
    // Constructors
    public BookingResponse() {}
    
    public BookingResponse(Integer bookingId, Booking booking) {
        this.bookingId = bookingId;
        this.booking = booking;
    }
    
    // Getters and Setters
    public Integer getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }
    
    public Booking getBooking() {
        return booking;
    }
    
    public void setBooking(Booking booking) {
        this.booking = booking;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingResponse that = (BookingResponse) o;
        return Objects.equals(bookingId, that.bookingId) &&
               Objects.equals(booking, that.booking);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(bookingId, booking);
    }
    
    @Override
    public String toString() {
        return "BookingResponse{" +
               "bookingId=" + bookingId +
               ", booking=" + booking +
               '}';
    }
}
