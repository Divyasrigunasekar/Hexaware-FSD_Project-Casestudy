package com.hexaware.fastx.ticket.booking.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BookingDTO {

    @Positive(message = "Booking ID must be positive")
    private int bookingId;

    @Positive(message = "User ID must be positive")
    private int userId;

    @Positive(message = "Route ID must be positive")
    private int routeId;

    @NotNull(message = "Booking time is required")
    private LocalDateTime bookingTime;

    @Positive(message = "Seats booked must be greater than zero")
    private int seatsBooked;

    @NotEmpty(message = "Seat numbers are required")
    @Size(max = 100, message = "Seat numbers count can't exceed 100")
    private List<String> seatNumbers;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status length can't exceed 50 characters")
    private String status;

    // Constructors
    public BookingDTO() {}

    public BookingDTO(int bookingId, int userId, int routeId, LocalDateTime bookingTime, int seatsBooked,
                      List<String> seatNumbers, BigDecimal totalAmount, String status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.routeId = routeId;
        this.bookingTime = bookingTime;
        this.seatsBooked = seatsBooked;
        this.seatNumbers = seatNumbers;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
