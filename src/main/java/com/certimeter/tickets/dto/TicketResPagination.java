package com.certimeter.tickets.dto;

import com.certimeter.tickets.model.Ticket;
import lombok.Data;

import java.util.List;
@Data
public class TicketResPagination {
    private List<Ticket> data;
    private int pageNo;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
