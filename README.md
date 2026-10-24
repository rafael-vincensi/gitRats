# GitRats

A social platform for developers to track coding activity, build consistency, and connect with other developers through check-ins, groups, and leaderboards.

## Features

- **Check-ins:** log coding sessions and track daily activity.
- **Streaks:** build consistency by maintaining coding streaks.
- **Groups:** connect with other developers and stay accountable together.
- **Activity feed:** follow recent coding activity from friends and groups.
- **Leaderboards:** compare activity and compete with other developers.
- **Profiles:** showcase coding activity, streaks, and personal statistics.
- **Discovery:** find developers and groups within the community.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot |
| Security | Spring Security, JWT |
| Persistence | Spring Data JPA, Hibernate |
| Database | PostgreSQL |
| Build Tool | Maven |

## Architecture

GitRats uses a RESTful architecture built with Spring Boot, separating application logic, authentication, and persistence.

Spring Security and JWT handle authentication and access control. Spring Data JPA and Hibernate manage relational persistence with PostgreSQL, supporting user accounts, coding activity, groups, and social interactions.

The platform is designed around the idea that coding consistency can be more than an individual habit: it can become a shared experience built through community, accountability, and friendly competition.

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- Maven (or the included Maven Wrapper)

### Running the Application

```bash
./mvnw spring-boot:run
```

### Configuration

Configure the database connection in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/[database_name]
spring.datasource.username=[username]
spring.datasource.password=[password]
```

Provide any additional security and application settings required by your local configuration. Avoid hardcoding secrets in the source code.


---

Built by [rafael-vincensi](https://github.com/rafael-vincensi)
