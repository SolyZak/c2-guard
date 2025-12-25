BEGIN;
-- ------------------------------------------------------------
-- 1) task_check: drop constraints added previously (if exist)
-- ------------------------------------------------------------
ALTER TABLE task_check
  DROP CONSTRAINT IF EXISTS ck_task_check_comment_check;

ALTER TABLE task_check
  DROP CONSTRAINT IF EXISTS ck_task_check_comment_allowed;
-- ------------------------------------------------------------
-- 2) task_check: drop the comment column (definition should not store comment)
ALTER TABLE task_check
  DROP COLUMN IF EXISTS comment;
-- ------------------------------------------------------------

ALTER TABLE task_check_patrol_execution
ADD COLUMN comment_check BOOLEAN DEFAULT FALSE,
ADD COLUMN comment VARCHAR(500);

ALTER TABLE task_check_patrol_execution
  ADD CONSTRAINT ck_task_check_patrol_execution_comment_allowed
  CHECK (comment_check = TRUE OR comment IS NULL);



COMMIT;