package com.flowdesk.flowdesk.ticket;

import com.flowdesk.flowdesk.security.ProjectAuthorizationService;
import com.flowdesk.flowdesk.ticket.dto.CreateTicketRequest;
import com.flowdesk.flowdesk.ticket.dto.TicketResponse;
import com.flowdesk.flowdesk.ticket.dto.UpdateTicketStatusRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.flowdesk.flowdesk.ticket.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

@RestController
@RequestMapping("/api/projects/{projectId}/tickets")
public class TicketController {

    private final TicketRepository tickets;
    private final ProjectAuthorizationService authz;
    private final TicketWorkflow workflow;

    public TicketController(TicketRepository tickets, ProjectAuthorizationService authz, TicketWorkflow workflow) {
        this.tickets = tickets;
        this.authz = authz;
        this.workflow = workflow;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TicketResponse create(
            Authentication auth,
            @PathVariable Long projectId,
            @Valid @RequestBody CreateTicketRequest request
    ) {
        Long userId = authz.requireUserId(auth.getName());
        authz.requireProjectMember(projectId, userId);

        Ticket ticket = new Ticket(
                projectId,
                request.title().trim(),
                request.description(),
                userId
        );

        Ticket saved = tickets.save(ticket);
        return toResponse(saved);
    }

    @GetMapping
    public PagedResponse<TicketResponse> list(
            Authentication auth,
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) TicketStatus status,
            @RequestParam(required = false) Long createdBy
    ) {
        Long userId = authz.requireUserId(auth.getName());
        authz.requireProjectMember(projectId, userId);

        if (size > 100) {
            throw new IllegalArgumentException("size must be <= 100");
        }

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Ticket> result;

        if (status != null && createdBy != null) {
            result = tickets.findByProjectIdAndStatusAndCreatedBy(
                    projectId, status, createdBy, pageable
            );
        } else if (status != null) {
            result = tickets.findByProjectIdAndStatus(
                    projectId, status, pageable
            );
        } else if (createdBy != null) {
            result = tickets.findByProjectIdAndCreatedBy(
                    projectId, createdBy, pageable
            );
        } else {
            result = tickets.findByProjectId(projectId, pageable);
        }

        return new PagedResponse<>(
                result.getContent().stream().map(this::toResponse).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    @GetMapping("/{ticketId}")
    public TicketResponse get(Authentication auth, @PathVariable Long projectId, @PathVariable Long ticketId) {
        Long userId = authz.requireUserId(auth.getName());
        authz.requireProjectMember(projectId, userId);

        Ticket ticket = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        return toResponse(ticket);
    }

    @PatchMapping("/{ticketId}/status")
    public TicketResponse updateStatus(
            Authentication auth,
            @PathVariable Long projectId,
            @PathVariable Long ticketId,
            @Valid @RequestBody UpdateTicketStatusRequest request
    ) {
        Long userId = authz.requireUserId(auth.getName());
        authz.requireProjectMember(projectId, userId);

        Ticket ticket = tickets.findByIdAndProjectId(ticketId, projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ticket not found"));

        TicketStatus target = request.status();
        if (!workflow.canTransition(ticket.getStatus(), target)) {
            throw new IllegalArgumentException("Invalid status transition: " + ticket.getStatus() + " -> " + target);
        }

        ticket.setStatus(target);
        Ticket saved = tickets.save(ticket);

        return toResponse(saved);
    }

    @DeleteMapping("/{ticketId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTicket(
            Authentication auth,
            @PathVariable Long projectId,
            @PathVariable Long ticketId
    ) {
        Long userId = authz.requireUserId(auth.getName());
        authz.requireProjectMember(projectId, userId);

        Ticket ticket = tickets.findByIdAndProjectId(ticketId, projectId)
                .filter(t -> !t.isDeleted())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        ticket.softDelete();
        tickets.save(ticket);
    }

    private TicketResponse toResponse(Ticket t) {
        return new TicketResponse(
                t.getId(),
                t.getProjectId(),
                t.getTitle(),
                t.getDescription(),
                t.getStatus(),
                t.getPriority(),
                t.getCreatedBy(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }
}
