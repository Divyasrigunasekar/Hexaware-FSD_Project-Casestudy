package com.hexaware.fastx.ticket.booking.exceptions;

public class BusNotFoundException extends RuntimeException {
    
    public BusNotFoundException(String message) {
        super(message);
    }
}
