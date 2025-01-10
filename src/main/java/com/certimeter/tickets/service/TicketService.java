package com.certimeter.tickets.service;

import com.certimeter.tickets.dto.TicketResPagination;
import com.certimeter.tickets.enumeration.*;
import com.certimeter.tickets.exception.FailureException;
import com.certimeter.tickets.model.Ticket;
import com.certimeter.tickets.repository.TicketRepository;
import com.certimeter.tickets.repository.TicketSpecification;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

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

    public TicketResPagination getAllTickets(int pageNo, int pageSize,
                                              Optional<String> modelName,
                                              Optional<String> modelNameMatchMode,
                                              Optional<String> username,
                                              Optional<String> usernameMatchMode,
                                              Optional<String> title,
                                              Optional<String> titleMatchMode,
                                              Optional<String> context,
                                              Optional<String> contextMatchMode,
                                              Optional<String> ticketType,
                                              Optional<String> ticketTypeMatchMode,
                                              Optional<String> status,
                                              Optional<String> statusMatchMode,
                                              Optional<String> priority,
                                              Optional<String> priorityMatchMode,
                                              Optional<String> issuedAt,
                                              Optional<String> issuedAtMatchMode,
                                              Optional<String> closedAt,
                                              Optional<String> closedAtMatchMode,
                                              Optional<String> resolutionDetails,
                                              Optional<String> resolutionDetailsMatchMode,
                                              Optional<String> lastUpdatedAt,
                                              Optional<String> lastUpdatedAtMatchMode) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Specification<Ticket> spec = buildTicketSpecifications(modelName, modelNameMatchMode, username, usernameMatchMode, title, titleMatchMode, context, contextMatchMode, ticketType, ticketTypeMatchMode, status, statusMatchMode, priority, priorityMatchMode, issuedAt, issuedAtMatchMode, closedAt, closedAtMatchMode, resolutionDetails, resolutionDetailsMatchMode, lastUpdatedAt, lastUpdatedAtMatchMode);

        Page<Ticket> pagedTickets = ticketRepository.findAll(spec, pageable);
        List<Ticket> tickets = pagedTickets.getContent();

        return buildTicketResPagination(pagedTickets, tickets);
    }

    public TicketResPagination getAllUserTickets(Long userId, int pageNo, int pageSize, Optional<String> modelName,
                                                 Optional<String> modelNameMatchMode,
                                                 Optional<String> username,
                                                 Optional<String> usernameMatchMode,
                                                 Optional<String> title,
                                                 Optional<String> titleMatchMode,
                                                 Optional<String> context,
                                                 Optional<String> contextMatchMode,
                                                 Optional<String> ticketType,
                                                 Optional<String> ticketTypeMatchMode,
                                                 Optional<String> status,
                                                 Optional<String> statusMatchMode,
                                                 Optional<String> priority,
                                                 Optional<String> priorityMatchMode,
                                                 Optional<String> issuedAt,
                                                 Optional<String> issuedAtMatchMode,
                                                 Optional<String> closedAt,
                                                 Optional<String> closedAtMatchMode,
                                                 Optional<String> resolutionDetails,
                                                 Optional<String> resolutionDetailsMatchMode,
                                                 Optional<String> lastUpdatedAt,
                                                 Optional<String> lastUpdatedAtMatchMode) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Specification<Ticket> spec = buildTicketSpecifications(modelName, modelNameMatchMode, username, usernameMatchMode, title, titleMatchMode, context, contextMatchMode, ticketType, ticketTypeMatchMode, status, statusMatchMode, priority, priorityMatchMode, issuedAt, issuedAtMatchMode, closedAt, closedAtMatchMode, resolutionDetails, resolutionDetailsMatchMode, lastUpdatedAt, lastUpdatedAtMatchMode);
        spec = spec.and((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("userId"), userId));

        Page<Ticket> pagedTickets = ticketRepository.findAll(spec, pageable);
        List<Ticket> tickets = pagedTickets.getContent();

        return buildTicketResPagination(pagedTickets, tickets);
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

    private Specification<Ticket> buildTicketSpecifications(Optional<String> modelName,
                                                            Optional<String> modelNameMatchMode,
                                                            Optional<String> username,
                                                            Optional<String> usernameMatchMode,
                                                            Optional<String> title,
                                                            Optional<String> titleMatchMode,
                                                            Optional<String> context,
                                                            Optional<String> contextMatchMode,
                                                            Optional<String> ticketType,
                                                            Optional<String> ticketTypeMatchMode,
                                                            Optional<String> status,
                                                            Optional<String> statusMatchMode,
                                                            Optional<String> priority,
                                                            Optional<String> priorityMatchMode,
                                                            Optional<String> issuedAt,
                                                            Optional<String> issuedAtMatchMode,
                                                            Optional<String> closedAt,
                                                            Optional<String> closedAtMatchMode,
                                                            Optional<String> resolutionDetails,
                                                            Optional<String> resolutionDetailsMatchMode,
                                                            Optional<String> lastUpdatedAt,
                                                            Optional<String> lastUpdatedAtMatchMode) {
        Specification<Ticket> spec = Specification.where(null);
        spec = addSpecification(spec, "modelName", modelName, modelNameMatchMode);
        spec = addSpecification(spec, "username", username, usernameMatchMode);
        spec = addSpecification(spec, "title", title, titleMatchMode);
        spec = addSpecification(spec, "context", context, contextMatchMode);
        spec = addSpecification(spec, "ticketType", ticketType, ticketTypeMatchMode);
        spec = addSpecification(spec, "status", status, statusMatchMode);
        spec = addSpecification(spec, "priority", priority, priorityMatchMode);
        spec = addSpecification(spec, "issuedAt", issuedAt, issuedAtMatchMode);
        spec = addSpecification(spec, "closedAt", closedAt, closedAtMatchMode);
        spec = addSpecification(spec, "resolutionDetails", resolutionDetails, resolutionDetailsMatchMode);
        spec = addSpecification(spec, "lastUpdatedAt", lastUpdatedAt, lastUpdatedAtMatchMode);
        return spec;
    }

    private Specification<Ticket> addSpecification(Specification<Ticket> spec, String field, Optional<String> value, Optional<String> matchModeStr) {
        if (value.isPresent() && matchModeStr.isPresent()) {
            MatchMode matchMode = getMatchModeFromString(matchModeStr.get());

            switch (field) {
                case "modelName":
                    return spec.and(TicketSpecification.matchModeInJoin("asset", "modelName", value.get(), matchMode));
                case "username":
                    return spec.and(TicketSpecification.matchModeInJoin("user", "username", value.get(), matchMode));
                default:
                    //date "2025-01-09" dateMatchMode "dateIs"
                    return spec.and(TicketSpecification.matchMode(field, value.get(), matchMode));
            }
        }
        return spec;
    }

    private MatchMode getMatchModeFromString(String matchModeStr) {
        switch (matchModeStr.toLowerCase()) {
            case "startswith":
                return MatchMode.STARTS_WITH;
            case "contains":
                return MatchMode.CONTAINS;
            case "notcontains":
                return MatchMode.NOT_CONTAINS;
            case "endswith":
                return MatchMode.ENDS_WITH;
            case "equals":
                return MatchMode.EQUALS;
            case "notequals":
                return MatchMode.NOT_EQUALS;
            case "nofilter":
                return MatchMode.NO_FILTER;
            case "dateis":
                return MatchMode.DATE_IS;
            case "dateisnot":
                return MatchMode.DATE_IS_NOT;
            case "datebefore":
                return MatchMode.DATE_BEFORE;
            case "dateafter":
                return MatchMode.DATE_AFTER;
            default:
                throw new IllegalArgumentException("Invalid match mode: " + matchModeStr);
        }
    }

    private TicketResPagination buildTicketResPagination(Page<Ticket> pagedTickets, List<Ticket> tickets) {
        return TicketResPagination.builder()
                .pageNo(pagedTickets.getNumber())
                .pageSize(pagedTickets.getSize())
                .totalElements(pagedTickets.getTotalElements())
                .totalPages(pagedTickets.getTotalPages())
                .last(pagedTickets.isLast())
                .data(tickets)
                .build();
    }
}
