-- ============================================================
-- 1. USERS
-- ============================================================
CREATE TABLE users (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email             VARCHAR(255) NOT NULL UNIQUE,
    password          VARCHAR(255) NOT NULL,
    username          VARCHAR(255) NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP NULL
);

-- ============================================================
-- 1B. REFRESH TOKENS
-- ============================================================
CREATE TABLE refresh_tokens (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token       VARCHAR(255) NOT NULL UNIQUE,
    expiry_date TIMESTAMP NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 2. PROJECTS
-- ============================================================
CREATE TABLE projects (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name              VARCHAR(255) NOT NULL,
    created_by_id     UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP NULL
);

-- ============================================================
-- 3. PROJECT MEMBERS
-- ============================================================
CREATE TABLE project_members (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    user_id           UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role              VARCHAR(20) NOT NULL,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP NULL,

    CONSTRAINT unique_project_member UNIQUE (project_id, user_id)
);

CREATE UNIQUE INDEX unique_project_owner ON project_members (project_id) WHERE role = 'OWNER';

-- ============================================================
-- 4. CHAT_SESSIONS
-- ============================================================
CREATE TABLE chat_sessions (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    title             VARCHAR(255),
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 5. CHAT_MESSAGES
-- ============================================================
CREATE TABLE chat_messages (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id        UUID NOT NULL REFERENCES chat_sessions(id) ON DELETE CASCADE,
    role              VARCHAR(20) NOT NULL,
    content           TEXT NOT NULL,
    prompt_tokens     INT DEFAULT 0,
    completion_tokens INT DEFAULT 0,
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 6. PROJECT_FILES (Live Code State)
-- ============================================================
CREATE TABLE project_files (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    file_path         VARCHAR(500) NOT NULL,
    content           TEXT,                   -- Store actual code here for fast AI context injection
    storage_key       VARCHAR(255),           -- Only use for images/binary assets
    mime_type         VARCHAR(100),
    updated_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at        TIMESTAMP NULL,
    
    CONSTRAINT unique_project_file UNIQUE (project_id, file_path)
);

-- ============================================================
-- 7. COMMITS (The Snapshot Engine)
-- ============================================================
CREATE TABLE commits (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    message_id        UUID NOT NULL REFERENCES chat_messages(id) ON DELETE CASCADE,
    snapshot_data     JSONB NOT NULL,         -- Store full file tree or diffs
    created_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 8. PREVIEWS
-- ============================================================
CREATE TABLE previews (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id        UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    preview_url       VARCHAR(500),
    status            VARCHAR(20) NOT NULL DEFAULT 'STARTING',
    started_at        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at        TIMESTAMP NULL
);
