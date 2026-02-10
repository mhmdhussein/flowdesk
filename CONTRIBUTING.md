# Contributing to FlowDesk

Thank you for your interest in contributing to FlowDesk 🎉

This project is currently focused on learning, experimentation, and clean backend architecture. Contributions are welcome as long as they align with the project goals and keep the codebase simple and maintainable.

---

## Development Setup

### Prerequisites
- Java 17
- Docker & Docker Compose
- Maven (or use the provided Maven Wrapper)

### Running the application
```bash
./mvnw spring-boot:run
````

Or using Docker:

```bash
docker compose up --build
```

Swagger UI will be available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Code Style & Guidelines

* Use **DTOs** for all API input/output
* Do not expose JPA entities directly from controllers
* Keep controllers thin, push logic into services
* Use Flyway for **all** database changes
* Maintain consistent API error responses
* Prefer explicit code over clever abstractions

---

## Branching & Commits

* Use descriptive branch names:

  ```
  feature/ticket-comments
  fix/project-authorization
  ```
* Write clear commit messages:

  ```
  Add soft delete support for tickets
  Fix project membership authorization
  ```

---

## Testing

Integration tests are intentionally minimal for this MVP.
If adding tests:

* Prefer integration tests over mocks
* Use Testcontainers when possible
* Keep tests readable and focused

---

## Submitting Changes

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Open a pull request with a clear description

---

## Questions or Ideas?

Feel free to open an issue to discuss improvements or ideas before implementing them.

Happy coding 🚀
