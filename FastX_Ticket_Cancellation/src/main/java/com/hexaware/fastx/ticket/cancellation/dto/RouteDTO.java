package com.hexaware.fastx.ticket.cancellation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class RouteDTO {
	@Positive(message = "Route ID must be positive")
	private int routeId;

	@Positive(message = "Bus ID must be positive")
    private int busId;

    @NotBlank(message = "Origin is required")
    @Size(max = 50, message = "Origin must be at most 50 characters")
    private String origin;

    @NotBlank(message = "Destination is required")
    @Size(max = 50, message = "Destination must be at most 50 characters")
    private String destination;

    @NotNull(message = "Departure time is required")
    private LocalDateTime departureTime;

    @NotNull(message = "Arrival time is required")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Fare is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Fare must be greater than zero")
    private BigDecimal fare;

    // Constructors
    public RouteDTO() {}

    public RouteDTO(int busId, String origin, String destination,
                    LocalDateTime departureTime, LocalDateTime arrivalTime,
                    BigDecimal fare) {
        this.busId = busId;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.fare = fare;
    }

    // Getters and Setters
    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDateTime getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(LocalDateTime departureTime) {
        this.departureTime = departureTime;
    }

    public LocalDateTime getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(LocalDateTime arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public BigDecimal getFare() {
        return fare;
    }

    public void setFare(BigDecimal fare) {
        this.fare = fare;
    }

    @Override
    public String toString() {
        return "RouteDTO [routeId=" + routeId + ", busId=" + busId + ", origin=" + origin + ", destination=" + destination
                + ", departureTime=" + departureTime + ", arrivalTime=" + arrivalTime + ", fare=" + fare + "]";
    }

	public int getRouteId() {
		return routeId;
	}

	public void setRouteId(int routeId) {
		this.routeId = routeId;
	}
}
