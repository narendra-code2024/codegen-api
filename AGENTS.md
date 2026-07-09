# Agent Guide

Backend API for an AI-driven code generation platform.

- **Stack:** Java 21 · Spring Boot 3.5.x · PostgreSQL · Spring Data JPA · Flyway · Maven
- **Architecture:** Controller → Service → Repository → Entity
- **Tone:** Professional, direct, architecture-first

## Rules (always apply)

@docs/rules/coding-standards.md
@docs/rules/persistence.md

## Agent Guardrails (Workflow)

- **Command Execution:** **Do not run terminal commands** (e.g. Maven compile, formatting, tests) yourself. Always suggest the appropriate command line string to the user so they can run it.
- **Formatting:** Suggest that the user run `./mvnw spotless:apply` to format Java code after you complete edits.
- **Compilation:** Suggest that the user run `./mvnw clean compile` to verify your changes compile successfully.
- **Testing:** Suggest that the user run `./mvnw test` to verify changes did not introduce regressions.
- **Completeness:** Never write placeholder code or `// TODO` comments unless explicitly instructed.
- **Entity Protection:** Never expose JPA entities via HTTP endpoints; always map to DTO records.

## Project Map

| Layer        | Path                                          |
|--------------|-----------------------------------------------|
| Entities     | `src/main/java/dev/codegen/api/entity/`       |
| Repositories | `src/main/java/dev/codegen/api/repository/`   |
| Services     | `src/main/java/dev/codegen/api/service/`      |
| Controllers  | `src/main/java/dev/codegen/api/controller/`   |
| DTOs         | `src/main/java/dev/codegen/api/dto/`          |
| Mappers      | `src/main/java/dev/codegen/api/mapper/`       |
| Security     | `src/main/java/dev/codegen/api/security/`     |
| Exceptions   | `src/main/java/dev/codegen/api/exception/`    |
| Enums        | `src/main/java/dev/codegen/api/enums/`        |
| Migrations   | `src/main/resources/db/migration/`            |
| Resources    | `src/main/resources/`                         |
