package com.certimeter.tickets.enumeration;

import lombok.Getter;

@Getter
public enum TicketFieldNameUpdateEnum {
    TICKET_TYPE("ticketType"),
    STATUS("status"),
    PRIORITY("priority"),
    CLOSED_AT("closedAt"),
    RESOLUTION_DETAILS("resolutionDetails"),
    LAST_UPDATED_AT("lastUpdatedAt");

    private final String fieldName;

    TicketFieldNameUpdateEnum(String fieldName) {
        this.fieldName = fieldName;
    }
}
