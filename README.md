# HMCTS Task Management Backend

This repository contains the backend for the HMCTS case management system. It is built with Java and Gradle, following a clean architecture with the Repository pattern for data access, robust entity management, and comprehensive unit testing.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Repository Structure](#repository-structure)
3. [Repository Pattern & Entity Management](#repository-pattern--entity-management)
4. [Quality Assurance](#quality-assurance)

---

## Getting Started

To get started with the project, follow these steps:

**Build the project:**
   ```bash
   ./gradlew build
   ```

**Spin up containerised DB:**
   ```bash
    docker run --name devdb-postgres \
      -e POSTGRES_DB=devdb \
      -e POSTGRES_USER=devuser \
      -e POSTGRES_PASSWORD=devpass \
      -p 5432:5432 \
      -d postgres
   ```

### Repository Structure

```
hmcts-dev-test-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── uk/
│   │   │       └── gov/
│   │   │           └── hmcts/
│   │   │               └── devtest/
│   │   │                   ├── entity/         # JPA entities representing domain models
│   │   │                   ├── repository/     # Repository interfaces and implementations
│   │   │                   ├── service/        # Business logic and service layer
│   │   │                   ├── controller/     # REST API controllers
│   │   │                   └── DevTestApplication.java # Main entry point
│   │   └── resources/
│   │       └── application.properties          # Application configuration
│   ├── test/
│   │   └── java/
│   │       └── uk/
│   │           └── gov/
│   │               └── hmcts/
│   │                   └── devtest/
│   │                       ├── entity/         # Entity unit tests
│   │                       ├── repository/     # Repository unit tests
│   │                       ├── service/        # Service layer unit tests
│   │                       └── controller/     # Controller (API) unit tests
│   ├── integration/
│   │   └── java/
│   │       └── uk/
│   │           └── gov/
│   │               └── hmcts/
│   │                   └── devtest/
│   │                       └── integration/    # Integration tests
├── build.gradle
└── README.md
```

### Repository Pattern & Entity Management
- Repository Pattern:
Data access is abstracted using the Repository pattern. Interfaces in repository/ define CRUD operations, typically extending Spring Data JPA repositories. Custom queries and logic are implemented in repository classes.

- Entity Management:
Domain entities are defined in entity/ using JPA annotations. Relationships (e.g., @OneToMany, @ManyToOne) are used to model associations. Entities are managed by the persistence context, ensuring consistency and transactional integrity.

### Security

The backend implements robust security measures to protect user data and application integrity:

**Authorization via Auth Token:**
Users must log in to receive an authentication token. This token is required in the Authorization header for all task-related API requests. The backend validates this header to ensure only authenticated users can access or modify tasks.

**SQL Injection Protection:**
All database operations use Spring Data JPA repositories, which leverage parameterized queries to prevent SQL injection attacks.

**Cross-Site Scripting (XSS) Protection:**
User input is sanitized using a static utility class before being persisted or returned in API responses. This ensures that malicious scripts cannot be injected or executed via stored data.

These measures help ensure that only authorized users can interact with the system and that user input does not compromise application security.

### Quality Assurance
The repository includes unit tests for entities, repositories, services, and controllers to ensure reliability and maintainability.

**Unit Tests**
- Location: src/test/java/uk/gov/hmcts/devtest/
- Purpose: Test individual components (entities, repositories, services, controllers) in isolation.
- Framework: JUnit 5, Mockito
- Run Command:
  ```bash
  ./gradlew test
  ```

**Integration/Functional Tests**
- Location: src/integration/java/uk/gov/hmcts/devtest/integration/
- Purpose: Test the full stack (controller, service, repository, entity) including database interactions.
- Database: H2 in-memory database for isolated test runs.
- Framework: JUnit 5, Spring Boot Test
- Run Command:
  ```bash
  ./gradlew integrationTest

**Code Quality**

Tools like Checkstyle and Sonarqube are integrated for code quality checks.

