create table "installation"
(
    id           BIGINT    NOT NULL PRIMARY KEY,
    account_id   BIGINT    NOT NULL,
    account_type VARCHAR   NOT NULL,
    sender_id    BIGINT    NOT NULL,
    sender_type  VARCHAR   NOT NULL,
    created_at   TIMESTAMP NOT NULL default now()
);
