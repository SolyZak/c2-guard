-- Drop the old legacy FK column (points to task_patrol_execution)
ALTER TABLE task_execution_slot DROP COLUMN IF EXISTS task_execution_id;

-- Rename new column to the canonical name
ALTER TABLE task_execution_slot RENAME COLUMN new_task_execution_id TO task_execution_id;

-- Update indexes
DROP INDEX IF EXISTS idx_task_execution_slot_new_task_execution_id;
DROP INDEX IF EXISTS idx_task_execution_slot_task_execution_id;
CREATE INDEX idx_task_execution_slot_task_execution_id
    ON task_execution_slot(task_execution_id);
