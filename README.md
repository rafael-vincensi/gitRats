# gitRats

> gitRats is a social accountability and gamified check-in backend for developers. Users can create or join groups of up to 20 members, publish photo-verified coding check-ins, build a global daily streak, and compete on weekly group leaderboards for check-ins and GitHub commits. Images are stored in Cloudinary, and the API is secured with JWT and GitHub OAuth.

## Tech Stack

<div align="center">

[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.x-brightgreen?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Cloudinary](https://img.shields.io/badge/Cloudinary-3448C5?style=for-the-badge&logo=cloudinary&logoColor=white)](https://cloudinary.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)
[![GitHub OAuth](https://img.shields.io/badge/GitHub_OAuth-181717?style=for-the-badge&logo=github&logoColor=white)](https://docs.github.com/en/apps/oauth-apps)
[![JUnit 5](https://img.shields.io/badge/JUnit_5-25A162?style=for-the-badge&logo=junit5&logoColor=white)](https://junit.org/junit5/)

</div>

## Features

- User registration, profile management, and GitHub account linking

- JWT-based authentication and GitHub OAuth login

- Create and join groups via invite code (capped at 20 members per group)

- Publish check-ins with a mandatory image upload, title, and activity description

- Check-in, group, and profile images stored in Cloudinary

- Global daily check-in streak tracking across the platform

- Weekly internal group rankings for total check-ins and GitHub commits

- Unit test suite built with JUnit 5 and Mockito

## Architecture

The application is a REST API organized into controllers, services, repositories, DTOs, and a global exception handler (`@ControllerAdvice`) that returns standardized error payloads (`status`, `message`, and `timestamp`). Images are uploaded to Cloudinary via `multipart/form-data`, while the database stores check-in metadata, image references, global streaks, and group leaderboard data.

## API Endpoints

### Authentication (`/auth`)

| Method | Route | Description |
|--------|-------|-------------|
| `POST` | `/auth/login` | Authenticates a user with email and password and returns a JWT |
| `GET` | `/auth/callback/github` | Handles GitHub OAuth callback (`code` param) and returns a JWT |

### Users (`/users`)

| Method | Route | Description |
|--------|-------|-------------|
| `POST` | `/users` | Create a new user |
| `GET` | `/users/me` | Get the authenticated user's profile and weekly GitHub commits |
| `GET` | `/users/{id}` | Get a user by ID |
| `GET` | `/users/username/{username}` | Get a user by username |
| `PATCH` | `/users` | Update the authenticated user's profile and optional avatar (`multipart/form-data`) |
| `POST` | `/users/{id}/github` | Link a GitHub account to a user |
| `DELETE` | `/users/{id}/github` | Unlink a GitHub account from a user |

### Groups (`/groups`)

| Method | Route | Description |
|--------|-------|-------------|
| `POST` | `/groups` | Create a new group |
| `GET` | `/groups` | List all groups the authenticated user belongs to (with weekly check-in & commit rankings) |
| `POST` | `/groups/{code}/join` | Join an existing group using its invite code (max 20 members) |
| `PATCH` | `/groups/{groupId}` | Update group details and optional cover image (`multipart/form-data`) |
| `DELETE` | `/groups/{groupId}/exit` | Leave a group |

### Check-ins (`/checkin`)

| Method | Route | Description |
|--------|-------|-------------|
| `POST` | `/checkin` | Create a check-in with a required image file, title, and description (`multipart/form-data`) |
| `GET` | `/checkin/feed` | List all check-ins across the authenticated user's groups |
| `GET` | `/checkin/group/{groupId}` | List all check-ins from a specific group |
| `DELETE` | `/checkin/{id}` | Delete a check-in |

## Authentication

Log in through `POST /auth/login` or `GET /auth/callback/github` to receive a JWT. Send it in the `Authorization` header on protected requests:

```http
Authorization: Bearer <token>
```

Tokens expire after 1 hour by default (`jwt.expiration=3600000`).

## Getting Started

### Prerequisites

- Java 21

- Maven (or the included `./mvnw` wrapper)

- PostgreSQL

- A Cloudinary account with API credentials

- A GitHub OAuth App (Client ID, Client Secret, and Redirect URL)

### Environment Variables

The application reads its configuration from environment variables:

| Variable | Description |
|----------|-------------|
| `DATABASE_URL` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/gitrats` |
| `DATABASE_USERNAME` | Database username |
| `DATABASE_PASSWORD` | Database password |
| `JWT_KEY` | Secret used to sign JWTs |
| `GITHUB_CLIENT_ID` | GitHub OAuth App Client ID |
| `GITHUB_SECRET` | GitHub OAuth App Client Secret |
| `GITHUB_REDIRECT_URL` | Callback URL configured in the GitHub OAuth App |
| `CLOUDINARY_NAME` | Cloudinary cloud name |
| `CLOUDINARY_KEY` | Cloudinary API key |
| `CLOUDINARY_SECRET` | Cloudinary API secret |

### Configuration

`application.properties`:

```properties
spring.application.name=gitrats

spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
jwt.secret=${JWT_KEY}
jwt.expiration=3600000

github.client.id=${GITHUB_CLIENT_ID}
github.client.secret=${GITHUB_SECRET}
github.redirect.url=${GITHUB_REDIRECT_URL}

cloudinary.cloud-name=${CLOUDINARY_NAME}
cloudinary.api-key=${CLOUDINARY_KEY}
cloudinary.api-secret=${CLOUDINARY_SECRET}
```

### Run the Application

```bash
./mvnw spring-boot:run
```

### Run Tests

```bash
./mvnw test
```

---

<div align="center">

Built by [rafael-vincensi](https://github.com/rafael-vincensi)

</div>
