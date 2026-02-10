package com.flowdesk.flowdesk.project;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@Entity
@Table(name = "project_members")
@IdClass(ProjectMember.Id.class)
public class ProjectMember {

    @jakarta.persistence.Id
    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @jakarta.persistence.Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    protected ProjectMember() {}

    public ProjectMember(Long projectId, Long userId) {
        this.projectId = projectId;
        this.userId = userId;
    }

    public Long getProjectId() { return projectId; }
    public Long getUserId() { return userId; }

    public static class Id implements Serializable {
        private Long projectId;
        private Long userId;

        public Id() {}

        public Id(Long projectId, Long userId) {
            this.projectId = projectId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Id id = (Id) o;
            return Objects.equals(projectId, id.projectId) && Objects.equals(userId, id.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(projectId, userId);
        }
    }
}
