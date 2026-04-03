package com.triage.ticket_triage.service;


import com.triage.ticket_triage.analyzer.AnalysisResult;
import com.triage.ticket_triage.analyzer.TicketAnalyzer;
import com.triage.ticket_triage.analyzer.TicketAnalyzer;
import com.triage.ticket_triage.dto.TicketMapper;
import com.triage.ticket_triage.dto.TicketRequest;
import com.triage.ticket_triage.dto.TicketResponse;
import com.triage.ticket_triage.exception.TicketNotFoundException;
import com.triage.ticket_triage.model.Ticket;
import com.triage.ticket_triage.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketAnalyzer ticketAnalyzer;
    private final TicketRepository ticketRepository;

    // ----------------------------------------------------------------
    // ANALYZE + PERSIST
    // ----------------------------------------------------------------
    @Transactional
    public TicketResponse analyzeAndSave(TicketRequest request) {
        log.debug("Analyzing ticket: {}", request.getMessage());

        // 1. Run NLP analysis
        AnalysisResult result = ticketAnalyzer.analyze(request.getMessage());

        // 2. Build entity
        Ticket ticket = Ticket.builder()
                .message(request.getMessage())
                .category(result.getCategory().name())
                .priority(result.getPriority().name())
                .urgencyDetected(result.isUrgencyDetected())
                .confidenceScore(result.getConfidenceScore())
                .keywords(TicketMapper.listToString(result.getMatchedKeywords()))
                .signals(TicketMapper.listToString(result.getSignals()))
                .build();

        // 3. Persist
        Ticket saved = ticketRepository.save(ticket);
        log.debug("Ticket saved with id={}", saved.getId());

        // 4. Return response DTO
        return TicketMapper.toResponse(saved);
    }

    // ----------------------------------------------------------------
    // GET ALL TICKETS — latest first
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public List<TicketResponse> getAllTickets() {
        return ticketRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(TicketMapper::toResponse)
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // GET SINGLE TICKET BY ID
    // ----------------------------------------------------------------
    @Transactional(readOnly = true)
    public TicketResponse getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id: " + id));
        return TicketMapper.toResponse(ticket);
    }
}
