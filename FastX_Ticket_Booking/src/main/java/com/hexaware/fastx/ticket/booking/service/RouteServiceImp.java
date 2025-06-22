package com.hexaware.fastx.ticket.booking.service;
import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.entity.Route;
import com.hexaware.fastx.ticket.booking.exceptions.BusNotFoundException;
import com.hexaware.fastx.ticket.booking.exceptions.RouteNotFoundException;
import com.hexaware.fastx.ticket.booking.repository.BusRepository;
import com.hexaware.fastx.ticket.booking.repository.RouteRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RouteServiceImp implements IRouteService {
	@Autowired
	private BusRepository busRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Override
    public Route addRoute(RouteDTO routeDTO) {
        log.info("Adding new route: {}", routeDTO);
        Route route = new Route();
        Bus bus = busRepository.findById(routeDTO.getBusId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + routeDTO.getBusId()));
        route.setBus(bus);
        route.setOrigin(routeDTO.getOrigin());
        route.setDestination(routeDTO.getDestination());
        route.setDepartureTime(routeDTO.getDepartureTime());
        route.setArrivalTime(routeDTO.getArrivalTime());
        route.setFare(routeDTO.getFare());
        return routeRepository.save(route);
    }

    @Override
    public Route updateRoute(int routeId, RouteDTO routeDTO) {
        log.info("Updating route with ID: {}", routeId);
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + routeId));

        Bus bus = busRepository.findById(routeDTO.getBusId())
                .orElseThrow(() -> new BusNotFoundException("Bus not found with id: " + routeDTO.getBusId()));
        route.setBus(bus);
        route.setOrigin(routeDTO.getOrigin());
        route.setDestination(routeDTO.getDestination());
        route.setDepartureTime(routeDTO.getDepartureTime());
        route.setArrivalTime(routeDTO.getArrivalTime());
        route.setFare(routeDTO.getFare());
        return routeRepository.save(route);
    }

    @Override
    public RouteDTO getRouteById(int routeId) {
        log.info("Fetching route with ID: {}", routeId);
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RouteNotFoundException("Route not found with id: " + routeId));
        return mapToDTO(route);
    }

    @Override
    public List<RouteDTO> getAllRoutes() {
        log.info("Fetching all routes");
        return mapToDTOList(routeRepository.findAll());
    }

    @Override
    public List<RouteDTO> findByOrigin(String origin) {
        List<Route> routes = routeRepository.findByOriginIgnoreCase(origin);
        if (routes.isEmpty()) {
            throw new RouteNotFoundException("No routes found from origin: " + origin);
        }
        return mapToDTOList(routes);
    }

    @Override
    public List<RouteDTO> findByDestination(String destination) {
        List<Route> routes = routeRepository.findByDestinationIgnoreCase(destination);
        if (routes.isEmpty()) {
            throw new RouteNotFoundException("No routes found to destination: " + destination);
        }
        return mapToDTOList(routes);
    }

    @Override
    public List<RouteDTO> findByOriginAndDestination(String origin, String destination) {
        List<Route> routes = routeRepository.findByOriginIgnoreCaseAndDestinationIgnoreCase(origin, destination);
        if (routes.isEmpty()) {
            throw new RouteNotFoundException("No routes found from " + origin + " to " + destination);
        }
        return mapToDTOList(routes);
    }

    @Override
    public List<RouteDTO> findByFareLessThanEqual(BigDecimal maxFare) {
        List<Route> routes = routeRepository.findByFareLessThanEqual(maxFare);
        if (routes.isEmpty()) {
            throw new RouteNotFoundException("No routes found with fare less than or equal to " + maxFare);
        }
        return mapToDTOList(routes);
    }

    @Override
    public void deleteRoute(int routeId) {
        log.info("Deleting route with ID: {}", routeId);
        if (!routeRepository.existsById(routeId)) {
            throw new RouteNotFoundException("Route not found with id: " + routeId);
        }
        routeRepository.deleteById(routeId);
    }
    
    public RouteDTO mapToDTO(Route route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setBusId(route.getBus() != null ? route.getBus().getBusId() : 0);
        dto.setOrigin(route.getOrigin());
        dto.setDestination(route.getDestination());
        dto.setDepartureTime(route.getDepartureTime());
        dto.setArrivalTime(route.getArrivalTime());
        dto.setFare(route.getFare());
        return dto;
    }

    public List<RouteDTO> mapToDTOList(List<Route> routes) {
        return routes.stream()
                     .map(this::mapToDTO)
                     .collect(Collectors.toList());
    }
}