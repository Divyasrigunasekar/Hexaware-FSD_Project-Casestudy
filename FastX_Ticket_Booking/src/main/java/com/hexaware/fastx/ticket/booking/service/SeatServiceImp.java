package com.hexaware.fastx.ticket.booking.service;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hexaware.fastx.ticket.booking.dto.SeatAvailabilityDTO;
import com.hexaware.fastx.ticket.booking.dto.SeatDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.entity.Seat;
import com.hexaware.fastx.ticket.booking.exceptions.BusNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.RouteNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.SeatNotFoundException;
import com.hexaware.fastx.ticket.booking.repository.BusRepository;
import com.hexaware.fastx.ticket.booking.repository.RouteRepository;
import com.hexaware.fastx.ticket.booking.repository.SeatRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SeatServiceImp implements ISeatService {

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private BusRepository busRepository;
    
    @Autowired
    private RouteRepository routeRepository;
    
    

    @Override
    public Seat addSeat(SeatDTO seatDTO) {
        log.info("Adding new seat(s) for bus ID {}", seatDTO.getBusId());

        Bus bus = busRepository.findById(seatDTO.getBusId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + seatDTO.getBusId()));
        Route route = routeRepository.findById(seatDTO.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + seatDTO.getRouteId()));

        try {
            int totalSeats = Integer.parseInt(seatDTO.getSeatNumber());
            // if seatNumber is a number > 1, create multiple seats from 1 to totalSeats
            if (totalSeats > 1) {
                for (int i = 1; i <= totalSeats; i++) {
                    Seat seat = new Seat();
                    seat.setBus(bus);
                    seat.setRoute(route);
                    seat.setSeatNumber(String.valueOf(i));
                    seat.setBooked(false);
                    seatRepository.save(seat);
                }
                return null; // bulk created, no single seat to return
            }
        } catch (NumberFormatException e) {
            // seatNumber is not a number, just add single seat below
        }

        // single seat creation fallback
        Seat seat = new Seat();
        seat.setBus(bus);
        seat.setRoute(route);
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setBooked(seatDTO.isBooked());

        return seatRepository.save(seat);
    }

    @Override
    public Seat updateSeat(int seatId, SeatDTO seatDTO) {
        log.info("Updating seat with ID {}", seatId);

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with id: " + seatId));

        Bus bus = busRepository.findById(seatDTO.getBusId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + seatDTO.getBusId()));

        seat.setBus(bus);
        seat.setSeatNumber(seatDTO.getSeatNumber());
        seat.setBooked(seatDTO.isBooked());

        return seatRepository.save(seat);
    }

    @Override
    public Seat getSeatById(int seatId) {
        return seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with id: " + seatId));
    }

    @Override
    public List<SeatDTO> getAllSeats() {
        List<Seat> seats = seatRepository.findAll();
        return mapToDTOList(seats);
    }

    @Override
    public void deleteSeat(int seatId) {
        if (!seatRepository.existsById(seatId)) {
            throw new SeatNotFoundException("Seat not found with id: " + seatId);
        }
        seatRepository.deleteById(seatId);
        log.info("Deleted seat with ID {}", seatId);
    }
    @Override
    public List<Seat> getSeatsByBusId(int busId) {
        log.info("Fetching seats for bus ID {}", busId);
        return seatRepository.findByBus_BusId(busId);
    }
    @Override
    public SeatAvailabilityDTO getSeatAvailabilityByBusId(int busId) {
        List<Seat> seats = seatRepository.findByBus_BusId(busId);
        List<String> booked = new ArrayList<>();
        List<String> available = new ArrayList<>();

        for (Seat seat : seats) {
            if (seat.isBooked()) {
                booked.add(seat.getSeatNumber());
            } else {
                available.add(seat.getSeatNumber());
            }
        }

        SeatAvailabilityDTO dto = new SeatAvailabilityDTO();
        dto.setBookedSeats(booked);
        dto.setAvailableSeats(available);
        return dto;
    }
    @Override
    public void assignAllSeatsToRoute(int busId, int routeId) {
        log.info("Assigning all seats of bus {} to route {}", busId, routeId);

        // Get the Bus
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + busId));

        // Get the Route
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + routeId));

        // Get all seats for that Bus
        List<Seat> seats = seatRepository.findByBus(bus);

        // Assign Route to each Seat
        for (Seat seat : seats) {
            seat.setRoute(route);
        }

        // Save all updated seats
        seatRepository.saveAll(seats);

        log.info("Assigned route {} to {} seats of bus {}", routeId, seats.size(), busId);
    }
    @Override
    public void unbookSeat(int seatId) {
        log.info("Unbooking seat with ID {}", seatId);
        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() -> new SeatNotFoundException("Seat not found with id: " + seatId));
        seat.setBooked(false);
        seatRepository.save(seat);
    }
    @Override
    public List<Seat> getAvailableSeatsByRoute(int routeId) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + routeId));

        Bus bus = route.getBus();

        List<Seat> allSeats = seatRepository.findByBus(bus);
        return allSeats.stream()
                       .filter(seat -> !seat.isBooked())
                       .collect(Collectors.toList());
    }
    public SeatDTO mapToDTO(Seat seat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(seat.getSeatId());
        dto.setSeatNumber(seat.getSeatNumber());
        dto.setBooked(seat.isBooked());
        
        if (seat.getBus() != null) {
            dto.setBusId(seat.getBus().getBusId());
        }

        if (seat.getRoute() != null) {
            dto.setRouteId(seat.getRoute().getRouteId());
        }

        return dto;
    }

    public List<SeatDTO> mapToDTOList(List<Seat> seats) {
        return seats.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
    }
    @Override
    public List<SeatDTO> getAllSeatsByRoute(int routeId) {
        Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + routeId));

        Bus bus = route.getBus();
        List<Seat> allSeats = seatRepository.findByBus(bus); // all seats for that bus
        return mapToDTOList(allSeats); // map to DTO with seatNumber + isBooked
    }
    public List<String> getSeatNumbersByIds(List<Integer> seatIds) {
        List<Seat> seats = seatRepository.findAllById(seatIds);
        return seats.stream()
            .map(Seat::getSeatNumber)
            .collect(Collectors.toList());
    }



}