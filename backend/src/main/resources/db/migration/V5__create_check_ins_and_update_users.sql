ALTER TABLE users ADD COLUMN github_access_token VARCHAR(255);
CREATE TABLE check_ins (
    id UUID PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    image_url VARCHAR(255),
    created_at TIMESTAMP,
    user_id UUID REFERENCES users(id),
    group_id UUID REFERENCES groups(id)
);

CREATE TABLE group_memberships (
    id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(id),
    group_id UUID REFERENCES groups(id),
    role VARCHAR(50),
    joined_at TIMESTAMP
);