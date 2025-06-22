package com.hexaware.fastx.ticket.booking.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hexaware.fastx.ticket.booking.dto.PaymentDTO;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.entity.Payment;
import com.hexaware.fastx.ticket.booking.repository.PaymentRepository;
import com.hexaware.fastx.ticket.booking.service.IPaymentService;

import jakarta.validation.Valid;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/payments")
public class PaymentRestController {

    @Autowired
    private IPaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;


    //  Create payment
    @PostMapping
    public ResponseEntity<PaymentDTO> addPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        Payment saved = paymentService.addPayment(paymentDTO);
        return ResponseEntity.ok(paymentService.mapToDTO(saved));
    }

    //  Update payment
    @PutMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable int paymentId,
                                                    @Valid @RequestBody PaymentDTO paymentDTO) {
        Payment updated = paymentService.updatePayment(paymentId, paymentDTO);
        return ResponseEntity.ok(paymentService.mapToDTO(updated));
    }

    //  Get payment by ID
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable int paymentId) {
        Payment payment = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(paymentService.mapToDTO(payment));
    }

    //  Get all payments (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> dtos = paymentService.getAllPaymentDTOs();
        return ResponseEntity.ok(dtos);
    }

    //  Delete payment by ID
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @DeleteMapping("/{paymentId}")
    public ResponseEntity<Void> deletePayment(@PathVariable int paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.noContent().build();
    }

    // Get all payments for a user (Admin or User)
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentDTO>> getPaymentsByUserId(@PathVariable int userId) {
        List<PaymentDTO> dtos = paymentService.getPaymentDTOsByUserId(userId);
        return ResponseEntity.ok(dtos);
    }

    //  Refund a payment
    @PutMapping("/refund/{paymentId}")
    public ResponseEntity<String> refundPayment(@PathVariable int paymentId) {
        String message = paymentService.refundPayment(paymentId);
        return ResponseEntity.ok(message);
    }
    
    @PutMapping("/partial-refund/{paymentId}")
    public ResponseEntity<String> handlePartialRefund(
            @PathVariable int paymentId,
            @RequestParam int canceledSeats) {

        Payment payment = paymentService.getPaymentById(paymentId);
        Booking booking = payment.getBooking();

        if ("CANCELLED".equalsIgnoreCase(booking.getStatus())) {
            payment.setPaymentStatus("REFUNDED");
        } else {
            payment.setPaymentStatus("PARTIALLY_REFUNDED");
        }

        paymentRepository.save(payment);
        return ResponseEntity.ok("Payment updated successfully");
    }



}
