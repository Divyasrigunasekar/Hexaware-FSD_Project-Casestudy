package com.hexaware.fastx.ticket.booking.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hexaware.fastx.ticket.booking.dto.PaymentDTO;
import com.hexaware.fastx.ticket.booking.entity.Booking;
import com.hexaware.fastx.ticket.booking.entity.Payment;
import com.hexaware.fastx.ticket.booking.exceptions.BookingNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.PaymentNotFoundException;
import com.hexaware.fastx.ticket.booking.repository.BookingRepository;
import com.hexaware.fastx.ticket.booking.repository.PaymentRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentServiceImp implements IPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    // Helper to convert DTO to Entity
    private Payment convertDtoToEntity(PaymentDTO dto) {
        Payment payment = new Payment();

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> {
                    log.error("Booking not found with id: {}", dto.getBookingId());
                    return new BookingNotFoundException("Booking not found with id: " + dto.getBookingId());
                });
        payment.setBooking(booking);
        payment.setAmount(booking.getTotalAmount());
        payment.setPaymentTime(dto.getPaymentTime());

        // Check if payment method is entered
        if (dto.getPaymentMethod() == null || dto.getPaymentMethod().isBlank()) {
            log.warn("No payment method provided. Marking status as PENDING.");
            payment.setPaymentStatus("PENDING");
        } else {
            payment.setPaymentMethod(dto.getPaymentMethod());
            payment.setPaymentStatus("SUCCESS"); // Only if method is provided
        }

        return payment;
    }
    private PaymentDTO convertEntityToDto(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentTime(payment.getPaymentTime());
        dto.setBookingId(payment.getBooking().getBookingId()); // assuming Payment has Booking
        return dto;
    }
    @Override
    public Payment addPayment(PaymentDTO paymentDTO) {
        log.info("Adding payment for booking ID: {}", paymentDTO.getBookingId());
        Payment payment = convertDtoToEntity(paymentDTO);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment added successfully: {}", savedPayment);
        return savedPayment;
    }

    @Override
    public Payment updatePayment(int paymentId, PaymentDTO paymentDTO) {
        log.info("Updating payment with ID: {}", paymentId);

        // Fetch existing payment
        Payment existingPayment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found with id: {}", paymentId);
                    return new PaymentNotFoundException("Payment not found with id: " + paymentId);
                });

        // Validate booking existence
        Booking booking = bookingRepository.findById(paymentDTO.getBookingId())
                .orElseThrow(() -> {
                    log.error("Booking not found with id: {}", paymentDTO.getBookingId());
                    return new BookingNotFoundException("Booking not found with id: " + paymentDTO.getBookingId());
                });

        existingPayment.setBooking(booking);
        existingPayment.setAmount(booking.getTotalAmount()); // always use actual booking amount
        existingPayment.setPaymentTime(paymentDTO.getPaymentTime());

        // Check if payment method is provided
        String method = paymentDTO.getPaymentMethod();
        if (method == null || method.isBlank()) {
            log.warn("No payment method provided. Keeping status as PENDING.");
            existingPayment.setPaymentStatus("PENDING");
            existingPayment.setPaymentMethod(null);
        } else {
            // Optionally validate allowed methods
            List<String> validMethods = List.of("UPI", "CARD", "NET_BANKING", "WALLET");
            if (!validMethods.contains(method.toUpperCase())) {
                throw new IllegalArgumentException("Invalid payment method: " + method);
            }
            existingPayment.setPaymentMethod(method.toUpperCase());
            existingPayment.setPaymentStatus("SUCCESS");
        }

        Payment updated = paymentRepository.save(existingPayment);
        log.info("Payment updated successfully: {}", updated);
        return updated;
    }

    @Override
    public Payment getPaymentById(int paymentId) {
        log.info("Fetching payment with ID: {}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    log.error("Payment not found with id: {}", paymentId);
                    return new PaymentNotFoundException("Payment not found with id: " + paymentId);
                });
    }

    @Override
    public List<Payment> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll();
    }

    @Override
    public void deletePayment(int paymentId) {
        log.info("Deleting payment with ID: {}", paymentId);
        if (!paymentRepository.existsById(paymentId)) {
            log.error("Payment not found with id: {}", paymentId);
            throw new PaymentNotFoundException("Payment not found with id: " + paymentId);
        }
        paymentRepository.deleteById(paymentId);
        log.info("Payment deleted successfully");
    }
    @Override
    public List<Payment> getPaymentsByUserId(int userId) {
        log.info("Fetching payments for user ID: {}", userId);
        return paymentRepository.findByBookingUserUserId(userId);
    }
    @Override
    public PaymentDTO getPaymentByBookingId(int bookingId) {
        Payment payment = paymentRepository.findByBooking_BookingId(bookingId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));
        return convertEntityToDto(payment);
    }
    public String refundPayment(int paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new PaymentNotFoundException("Payment not found"));

        if ("REFUNDED".equalsIgnoreCase(payment.getPaymentStatus())) {
            log.warn("Payment {} is already refunded", paymentId);
            return "Payment was already marked as REFUNDED";
        }

        payment.setPaymentStatus("REFUNDED");
        paymentRepository.save(payment);

        log.info("Payment {} marked as REFUNDED", paymentId);
        return "Payment marked as REFUNDED";
    }
    public PaymentDTO mapToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setBookingId(payment.getBooking() != null ? payment.getBooking().getBookingId() : 0); // ✅ important
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setPaymentTime(payment.getPaymentTime());
        return dto;
    }
    public List<PaymentDTO> mapToDTOList(List<Payment> payments) {
        return payments.stream()
                       .map(this::mapToDTO)
                       .collect(Collectors.toList());
    }
    @Override
    public List<PaymentDTO> getAllPaymentDTOs() {
        List<Payment> payments = paymentRepository.findAll();
        return payments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<PaymentDTO> getPaymentDTOsByUserId(int userId) {
        List<Payment> payments = paymentRepository.findByBookingUserUserId(userId);
        return payments.stream().map(this::mapToDTO).collect(Collectors.toList());
    }
}