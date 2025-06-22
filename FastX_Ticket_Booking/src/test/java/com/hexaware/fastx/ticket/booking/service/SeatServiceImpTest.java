package com.hexaware.fastx.ticket.booking.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hexaware.fastx.ticket.booking.dto.BusDTO;
import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.dto.SeatAvailabilityDTO;
import com.hexaware.fastx.ticket.booking.dto.SeatDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.entity.Seat;
import com.hexaware.fastx.ticket.booking.exceptions.SeatNotFoundException;

@SpringBootTest
@Transactional
class SeatServiceImpTest {

    @Autowired
    private IBusService busService;

    @Autowired
    private IRouteService routeService;

    @Autowired
    private ISeatService seatService;

    private Bus createSampleBus() {
        BusDTO busDTO = new BusDTO();
        busDTO.setBusName("Test Bus");
        busDTO.setBusNumber("KA01AB1234");
        busDTO.setBusType("Non-AC");
        busDTO.setTotalSeats(10);
        busDTO.setAmenities("Water");
        return busService.addBus(busDTO);
    }

    private Route createSampleRoute(Bus bus) {
        RouteDTO routeDTO = new RouteDTO();
        routeDTO.setBusId(bus.getBusId());
        routeDTO.setOrigin("CityA");
        routeDTO.setDestination("CityB");
        routeDTO.setDepartureTime(java.time.LocalDateTime.now().plusDays(1));
        routeDTO.setArrivalTime(java.time.LocalDateTime.now().plusDays(1).plusHours(5));
        routeDTO.setFare(java.math.BigDecimal.valueOf(500));
        return routeService.addRoute(routeDTO);
    }

    @Test
    void testAddSingleSeat() {
        Bus bus = createSampleBus();
        Route route = createSampleRoute(bus);

        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setBusId(bus.getBusId());
        seatDTO.setRouteId(route.getRouteId());
        seatDTO.setSeatNumber("A1");
        seatDTO.setBooked(false);

        Seat seat = seatService.addSeat(seatDTO);
        assertNotNull(seat);
        assertEquals("A1", seat.getSeatNumber());
        assertFalse(seat.isBooked());
    }

    @Test
    void testBulkAddSeats() {
        Bus bus = createSampleBus();
        Route route = createSampleRoute(bus);

        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setBusId(bus.getBusId());
        seatDTO.setRouteId(route.getRouteId());
        seatDTO.setSeatNumber("5");  // bulk create seats 1 to 5

        Seat result = seatService.addSeat(seatDTO);
        assertNull(result); // bulk creation returns null

        List<Seat> seats = seatService.getSeatsByBusId(bus.getBusId());
        assertEquals(5, seats.size());

        for (int i = 1; i <= 5; i++) {
            final String seatNum = String.valueOf(i);
            assertTrue(seats.stream().anyMatch(s -> s.getSeatNumber().equals(seatNum)));
        }
    }

    @Test
    void testUpdateSeat() {
        Bus bus = createSampleBus();
        Route route = createSampleRoute(bus);

        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setBusId(bus.getBusId());
        seatDTO.setRouteId(route.getRouteId());
        seatDTO.setSeatNumber("B1");
        seatDTO.setBooked(false);

        Seat seat = seatService.addSeat(seatDTO);

        SeatDTO updateDTO = new SeatDTO();
        updateDTO.setBusId(bus.getBusId());
        updateDTO.setSeatNumber("B2");
        updateDTO.setBooked(true);

        Seat updated = seatService.updateSeat(seat.getSeatId(), updateDTO);
        assertEquals("B2", updated.getSeatNumber());
        assertTrue(updated.isBooked());
    }

    @Test
    void testGetSeatById_ThrowsException() {
        assertThrows(SeatNotFoundException.class, () -> seatService.getSeatById(-1));
    }

    @Test
    void testGetSeatAvailability() {
        Bus bus = createSampleBus();
        Route route = createSampleRoute(bus);

        // Create seats: 3 booked, 2 available
        for (int i = 1; i <= 5; i++) {
            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setBusId(bus.getBusId());
            seatDTO.setRouteId(route.getRouteId());
            seatDTO.setSeatNumber("S" + i);
            seatDTO.setBooked(i <= 3); // first 3 booked, rest available
            seatService.addSeat(seatDTO);
        }

        SeatAvailabilityDTO availability = seatService.getSeatAvailabilityByBusId(bus.getBusId());
        assertEquals(3, availability.getBookedSeats().size());
        assertEquals(2, availability.getAvailableSeats().size());
    }

    @Test
    void testDeleteSeat() {
        Bus bus = createSampleBus();
        Route route = createSampleRoute(bus);

        SeatDTO seatDTO = new SeatDTO();
        seatDTO.setBusId(bus.getBusId());
        seatDTO.setRouteId(route.getRouteId());
        seatDTO.setSeatNumber("D1");
        seatDTO.setBooked(false);

        Seat seat = seatService.addSeat(seatDTO);
        int seatId = seat.getSeatId();

        seatService.deleteSeat(seatId);
        assertThrows(SeatNotFoundException.class, () -> seatService.getSeatById(seatId));
    }

    @Test
    void testAssignAllSeatsToRoute() {
        Bus bus = createSampleBus();
        Route route1 = createSampleRoute(bus);
        Route route2 = createSampleRoute(bus);

        // Add some seats without route assigned
        for (int i = 1; i <= 3; i++) {
            SeatDTO seatDTO = new SeatDTO();
            seatDTO.setBusId(bus.getBusId());
            seatDTO.setRouteId(route1.getRouteId());
            seatDTO.setSeatNumber("R" + i);
            seatDTO.setBooked(false);
            seatService.addSeat(seatDTO);
        }

        // Assign all seats of bus to route2
        seatService.assignAllSeatsToRoute(bus.getBusId(), route2.getRouteId());

        List<Seat> seats = seatService.getSeatsByBusId(bus.getBusId());
        for (Seat s : seats) {
            assertEquals(route2.getRouteId(), s.getRoute().getRouteId());
        }
    }
}