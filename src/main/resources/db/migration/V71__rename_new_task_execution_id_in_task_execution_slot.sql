-- Phase E: rename new_task_execution_id → task_execution_id in task_execution_slot.
-- Idempotent: only runs the rename if new_task_execution_id still exists.
-- Some environments may already have the column renamed manually.
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_name = 'task_execution_slot'
          AND column_name = 'new_task_execution_id'
    ) THEN
        -- Drop the old legacy FK column (points to task_patrol_execution)
        ALTER TABLE task_execution_slot DROP COLUMN IF EXISTS task_execution_id;
        -- Rename the new-module column to the canonical name
        ALTER TABLE task_execution_slot RENAME COLUMN new_task_execution_id TO task_execution_id;
        -- Re-create index with canonical name
        DROP INDEX IF EXISTS idx_task_execution_slot_new_task_execution_id;
        CREATE INDEX IF NOT EXISTS idx_task_execution_slot_task_execution_id
            ON task_execution_slot(task_execution_id);
    END IF;
END $$;
