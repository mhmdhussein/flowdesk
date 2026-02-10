package com.flowdesk.flowdesk.ticket;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status;

    // Keep this aligned with Flyway V1: column exists and is NOT NULL.
    @Column(nullable = false)
    private String priority;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
        touch();
    }

    protected Ticket() {}

    public Ticket(Long projectId, String title, String description, Long createdBy) {
        this.projectId = projectId;
        this.title = title;
        this.description = description;
        this.createdBy = createdBy;
        this.status = TicketStatus.OPEN;
        this.priority = "MEDIUM"; // MVP default, we will formalize later
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getProjectId() { return projectId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TicketStatus getStatus() { return status; }
    public String getPriority() { return priority; }
    public Long getAssigneeId() { return assigneeId; }
    public Long getCreatedBy() { return createdBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setTitle(String title) { this.title = title; touch(); }
    public void setDescription(String description) { this.description = description; touch(); }

    public void setStatus(TicketStatus status) {
        this.status = status;
        touch();
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }
}
