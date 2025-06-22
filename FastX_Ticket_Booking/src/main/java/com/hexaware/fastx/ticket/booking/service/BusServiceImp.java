package com.hexaware.fastx.ticket.booking.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hexaware.fastx.ticket.booking.dto.BusDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.exceptions.BusNotFoundException;
import com.hexaware.fastx.ticket.booking.repository.BusRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BusServiceImp implements IBusService {

    @Autowired
    private BusRepository busRepository;

    

    @Override
    public Bus addBus(BusDTO busDTO) {
        log.info("Adding new bus: {}", busDTO.getBusName());
        Bus bus = new Bus();
        bus.setBusName(busDTO.getBusName());
        bus.setBusNumber(busDTO.getBusNumber());
        bus.setBusType(busDTO.getBusType());
        bus.setTotalSeats(busDTO.getTotalSeats());
        bus.setAmenities(busDTO.getAmenities());

        Bus savedBus = busRepository.save(bus);
        log.info("Bus added with ID: {}", savedBus.getBusId());

        // DO NOT create seats here — seats will be created after route is added
        return savedBus;
    }

    @Override
    public Bus updateBus(int busId, BusDTO busDTO) {
        log.info("Updating bus with ID: {}", busId);
        Optional<Bus> optionalBus = busRepository.findById(busId);
        if (optionalBus.isPresent()) {
            Bus bus = optionalBus.get();
            bus.setBusName(busDTO.getBusName());
            bus.setBusNumber(busDTO.getBusNumber());
            bus.setBusType(busDTO.getBusType());
            bus.setTotalSeats(busDTO.getTotalSeats());
            bus.setAmenities(busDTO.getAmenities());
            Bus updatedBus = busRepository.save(bus);
            log.info("Bus updated with ID: {}", busId);
            return updatedBus;
        } else {
            log.error("Bus not found with ID: {}", busId);
            throw new BusNotFoundException("Bus not found with id: " + busId);
        }
    }

    @Override
    public Bus getBusById(int busId) {
        log.info("Fetching bus with ID: {}", busId);
        return busRepository.findById(busId)
                .orElseThrow(() -> {
                    log.error("Bus not found with ID: {}", busId);
                    return new BusNotFoundException("Bus not found with id: " + busId);
                });
    }

    @Override
    public List<Bus> getAllBuses() {
        log.info("Fetching all buses");
        return busRepository.findAll();
    }

    @Override
    public void deleteBus(int busId) {
        log.info("Attempting to delete bus with ID: {}", busId);
        if (!busRepository.existsById(busId)) {
            log.warn("Bus with ID {} not found. Cannot delete.", busId);
            throw new BusNotFoundException("Cannot delete. Bus not found with id: " + busId);
        }
        busRepository.deleteById(busId);
        log.info("Successfully deleted bus with ID: {}", busId);
    }
}