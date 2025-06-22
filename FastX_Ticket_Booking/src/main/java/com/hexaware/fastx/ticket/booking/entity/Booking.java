package com.hexaware.fastx.ticket.booking.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookingId;

    @NotNull(message = "Booking time is required")
    private LocalDateTime bookingTime;

    @Positive(message = "Seats booked must be greater than zero")
    private int seatsBooked;

    @NotBlank(message = "Seat numbers are required")
    @Size(max = 100, message = "Seat numbers length can't exceed 100 characters")
    private String seatNumbers;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotBlank(message = "Status is required")
    @Size(max = 50, message = "Status length can't exceed 50 characters")
    private String status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    @JsonBackReference // Prevent infinite recursion from User -> Bookings -> User
    private User user;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    @NotNull(message = "Route is required")
    @JsonBackReference // Prevent infinite recursion from Route -> Bookings -> Route
    private Route route;

    // New: bidirectional one-to-one with Payment
    @OneToOne(mappedBy = "booking")
    @JsonManagedReference
    private Payment payment;

    // Getters and setters...

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
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

    public String getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(String seatNumbers) {
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", bookingTime=" + bookingTime +
                ", seatsBooked=" + seatsBooked +
                ", seatNumbers='" + seatNumbers + '\'' +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", userId=" + (user != null ? user.getUserId() : "null") +
                ", routeId=" + (route != null ? route.getRouteId() : "null") +
                '}';
    }
}
