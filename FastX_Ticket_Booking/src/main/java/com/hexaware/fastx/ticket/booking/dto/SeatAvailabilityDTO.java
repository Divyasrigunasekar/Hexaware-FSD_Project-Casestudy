package com.hexaware.fastx.ticket.booking.dto;

import java.util.List;

public class SeatAvailabilityDTO {
    private List<String> bookedSeats;
    private List<String> availableSeats;
	public List<String> getBookedSeats() {
		return bookedSeats;
	}
	public void setBookedSeats(List<String> bookedSeats) {
		this.bookedSeats = bookedSeats;
	}
	public List<String> getAvailableSeats() {
		return availableSeats;
	}
	public void setAvailableSeats(List<String> availableSeats) {
		this.availableSeats = availableSeats;
	}

    // Getters and Setters
}