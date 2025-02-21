CREATE TABLE users
(
    id                 VARCHAR(255) PRIMARY KEY,
    email              VARCHAR(255) UNIQUE NOT NULL,
    password_hash      VARCHAR(255),
    google_id          VARCHAR(255),
    totp_secret        VARCHAR(255),
    two_factor_enabled BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_google_id ON users (google_id);