# Data & Persistence Rules

## Database Migrations

All database schema changes are managed via **Flyway**.
- **Location:** Migration scripts reside in `src/main/resources/db/migration/`.
- **Rules:**
  - **Important:** Never edit an existing migration script once applied.
  - Always create a new versioned file (e.g. `V2__add_new_columns.sql`) for any schema modifications.
  - Naming pattern: `V<Version>__<description>.sql`.

## Entity Modeling & Lombok

- **Use:** `@Getter`, `@Setter`, `@NoArgsConstructor`.
- **Avoid:** `@Data`, `@EqualsAndHashCode`, `@ToString` — they trigger lazy-loading bugs.
- **equals/hashCode:** Implement manually using only the stable primary key (ID).

## JPA Entity Conventions

- **Primary keys:** `UUID` via `@GeneratedValue(strategy = GenerationType.UUID)`.
- **Migrations as Source of Truth**: Database migrations (Flyway) are the sole source of truth for all schema constraints (`NOT NULL`, `UNIQUE`, `FOREIGN KEY`, `CHECK`, indexes).
- **Nullability in Entities**: Define `nullable = false` selectively on `@Column` and `@JoinColumn` mappings for core business-critical fields (e.g., names, credentials, keys) and relationships that represent absolute requirements for the object to exist.
  - *Why*:
    - **Fail-Fast Validation**: Hibernate throws a clear `PropertyValueException` (identifying the exact class and field name) before flush, instead of a database driver constraint violation.
    - **Self-Documenting Code**: Developers immediately see which core properties and relationships are mandatory without inspecting migrations.
    - **Startup Validation**: Paired with `spring.jpa.hibernate.ddl-auto=validate`, any mapping drift is caught immediately at boot time.
  - *Avoid*: Do not mechanically add `nullable = false` to every single database column; reserve it for critical fields to minimize maintenance overhead.
- **Avoid Uniqueness in Entities**: Do not define `@Column(unique = true)` or `@Table(uniqueConstraints = ...)` on entities. Let database migrations handle uniqueness constraints to prevent duplicate definitions and drift.
- **Naming:** Explicit snake_case via `@Table(name = "...")` and `@Column(name = "...")`.
- **FetchType:** Default all associations to `FetchType.LAZY`.
- **Collections:** Use `Set` for `@ManyToMany` and `@OneToMany`.
- **Types:** `@Enumerated(EnumType.STRING)` for enums; `@JdbcTypeCode(SqlTypes.JSON)` for JSONB.
- **Auditing:** `@CreationTimestamp` and `@UpdateTimestamp` on core business entities (e.g. `Project`, `User`).
- **Soft delete:** Use Hibernate 6.4+ `@SoftDelete` where history retention is critical.
- **Entity Associations vs. Raw IDs**: In standard monolithic production applications, always prefer mapping associations as actual entity relationships (e.g., `@ManyToOne` or `@OneToOne` mappings like `@ManyToOne User user`) rather than raw ID columns (e.g., `UUID userId`) when the referenced entity resides in the same database/persistence unit.
  - *When to use raw IDs*: Only use flat ID fields (e.g., `userId`) instead of entity relationships if you are building microservices where you don't own the referenced entity, the entity resides in another database/service, or you intentionally want to bypass ORM relationships.
  - *Why*: It keeps the monolithic domain model clean, supports native object-oriented navigation, and maintains proper relational integrity.
- **Efficient Reference Fetching**: When creating relationships or performing writes where only the foreign key ID is available, always use `repository.getReferenceById(id)` to obtain a lightweight lazy proxy and avoid redundant database `SELECT` queries.
