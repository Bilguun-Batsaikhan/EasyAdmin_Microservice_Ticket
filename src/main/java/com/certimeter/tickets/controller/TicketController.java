package com.certimeter.tickets.controller;

import com.certimeter.tickets.dto.TicketResPagination;
import com.certimeter.tickets.enumeration.HttpResponseEnum;
import com.certimeter.tickets.enumeration.UserRoleEnum;
import com.certimeter.tickets.exception.FailureException;
import com.certimeter.tickets.model.Ticket;
import com.certimeter.tickets.requestcontext.RequestContext;
import com.certimeter.tickets.service.AuthorizationService;
import com.certimeter.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.EnumSet;
import java.util.Map;

@RestController
@RequestMapping("/tickets")
public class TicketController {
    private final TicketService ticketService;
    private final RequestContext requestContext;
    private final AuthorizationService authorizationService;

    public TicketController(TicketService ticketService, RequestContext requestContext, AuthorizationService authorizationService) {
        this.ticketService = ticketService;
        this.requestContext = requestContext;
        this.authorizationService = authorizationService;
    }

    //--------------------------//
    //CRUD operations for Ticket//
    //--------------------------//
    @GetMapping
    public ResponseEntity<TicketResPagination> getAllTickets(
            @RequestParam Optional<String> modelName,
            @RequestParam Optional<String> modelNameMatchMode,
            @RequestParam Optional<String> username,
            @RequestParam Optional<String> usernameMatchMode,
            @RequestParam Optional<String> title,
            @RequestParam Optional<String> titleMatchMode,
            @RequestParam Optional<String> context,
            @RequestParam Optional<String> contextMatchMode,
            @RequestParam Optional<String> ticketType,
            @RequestParam Optional<String> ticketTypeMatchMode,
            @RequestParam Optional<String> status,
            @RequestParam Optional<String> statusMatchMode,
            @RequestParam Optional<String> priority,
            @RequestParam Optional<String> priorityMatchMode,
            @RequestParam Optional<String> issuedAt,
            @RequestParam Optional<String> issuedAtMatchMode,
            @RequestParam Optional<String> closedAt,
            @RequestParam Optional<String> closedAtMatchMode,
            @RequestParam Optional<String> resolutionDetails,
            @RequestParam Optional<String> resolutionDetailsMatchMode,
            @RequestParam Optional<String> lastUpdatedAt,
            @RequestParam Optional<String> lastUpdatedAtMatchMode,
            @RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {

        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN, UserRoleEnum.SYSTEM_ADMIN);

        if (authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            return new ResponseEntity<>(ticketService.getAllTickets(
                    pageNo,
                    pageSize,
                    modelName,
                    modelNameMatchMode,
                    username,
                    usernameMatchMode,
                    title,
                    titleMatchMode,
                    context,
                    contextMatchMode,
                    ticketType,
                    ticketTypeMatchMode,
                    status,
                    statusMatchMode,
                    priority,
                    priorityMatchMode,
                    issuedAt,
                    issuedAtMatchMode,
                    closedAt,
                    closedAtMatchMode,
                    resolutionDetails,
                    resolutionDetailsMatchMode,
                    lastUpdatedAt,
                    lastUpdatedAtMatchMode
            ), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(ticketService.getAllUserTickets(requestContext.getUserId(), pageNo,
                    pageSize,
                    modelName,
                    modelNameMatchMode,
                    username,
                    usernameMatchMode,
                    title,
                    titleMatchMode,
                    context,
                    contextMatchMode,
                    ticketType,
                    ticketTypeMatchMode,
                    status,
                    statusMatchMode,
                    priority,
                    priorityMatchMode,
                    issuedAt,
                    issuedAtMatchMode,
                    closedAt,
                    closedAtMatchMode,
                    resolutionDetails,
                    resolutionDetailsMatchMode,
                    lastUpdatedAt,
                    lastUpdatedAtMatchMode), HttpStatus.OK);
        }
    }

    @PostMapping
    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.USER);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(HttpResponseEnum.FORBIDDEN);
        }
        Long userId = requestContext.getUserId();
        ticket.setUserId(userId);

        Ticket createdTicket = ticketService.createTicket(ticket);
        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Ticket> updateTicket(@Valid @PathVariable Long id, @RequestBody Map<String, Object> updates) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN, UserRoleEnum.SYSTEM_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(HttpResponseEnum.FORBIDDEN);
        }
        Ticket updatedTicket = ticketService.updateTicket(id, updates);
        return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
    }
}
