-- Users
CREATE TABLE users (
                       id            BIGSERIAL PRIMARY KEY,
                       email         VARCHAR(255) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       role          VARCHAR(32)  NOT NULL,
                       created_at    TIMESTAMPTZ  NOT NULL DEFAULT NOW()
);

-- Projects
CREATE TABLE projects (
                          id          BIGSERIAL PRIMARY KEY,
                          name        VARCHAR(255) NOT NULL,
                          created_by  BIGINT NOT NULL REFERENCES users(id),
                          created_at  TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- Project members
CREATE TABLE project_members (
                                 project_id BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                                 user_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
                                 PRIMARY KEY (project_id, user_id)
);

CREATE INDEX idx_project_members_user_id ON project_members(user_id);

-- Tickets
CREATE TABLE tickets (
                         id           BIGSERIAL PRIMARY KEY,
                         project_id   BIGINT NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
                         title        VARCHAR(255) NOT NULL,
                         description  TEXT,
                         status       VARCHAR(32) NOT NULL,
                         priority     VARCHAR(32) NOT NULL,
                         assignee_id  BIGINT REFERENCES users(id),
                         created_by   BIGINT NOT NULL REFERENCES users(id),
                         created_at   TIMESTAMPTZ NOT NULL DEFAULT NOW(),
                         updated_at   TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tickets_project_id  ON tickets(project_id);
CREATE INDEX idx_tickets_status      ON tickets(status);
CREATE INDEX idx_tickets_assignee_id ON tickets(assignee_id);
CREATE INDEX idx_tickets_created_at  ON tickets(created_at);
