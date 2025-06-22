package com.hexaware.fastx.ticket.booking.service;
import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hexaware.fastx.ticket.booking.dto.BusDTO;
import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.exceptions.RouteNotFoundException;

@SpringBootTest
@Transactional
class RouteServiceImpTest {

    @Autowired
    private IBusService busService;

    @Autowired
    private IRouteService routeService;

    // Helper method to create and return a Bus
    private Bus createSampleBus() {
        BusDTO busDTO = new BusDTO();
        busDTO.setBusName("Test Bus");
        busDTO.setBusNumber("TN01AB1234");
        busDTO.setBusType("AC");
        busDTO.setTotalSeats(40);
        busDTO.setAmenities("WiFi");
        return busService.addBus(busDTO);
    }

    @Test
    void testAddAndGetRoute() {
        Bus bus = createSampleBus();

        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setBusId(bus.getBusId());
        routeDTO.setOrigin("Chennai");
        routeDTO.setDestination("Bangalore");
        routeDTO.setDepartureTime(LocalDateTime.now().plusDays(1));
        routeDTO.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(6));
        routeDTO.setFare(new BigDecimal("1200.00"));

        Route savedRoute = routeService.addRoute(routeDTO);
        assertNotNull(savedRoute);
        assertEquals("Chennai", savedRoute.getOrigin());

        RouteDTO fetchedRoute = routeService.getRouteById(savedRoute.getRouteId());
        assertEquals(savedRoute.getRouteId(), fetchedRoute.getRouteId());
    }

    @Test
    void testUpdateRoute() {
        Bus bus = createSampleBus();

        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setBusId(bus.getBusId());
        routeDTO.setOrigin("Delhi");
        routeDTO.setDestination("Agra");
        routeDTO.setDepartureTime(LocalDateTime.now().plusDays(2));
        routeDTO.setArrivalTime(LocalDateTime.now().plusDays(2).plusHours(3));
        routeDTO.setFare(new BigDecimal("800.00"));

        Route savedRoute = routeService.addRoute(routeDTO);

        RouteDTO updateDTO = new RouteDTO();
        updateDTO.setBusId(bus.getBusId());
        updateDTO.setOrigin("Delhi");
        updateDTO.setDestination("Jaipur");
        updateDTO.setDepartureTime(routeDTO.getDepartureTime());
        updateDTO.setArrivalTime(routeDTO.getArrivalTime().plusHours(2));
        updateDTO.setFare(new BigDecimal("900.00"));

        Route updatedRoute = routeService.updateRoute(savedRoute.getRouteId(), updateDTO);

        assertEquals("Jaipur", updatedRoute.getDestination());
        assertEquals(new BigDecimal("900.00"), updatedRoute.getFare());
    }

    @Test
    void testFindByOriginDestinationFare() {
        Bus bus = createSampleBus();

        RouteDTO routeDTO1 = new RouteDTO();
        routeDTO1.setBusId(bus.getBusId());
        routeDTO1.setOrigin("Mumbai");
        routeDTO1.setDestination("Pune");
        routeDTO1.setDepartureTime(LocalDateTime.now().plusDays(1));
        routeDTO1.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(3));
        routeDTO1.setFare(new BigDecimal("500.00"));
        routeService.addRoute(routeDTO1);

        RouteDTO routeDTO2 = new RouteDTO();
        routeDTO2.setBusId(bus.getBusId());
        routeDTO2.setOrigin("Mumbai");
        routeDTO2.setDestination("Nashik");
        routeDTO2.setDepartureTime(LocalDateTime.now().plusDays(1));
        routeDTO2.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(5));
        routeDTO2.setFare(new BigDecimal("700.00"));
        routeService.addRoute(routeDTO2);

        List<RouteDTO> originRoutes = routeService.findByOrigin("Mumbai");
        assertFalse(originRoutes.isEmpty());

        List<RouteDTO> destinationRoutes = routeService.findByDestination("Pune");
        assertFalse(destinationRoutes.isEmpty());

        List<RouteDTO> originDestRoutes = routeService.findByOriginAndDestination("Mumbai", "Pune");
        assertFalse(originDestRoutes.isEmpty());

        List<RouteDTO> fareRoutes = routeService.findByFareLessThanEqual(new BigDecimal("600.00"));
        assertFalse(fareRoutes.isEmpty());
    }

    @Test
    void testDeleteRoute() {
        Bus bus = createSampleBus();

        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setBusId(bus.getBusId());
        routeDTO.setOrigin("Hyderabad");
        routeDTO.setDestination("Vijayawada");
        routeDTO.setDepartureTime(LocalDateTime.now().plusDays(1));
        routeDTO.setArrivalTime(LocalDateTime.now().plusDays(1).plusHours(4));
        routeDTO.setFare(new BigDecimal("600.00"));

        Route savedRoute = routeService.addRoute(routeDTO);
        int id = savedRoute.getRouteId();

        routeService.deleteRoute(id);

        assertThrows(RouteNotFoundException.class, () -> routeService.getRouteById(id));
    }
}