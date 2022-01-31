ALTER TABLE "github_token" RENAME TO "user_account";
ALTER TABLE "user_account" ADD COLUMN eth_wallet_address VARCHAR;
ALTER TABLE "user_account" RENAME COLUMN "access_token" to "github_access_token";
