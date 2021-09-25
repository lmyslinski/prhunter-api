create table "bounty"
(
    id              SERIAL    NOT NULL PRIMARY KEY,
    repo_id         BIGINT    NOT NULL,
    issue_id        BIGINT    NOT NULL,
    title           VARCHAR   NOT NULL,
    body            VARCHAR   NOT NULL,
    languages       VARCHAR[] NOT NULL,
    bounty_value    DECIMAL   NOT NULL,
    bounty_currency VARCHAR   NOT NULL,
    created_at      TIMESTAMP NOT NULL default now(),
    updated_at      TIMESTAMP NOT NULL default now()
);
