-- Users: core identity, no Spotify-specific data here
CREATE TABLE users (
    id          BIGSERIAL PRIMARY KEY,
    email       VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Spotify credentials: separate lifecycle from the user
-- authorized_at stored here because Spotify's token doesn't expose it
-- needed to calculate the 6-month refresh token expiry (see Day 1 notes)
CREATE TABLE spotify_credentials (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    access_token    TEXT NOT NULL,
    refresh_token   TEXT NOT NULL,
    expires_at      TIMESTAMP NOT NULL,
    authorized_at   TIMESTAMP NOT NULL
);

-- Taste profile: derived from Spotify data, stored as JSON for flexibility
CREATE TABLE taste_profiles (
    id              BIGSERIAL PRIMARY KEY,
    user_id         BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    genre_weights   JSONB,
    top_artists     JSONB,
    updated_at      TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Books: cached from Open Library API so we're not hitting it on every request
CREATE TABLE books (
    id              BIGSERIAL PRIMARY KEY,
    open_library_id VARCHAR(50) NOT NULL UNIQUE,
    title           VARCHAR(500) NOT NULL,
    author          VARCHAR(255),
    genres          JSONB,
    cover_url       TEXT
);

-- Recommendations: links a user to a book, records which engine and why
CREATE TABLE recommendations (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    book_id     BIGINT NOT NULL REFERENCES books(id),
    engine      VARCHAR(20) NOT NULL CHECK (engine IN ('RULE_BASED', 'LLM')),
    reasoning   TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);