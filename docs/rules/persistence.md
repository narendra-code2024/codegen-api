# Data & Persistence Rules

## Identity & auditing

- **Primary keys:** `UUID` via `@GeneratedValue(strategy = GenerationType.UUID)`.
- **Auditing:** `@CreationTimestamp` and `@UpdateTimestamp` on core business entities
  (e.g. `Project`, `User`).
- **Soft delete:** Use Hibernate 6.4+ `@SoftDelete` where history retention is critical.

## Lombok on entities

- **Use:** `@Getter`, `@Setter`, `@NoArgsConstructor`.
- **Avoid:** `@Data`, `@EqualsAndHashCode`, `@ToString` — they trigger lazy-loading bugs.
- **equals/hashCode:** Implement manually using only the stable primary key (ID).

## JPA best practices

- **FetchType:** Default all associations to `FetchType.LAZY`.
- **Collections:** Use `Set` for `@ManyToMany` and `@OneToMany`.
- **Naming:** Explicit snake_case via `@Table(name = "...")` and `@Column(name = "...")`.
- **Types:** `@Enumerated(EnumType.STRING)` for enums; `@JdbcTypeCode(SqlTypes.JSON)` for JSONB.

## Transaction Management

- **Use `@Transactional` on all write operations:** Ensure multi-step modifications (e.g., creating a project and a chat session together) are atomic. If any step fails, the entire block is rolled back.
- **Always specify `readOnly = true` for read-only queries:**
  - Disables Hibernate dirty checking (saving memory and CPU).
  - Optimizes database execution and locks.
  - Allows routing to read-only database replicas in production.

## Database Migrations

All database schema changes are managed via **Flyway**.
- **Location:** Migration scripts reside in `src/main/resources/db/migration/`.
- **Rules:**
  - **Important:** Never edit an existing migration script once applied.
  - Always create a new versioned file (e.g. `V2__add_new_columns.sql`) for any schema modifications.
  - Naming pattern: `V<Version>__<description>.sql`.
