package com.triage.ticket_triage.dto;


import com.triage.ticket_triage.model.Ticket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TicketMapper {

    // Convert Entity → Response DTO
    public static TicketResponse toResponse(Ticket ticket) {
        return TicketResponse.builder()
                .id(ticket.getId())
                .message(ticket.getMessage())
                .category(ticket.getCategory())
                .priority(ticket.getPriority())
                .urgencyDetected(ticket.isUrgencyDetected())
                .confidenceScore(ticket.getConfidenceScore())
                .keywords(splitToList(ticket.getKeywords()))
                .signals(splitToList(ticket.getSignals()))
                .createdAt(ticket.getCreatedAt())
                .build();
    }

    // Convert List<String> → comma-separated String for DB storage
    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(",", list);
    }

    // Convert comma-separated String → List<String>
    public static List<String> splitToList(String value) {
        if (value == null || value.isBlank()) return Collections.emptyList();
        return Arrays.asList(value.split(","));
    }
}
