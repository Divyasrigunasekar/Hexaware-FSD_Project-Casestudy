package com.hexaware.fastx.ticket.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class BusDTO {

	@NotBlank(message = "Bus name is required")
    @Size(max = 100, message = "Bus name can't exceed 100 characters")
    private String busName;

    @NotBlank(message = "Bus number is required")
    @Size(max = 50, message = "Bus number can't exceed 50 characters")
    private String busNumber;

    @NotBlank(message = "Bus type is required")
    @Size(max = 50, message = "Bus type can't exceed 50 characters")
    private String busType;

    @Positive(message = "Total seats must be positive")
    private int totalSeats;

    @Size(max = 255, message = "Amenities description can't exceed 255 characters")
    private String amenities;
    

    public BusDTO() {}

    public BusDTO(String busName, String busNumber, String busType, int totalSeats, String amenities) {
        this.busName = busName;
        this.busNumber = busNumber;
        this.busType = busType;
        this.totalSeats = totalSeats;
        this.amenities = amenities;
    }

    // Getters and Setters

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public String getBusType() {
        return busType;
    }

    public void setBusType(String busType) {
        this.busType = busType;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }

    public String getAmenities() {
        return amenities;
    }

    public void setAmenities(String amenities) {
        this.amenities = amenities;
    }
}