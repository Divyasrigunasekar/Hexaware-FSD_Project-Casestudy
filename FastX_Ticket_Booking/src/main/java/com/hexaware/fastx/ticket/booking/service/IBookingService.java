package com.hexaware.fastx.ticket.booking.service;
import java.util.List;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.dto.BookingDTO;

public interface IBookingService {
    Booking addBooking(BookingDTO bookingDTO);
    Booking updateBooking(int bookingId, BookingDTO bookingDTO);
    Booking getBookingById(int bookingId);
    List<Booking> getAllBookings();
    void deleteBooking(int bookingId);

    // New: get bookings by user
    List<Booking> getBookingsByUserId(int userId);
    Booking cancelBooking(int bookingId);
    
    BookingDTO mapToDTO(Booking booking);
    List<BookingDTO> getAllBookingDTOs();
    List<BookingDTO> getBookingDTOsByUserId(int userId);
	Booking partiallyCancelSeats(int bookingId, List<String> seatsToCancel);
    
}