package com.triage.ticket_triage.repository;


import com.triage.ticket_triage.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Latest tickets first
    List<Ticket> findAllByOrderByCreatedAtDesc();

    // Find by category
    List<Ticket> findByCategoryOrderByCreatedAtDesc(String category);

    // Find by priority
    List<Ticket> findByPriorityOrderByCreatedAtDesc(String priority);
}
