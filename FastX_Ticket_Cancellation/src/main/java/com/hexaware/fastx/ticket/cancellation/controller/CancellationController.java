package com.hexaware.fastx.ticket.cancellation.controller;


import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hexaware.fastx.ticket.cancellation.config.JwtService;
import com.hexaware.fastx.ticket.cancellation.dto.CancellationRequestDTO;
import com.hexaware.fastx.ticket.cancellation.dto.CancellationResponseDTO;
import com.hexaware.fastx.ticket.cancellation.entity.Cancellation;
import com.hexaware.fastx.ticket.cancellation.service.ICancellationService;

import lombok.RequiredArgsConstructor;
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/cancellation")

public class CancellationController {

    private final ICancellationService cancellationService;
    private final JwtService jwtService;
    private int extractUserIdFromToken() {
        // Get the Authorization header from the current request
        String authHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid Authorization header");
        }

        String token = authHeader.substring(7); // remove "Bearer " prefix

        return jwtService.extractUserId(token);
    }


    @PostMapping
    public ResponseEntity<CancellationResponseDTO> cancelBooking(@RequestBody CancellationRequestDTO dto) {
        CancellationResponseDTO response = cancellationService.cancelBooking(dto);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/unbook-seat/{seatId}")
    public ResponseEntity<String> unbookSeat(@PathVariable int seatId) {
        cancellationService.unbookSeat(seatId);
        return ResponseEntity.ok("Seat unbooked successfully");
    }

    @GetMapping("/all")  // Admin only
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Cancellation>> getAllCancellations() {
        return ResponseEntity.ok(cancellationService.getAllCancellations());
    }

    @GetMapping  // User’s own cancellations
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Cancellation>> getMyCancellations() {
        int userId = extractUserIdFromToken();
        return ResponseEntity.ok(cancellationService.getCancellationsByUserId(userId));
    }

    @DeleteMapping("/{id}")  // Admin can delete any cancellation
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCancellation(@PathVariable int id) {
        cancellationService.deleteCancellationById(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/my/{id}")  // User can delete own cancellation
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteMyCancellation(@PathVariable int id) {
        int userId = extractUserIdFromToken();
        cancellationService.deleteCancellationByIdAndUserId(id, userId);
        return ResponseEntity.noContent().build();
    }
}
