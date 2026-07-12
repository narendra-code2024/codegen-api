-- ============================================================
-- CREATE PROJECT INVITATIONS TABLE
-- ============================================================
CREATE TABLE project_invitations (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id    UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    email         VARCHAR(255) NOT NULL,
    role          VARCHAR(20) NOT NULL,
    status        VARCHAR(20) NOT NULL,
    invited_at    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at    TIMESTAMP NOT NULL,
    accepted_at   TIMESTAMP NULL,

    CONSTRAINT unique_project_invite_email UNIQUE (project_id, email)
);
