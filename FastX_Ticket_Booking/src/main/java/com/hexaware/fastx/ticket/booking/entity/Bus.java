/**
 * Author : DIVYA SRI G
 * Date   : 02-06-2025
 * Represents a bus in the FastX system.
 * Contains metadata such as bus number, routes, and seat configuration.
 */


package com.hexaware.fastx.ticket.booking.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "buses")
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int busId;

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
    
    @OneToMany(mappedBy = "bus", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Route> routes;
    

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

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

    public List<Route> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Route> routes) {
        this.routes = routes;
    }

    @Override
    public String toString() {
        return "Bus{" +
                "busId=" + busId +
                ", busName='" + busName + '\'' +
                ", busNumber='" + busNumber + '\'' +
                ", busType='" + busType + '\'' +
                ", totalSeats=" + totalSeats +
                ", amenities='" + amenities + '\'' +
                '}';
    }
}