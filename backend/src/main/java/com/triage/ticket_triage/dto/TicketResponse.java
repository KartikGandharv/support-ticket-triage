package com.triage.ticket_triage.dto;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class TicketResponse {

    private Long id;
    private String message;
    private String category;
    private String priority;
    private boolean urgencyDetected;
    private double confidenceScore;
    private List<String> keywords;
    private List<String> signals;
    private LocalDateTime createdAt;
}
