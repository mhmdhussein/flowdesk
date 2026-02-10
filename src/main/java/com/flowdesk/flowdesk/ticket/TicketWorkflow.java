package com.flowdesk.flowdesk.ticket;

import org.springframework.stereotype.Component;

@Component
public class TicketWorkflow {

    public boolean canTransition(TicketStatus from, TicketStatus to) {
        if (from == null || to == null) return false;
        if (from == to) return true;

        return switch (from) {
            case OPEN -> (to == TicketStatus.IN_PROGRESS);
            case IN_PROGRESS -> (to == TicketStatus.DONE);
            case DONE -> false;
        };
    }
}
