package com.hexaware.fastx.ticket.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexaware.fastx.ticket.booking.entity.Booking;
@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
	List<Booking> findByUserUserId(int userId);
}