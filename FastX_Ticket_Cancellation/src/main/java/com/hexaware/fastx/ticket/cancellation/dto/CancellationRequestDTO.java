package com.hexaware.fastx.ticket.cancellation.dto;

import java.util.List;


public class CancellationRequestDTO {
	    private int bookingId;
	    private int paymentId;
	    private int userId;
	    private List<Integer> seatIds;
	    private String reason;

	    public String getReason() {
	        return reason;
	    }

	    public void setReason(String reason) {
	        this.reason = reason;
	    }


	    public int getBookingId() {
	        return bookingId;
	    }

	    public void setBookingId(int bookingId) {
	        this.bookingId = bookingId;
	    }

	    public int getPaymentId() {
	        return paymentId;
	    }

	    public void setPaymentId(int paymentId) {
	        this.paymentId = paymentId;
	    }

	    public  List<Integer> getSeatIds() {
	        return seatIds;
	    }

	    public void setSeatIds(List<Integer> seatIds) {
	        this.seatIds = seatIds;
	    }

		public int getUserId() {
			return userId;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}
	}

