# Coding Standards

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

## Controller Parameter Ordering

Always structure controller handler parameters in a predictable hierarchy from business inputs to system infrastructure:

1. **Path Variables (`@PathVariable`):** Establish the resource scope mirroring the URI structure.
2. **Payloads (`@Valid @RequestBody`):** Direct incoming request bodies.
   * *Critical Spring Rule:* If a `BindingResult` or `Errors` argument is used, it **must** immediately follow the object it validates.
3. **Filtering & Query (`@RequestParam`, `Pageable`):** Fixed query parameters or paging parameters.
4. **System & Infrastructure Contexts:** Injected utilities (e.g. `@RequestHeader`, `@AuthenticationPrincipal`, `HttpServletRequest`, `HttpServletResponse`) positioned at the very end.
