package com.hexaware.fastx.ticket.booking.service;

import java.util.List;

import com.hexaware.fastx.ticket.booking.dto.SeatAvailabilityDTO;
import com.hexaware.fastx.ticket.booking.dto.SeatDTO;
import com.hexaware.fastx.ticket.booking.entity.Seat;

public interface ISeatService {
    Seat addSeat(SeatDTO seatDTO);
    Seat updateSeat(int seatId, SeatDTO seatDTO);
    Seat getSeatById(int seatId);
    List<SeatDTO> getAllSeats();
    void deleteSeat(int seatId);
    List<Seat> getSeatsByBusId(int busId);
    SeatAvailabilityDTO getSeatAvailabilityByBusId(int busId);
    public void assignAllSeatsToRoute(int busId, int routeId);
	void unbookSeat(int seatId);
	List<Seat> getAvailableSeatsByRoute(int routeId);
	List<SeatDTO> getAllSeatsByRoute(int routeId);
	List<String> getSeatNumbersByIds(List<Integer> seatIds);
}
