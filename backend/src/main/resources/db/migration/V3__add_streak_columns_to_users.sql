ALTER TABLE users
    ADD COLUMN current_streak INT DEFAULT 0,
    ADD COLUMN last_check_in DATE,
    ADD COLUMN highest_streak INT DEFAULT 0;