package com.hexaware.fastx.ticket.booking.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import com.hexaware.fastx.ticket.booking.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
	List<Payment> findByBookingUserUserId(int userId);

	Optional<Payment> findByBooking_BookingId(int bookingId);

	
	
}