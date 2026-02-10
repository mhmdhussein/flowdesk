package com.flowdesk.flowdesk.ticket.dto;

import com.flowdesk.flowdesk.ticket.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTicketStatusRequest(
        @NotNull(message = "status is required")
        TicketStatus status
) {}
