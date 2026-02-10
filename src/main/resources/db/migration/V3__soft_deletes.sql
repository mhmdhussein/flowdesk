ALTER TABLE projects
    ADD COLUMN deleted_at TIMESTAMP NULL;

ALTER TABLE tickets
    ADD COLUMN deleted_at TIMESTAMP NULL;

CREATE INDEX idx_projects_deleted_at ON projects(deleted_at);
CREATE INDEX idx_tickets_deleted_at ON tickets(deleted_at);
