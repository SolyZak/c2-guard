-- Make task_definition_id mandatory (all rows already populated)
ALTER TABLE patrol_detail ALTER COLUMN task_definition_id SET NOT NULL;

-- Drop legacy FK column and its index
DROP INDEX IF EXISTS idx_patrol_detail_task_id;
ALTER TABLE patrol_detail DROP COLUMN IF EXISTS task_id;
