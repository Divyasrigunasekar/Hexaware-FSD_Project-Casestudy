package com.hexaware.fastx.ticket.cancellation.exception;

public class DuplicateCancellationException extends RuntimeException {
    public DuplicateCancellationException(String message) {
        super(message);
    }
}
