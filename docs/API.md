# API Design Specification

This document outlines the RESTful API endpoints for the AI-Builder backend.

## 1. Authentication (`/api/auth`)

| Endpoint    | Method | Description                               |
|:------------|:-------|:------------------------------------------|
| `/signup`   | `POST` | Create a new user account.                |
| `/login`    | `POST` | Authenticate user and return tokens.      |
| `/refresh`  | `POST` | Refresh access token using refresh token. |
| `/me`       | `GET`  | Get current user profile.                 |
| `/logout`   | `POST` | Logout current user and revoke tokens.    |

> See [AUTH.md](AUTH.md) for the token delivery model and security details.

## 2. Projects (`/api/projects`)

| Endpoint | Method   | Description                                                                     |
|:---------|:---------|:--------------------------------------------------------------------------------|
| `/`      | `GET`    | List all projects owned by the user.                                            |
| `/`      | `POST`   | **Action**: Create new project. *Note: Also bootstraps the first Chat Session.* |
| `/{id}`  | `GET`    | Get metadata for a specific project.                                            |
| `/{id}`  | `PATCH`  | Update project metadata (e.g., rename).                                         |
| `/{id}`  | `DELETE` | Soft-delete a project.                                                          |

## 3. Project Files (`/api/projects/{projectId}/files`)

| Endpoint               | Method | Description                                   |
|:-----------------------|:-------|:----------------------------------------------|
| `/`                    | `GET`  | Fetch the current file tree and full content. |
| `/content?path={path}` | `GET`  | Fetch content of a specific file by its path. |
| `/download`            | `GET`  | Stream the entire project as a ZIP file.      |

> **Why Query Params?** Using `?path=src/app/page.tsx` avoids issues with slashes (`/`) breaking REST routing and is the standard way to handle lookups for deeply nested resources.

## 4. Chat & AI (`/api/projects/{projectId}/chat`)

The frontend interacts with the Project's active chat. The backend handles session management and context isolation internally.

| Endpoint    | Method | Description                                                 |
|:------------|:-------|:------------------------------------------------------------|
| `/messages` | `GET`  | Fetch the current active conversation history.              |
| `/stream`   | `POST` | **Core Endpoint (SSE)**: Send prompt to the active session. |

### `POST /stream` Flow:
1. Receive user prompt.
2. **Backend**: Find/Create the active `ChatSession` for the project.
3. Fetch current code from `project_files`.
4. Construct System Prompt (Rules + Code Context).
5. Call LLM (Gemini/Claude) via **Spring AI**.
6. Stream tokens back to Next.js using **Server-Sent Events (SSE)**.
7. **On Completion**: Parse generated code, update `project_files`, and save a `Commit` snapshot.

## 5. History & Rollback (`/api/projects/{projectId}/history`)

| Endpoint               | Method | Description                                                |
|:-----------------------|:-------|:-----------------------------------------------------------|
| `/commits`             | `GET`  | List all checkpoints (snapshots) for the project.          |
| `/rollback/{commitId}` | `POST` | **Undo**: Revert `project_files` to this snapshot's state. |

## 6. Preview (`/api/projects/{projectId}/preview`)

| Endpoint | Method   | Description                         |
|:---------|:---------|:------------------------------------|
| `/`      | `GET`    | Get current preview URL and status. |
| `/start` | `POST`   | Trigger preview environment boot.   |
| `/stop`  | `DELETE` | Shut down the preview environment.  |

---

## 7. Implementation Tips

### Server-Sent Events (SSE) in Spring Boot
Use `SseEmitter` or return a `Flux<String>` (if using WebFlux).
```java
@PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> chatStream(@PathVariable UUID projectId, @RequestBody String prompt) {
    // Logic to find active session and call Spring AI
}
```

### Type Safety
Use **Swagger/OpenAPI** in Spring Boot to generate documentation, and use **Orval** or **openapi-ts** in Next.js to generate TypeScript hooks.
