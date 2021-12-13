drop table "github_user";
create table github_token
(
    firebase_user_id VARCHAR NOT NULL PRIMARY KEY,
    access_token VARCHAR NOT NULL
)