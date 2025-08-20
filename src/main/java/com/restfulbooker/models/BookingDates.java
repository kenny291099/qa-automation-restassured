package com.restfulbooker.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Model class representing booking dates
 */
public class BookingDates {
    
    @JsonProperty("checkin")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;
    
    @JsonProperty("checkout")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;
    
    // Constructors
    public BookingDates() {}
    
    public BookingDates(LocalDate checkIn, LocalDate checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
    
    // Getters and Setters
    public LocalDate getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    
    public LocalDate getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookingDates that = (BookingDates) o;
        return Objects.equals(checkIn, that.checkIn) &&
               Objects.equals(checkOut, that.checkOut);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(checkIn, checkOut);
    }
    
    @Override
    public String toString() {
        return "BookingDates{" +
               "checkIn=" + checkIn +
               ", checkOut=" + checkOut +
               '}';
    }
}
