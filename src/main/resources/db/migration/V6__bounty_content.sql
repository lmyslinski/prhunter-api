ALTER TABLE "bounty" RENAME COLUMN body TO problem_statement;
ALTER TABLE "bounty" ADD COLUMN acceptance_criteria VARCHAR NOT NULL DEFAULT '';
