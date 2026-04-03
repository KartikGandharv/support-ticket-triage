package com.triage.ticket_triage.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String priority;

    @Column(name = "urgency_detected", nullable = false)
    private boolean urgencyDetected;

    @Column(name = "confidence_score", nullable = false)
    private double confidenceScore;

    // Stored as comma-separated string, converted via helper
    @Column(name = "keywords", columnDefinition = "TEXT")
    private String keywords;

    // Stored as comma-separated string
    @Column(name = "signals", columnDefinition = "TEXT")
    private String signals;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}