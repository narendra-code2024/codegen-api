# Coding Standards

## Architecture & Component Design

### Layer Responsibility
Standard flow: **Controller → Service → Repository → Entity.** Keep responsibilities in their own layer; controllers stay thin, business logic lives in services.

### Controller-Service Boundaries
- **Rule**: Controllers must never query or instantiate database entities to pass them to other services.
- **Practice**: Controllers should remain thin, passing only path variables (e.g., `UUID projectId`) and request DTOs directly down to the service layer.

### Service Coupling & Circular Dependency Prevention
- **Rule**: A service must **never** inject a foreign repository. All repository access must be encapsulated within its own service.
- **Circular Dependency Prevention Options**:
  1. **Hierarchical Domain Flow (DDD Aggregate Roots)**: Design services around Aggregate Roots (e.g., `Project` which can exist independently) and Child Entities (e.g., `ProjectMember`). Injection flows strictly from Parent Service to Child Service. Child services must **never** inject parent services; pass necessary parent info as method parameters instead.
  2. **Facade / Orchestrator Services**: If two services are co-dependent or have complex coordination coupling, extract the workflow logic into a higher-level Facade or Orchestrator service that coordination-calls both, breaking the circular dependency loop.
  3. **Event-Driven Decoupling**: Publish Spring Application Events (e.g., `ProjectCreatedEvent`) from the primary service. Other services listen to these events and run their logic independently, eliminating direct service-to-service coupling.

## API & Controller Layer

### Controller Parameter Ordering
Always structure controller handler parameters in a predictable hierarchy from business inputs to system infrastructure:
1. **Path Variables (`@PathVariable`):** Establish the resource scope mirroring the URI structure.
2. **Payloads (`@Valid @RequestBody`):** Direct incoming request bodies.
   * *Critical Spring Rule:* If a `BindingResult` or `Errors` argument is used, it **must** immediately follow the object it validates.
3. **Filtering & Query (`@RequestParam`, `Pageable`):** Fixed query parameters or paging parameters.
4. **System & Infrastructure Contexts:** Injected utilities (e.g. `@RequestHeader`, `@AuthenticationPrincipal`, `HttpServletRequest`, `HttpServletResponse`) positioned at the very end.

### Service-Level Method Security

- **Practice**: API access control must be managed declaratively at the Service layer (rather than the Controller layer) using Spring Security's expression-based annotations (e.g., `@PreAuthorize("@projectSecurity.isOwner(#id)")`).
- **Rationale**: Enforcing security at the Service layer acts as an iron-clad gateway that protects against Insecure Direct Object References (IDOR), ensuring that all service-to-service calls and any future controller extensions are automatically secured without duplication.
- **Security**: The custom security evaluator bean should throw `ResourceNotFoundException` instead of returning `false` on unauthorized access to prevent resource existence disclosure (returning a clean 404 instead of 403).

## Data Transfer Objects (DTOs) & Validation

### DTO Rules
- **Mandatory**: Always use DTOs for request/response. Never expose Entities.
- **Implementation**: Prefer Java **Records** for immutable DTOs.
- **Organization**: Group by domain in sub-packages (e.g. `dev.codegen.api.dto.auth`).

### Naming Conventions
Pattern: `[Subject][Action][Suffix]` — keeps related files grouped alphabetically.

| Kind         | Suffix     | Example                            |
|--------------|------------|------------------------------------|
| Requests     | `Request`  | `LoginRequest`, `ProjectCreateRequest` |
| Responses    | `Response` | `AuthResponse`, `ProjectResponse`  |
| General DTOs | `Dto`      | `FileTreeDto`                      |

### Request Validation
- **Scope**: Request DTO validation applies primarily to **Create (POST) and Update (PUT/PATCH)** APIs that accept payloads. Read-only (`GET`) and simple delete (`DELETE`) requests generally rely on path variables or query parameters instead.
- **Request DTOs**: Always apply Spring Boot Validation (`jakarta.validation`) constraints (e.g., `@NotBlank`, `@NotNull`, `@Size`) to fields on all request DTO classes.
- **Controller Enforcement**: Every request payload parameter in Controller endpoints (e.g., `@RequestBody`) **must** be prefixed with `@Valid` to trigger request validation.
