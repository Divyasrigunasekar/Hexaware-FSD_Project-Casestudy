package com.hexaware.fastx.ticket.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class SeatDTO {

    @Positive(message = "Seat ID must be positive")
    private int seatId;

    @Positive(message = "Route ID must be positive")
    private int routeId;

    @Positive(message = "Bus ID must be positive")
    private int busId;

    @NotBlank(message = "Seat number is required")
    @Size(max = 10, message = "Seat number must be at most 10 characters")
    private String seatNumber;

    private boolean isBooked;

    // Constructors
    public SeatDTO() {}

    public SeatDTO(int seatId, int routeId, int busId, String seatNumber, boolean isBooked) {
        this.seatId = seatId;
        this.routeId = routeId;
        this.busId = busId;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
    }

    // Getters and Setters
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public void setBooked(boolean isBooked) {
        this.isBooked = isBooked;
    }
}
