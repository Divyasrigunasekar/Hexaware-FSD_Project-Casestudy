package com.hexaware.fastx.ticket.booking.service;

import java.util.List;

import com.hexaware.fastx.ticket.booking.dto.BusDTO;
import com.hexaware.fastx.ticket.booking.entity.Bus;

public interface IBusService {

    Bus addBus(BusDTO busDTO);

    Bus updateBus(int busId, BusDTO busDTO);

    Bus getBusById(int busId);

    List<Bus> getAllBuses();

    void deleteBus(int busId);
}