ALTER TABLE task_check
ADD COLUMN comment_check BOOLEAN DEFAULT FALSE,
ADD COLUMN comment VARCHAR(500);

ALTER TABLE task_check
ADD CONSTRAINT ck_task_check_comment_check
CHECK (comment_check IN (FALSE, TRUE));

ALTER TABLE task_check
ADD CONSTRAINT ck_task_check_comment_allowed
CHECK (comment_check = TRUE OR comment IS NULL);
