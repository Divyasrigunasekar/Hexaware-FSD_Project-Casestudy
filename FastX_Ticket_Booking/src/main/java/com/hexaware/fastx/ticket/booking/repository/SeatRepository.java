package com.hexaware.fastx.ticket.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.entity.Seat;
@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {
	List<Seat> findByBus_BusId(int busId);
	List<Seat> findByRouteRouteIdAndSeatNumberIn(int routeId, List<String> seatNumbers);
	List<Seat> findByBus(Bus bus);
	
	
}