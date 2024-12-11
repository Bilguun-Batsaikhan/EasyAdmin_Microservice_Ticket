package com.example.tickets.service;

import com.example.tickets.dto.TicketResPagination;
import com.example.tickets.model.Ticket;
import com.example.tickets.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {
    private final TicketRepository ticketRepository;
    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }
    //TODO: Implement authorization logic then based on the user role, return the appropriate tickets
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
    //TODO: Implement POST method to open a ticket
    //TODO: When opening a ticket, check if another ticket is already open for the asset
}
