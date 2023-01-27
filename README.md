# tc-local-dev
Spring Boot application connected to a Postgres database. Configured for local development with [Testcontainers](https://www.testcontainers.org/) as explained in [this blog post](https://bsideup.github.io/posts/local_development_with_testcontainers/).

- No need to install Postgres
- No need to install Docker

## Requirements
- [Testcontainers Cloud](https://www.testcontainers.cloud/) or Docker
- Java 17+

## Running the project
- Import project into your IDE
- Run `com.example.tclocaldev.run.TestApplication`