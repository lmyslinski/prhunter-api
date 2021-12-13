ALTER TABLE "bounty"
    ALTER COLUMN github_user_id TYPE VARCHAR;
ALTER TABLE "bounty"
    rename column github_user_id to firebase_user_id;