# Codegen API

Backend API for the AI-driven code generation platform.

## Technology Stack
* **Java 21**
* **Spring Boot 3.5.x**
* **PostgreSQL**
* **Spring Data JPA**
* **Flyway** (Database migrations)
* **MapStruct** & **Lombok** (DTO mapping and boilerplate generation)
* **Spring Security & JJWT** (Stateless JWT authentication)

## Local Development via CLI

The project includes the Maven Wrapper (`mvnw` for Linux/macOS and `mvnw.cmd` for Windows). This allows you to build, compile, and run the project from any terminal without needing a local installation of Maven.

### 1. Compile and Verify the Project
Runs a clean build and compiles the source code to verify there are no compilation errors:
```bash
# On Linux/macOS or Git Bash/PowerShell:
./mvnw clean compile

# On Windows Command Prompt:
mvnw.cmd clean compile
```

### 2. Run the Application locally
Starts the Spring Boot embedded Tomcat server in development mode:
```bash
# On Linux/macOS or Git Bash/PowerShell:
./mvnw spring-boot:run

# On Windows Command Prompt:
mvnw.cmd spring-boot:run
```

### 3. Database Migration & Management (Flyway)
To clean, migrate, or repair your database schema locally:
```bash
# On Linux/macOS or Git Bash/PowerShell:
./mvnw flyway:clean    # WARNING: NEVER run in production! (Drops all tables and data)
./mvnw flyway:migrate  # Runs all pending migration scripts
./mvnw flyway:repair   # Fixes schema history checksum issues

# On Windows Command Prompt:
mvnw.cmd flyway:clean  # WARNING: NEVER run in production! (Drops all tables and data)
mvnw.cmd flyway:migrate
mvnw.cmd flyway:repair
```

### 4. Run Unit and Integration Tests
Executes the test suite:
```bash
# On Linux/macOS or Git Bash/PowerShell:
./mvnw test

# On Windows Command Prompt:
mvnw.cmd test
```

### 4. Package the Application
Builds the production-ready runnable JAR artifact in the `target/` directory:
```bash
# On Linux/macOS or Git Bash/PowerShell:
./mvnw clean package

# On Windows Command Prompt:
mvnw.cmd clean package
```

### 5. Run the Packaged JAR
Once packaged, you can run the application directly using standard Java:
```bash
java -jar target/codegen-api-0.0.1-SNAPSHOT.jar
```

## Code Quality & Formatting

To maintain a consistent style and catch potential bugs early, the project utilizes:
* **Spotless** (with Google Java Format) for auto-formatting
* **Spotbugs** for static code analysis

### Running Checks Manually
```bash
# Auto-format all Java files
./mvnw spotless:apply

# Run Spotbugs static analysis
./mvnw spotbugs:check
```

## Pre-push Validation

Before pushing code or submitting a PR, it is recommended to run all quality and test checks. You can run them all in a single command:

```bash
# On Linux/macOS or Git Bash/PowerShell:
./mvnw clean spotless:apply test spotbugs:check

# On Windows Command Prompt:
mvnw.cmd clean spotless:apply test spotbugs:check
```

## Documentation
* [API Design](docs/api.md) — REST endpoints overview
* [Auth Design](docs/auth.md) — Token delivery model and security
* [Schema Design](docs/schema.md) — Database schema and entity relationships
* [Postman Testing Setup](docs/postman.md) — Authentication flows and environment setup
* [Coding Standards](docs/rules/coding-standards.md) — Layer boundaries, parameter ordering, and DTO validation
* [Persistence Rules](docs/rules/persistence.md) — Database migrations, mapping conventions, and transactional guidelines
