package com.hexaware.fastx.ticket.booking.service;

import com.hexaware.fastx.ticket.booking.dto.BookingDTO;
import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.entity.User;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.exceptions.BookingNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.SeatAlreadyBookedException;
import com.hexaware.fastx.ticket.booking.exceptions.UserNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.RouteNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImpTest {

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IRouteService routeService;

    @Test
    void testAddBooking() {
        // Prepare user and route
        User user = prepareTestUser("bookingtest1@example.com");
        RouteDTO route = prepareTestRoute(); // You need to implement or fetch an existing route

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(user.getUserId());
        bookingDTO.setRouteId(route.getRouteId());
        bookingDTO.setBookingTime(LocalDateTime.now());
        bookingDTO.setSeatNumbers(Arrays.asList("A1", "A2")); // Make sure these seats exist and not booked
        bookingDTO.setSeatsBooked(2);
        bookingDTO.setStatus("CONFIRMED");

        Booking booking = bookingService.addBooking(bookingDTO);

        assertNotNull(booking);
        assertEquals(user.getUserId(), booking.getUser().getUserId());
        assertEquals(route.getRouteId(), booking.getRoute().getRouteId());
        assertEquals("A1,A2", booking.getSeatNumbers());
        assertEquals(2, booking.getSeatsBooked());
        assertEquals("CONFIRMED", booking.getStatus());
        assertTrue(booking.getTotalAmount().compareTo(route.getFare().multiply(new java.math.BigDecimal(2))) == 0);
    }

    @Test
    void testGetBookingById() {
        Booking booking = createSampleBooking();
        Booking found = bookingService.getBookingById(booking.getBookingId());

        assertNotNull(found);
        assertEquals(booking.getBookingId(), found.getBookingId());
    }

    @Test
    void testUpdateBooking() {
        Booking booking = createSampleBooking();

        BookingDTO updateDTO = new BookingDTO();
        updateDTO.setUserId(booking.getUser().getUserId());
        updateDTO.setRouteId(booking.getRoute().getRouteId());
        updateDTO.setBookingTime(LocalDateTime.now());
        updateDTO.setSeatNumbers(Arrays.asList("A3")); // Change seats, ensure A3 available
        updateDTO.setSeatsBooked(1);
        updateDTO.setStatus("CANCELLED");

        Booking updated = bookingService.updateBooking(booking.getBookingId(), updateDTO);

        assertEquals("CANCELLED", updated.getStatus());
        assertEquals("A3", updated.getSeatNumbers());
        assertEquals(1, updated.getSeatsBooked());
    }

    @Test
    void testDeleteBooking() {
        Booking booking = createSampleBooking();

        bookingService.deleteBooking(booking.getBookingId());

        assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(booking.getBookingId()));
    }

    @Test
    void testGetBookingsByUserId() {
        Booking booking1 = createSampleBooking();
        List<Booking> bookings = bookingService.getBookingsByUserId(booking1.getUser().getUserId());

        assertNotNull(bookings);
        assertTrue(bookings.size() >= 1);
        assertTrue(bookings.stream().anyMatch(b -> b.getBookingId() == booking1.getBookingId()));
    }

    // Utility methods

    private User prepareTestUser(String email) {
        // Check if user exists, else create
        User existing = null;
        try {
            existing = userService.findByEmail(email);
        } catch (UserNotFoundException e) {
            // ignored
        }
        if (existing != null) return existing;

        // Register new user DTO
        com.hexaware.fastx.ticket.booking.dto.UserDTO dto = new com.hexaware.fastx.ticket.booking.dto.UserDTO();
        dto.setName("Booking Test User");
        dto.setEmail(email);
        dto.setPassword("password123");
        dto.setGender("Other");
        dto.setContactNumber("1234567890");
        dto.setRole("USER");

        userService.registerUser(dto);
        return userService.findByEmail(email);
    }

    private RouteDTO prepareTestRoute() {
        // Here you must either create a route or fetch an existing route that has seats A1, A2, A3 free.
        // For brevity, fetch the first route
        List<RouteDTO> routes = routeService.getAllRoutes();
        if (routes.isEmpty()) {
            fail("No route available for booking test");
        }
        return routes.get(0);
    }

    private Booking createSampleBooking() {
        User user = prepareTestUser("bookingtest2@example.com");
        RouteDTO route = prepareTestRoute();

        BookingDTO bookingDTO = new BookingDTO();
        bookingDTO.setUserId(user.getUserId());
        bookingDTO.setRouteId(route.getRouteId());
        bookingDTO.setBookingTime(LocalDateTime.now());
        bookingDTO.setSeatNumbers(Arrays.asList("A1")); // ensure seat available
        bookingDTO.setSeatsBooked(1);
        bookingDTO.setStatus("CONFIRMED");

        return bookingService.addBooking(bookingDTO);
    }
}
