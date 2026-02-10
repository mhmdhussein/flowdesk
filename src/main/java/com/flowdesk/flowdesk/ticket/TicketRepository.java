package com.flowdesk.flowdesk.ticket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    Optional<Ticket> findByIdAndProjectId(Long id, Long projectId);

    Page<Ticket> findByProjectId(
            Long projectId,
            Pageable pageable
    );

    Page<Ticket> findByProjectIdAndStatus(
            Long projectId,
            TicketStatus status,
            Pageable pageable
    );

    Page<Ticket> findByProjectIdAndCreatedBy(
            Long projectId,
            Long createdBy,
            Pageable pageable
    );

    Page<Ticket> findByProjectIdAndStatusAndCreatedBy(
            Long projectId,
            TicketStatus status,
            Long createdBy,
            Pageable pageable
    );
}
