create table "github_user"
(
    id                   BIGINT    NOT NULL PRIMARY KEY,
    login                VARCHAR   NOT NULL UNIQUE,
    email                VARCHAR,
    full_name            VARCHAR,
    access_token         VARCHAR,
    github_registered_at TIMESTAMP NOT NULL,
    registered_at        TIMESTAMP NOT NULL DEFAULT NOW()
);
