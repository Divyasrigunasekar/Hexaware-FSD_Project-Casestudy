package com.hexaware.fastx.ticket.booking.controller;

import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.repository.RouteRepository;
import com.hexaware.fastx.ticket.booking.service.IRouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/routes")
public class RouteRestController {

    @Autowired
    private IRouteService routeService;

    @Autowired
    private RouteRepository routeRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<RouteDTO> addRoute(@RequestBody RouteDTO routeDTO) {
        Route addedRoute = routeService.addRoute(routeDTO);
        return ResponseEntity.ok(routeService.mapToDTO(addedRoute));
    }

    @PutMapping("/{routeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable int routeId, @RequestBody RouteDTO routeDTO) {
        Route updatedRoute = routeService.updateRoute(routeId, routeDTO);
        return ResponseEntity.ok(routeService.mapToDTO(updatedRoute));
    }

    @GetMapping("/{routeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable int routeId) {
        return ResponseEntity.ok(routeService.getRouteById(routeId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    @DeleteMapping("/{routeId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR')")
    public ResponseEntity<Void> deleteRoute(@PathVariable int routeId) {
        routeService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/origin/{origin}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<List<RouteDTO>> getRoutesByOrigin(@PathVariable String origin) {
        return ResponseEntity.ok(routeService.findByOrigin(origin));
    }

    @GetMapping("/destination/{destination}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<List<RouteDTO>> getRoutesByDestination(@PathVariable String destination) {
        return ResponseEntity.ok(routeService.findByDestination(destination));
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<List<RouteDTO>> getRoutesByOriginAndDestination(
            @RequestParam String origin,
            @RequestParam String destination) {
        return ResponseEntity.ok(routeService.findByOriginAndDestination(origin, destination));
    }

    @GetMapping("/fare")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OPERATOR') or hasRole('USER')")
    public ResponseEntity<List<RouteDTO>> getRoutesByFare(@RequestParam BigDecimal maxFare) {
        return ResponseEntity.ok(routeService.findByFareLessThanEqual(maxFare));
    }

    @GetMapping("/{routeId}/fare")
    public ResponseEntity<BigDecimal> getRouteFare(@PathVariable int routeId) {
        Optional<Route> optionalRoute = routeRepository.findById(routeId);
        return optionalRoute.map(route -> ResponseEntity.ok(route.getFare()))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
