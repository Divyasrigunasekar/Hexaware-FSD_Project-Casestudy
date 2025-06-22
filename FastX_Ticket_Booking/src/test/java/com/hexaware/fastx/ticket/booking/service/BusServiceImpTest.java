package com.hexaware.fastx.ticket.booking.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hexaware.fastx.ticket.booking.dto.BusDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;
import com.hexaware.fastx.ticket.booking.exceptions.BusNotFoundException;

@SpringBootTest
@Transactional  // ensures test data rollback after each test
class BusServiceImpTest {

    @Autowired
    private IBusService busService;

    @Test
    void testAddBus() {
        BusDTO busDTO = new BusDTO();
        busDTO.setBusName("Test Bus");
        busDTO.setBusNumber("TN01AB1234");
        busDTO.setBusType("AC");
        busDTO.setTotalSeats(40);
        busDTO.setAmenities("WiFi, Water");

        Bus savedBus = busService.addBus(busDTO);

        assertNotNull(savedBus);
        assertEquals("Test Bus", savedBus.getBusName());
    }

    @Test
    void testGetBusByIdAndUpdateBus() {
        BusDTO busDTO = new BusDTO();
        busDTO.setBusName("Sample Bus");
        busDTO.setBusNumber("KA05XY5678");
        busDTO.setBusType("Non-AC");
        busDTO.setTotalSeats(30);
        busDTO.setAmenities("Charging Ports");

        Bus createdBus = busService.addBus(busDTO);
        int id = createdBus.getBusId();

        Bus fetchedBus = busService.getBusById(id);
        assertEquals("Sample Bus", fetchedBus.getBusName());

        BusDTO updateDTO = new BusDTO();
        updateDTO.setBusName("Updated Bus");
        updateDTO.setBusNumber("KA05XY5678");
        updateDTO.setBusType("AC");
        updateDTO.setTotalSeats(35);
        updateDTO.setAmenities("Charging Ports, WiFi");

        Bus updatedBus = busService.updateBus(id, updateDTO);
        assertEquals("Updated Bus", updatedBus.getBusName());
        assertEquals("AC", updatedBus.getBusType());
    }

    @Test
    void testGetAllBuses() {
        List<Bus> beforeAdd = busService.getAllBuses();

        BusDTO busDTO = new BusDTO();
        busDTO.setBusName("Bus for List");
        busDTO.setBusNumber("DL08MN0987");
        busDTO.setBusType("Sleeper");
        busDTO.setTotalSeats(50);
        busDTO.setAmenities("Blankets");

        busService.addBus(busDTO);

        List<Bus> afterAdd = busService.getAllBuses();

        assertTrue(afterAdd.size() > beforeAdd.size());
    }

    @Test
    void testDeleteBus() {
        BusDTO busDTO = new BusDTO();
        busDTO.setBusName("Bus to Delete");
        busDTO.setBusNumber("MH12CD3456");
        busDTO.setBusType("Semi-Sleeper");
        busDTO.setTotalSeats(45);
        busDTO.setAmenities("WiFi");

        Bus createdBus = busService.addBus(busDTO);
        int id = createdBus.getBusId();

        // Delete the bus
        busService.deleteBus(id);

        // Now, trying to get it should throw exception
        assertThrows(BusNotFoundException.class, () -> {
            busService.getBusById(id);
        });
    }
}