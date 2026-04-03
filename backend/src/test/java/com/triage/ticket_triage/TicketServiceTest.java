package com.triage.ticket_triage;


import com.triage.ticket_triage.analyzer.AnalysisResult;
import com.triage.ticket_triage.analyzer.TicketAnalyzer;
import com.triage.ticket_triage.dto.TicketRequest;
import com.triage.ticket_triage.dto.TicketResponse;
import com.triage.ticket_triage.exception.TicketNotFoundException;
import com.triage.ticket_triage.model.Category;
import com.triage.ticket_triage.model.Priority;
import com.triage.ticket_triage.model.Ticket;
import com.triage.ticket_triage.repository.TicketRepository;
import com.triage.ticket_triage.service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    private TicketAnalyzer ticketAnalyzer;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketService ticketService;

    private AnalysisResult mockAnalysisResult;
    private Ticket mockTicket;

    @BeforeEach
    void setUp() {
        mockAnalysisResult = AnalysisResult.builder()
                .category(Category.BILLING)
                .priority(Priority.P2)
                .urgencyDetected(false)
                .confidenceScore(0.60)
                .matchedKeywords(List.of("invoice", "refund"))
                .signals(List.of("Matched keywords: invoice, refund"))
                .build();

        mockTicket = Ticket.builder()
                .id(1L)
                .message("I need a refund for my invoice")
                .category("BILLING")
                .priority("P2")
                .urgencyDetected(false)
                .confidenceScore(0.60)
                .keywords("invoice,refund")
                .signals("Matched keywords: invoice, refund")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should analyze and save ticket successfully")
    void shouldAnalyzeAndSaveTicket() {
        TicketRequest request = new TicketRequest();
        request.setMessage("I need a refund for my invoice");

        when(ticketAnalyzer.analyze(any())).thenReturn(mockAnalysisResult);
        when(ticketRepository.save(any())).thenReturn(mockTicket);

        TicketResponse response = ticketService.analyzeAndSave(request);

        assertThat(response).isNotNull();
        assertThat(response.getCategory()).isEqualTo("BILLING");
        assertThat(response.getPriority()).isEqualTo("P2");
        assertThat(response.getConfidenceScore()).isEqualTo(0.60);

        verify(ticketAnalyzer, times(1)).analyze(any());
        verify(ticketRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Should return all tickets ordered by latest first")
    void shouldReturnAllTickets() {
        when(ticketRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(List.of(mockTicket));

        List<TicketResponse> tickets = ticketService.getAllTickets();

        assertThat(tickets).hasSize(1);
        assertThat(tickets.get(0).getId()).isEqualTo(1L);
        verify(ticketRepository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    @DisplayName("Should return ticket by id")
    void shouldReturnTicketById() {
        when(ticketRepository.findById(1L)).thenReturn(Optional.of(mockTicket));

        TicketResponse response = ticketService.getTicketById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCategory()).isEqualTo("BILLING");
    }

    @Test
    @DisplayName("Should throw TicketNotFoundException when id not found")
    void shouldThrowExceptionWhenTicketNotFound() {
        when(ticketRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> ticketService.getTicketById(99L))
                .isInstanceOf(TicketNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    @DisplayName("Should return empty list when no tickets exist")
    void shouldReturnEmptyListWhenNoTickets() {
        when(ticketRepository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        List<TicketResponse> tickets = ticketService.getAllTickets();

        assertThat(tickets).isEmpty();
    }
}
