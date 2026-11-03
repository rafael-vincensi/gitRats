CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    name VARCHAR(255),
    email VARCHAR(255) UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    avatar_url VARCHAR(255),
    bio TEXT,
    timezone VARCHAR(50),
    github_id VARCHAR(255),
    github_username VARCHAR(255)
);