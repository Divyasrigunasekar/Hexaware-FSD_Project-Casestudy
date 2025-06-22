package com.hexaware.fastx.ticket.cancellation.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hexaware.fastx.ticket.cancellation.entity.Cancellation;

public interface CancellationRepository extends JpaRepository<Cancellation, Integer> {
	List<Cancellation> findByUserId(int userId);

	List<Cancellation> findByBookingId(int bookingId);
	Optional<Cancellation> findFirstByBookingId(int bookingId);
	
}