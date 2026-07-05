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

## Database Migrations

All database schema changes are managed via **Flyway**.
- **Location:** Migration scripts reside in `src/main/resources/db/migration/`.
- **Rules:**
  - **Important:** Never edit an existing migration script once applied.
  - Always create a new versioned file (e.g. `V2__add_new_columns.sql`) for any schema modifications.
  - Naming pattern: `V<Version>__<description>.sql`.
