package com.hexaware.fastx.ticket.cancellation.service;

import java.util.List;

import com.hexaware.fastx.ticket.cancellation.dto.CancellationRequestDTO;
import com.hexaware.fastx.ticket.cancellation.dto.CancellationResponseDTO;
import com.hexaware.fastx.ticket.cancellation.entity.Cancellation;

public interface ICancellationService {
    CancellationResponseDTO cancelBooking(CancellationRequestDTO dto);

	void unbookSeat(int seatId);
	List<Cancellation> getAllCancellations();  // For admin only: list all cancellations

    List<Cancellation> getCancellationsByUserId(int userId);  // User's own cancellations

    void deleteCancellationById(int cancellationId);  // Admin can delete any cancellation

    void deleteCancellationByIdAndUserId(int cancellationId, int userId);  // User can delete own cancellation
    

}