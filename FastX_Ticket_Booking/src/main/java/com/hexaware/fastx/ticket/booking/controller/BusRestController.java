package com.hexaware.fastx.ticket.booking.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hexaware.fastx.ticket.booking.dto.BusDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.service.IBusService;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

@CrossOrigin(origins = "http://localhost:4200") 
@RestController
@RequestMapping("/api/buses")
@Slf4j
@Validated
public class BusRestController {

    @Autowired
    private IBusService busService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PostMapping
    public ResponseEntity<Bus> addBus(@Valid @RequestBody BusDTO busDTO) {
        log.info("Request to add new bus: {}", busDTO.getBusName());
        Bus createdBus = busService.addBus(busDTO);
        return new ResponseEntity<>(createdBus, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    @PutMapping("/{busId}")
    public ResponseEntity<Bus> updateBus(@PathVariable int busId, @Valid @RequestBody BusDTO busDTO) {
        log.info("Request to update bus with ID: {}", busId);
        Bus updatedBus = busService.updateBus(busId, busDTO);
        return ResponseEntity.ok(updatedBus);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'USER')")
    @GetMapping("/{busId}")
    public ResponseEntity<Bus> getBusById(@PathVariable int busId) {
        log.info("Request to get bus with ID: {}", busId);
        Bus bus = busService.getBusById(busId);
        return ResponseEntity.ok(bus);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'OPERATOR', 'USER')")
    @GetMapping
    public ResponseEntity<List<Bus>> getAllBuses() {
        log.info("Request to get all buses");
        List<Bus> buses = busService.getAllBuses();
        return ResponseEntity.ok(buses);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{busId}")
    public ResponseEntity<Map<String, String>> deleteBus(@PathVariable int busId) {
        log.info("Request to delete bus with ID: {}", busId);
        busService.deleteBus(busId);
        return ResponseEntity.ok(Map.of("message", "Bus deleted successfully"));
    }
}