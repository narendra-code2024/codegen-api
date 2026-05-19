# Project GEMINI Instructions

## 🎯 Project Overview
- **Name:** codegen-api
- **Goal:** Backend API for an AI-driven code generation platform.
- **Tone:** Professional, direct, and architecture-first.

## 🛠️ Tech Stack
- **Language:** Java 21
- **Framework:** Spring Boot 3.5.x
- **Persistence:** Spring Data JPA (PostgreSQL)
- **Migrations:** Flyway (Strictly required for all schema changes)
- **Build Tool:** Maven (`./mvnw`)

## 🏛️ Architecture & Patterns
- **Standard Layers:** Controller -> Service -> Repository -> Entity.
- **DTOs:** Mandatory for request/response. Never expose Entities directly.
- **Identity:** Use `UUID` for Primary Keys (`@GeneratedValue(strategy = GenerationType.UUID)`).
- **Auditing:** Include `@CreationTimestamp` and `@UpdateTimestamp` only for core business entities (e.g., `Project`, `User`, `Commit`). Simple lookup or metadata tables may omit these.
- **Soft Delete:** Use Hibernate 6.4+ `@SoftDelete` where history retention is critical.

## 📏 Coding Standards & JPA Best Practices
- **Lombok on Entities:**
  - Use `@Getter` and `@Setter` at the class level.
  - Use `@NoArgsConstructor` to satisfy JPA requirements.
  - **AVOID:** `@Data`, `@EqualsAndHashCode`, and `@ToString`. These trigger lazy-loading of all associations and cause circular reference/recursion bugs.
  - **Manual Equals/HashCode:** Implement `equals` and `hashCode` using only the stable Primary Key (ID) or a unique business key.
- **Relationships:**
  - **FetchType:** Default all associations (`@OneToOne`, `@ManyToOne`, `@OneToMany`) to `FetchType.LAZY`.
  - **Collections:** Use `Set` for `@ManyToMany` and `@OneToMany` to avoid Hibernate "bag" performance issues.
  - **Helper Methods:** Provide `addXxx` and `removeXxx` methods in the "parent" entity for bidirectional relationships.
- **Mappings:**
  - **Naming:** Use `@Table(name = "xxx")` and `@Column(name = "xxx")` for explicit snake_case naming.
  - **Enums:** Always use `@Enumerated(EnumType.STRING)`.
  - **JSON:** Use `@JdbcTypeCode(SqlTypes.JSON)` for mapping `Map` or `List` to JSONB columns (native Hibernate 6 support).
- **Validation:** Use Spring Boot Validation (`jakarta.validation` annotations) on DTOs and relevant Entity fields.

## 🚀 AI Behavioral Mandates (IMPORTANT)
1. **Research First:** Always investigate the codebase before proposing changes.
2. **Strategy Before Action:** For any modification, provide a clear **Plan** and wait for user approval or a clear "Directive" before editing files.
3. **No Auto-Updates:** Do not modify files during "Inquiry" or "Research" phases unless explicitly instructed.
4. **Schema Discipline:** All database changes must be in `src/main/resources/db/migration/`. Never modify existing migration files; always create a new one.
5. **Validation:** After any change, run `./mvnw clean compile` to verify the build.

## 📂 Key File Locations
- **Entities:** `src/main/java/dev/codegen/api/entity/`
- **Repositories:** `src/main/java/dev/codegen/api/repository/`
- **Services:** `src/main/java/dev/codegen/api/service/`
- **Controllers:** `src/main/java/dev/codegen/api/controller/`
- **DTOs:** `src/main/java/dev/codegen/api/dto/`
- **Enums:** `src/main/java/dev/codegen/api/enums/`
- **Migrations:** `src/main/resources/db/migration/`
- **Resources:** `src/main/resources/`
