package com.hexaware.fastx.ticket.booking.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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

import com.hexaware.fastx.ticket.booking.dto.SeatAvailabilityDTO;
import com.hexaware.fastx.ticket.booking.dto.SeatDTO;
import com.hexaware.fastx.ticket.booking.entity.Seat;
import com.hexaware.fastx.ticket.booking.service.ISeatService;

@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/seats")
public class SeatRestController {

    @Autowired
    private ISeatService seatService;

    // Optional: If needed outside of Bus creation (e.g. manual seat addition)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<?> addSeat(@RequestBody SeatDTO seatDTO) {
        Object response = seatService.addSeat(seatDTO);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("message", "Bulk seats created successfully"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{seatId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public Seat updateSeat(@PathVariable int seatId, @RequestBody SeatDTO seatDTO) {
        return seatService.updateSeat(seatId, seatDTO);
    }

    @GetMapping("/{seatId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public Seat getSeatById(@PathVariable int seatId) {
        return seatService.getSeatById(seatId);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public List<SeatDTO> getAllSeats() {
        return seatService.getAllSeats();
    }

    @DeleteMapping("/{seatId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public void deleteSeat(@PathVariable int seatId) {
        seatService.deleteSeat(seatId);
    }
    @GetMapping("/bus/{busId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public List<Seat> getSeatsByBusId(@PathVariable int busId) {
        return seatService.getSeatsByBusId(busId);
    }
    @GetMapping("/availability/{busId}")
    public ResponseEntity<SeatAvailabilityDTO> getSeatAvailability(@PathVariable int busId) {
        SeatAvailabilityDTO result = seatService.getSeatAvailabilityByBusId(busId);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/unbook/{seatId}")
        public ResponseEntity<String> unbookSeat(@PathVariable int seatId) {
           
            seatService.unbookSeat(seatId);
            return ResponseEntity.ok("Seat unbooked successfully");
        }
    @GetMapping("/available/{routeId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<List<Seat>> getAvailableSeatsByRoute(@PathVariable int routeId) {
        List<Seat> availableSeats = seatService.getAvailableSeatsByRoute(routeId);
        return ResponseEntity.ok(availableSeats);
    }
    @GetMapping("/all/{routeId}")
    public List<SeatDTO> getAllSeatsByRoute(@PathVariable int routeId) {
        return seatService.getAllSeatsByRoute(routeId);
    }
    @PostMapping("/seat-numbers-by-ids")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<List<String>> getSeatNumbersByIds(@RequestBody List<Integer> seatIds) {
        List<String> seatNumbers = seatService.getSeatNumbersByIds(seatIds);
        return ResponseEntity.ok(seatNumbers);
    }


    }
