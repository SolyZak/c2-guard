-- [TASK-MIGRATION] NEW: links task_execution_slot to the new task_execution table (task_management module).
-- The existing task_execution_id FK points to task_patrol_execution (legacy table).
-- new_task_execution_id points to task_execution (new module).
-- Both coexist: old slots use task_execution_id, new slots use new_task_execution_id.
--
-- CLEANUP (Phase E):
--   1. ALTER TABLE task_execution_slot DROP COLUMN task_execution_id;
--   2. ALTER COLUMN new_task_execution_id RENAME TO task_execution_id;

ALTER TABLE task_execution_slot
    ADD COLUMN new_task_execution_id BIGINT
        REFERENCES task_execution(id);

CREATE INDEX idx_task_execution_slot_new_task_execution_id
    ON task_execution_slot(new_task_execution_id);
