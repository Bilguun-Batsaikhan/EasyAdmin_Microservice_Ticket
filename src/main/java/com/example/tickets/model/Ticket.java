package com.example.tickets.model;

import com.example.tickets.enumeration.Priority;
import com.example.tickets.enumeration.Status;
import com.example.tickets.enumeration.TicketType;
import com.example.tickets.enumeration.TicketStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.Timestamp;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "asset_id", nullable = false)
    private Long assetId;

    @NotBlank
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull
    @Column(name = "title", length = 150, nullable = false)
    private String title;

    @Column(name = "context", length = 255)
    private String context;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_type", nullable = false)
    private TicketType ticketType;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.MEDIUM;

    @NotNull
    @Column(name = "issued_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp issuedAt;

    @Column(name = "closed_at")
    private Timestamp closedAt;

    @Column(name = "resolution_details", columnDefinition = "TEXT")
    private String resolutionDetails;

    @Column(name = "last_updated_at", nullable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private Timestamp lastUpdatedAt;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status", nullable = false, columnDefinition = "ENUM('NEW', 'IN_PROGRESS', 'RESOLVED') DEFAULT 'NEW'")
    private TicketStatus ticketStatus = TicketStatus.NEW;
}