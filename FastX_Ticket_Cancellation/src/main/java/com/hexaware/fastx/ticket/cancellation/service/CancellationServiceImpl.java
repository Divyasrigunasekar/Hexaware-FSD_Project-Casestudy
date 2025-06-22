package com.hexaware.fastx.ticket.cancellation.service;

import org.springframework.security.access.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.hexaware.fastx.ticket.cancellation.config.JwtService;
import com.hexaware.fastx.ticket.cancellation.dto.CancellationRequestDTO;
import com.hexaware.fastx.ticket.cancellation.dto.CancellationResponseDTO;
import com.hexaware.fastx.ticket.cancellation.dto.RouteDTO;
import com.hexaware.fastx.ticket.cancellation.entity.Cancellation;
import com.hexaware.fastx.ticket.cancellation.exception.DuplicateCancellationException;
import com.hexaware.fastx.ticket.cancellation.exception.ResourceNotFoundException;
import com.hexaware.fastx.ticket.cancellation.repository.CancellationRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CancellationServiceImpl implements ICancellationService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String bookingServiceUrl = "http://localhost:9090/api/bookings";
    private final String paymentServiceUrl = "http://localhost:9090/api/payments";
    private final String seatServiceUrl = "http://localhost:9090/api/seats";
    private final String routeServiceUrl = "http://localhost:9090/api/routes";

    @Autowired
    private HttpServletRequest httpServletRequest;
    @Autowired
    private CancellationRepository cancellationRepository;
    @Autowired
    private JwtService jwtService;

    @Override
    public CancellationResponseDTO cancelBooking(CancellationRequestDTO dto) {
        HttpEntity<?> entity = createHttpEntityWithAuth();

        //  Step 1: Prevent duplicate cancellation
        List<Cancellation> existingCancellations = cancellationRepository.findByBookingId(dto.getBookingId());

        List<Integer> previouslyCancelledSeatIds = existingCancellations.stream()
            .flatMap(c -> List.of(c.getSeatIds().split(",")).stream())
            .map(Integer::parseInt)
            .toList();

        List<Integer> duplicateSeats = dto.getSeatIds().stream()
            .filter(previouslyCancelledSeatIds::contains)
            .toList();

        if (!duplicateSeats.isEmpty()) {
            throw new RuntimeException("Some of the selected seats are already cancelled: " + duplicateSeats);
        }

        //  Step 2: Get route ID from booking
        int routeId = restTemplate.exchange(
            bookingServiceUrl + "/" + dto.getBookingId() + "/route-id",
            HttpMethod.GET,
            entity,
            Integer.class
        ).getBody();

        // Step 3: Get departure time from route
        LocalDateTime departureTime = restTemplate.exchange(
            routeServiceUrl + "/" + routeId,
            HttpMethod.GET,
            entity,
            RouteDTO.class
        ).getBody().getDepartureTime();

        if (departureTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Cancellation not allowed after journey departure time.");
        }

        //  Step 4: Fetch seat numbers for the given seat IDs
        List<String> seatNumbers = restTemplate.exchange(
            seatServiceUrl + "/seat-numbers-by-ids",
            HttpMethod.POST,
            new HttpEntity<>(dto.getSeatIds(), entity.getHeaders()),
            List.class
        ).getBody();

        // Step 5: Call booking service to partially cancel those seat numbers
        restTemplate.exchange(
            bookingServiceUrl + "/" + dto.getBookingId() + "/partial-cancel",
            HttpMethod.PUT,
            new HttpEntity<>(seatNumbers, entity.getHeaders()),
            Void.class
        );

        // Step 6: Unbook selected seat IDs
        for (int seatId : dto.getSeatIds()) {
            try {
                restTemplate.exchange(
                    seatServiceUrl + "/unbook/" + seatId,
                    HttpMethod.PUT,
                    entity,
                    Void.class
                );
            } catch (Exception e) {
                log.warn("Seat {} could not be unbooked: {}", seatId, e.getMessage());
            }
        }

        //  Step 7: Mark payment as PARTIALLY_REFUNDED
        try {
            restTemplate.exchange(
                paymentServiceUrl + "/partial-refund/" + dto.getPaymentId() +
                    "?canceledSeats=" + dto.getSeatIds().size(),
                HttpMethod.PUT,
                entity,
                String.class
            );
        } catch (Exception e) {
            log.warn("Payment {} could not be updated: {}", dto.getPaymentId(), e.getMessage());
        }

        //  Step 8: Save cancellation record
        String token = httpServletRequest.getHeader("Authorization").substring(7);
        int userId = jwtService.extractUserId(token);

        Cancellation cancellation = new Cancellation();
        cancellation.setUserId(userId);
        cancellation.setBookingId(dto.getBookingId());
        cancellation.setPaymentId(dto.getPaymentId());
        cancellation.setSeatIds(dto.getSeatIds().toString().replaceAll("[\\[\\]\\s]", ""));
        cancellation.setReason(dto.getReason());
        cancellation.setCancelledAt(LocalDateTime.now());

        cancellationRepository.save(cancellation);

        return new CancellationResponseDTO("Cancellation successful. Seats unbooked, booking updated, and payment marked accordingly.");
    }


    private HttpEntity<?> createHttpEntityWithAuth() {
        String authHeader = httpServletRequest.getHeader("Authorization");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return new HttpEntity<>(headers);
    }

    @Override
    public List<Cancellation> getAllCancellations() {
        return cancellationRepository.findAll();
    }

    @Override
    public List<Cancellation> getCancellationsByUserId(int userId) {
        return cancellationRepository.findByUserId(userId);
    }

    @Override
    public void deleteCancellationById(int cancellationId) {
        cancellationRepository.deleteById(cancellationId);
    }

    @Override
    public void deleteCancellationByIdAndUserId(int cancellationId, int userId) {
        Cancellation cancellation = cancellationRepository.findById(cancellationId)
            .orElseThrow(() -> new ResourceNotFoundException("Cancellation not found"));
        if (cancellation.getUserId() != userId) {
            throw new AccessDeniedException("You cannot delete cancellations of other users");
        }
        cancellationRepository.deleteById(cancellationId);
    }

    public void unbookSeat(int seatId) {
        try {
            HttpEntity<?> entity = createHttpEntityWithAuth();
            ResponseEntity<Void> response = restTemplate.exchange(
                seatServiceUrl + "/unbook/" + seatId,
                HttpMethod.PUT,
                entity,
                Void.class
            );

            if (!response.getStatusCode().is2xxSuccessful()) {
                log.error("Failed to unbook seat with ID {}. Status: {}", seatId, response.getStatusCode());
                throw new RuntimeException("Failed to unbook seat");
            }

            log.info("Seat {} successfully unbooked", seatId);
        } catch (Exception e) {
            log.error("Exception when unbooking seat: {}", e.getMessage());
            throw e;
        }
    }
}
