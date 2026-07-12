# Transaction Management Rules

This document outlines the rules, boundaries, and read-only optimizations for transactions in the application.

## Transaction Boundaries

- **Service Layer Ownership**: Transaction boundaries belong exclusively on **Service methods** (or Service classes).
  - *Why*: Business logic coordinates multiple database operations. Declaring transactions at the service layer ensures that either all operations succeed together or all roll back.

## Write Operations

- **Use `@Transactional` on all write operations**: Ensure multi-step modifications (e.g., creating a project and a chat session together) are atomic. If any step fails, the entire block is rolled back.

## Read-Only Operations

Always specify `readOnly = true` for read-only queries (either at the class level of a query-heavy service, or on individual read methods).

### Benefits & Mechanics:
- **Performance Optimization (Hibernate Dirty-Checking)**:
  - *Dirty-checking* is the process where Hibernate keeps a copy of the entity and compares it at flush-time to see if anything changed.
  - Specifying `readOnly = true` disables this process, saving significant memory and CPU overhead.
- **Preventing `LazyInitializationException`**:
  - If a service method is not annotated with `@Transactional`, the database session closes immediately after the repository method returns.
  - If you then try to read a property of a lazy-loaded association (like a `@ManyToOne` relationship), you get a `LazyInitializationException`.
  - `@Transactional` keeps the session open for the entire duration of the service method, serving as a safety net.
- **Optimizes Database Locks**: Reduces database locking overhead, improving throughput.
- **Read Replicas**: Allows infrastructure to route queries to read-only database replicas in production environments.
