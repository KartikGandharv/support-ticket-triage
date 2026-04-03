package com.triage.ticket_triage.controller;


import com.triage.ticket_triage.dto.TicketRequest;
import com.triage.ticket_triage.dto.TicketResponse;
import com.triage.ticket_triage.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // ----------------------------------------------------------------
    // POST /tickets/analyze
    // ----------------------------------------------------------------
    @PostMapping("/analyze")
    public ResponseEntity<TicketResponse> analyzeTicket(
            @Valid @RequestBody TicketRequest request) {

        log.info("POST /tickets/analyze — message length={}", request.getMessage().length());
        TicketResponse response = ticketService.analyzeAndSave(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ----------------------------------------------------------------
    // GET /tickets
    // ----------------------------------------------------------------
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        log.info("GET /tickets");
        List<TicketResponse> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    // ----------------------------------------------------------------
    // GET /tickets/{id}
    // ----------------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(@PathVariable Long id) {
        log.info("GET /tickets/{}", id);
        TicketResponse response = ticketService.getTicketById(id);
        return ResponseEntity.ok(response);
    }
}
