-- Make task_definition_id mandatory (all rows already populated)
ALTER TABLE task_distribution ALTER COLUMN task_definition_id SET NOT NULL;

-- Drop legacy FK column and its index
DROP INDEX IF EXISTS idx_task_distribution_task_id;
ALTER TABLE task_distribution DROP COLUMN IF EXISTS task_id;
