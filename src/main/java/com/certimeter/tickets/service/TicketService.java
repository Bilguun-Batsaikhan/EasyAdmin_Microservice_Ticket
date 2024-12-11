package com.certimeter.tickets.service;

import com.certimeter.tickets.dto.TicketResPagination;
import com.certimeter.tickets.enumeration.*;
import com.certimeter.tickets.exception.FailureException;
import com.certimeter.tickets.model.Ticket;
import com.certimeter.tickets.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    public TicketResPagination getAllTickets(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Ticket> pagedTickets = ticketRepository.findAll(pageable);
        List<Ticket> tickets = pagedTickets.getContent();
        TicketResPagination ticketResPagination = new TicketResPagination();

        ticketResPagination.setPageNo(pageNo);
        ticketResPagination.setPageSize(pageSize);
        ticketResPagination.setTotalElements(ticketRepository.count());
        ticketResPagination.setTotalPages(pagedTickets.getTotalPages());
        ticketResPagination.setLast(pagedTickets.isLast());
        ticketResPagination.setData(tickets);

        return ticketResPagination;
    }

    public TicketResPagination getAllUserTickets(Long userId, int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Ticket> pagedTickets = ticketRepository.findTicketsByUserId(userId, pageable);
        List<Ticket> tickets = pagedTickets.getContent();
        TicketResPagination ticketResPagination = new TicketResPagination();

        ticketResPagination.setPageNo(pageNo);
        ticketResPagination.setPageSize(pageSize);
        ticketResPagination.setTotalElements(ticketRepository.countByUserId(userId));
        ticketResPagination.setTotalPages(pagedTickets.getTotalPages());
        ticketResPagination.setLast(pagedTickets.isLast());
        ticketResPagination.setData(tickets);

        return ticketResPagination;
    }

    public Ticket createTicket(Ticket ticket) {
        try {
            return ticketRepository.save(ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Ticket updateTicket(Long id, Map<String, ?> updates) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);
        if (ticketOptional.isEmpty()) {
            throw new FailureException(HttpResponseEnum.RESOURCE_NOT_FOUND, "Ticket not found");
        }

        Ticket ticket = ticketOptional.get();

        for (Map.Entry<String, ?> entry : updates.entrySet()) {
            TicketFieldNameUpdateEnum fieldEnum = Arrays.stream(TicketFieldNameUpdateEnum.values())
                    .filter(enumValue -> enumValue.getFieldName().equals(entry.getKey()))
                    .findFirst()
                    .orElseThrow(() -> new FailureException(HttpResponseEnum.INVALID_INPUT, "Invalid field name"));

            switch (fieldEnum) {
                case TICKET_TYPE:
                    ticket.setTicketType(TicketType.valueOf((String) entry.getValue()));
                    break;
                case STATUS:
                    Status newStatus = Status.valueOf((String) entry.getValue());
                    if (newStatus == Status.CLOSED) {
                        if (ticket.getClosedAt() == null) {
                            ticket.setClosedAt(new Timestamp(System.currentTimeMillis()));
                        }
                    }
                    ticket.setStatus(newStatus);
                    break;
                case PRIORITY:
                    ticket.setPriority(Priority.valueOf((String) entry.getValue()));
                    break;
                case CLOSED_AT:
                    ticket.setClosedAt(Timestamp.valueOf((String) entry.getValue()));
                    break;
                case RESOLUTION_DETAILS:
                    ticket.setResolutionDetails((String) entry.getValue());
                    break;
                case LAST_UPDATED_AT:
                    ticket.setLastUpdatedAt(Timestamp.valueOf((String) entry.getValue()));
                    break;
                default:
                    throw new FailureException(HttpResponseEnum.INVALID_INPUT, "Invalid field name");
            }
        }

        if (ticket.getStatus() == Status.CLOSED) {
            if (ticket.getClosedAt() == null || ticket.getResolutionDetails() == null) {
                throw new FailureException(HttpResponseEnum.INVALID_INPUT, "closed_at and resolution_details must be populated when status is CLOSED");
            }
        }

        return ticketRepository.save(ticket);
    }
}
