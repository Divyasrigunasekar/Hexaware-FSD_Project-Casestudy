package com.hexaware.fastx.ticket.cancellation.dto;

public class CancellationResponseDTO {
    private String message;

    public CancellationResponseDTO() {
    }

    public CancellationResponseDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
