package com.hexaware.fastx.ticket.booking.service;

import com.hexaware.fastx.ticket.booking.dto.PaymentDTO;
import com.hexaware.fastx.ticket.booking.entity.Payment;

import java.util.List;

public interface IPaymentService {
    Payment addPayment(PaymentDTO paymentDTO);
    Payment updatePayment(int paymentId, PaymentDTO paymentDTO);
    Payment getPaymentById(int paymentId);
    List<Payment> getAllPayments();
    void deletePayment(int paymentId);
    List<Payment> getPaymentsByUserId(int userId);
    PaymentDTO getPaymentByBookingId(int bookingId);
	String refundPayment(int paymentId);
	List<PaymentDTO> getAllPaymentDTOs();
	List<PaymentDTO> getPaymentDTOsByUserId(int userId);
	PaymentDTO mapToDTO(Payment payment);

}