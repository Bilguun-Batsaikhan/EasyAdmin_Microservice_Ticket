package com.certimeter.tickets.repository;

import com.certimeter.tickets.model.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Page<Ticket> findTicketsByUserId(Long userId, Pageable pageable);
    long countByUserId(Long userId);
}
