/** Author : DIVYA SRI G
 * Date   : 02-06-2025
 * Represents an individual seat on a bus.
 * Each seat is associated with a specific bus and can be booked or unbooked by users.
 */

package com.hexaware.fastx.ticket.booking.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "seats")
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seatId;
    @NotBlank(message = "Seat number is required")
    @Size(max = 10, message = "Seat number must be at most 10 characters")
    private String seatNumber;

    private boolean isBooked;

    @ManyToOne
    @JoinColumn(name = "route_id", nullable = false)
    @JsonBackReference
    private Route route;

    @ManyToOne
    @JoinColumn(name = "bus_id", nullable = false)
    @NotNull(message = "Bus is required")
    @JsonBackReference
    private Bus bus;

    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
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

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Bus getBus() {
        return bus;
    }

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "seatId=" + seatId +
                ", seatNumber=" + seatNumber +
                ", isBooked=" + isBooked +
                ", routeId=" + (route != null ? route.getRouteId() : "null") +
                ", busId=" + (bus != null ? bus.getBusId() : "null") +
                '}';
    }
}