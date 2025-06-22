package com.hexaware.fastx.ticket.booking.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hexaware.fastx.ticket.booking.entity.Route;
@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
	List<Route> findByOriginIgnoreCase(String origin);
    List<Route> findByDestinationIgnoreCase(String destination);
    List<Route> findByOriginIgnoreCaseAndDestinationIgnoreCase(String origin, String destination);
    List<Route> findByFareLessThanEqual(BigDecimal fare);
}