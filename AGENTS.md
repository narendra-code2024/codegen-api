# codegen-api — Agent Guide

Backend API for an AI-driven code generation platform.

- **Stack:** Java 21 · Spring Boot 3.5.x · PostgreSQL · Spring Data JPA · Flyway · Maven
- **Architecture:** Controller → Service → Repository → Entity
- **Tone:** Professional, direct, architecture-first

## Rules (always apply)

@docs/rules/coding-standards.md
@docs/rules/persistence.md

## Build & Verify

- Compile / verify a change: `./mvnw clean compile`

## Project Map

| Layer        | Path                                          |
|--------------|-----------------------------------------------|
| Entities     | `src/main/java/dev/codegen/api/entity/`       |
| Repositories | `src/main/java/dev/codegen/api/repository/`   |
| Services     | `src/main/java/dev/codegen/api/service/`      |
| Controllers  | `src/main/java/dev/codegen/api/controller/`   |
| DTOs         | `src/main/java/dev/codegen/api/dto/`          |
| Enums        | `src/main/java/dev/codegen/api/enums/`        |
| Migrations   | `src/main/resources/db/migration/`            |
| Resources    | `src/main/resources/`                         |
