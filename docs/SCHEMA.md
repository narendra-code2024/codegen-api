# Database Schema Documentation

This project uses **PostgreSQL** with an architecture optimized for an AI-driven development platform (Lovable clone). It includes a `chat_sessions` layer to allow for multiple conversation threads per project. All primary and foreign keys use **UUIDs**.

## 1. Tables Overview

### `users`
Handles user identity and authentication.
- `id` (UUID, PK): Primary identifier.
- `email` (VARCHAR, Unique): User email.
- `password_hash` (VARCHAR): Encrypted password.
- `name` (VARCHAR): Display name.
- `created_at` (TIMESTAMP): Creation time.
- `updated_at` (TIMESTAMP): Last update time.
- `deleted_at` (TIMESTAMP): Soft delete timestamp.

### `projects`
The container for a generated application.
- `id` (UUID, PK): Primary identifier.
- `owner_id` (UUID, FK): Links to `users.id`.
- `name` (VARCHAR): Project name.
- `framework` (VARCHAR): Target framework (e.g., 'nextjs').
- `status` (VARCHAR): `INITIALIZING`, `ACTIVE`, `ARCHIVED`.
- `created_at`, `updated_at`, `deleted_at`: Audit fields.

### `chat_sessions`
Groups conversation history for a project.
- `id` (UUID, PK): Primary identifier.
- `project_id` (UUID, FK): Links to `projects.id`.
- `title` (VARCHAR): Optional title for the session.
- `created_at`, `updated_at`: Audit fields.

### `chat_messages`
The individual messages within a chat session.
- `id` (UUID, PK): Primary identifier.
- `session_id` (UUID, FK): Links to `chat_sessions.id`.
- `role` (VARCHAR): `user`, `assistant`, or `system`. (Persisted as lowercase).
- `content` (TEXT): The message text or AI-generated response.
- `prompt_tokens`, `completion_tokens` (INT): Usage tracking.
- `created_at`: Creation time.

### `project_files`
Represents the current "Live" state of the codebase.
- `id` (UUID, PK): Primary identifier.
- `project_id` (UUID, FK): Links to `projects.id`.
- `file_path` (VARCHAR): Unique path within the project (e.g., `src/App.tsx`).
- `content` (TEXT): The raw source code (stored in-DB for fast context injection).
- `storage_key` (VARCHAR): Nullable; used for binary assets (images, etc.).
- `mime_type` (VARCHAR): File content type.
- `updated_at`, `deleted_at`: Audit fields.
- **Constraint**: Unique on `(project_id, file_path)`.

### `commits`
The snapshot engine for the "Undo" feature.
- `id` (UUID, PK): Primary identifier.
- `project_id` (UUID, FK): Links to `projects.id`.
- `message_id` (UUID, FK): The `chat_messages.id` that triggered this version.
- `snapshot_data` (JSONB): A complete map of `{ filePath: content }` at this point in time.
- `created_at`: Creation time.

### `previews`
Tracks the execution environment for live previews.
- `id` (UUID, PK): Primary identifier.
- `project_id` (UUID, FK): Links to `projects.id`.
- `preview_url` (VARCHAR): URL to access the preview.
- `status` (VARCHAR): `STARTING`, `READY`, `STALLED`, `FAILED`, `EXPIRED`.
- `started_at`, `expires_at`: Timing fields.

## 2. Relationships
- **User 1:N Projects**: A user can own multiple projects.
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
- **Lowercase Roles**: `MessageRole` enum is persisted as lowercase strings to match standard LLM API expectations.
