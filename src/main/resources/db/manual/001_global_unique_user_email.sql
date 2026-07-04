-- Manual migration for existing databases upgrading to globally unique user emails.
-- Run once against your MySQL database before restarting the application.
--
-- 1) Resolve duplicate emails (same email on multiple tenants) before applying the unique index.
--    Example audit query:
--      SELECT email, COUNT(*) AS cnt FROM users GROUP BY email HAVING cnt > 1;
--
-- 2) Normalize stored emails to lowercase (recommended):
--      UPDATE users SET email = LOWER(TRIM(email));

ALTER TABLE users DROP INDEX uq_user_email_per_tenant;

ALTER TABLE users ADD CONSTRAINT uq_user_email UNIQUE (email);
