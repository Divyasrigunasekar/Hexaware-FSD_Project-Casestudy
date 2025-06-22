package com.hexaware.fastx.ticket.booking.service;
import com.hexaware.fastx.ticket.booking.dto.PaymentDTO;
import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.entity.Payment;
import com.hexaware.fastx.ticket.booking.entity.User;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.exceptions.PaymentNotFoundException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PaymentServiceImpTest {

    @Autowired
    private IPaymentService paymentService;

    @Autowired
    private IUserService userService;

    @Autowired
    private IBookingService bookingService;

    @Autowired
    private IRouteService routeService;

    @Test
    void testAddPayment() {
        Booking booking = createSampleBooking();

        PaymentDTO dto = new PaymentDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setPaymentMethod("CARD");
        dto.setPaymentStatus("SUCCESS");
        dto.setPaymentTime(LocalDateTime.now());

        Payment saved = paymentService.addPayment(dto);
        assertNotNull(saved);
        assertEquals("CARD", saved.getPaymentMethod());
        assertEquals("SUCCESS", saved.getPaymentStatus());
        assertEquals(booking.getBookingId(), saved.getBooking().getBookingId());
    }

    @Test
    void testGetPaymentById() {
        Payment payment = createSamplePayment();

        Payment found = paymentService.getPaymentById(payment.getPaymentId());
        assertNotNull(found);
        assertEquals(payment.getPaymentId(), found.getPaymentId());
    }

    @Test
    void testUpdatePayment() {
        Payment payment = createSamplePayment();

        PaymentDTO updateDto = new PaymentDTO();
        updateDto.setBookingId(payment.getBooking().getBookingId());
        updateDto.setPaymentMethod("UPI");
        updateDto.setPaymentStatus("FAILED");
        updateDto.setPaymentTime(LocalDateTime.now());

        Payment updated = paymentService.updatePayment(payment.getPaymentId(), updateDto);
        assertEquals("UPI", updated.getPaymentMethod());
        assertEquals("FAILED", updated.getPaymentStatus());
    }

    @Test
    void testDeletePayment() {
        Payment payment = createSamplePayment();

        paymentService.deletePayment(payment.getPaymentId());

        assertThrows(PaymentNotFoundException.class,
            () -> paymentService.getPaymentById(payment.getPaymentId()));
    }

    @Test
    void testGetAllPayments() {
        createSamplePayment();
        createSamplePayment();

        List<Payment> payments = paymentService.getAllPayments();
        assertTrue(payments.size() >= 2);
    }

    @Test
    void testGetPaymentsByUserId() {
        Payment payment = createSamplePayment();
        int userId = payment.getBooking().getUser().getUserId();

        List<Payment> payments = paymentService.getPaymentsByUserId(userId);
        assertNotNull(payments);
        assertTrue(payments.stream().anyMatch(p -> p.getPaymentId() == payment.getPaymentId()));
    }

    @Test
    void testGetPaymentByBookingId() {
        Payment payment = createSamplePayment();

        PaymentDTO dto = paymentService.getPaymentByBookingId(payment.getBooking().getBookingId());
        assertNotNull(dto);
        assertEquals(payment.getPaymentId(), dto.getPaymentId());
        assertEquals(payment.getPaymentMethod(), dto.getPaymentMethod());
    }

    // Utilities to prepare sample data

    private Booking createSampleBooking() {
        User user = prepareTestUser("paymenttestuser@example.com");
        RouteDTO route = prepareTestRoute();

        com.hexaware.fastx.ticket.booking.dto.BookingDTO bookingDTO = new com.hexaware.fastx.ticket.booking.dto.BookingDTO();
        bookingDTO.setUserId(user.getUserId());
        bookingDTO.setRouteId(route.getRouteId());
        bookingDTO.setBookingTime(LocalDateTime.now());
        bookingDTO.setSeatNumbers(List.of("A1"));
        bookingDTO.setSeatsBooked(1);
        bookingDTO.setStatus("CONFIRMED");

        return bookingService.addBooking(bookingDTO);
    }

    private Payment createSamplePayment() {
        Booking booking = createSampleBooking();

        PaymentDTO dto = new PaymentDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setPaymentMethod("CARD");
        dto.setPaymentStatus("SUCCESS");
        dto.setPaymentTime(LocalDateTime.now());

        return paymentService.addPayment(dto);
    }

    private User prepareTestUser(String email) {
        try {
            return userService.findByEmail(email);
        } catch (Exception e) {
            com.hexaware.fastx.ticket.booking.dto.UserDTO dto = new com.hexaware.fastx.ticket.booking.dto.UserDTO();
            dto.setName("Payment Test User");
            dto.setEmail(email);
            dto.setPassword("password123");
            dto.setGender("Other");
            dto.setContactNumber("1234567890");
            dto.setRole("USER");
            userService.registerUser(dto);
            return userService.findByEmail(email);
        }
    }

    private RouteDTO prepareTestRoute() {
        List<RouteDTO> routes = routeService.getAllRoutes();
        if (routes.isEmpty()) {
            fail("No route available for payment tests");
        }
        return routes.get(0);
    }
}