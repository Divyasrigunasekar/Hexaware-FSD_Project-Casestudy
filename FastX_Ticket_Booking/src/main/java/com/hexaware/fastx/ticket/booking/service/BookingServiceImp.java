/**
 * Implements business logic for booking tickets.
 * Interacts with repositories to persist bookings and validate availability.
 */


package com.hexaware.fastx.ticket.booking.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.hexaware.fastx.ticket.booking.dto.BookingDTO;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.entity.Payment;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.entity.Seat;
import com.hexaware.fastx.ticket.booking.entity.User;
import com.hexaware.fastx.ticket.booking.exceptions.BookingNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.SeatAlreadyBookedException;
import com.hexaware.fastx.ticket.booking.exceptions.UserNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.RouteNotFoundException;
import com.hexaware.fastx.ticket.booking.repository.BookingRepository;
import com.hexaware.fastx.ticket.booking.repository.PaymentRepository;
import com.hexaware.fastx.ticket.booking.repository.SeatRepository;
import com.hexaware.fastx.ticket.booking.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import com.hexaware.fastx.ticket.booking.repository.RouteRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BookingServiceImp implements IBookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RouteRepository routeRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Booking addBooking(BookingDTO bookingDTO) {
        log.info("Adding booking for user ID: {}", bookingDTO.getUserId());

        // Validate seat numbers list is present and not empty
        if (bookingDTO.getSeatNumbers() == null || bookingDTO.getSeatNumbers().isEmpty()) {
            throw new IllegalArgumentException("Seat numbers must be provided for booking");
        }

        // Fetch user
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Fetch route
        Route route = routeRepository.findById(bookingDTO.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        // Fetch seats for the route
        List<Seat> seats = seatRepository.findByRouteRouteIdAndSeatNumberIn(
                route.getRouteId(), bookingDTO.getSeatNumbers());

        // Validate seat count matches input
        if (seats.size() != bookingDTO.getSeatNumbers().size()) {
            throw new SeatAlreadyBookedException("Some selected seats are invalid or missing");
        }

        // Check if any seats are already booked
        List<String> alreadyBooked = seats.stream()
                .filter(Seat::isBooked)
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());

        if (!alreadyBooked.isEmpty()) {
            throw new SeatAlreadyBookedException("Seats already booked: " + alreadyBooked);
        }

        // Mark seats as booked
        for (Seat seat : seats) {
            seat.setBooked(true);
        }
        seatRepository.saveAll(seats);

        // Calculate total amount
        BigDecimal fare = route.getFare();
        BigDecimal totalAmount = fare.multiply(new BigDecimal(seats.size()));

        // Save booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoute(route);
        booking.setBookingTime(bookingDTO.getBookingTime());

        // Set seatsBooked based on actual seats booked (avoid mismatch)
        booking.setSeatsBooked(seats.size());

        String seatNumbersString = String.join(",", bookingDTO.getSeatNumbers());
        booking.setSeatNumbers(seatNumbersString);

        booking.setTotalAmount(totalAmount);

        // Now set status to BOOKED only after seat checks and assignment
        booking.setStatus("BOOKED");

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking added successfully: {}", savedBooking);
        return savedBooking;
    }
    @Override
    public Booking updateBooking(int bookingId, BookingDTO bookingDTO) {
        log.info("Updating booking with ID: {}", bookingId);
        Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isEmpty()) {
            throw new BookingNotFoundException("Booking not found with id: " + bookingId);
        }

        // Validate seat numbers
        if (bookingDTO.getSeatNumbers() == null || bookingDTO.getSeatNumbers().isEmpty()) {
            throw new IllegalArgumentException("Seat numbers must be provided for updating booking");
        }

        Booking booking = optionalBooking.get();

        // Validate user and route presence
        User user = userRepository.findById(bookingDTO.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        Route route = routeRepository.findById(bookingDTO.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found"));

        // Optional: Check if new seats are already booked (excluding this booking's old seats)
        List<Seat> newSeats = seatRepository.findByRouteRouteIdAndSeatNumberIn(
                route.getRouteId(), bookingDTO.getSeatNumbers());

        List<String> alreadyBooked = newSeats.stream()
                .filter(Seat::isBooked)
                .filter(seat -> !booking.getSeatNumbers().contains(seat.getSeatNumber())) // skip already owned seats
                .map(Seat::getSeatNumber)
                .collect(Collectors.toList());

        if (!alreadyBooked.isEmpty()) {
            throw new SeatAlreadyBookedException("Seats already booked: " + alreadyBooked);
        }

        // Mark all new seats as booked
        for (Seat seat : newSeats) {
            seat.setBooked(true);
        }
        seatRepository.saveAll(newSeats);

        // Update booking info
        booking.setUser(user);
        booking.setRoute(route);
        booking.setBookingTime(bookingDTO.getBookingTime());

        int seatCount = bookingDTO.getSeatNumbers().size();
        booking.setSeatsBooked(seatCount);

        String seatNumbersString = String.join(",", bookingDTO.getSeatNumbers());
        booking.setSeatNumbers(seatNumbersString);

        BigDecimal totalAmount = route.getFare().multiply(new BigDecimal(seatCount));
        booking.setTotalAmount(totalAmount);

        booking.setStatus("BOOKED"); // You can override whatever is passed in DTO for consistency

        Booking updated = bookingRepository.save(booking);
        log.info("Booking updated successfully: {}", updated);
        return updated;
    }

    @Override
    public Booking getBookingById(int bookingId) {
        log.info("Fetching booking with ID: {}", bookingId);
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));
    }

    @Override
    public List<Booking> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll();
    }

    @Override
    public void deleteBooking(int bookingId) {
        log.info("Attempting to delete booking with ID: {}", bookingId);

        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        //  Delete associated payment first
        Optional<Payment> optionalPayment = paymentRepository.findByBooking_BookingId(bookingId);
        if (optionalPayment.isPresent()) {
            paymentRepository.delete(optionalPayment.get());
            log.info("Associated payment deleted for booking ID: {}", bookingId);
        } else {
            log.info("No associated payment found for booking ID: {}", bookingId);
        }

        //  Unbook seats
        List<String> seatNumbers = List.of(booking.getSeatNumbers().split(","));
        List<Seat> seats = seatRepository.findByRouteRouteIdAndSeatNumberIn(
            booking.getRoute().getRouteId(), seatNumbers);

        for (Seat seat : seats) {
            seat.setBooked(false);
        }
        seatRepository.saveAll(seats);

        // Now safely delete the booking
        bookingRepository.delete(booking);
        log.info("Booking with ID {} deleted successfully.", bookingId);
    }
    @Override
    public List<Booking> getBookingsByUserId(int userId) {
        return bookingRepository.findByUserUserId(userId);
    }
    @Transactional
    @Override
    public Booking cancelBooking(int bookingId) {
        log.info("Cancelling booking with ID: {}", bookingId);

        // 1. Fetch the booking
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with id: " + bookingId));

        // 2. Mark booking as cancelled
        booking.setStatus("CANCELLED");

        // 3. Unbook associated seats
        List<String> seatNumbers = List.of(booking.getSeatNumbers().split(","));
        List<Seat> seats = seatRepository.findByRouteRouteIdAndSeatNumberIn(
            booking.getRoute().getRouteId(), seatNumbers);

        for (Seat seat : seats) {
            seat.setBooked(false);
        }
        seatRepository.saveAll(seats);

        // 4. Handle payment status (if payment exists)
        Optional<Payment> optionalPayment = paymentRepository.findByBooking_BookingId(bookingId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            String status = payment.getPaymentStatus();

            if ("SUCCESS".equalsIgnoreCase(status)) {
                payment.setPaymentStatus("REFUNDED");
                log.info("Payment marked as REFUNDED for booking ID: {}", bookingId);
            } else if ("PENDING".equalsIgnoreCase(status)) {
                payment.setPaymentStatus("CANCELED");
                log.info("Payment marked as CANCELED for booking ID: {}", bookingId);
            }

            paymentRepository.save(payment);
        } else {
            log.info("No payment found for booking ID: {}, skipping payment status update.", bookingId);
        }

        // 5. Save and return updated booking
        Booking cancelledBooking = bookingRepository.save(booking);
        log.info("Booking cancelled successfully: {}", cancelledBooking);
        return cancelledBooking;
    }

    
    public BookingDTO mapToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());  // newly added field
        dto.setUserId(booking.getUser().getUserId());
        dto.setRouteId(booking.getRoute().getRouteId());
        dto.setBookingTime(booking.getBookingTime());
        dto.setSeatsBooked(booking.getSeatsBooked());

        // Convert comma-separated String to List<String>
        List<String> seatList = Arrays.asList(booking.getSeatNumbers().split(","));
        dto.setSeatNumbers(seatList);

        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        return dto;
    }

    public List<BookingDTO> mapToDTOList(List<Booking> bookings) {
        return bookings.stream()
                       .map(this::mapToDTO)
                       .collect(Collectors.toList());
    }
    @Override
    public List<BookingDTO> getAllBookingDTOs() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public List<BookingDTO> getBookingDTOsByUserId(int userId) {
        List<Booking> bookings = bookingRepository.findByUserUserId(userId);
        return bookings.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    @Override
    public Booking partiallyCancelSeats(int bookingId, List<String> seatsToCancel) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException("Booking not found"));

        List<String> currentSeats = Arrays.asList(booking.getSeatNumbers().split(","));
        List<String> remainingSeats = currentSeats.stream()
            .filter(seat -> !seatsToCancel.contains(seat))
            .toList();

        if (remainingSeats.isEmpty()) {
            // Fully cancel the booking using existing method
            return cancelBooking(bookingId);
        }

        // Partial cancel logic
        booking.setStatus("PARTIALLY_CANCELLED");
        booking.setSeatNumbers(String.join(",", remainingSeats));
        booking.setSeatsBooked(remainingSeats.size());

        BigDecimal fare = booking.getRoute().getFare();
        booking.setTotalAmount(fare.multiply(BigDecimal.valueOf(remainingSeats.size())));

        return bookingRepository.save(booking);
    }


}