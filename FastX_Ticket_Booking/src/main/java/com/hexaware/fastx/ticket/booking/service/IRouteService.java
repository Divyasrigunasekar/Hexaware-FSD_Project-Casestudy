package com.hexaware.fastx.ticket.booking.service;

import com.hexaware.fastx.ticket.booking.dto.RouteDTO;
import com.hexaware.fastx.ticket.booking.entity.Route;

import java.math.BigDecimal;
import java.util.List;

public interface IRouteService {
    Route addRoute(RouteDTO routeDTO); // still returns entity since we save it
    Route updateRoute(int routeId, RouteDTO routeDTO); // still returns entity for save/update
    RouteDTO getRouteById(int routeId);
    List<RouteDTO> getAllRoutes();
    void deleteRoute(int routeId);

    // Search and filter methods return DTOs
    List<RouteDTO> findByOrigin(String origin);
    List<RouteDTO> findByDestination(String destination);
    List<RouteDTO> findByOriginAndDestination(String origin, String destination);
    List<RouteDTO> findByFareLessThanEqual(BigDecimal maxFare);
    RouteDTO mapToDTO(Route route);
    List<RouteDTO> mapToDTOList(List<Route> routes);
}
