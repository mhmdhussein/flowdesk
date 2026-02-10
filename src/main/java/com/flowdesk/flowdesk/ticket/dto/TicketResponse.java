package com.flowdesk.flowdesk.ticket.dto;

import com.flowdesk.flowdesk.ticket.TicketStatus;

import java.time.Instant;

public record TicketResponse(
        Long id,
        Long projectId,
        String title,
        String description,
        TicketStatus status,
        String priority,
        Long createdBy,
        Instant createdAt,
        Instant updatedAt
) {}
