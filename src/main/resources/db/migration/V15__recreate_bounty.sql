drop table "bounty";

create table "bounty"
(
    id                  UUID    DEFAULT gen_random_uuid() PRIMARY KEY,
    repo_id             BIGINT                                       NOT NULL,
    issue_id            BIGINT                                       NOT NULL,
    title               VARCHAR                                      NOT NULL,
    acceptance_criteria varchar default ''::character varying        not null,
    problem_statement   varchar default ''::character varying        not null,
    languages           VARCHAR[]                                    NOT NULL,
    bounty_value        DECIMAL                                      NOT NULL,
    bounty_value_usd    numeric default 0.0                          not null,
    bounty_currency     VARCHAR
        NOT NULL,
    tags                character varying[]                          not null,
    experience          varchar                                      not null,
    bounty_type         varchar                                      not null,
    firebase_user_id    varchar                                      not null,
    issue_number        bigint                                       not null,
    repo_owner          varchar                                      not null,
    repo_name           varchar                                      not null,
    bounty_status       varchar default 'PENDING'::character varying not null,
    completed_by        varchar,
    completed_at        timestamp,
    created_at          TIMESTAMP                                    NOT NULL default now()
);
