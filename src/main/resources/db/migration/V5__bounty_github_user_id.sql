alter table "bounty" add column github_user_id BIGINT NOT NULL;
alter table "bounty" add column issue_number BIGINT NOT NULL;
alter table "bounty" add column repo_owner VARCHAR NOT NULL;
alter table "bounty" add column repo_name VARCHAR NOT NULL;
