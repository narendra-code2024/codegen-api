# Architecture & API Rules

## Layers

Standard flow: **Controller → Service → Repository → Entity.** Keep responsibilities in
their own layer; controllers stay thin, business logic lives in services.

## DTOs

- **Mandatory:** Always use DTOs for request/response. Never expose Entities.
- **Implementation:** Prefer Java **Records** for immutable DTOs.
- **Organization:** Group by domain in sub-packages (e.g. `dev.codegen.api.dto.auth`).

### Naming convention

Pattern: `[Subject][Action][Suffix]` — keeps related files grouped alphabetically.

| Kind         | Suffix     | Example                            |
|--------------|------------|------------------------------------|
| Requests     | `Request`  | `LoginRequest`, `ProjectCreateRequest` |
| Responses    | `Response` | `AuthResponse`, `ProjectResponse`  |
| General DTOs | `Dto`      | `FileTreeDto`                      |

## Validation

Use Spring Boot Validation (`jakarta.validation`) on both DTOs and Entity fields.
