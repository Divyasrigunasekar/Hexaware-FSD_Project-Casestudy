package com.hexaware.fastx.ticket.booking.controller;

import com.hexaware.fastx.ticket.booking.dto.BookingDTO;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.service.IBookingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

    @Autowired
    private IBookingService bookingService;

    // Create booking - only USER role
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingDTO> addBooking(@RequestBody BookingDTO bookingDTO) {
        Booking booking = bookingService.addBooking(bookingDTO);
        BookingDTO responseDTO = bookingService.mapToDTO(booking);
        return ResponseEntity.ok(responseDTO);
    }

    // Update booking - only ADMIN role
    @PutMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable int bookingId, @RequestBody BookingDTO bookingDTO) {
        Booking updatedBooking = bookingService.updateBooking(bookingId, bookingDTO);
        BookingDTO responseDTO = bookingService.mapToDTO(updatedBooking);
        return ResponseEntity.ok(responseDTO);
    }

    // Get booking by ID - USER or ADMIN
    @GetMapping("/{bookingId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable int bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        BookingDTO responseDTO = bookingService.mapToDTO(booking);
        return ResponseEntity.ok(responseDTO);
    }

    // Get all bookings - ADMIN only
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        List<BookingDTO> bookings = bookingService.getAllBookingDTOs();
        return ResponseEntity.ok(bookings);
    }

    // Get bookings by user ID - USER can get only their own bookings, ADMIN can get any user bookings
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('USER') and #userId == principal.userId)")
    public ResponseEntity<List<BookingDTO>> getBookingsByUserId(@PathVariable int userId) {
        List<BookingDTO> bookings = bookingService.getBookingDTOsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    // Delete booking - ADMIN only
    @DeleteMapping("/{bookingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBooking(@PathVariable int bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    // Cancel booking - USER or ADMIN
    @PutMapping("/{bookingId}/cancel")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable int bookingId) {
        Booking cancelledBooking = bookingService.cancelBooking(bookingId);
        BookingDTO responseDTO = bookingService.mapToDTO(cancelledBooking);
        return ResponseEntity.ok(responseDTO);
    }
    
 // Get routeId by bookingId – used by Cancellation microservice
    @GetMapping("/{bookingId}/route-id")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Integer> getRouteIdByBookingId(@PathVariable int bookingId) {
        int routeId = bookingService.getBookingById(bookingId).getRoute().getRouteId();
        return ResponseEntity.ok(routeId);
    }
    
    @PutMapping("/{bookingId}/partial-cancel")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<BookingDTO> partiallyCancelSeats(
            @PathVariable int bookingId,
            @RequestBody List<String> seatsToCancel) {

        Booking updated = bookingService.partiallyCancelSeats(bookingId, seatsToCancel);
        return ResponseEntity.ok(bookingService.mapToDTO(updated));
    }


}
