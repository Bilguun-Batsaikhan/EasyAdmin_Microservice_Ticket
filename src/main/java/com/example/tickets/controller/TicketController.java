package com.example.tickets.controller;

import com.example.tickets.dto.TicketResPagination;
import com.example.tickets.enumeration.HttpResponseEnum;
import com.example.tickets.enumeration.UserRoleEnum;
import com.example.tickets.exception.FailureException;
import com.example.tickets.model.Ticket;
import com.example.tickets.requestcontext.RequestContext;
import com.example.tickets.service.AuthorizationService;
import com.example.tickets.service.TicketService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<TicketResPagination> getAllTickets(@RequestParam(value = "page", defaultValue = "0", required = false) int pageNo,
                                                             @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize) {
        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN, UserRoleEnum.SYSTEM_ADMIN);
        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
            throw new FailureException(HttpResponseEnum.FORBIDDEN);
        }
        return new ResponseEntity<>(ticketService.getAllTickets(pageNo, pageSize), HttpStatus.OK);
    }
//    @PostMapping
//    public ResponseEntity<Ticket> createTicket(@Valid @RequestBody Ticket ticket) {
//        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN, UserRoleEnum.SYSTEM_ADMIN);
//        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
//            throw new FailureException(HttpResponseEnum.FORBIDDEN);
//        }
//        Ticket createdTicket = ticketService.createTicket(ticket);
//        return new ResponseEntity<>(createdTicket, HttpStatus.CREATED);
//    }
//    @PatchMapping("/{id}")
//    public ResponseEntity<Ticket> updateTicket(@Valid @PathVariable Long id, @RequestBody Map<String, Object> updates) {
//        EnumSet<UserRoleEnum> authorizedRoles = EnumSet.of(UserRoleEnum.SUPER_ADMIN, UserRoleEnum.SYSTEM_ADMIN);
//        if (!authorizationService.isAuthorized(requestContext, authorizedRoles)) {
//            throw new FailureException(HttpResponseEnum.FORBIDDEN);
//        }
//        Ticket updatedTicket = ticketService.updateTicket(id, updates);
//        return new ResponseEntity<>(updatedTicket, HttpStatus.OK);
//    }
}
