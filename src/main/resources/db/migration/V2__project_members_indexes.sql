-- Faster membership checks and project listing
CREATE INDEX IF NOT EXISTS idx_project_members_project_id ON project_members(project_id);
CREATE INDEX IF NOT EXISTS idx_projects_created_by ON projects(created_by);
