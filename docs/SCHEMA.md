# Database Schema Documentation

This project uses **PostgreSQL** with an architecture optimized for an AI-driven development platform (Lovable clone). It includes a `chat_sessions` layer to allow for multiple conversation threads per project. All primary and foreign keys use **UUIDs**.

## 1. Tables Overview

### `users`
Handles user identity and authentication.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `email` (VARCHAR, UNIQUE, NOT NULL): User email.
- `password` (VARCHAR, NOT NULL): Encrypted password.
- `username` (VARCHAR, NOT NULL): User's handle/display name.
- `created_at` (TIMESTAMP, NOT NULL): Creation time.
- `updated_at` (TIMESTAMP, NOT NULL): Last update time.
- `deleted_at` (TIMESTAMP): Soft delete timestamp.

### `refresh_tokens`
Tracks active user session refresh tokens for authentication.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `user_id` (UUID, FK, NOT NULL): Links to `users.id`.
- `token` (VARCHAR, UNIQUE, NOT NULL): Secure refresh token string.
- `expiry_date` (TIMESTAMP, NOT NULL): Expiration timestamp.
- `created_at` (TIMESTAMP, NOT NULL): Creation time.

### `projects`
The container for a generated application.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `name` (VARCHAR, NOT NULL): Project name.
- `created_by_id` (UUID, FK, NOT NULL): Links to `users.id`. Represents the project owner/creator.
- `created_at` (TIMESTAMP, NOT NULL): Creation time.
- `updated_at` (TIMESTAMP, NOT NULL): Last update time.
- `deleted_at` (TIMESTAMP): Soft delete timestamp.

### `project_members`
Maps users to projects with a role. Every project has exactly one member with `role=OWNER`.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `project_id` (UUID, FK, NOT NULL): Links to `projects.id`.
- `user_id` (UUID, FK, NOT NULL): Links to `users.id`.
- `role` (VARCHAR, NOT NULL): `OWNER`, `EDITOR`, or `VIEWER`.
- `created_at` (TIMESTAMP, NOT NULL): When the membership was created.
- `updated_at` (TIMESTAMP, NOT NULL): Last update time.
- `deleted_at` (TIMESTAMP): Soft delete timestamp.
- **Constraint**: Unique on `(project_id, user_id)`.

### `project_invitations`
Tracks pending project membership invitations. The primary key `id` serves as the secure invitation token.
- `id` (UUID, PK, NOT NULL): Primary identifier (acts as the secure token).
- `project_id` (UUID, FK, NOT NULL): Links to `projects.id`.
- `email` (VARCHAR, NOT NULL): Target invited user email.
- `role` (VARCHAR, NOT NULL): Invited role (`EDITOR` or `VIEWER`).
- `status` (VARCHAR, NOT NULL): `PENDING`, `ACCEPTED`, `EXPIRED`, or `REVOKED`.
- `invited_at` (TIMESTAMP, NOT NULL): Timestamp when the invitation was sent.
- `expires_at` (TIMESTAMP, NOT NULL): Timestamp when the invitation expires.
- **Constraint**: Unique on `(project_id, email)`.

### `chat_sessions`
Groups conversation history for a project.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `project_id` (UUID, FK, NOT NULL): Links to `projects.id`.
- `title` (VARCHAR): Optional title for the session.
- `created_at` (TIMESTAMP, NOT NULL): Creation time.
- `updated_at` (TIMESTAMP, NOT NULL): Last update time.

### `chat_messages`
The individual messages within a chat session.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `session_id` (UUID, FK, NOT NULL): Links to `chat_sessions.id`.
- `role` (VARCHAR, NOT NULL): `user`, `assistant`, or `system`. (Persisted as lowercase).
- `content` (TEXT, NOT NULL): The message text or AI-generated response.
- `prompt_tokens` (INT): Prompt token count usage tracking.
- `completion_tokens` (INT): Completion token count usage tracking.
- `created_at` (TIMESTAMP, NOT NULL): Creation time.

### `project_files`
Represents the current "Live" state of the codebase.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `project_id` (UUID, FK, NOT NULL): Links to `projects.id`.
- `file_path` (VARCHAR, NOT NULL): Unique path within the project (e.g., `src/App.tsx`).
- `content` (TEXT): The raw source code (stored in-DB for fast context injection). Nullable for deleted or empty files.
- `storage_key` (VARCHAR): Used for binary assets (images, etc.).
- `mime_type` (VARCHAR): File content type.
- `updated_at` (TIMESTAMP, NOT NULL): Last update time.
- `deleted_at` (TIMESTAMP): Soft delete timestamp.
- **Constraint**: Unique on `(project_id, file_path)`.

### `commits`
The snapshot engine for the "Undo" feature.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `project_id` (UUID, FK, NOT NULL): Links to `projects.id`.
- `message_id` (UUID, FK, NOT NULL): The `chat_messages.id` that triggered this version.
- `snapshot_data` (JSONB, NOT NULL): A complete map of `{ filePath: content }` at this point in time.
- `created_at` (TIMESTAMP, NOT NULL): Creation time.

### `previews`
Tracks the execution environment for live previews.
- `id` (UUID, PK, NOT NULL): Primary identifier.
- `project_id` (UUID, FK, NOT NULL): Links to `projects.id`.
- `preview_url` (VARCHAR): URL to access the preview environment.
- `status` (VARCHAR, NOT NULL): `STARTING`, `READY`, `STALLED`, `FAILED`, `EXPIRED`.
- `started_at` (TIMESTAMP, NOT NULL): Start timestamp.
- `expires_at` (TIMESTAMP): Expiry timestamp.

## 2. Relationships
- **User N:M Projects**: A user can own (via `project_members` with `role=OWNER`) or be a member of multiple projects.
- **Project 1:N Members**: A project has many members, each with a role.
- **Project 1:N ChatSessions**: A project can have multiple conversations (managed internally).
- **ChatSession 1:N ChatMessages**: Each session has a timeline of messages.
- **Project 1:N Files**: A project contains many files (Live state).
- **Project 1:N Commits**: Every AI-driven change creates a checkpoint.
- **ChatMessage 1:1 Commit**: An AI response that changes code maps to exactly one snapshot.

## 3. Key Architectural Decisions
- **Decoupled Chat**: Introducing `chat_sessions` ensures that conversation context is isolated and manageable, preventing token limit issues in long-lived projects.
- **In-DB Code Storage**: Storing source code as `TEXT` in `project_files` allows the backend to build AI prompts without external storage latency.
- **JSONB Snapshots**: Using PostgreSQL's JSONB for `snapshot_data` allows for flexible "point-in-time" recovery of the entire file tree.
- **UUIDs**: Native UUID generation (`gen_random_uuid()`) ensures non-guessable URLs and IDs.
