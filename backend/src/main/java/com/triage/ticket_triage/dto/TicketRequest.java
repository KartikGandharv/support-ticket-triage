package com.triage.ticket_triage.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TicketRequest {

    @NotBlank(message = "Message cannot be blank")
    @Size(min = 10, max = 5000, message = "Message must be between 10 and 5000 characters")
    private String message;
}
