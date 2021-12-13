drop table "github_user";
create table github_token
(
    firebase_user_id VARCHAR NOT NULL PRIMARY KEY,
    github_user_id BIGINT NOT NULL,
    access_token VARCHAR NOT NULL
)