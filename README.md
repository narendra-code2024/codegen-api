# Codegen API

Backend API for the AI-driven code generation platform.

## Technology Stack
* **Java 21**
* **Spring Boot 3.5.x**
* **PostgreSQL**
* **Spring Data JPA**
* **Flyway** (Database migrations)
* **MapStruct** & **Lombok** (DTO mapping and boilerplate generation)
* **Spring Security & JJWT** (Stateless JWT token authentication)

---

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

### 3. Run Unit and Integration Tests
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

---

## Database Migrations
All database schema changes are managed via Flyway. 
* Migration scripts reside in `src/main/resources/db/migration/`.
* **Important:** Never edit an existing migration script once applied. Always create a new versioned file (e.g. `V2__add_new_columns.sql`) for any schema modifications.

---

## Postman Testing Setup

To automate token injection for authenticated requests in Postman:

### 1. Configure Login Request
In your **`POST /api/auth/login`** request, navigate to the **Tests** tab and add this script:
```javascript
// Parse the JSON response
var responseData = pm.response.json();

// Store the token in environment variables if it exists
if (responseData.accessToken) {
    pm.environment.set("accessToken", responseData.accessToken);
    console.log("Access token successfully stored as 'accessToken'!");
}
```

### 2. Configure Authentication on Protected Requests
For your other API endpoints (like `GET /api/auth/me` or `/api/projects`):
* Navigate to the **Authorization** tab (either at the collection level or for individual requests).
* Select **Type**: `Bearer Token`.
* Set **Token**: `{{accessToken}}`.

### 3. Configure Logout Request
In your **`POST /api/auth/logout`** request, navigate to the **Tests** tab and add this script to clear the token in Postman:
```javascript
// Remove the saved access token on logout
pm.environment.unset("accessToken");
console.log("Logged out: accessToken cleared from environment.");
```

