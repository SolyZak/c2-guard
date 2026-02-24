-- [TASK-MIGRATION] NEW: task_definition_id column for task_management module integration.
--
-- Strategy: dual-column coexistence.
--   - task_id is made nullable so new records (via taskDefinitionId path) don't require a legacy task.
--   - task_definition_id is nullable so old records (with task_id) continue to work unchanged.
--   - Both columns are populated exclusively: old path sets task_id, new path sets task_definition_id.
--
-- CLEANUP (Phase E) — run only after task_definition_id has zero NULLs in production:
--   1. ALTER TABLE task_distribution ALTER COLUMN task_definition_id SET NOT NULL;
--   2. ALTER TABLE task_distribution DROP COLUMN task_id;
--   3. DROP INDEX idx_task_distribution_task_id;

-- Make task_id nullable to allow new records that use taskDefinitionId instead.
ALTER TABLE task_distribution
    ALTER COLUMN task_id DROP NOT NULL;

-- Add task_definition_id as a nullable FK to the new task_management module table.
ALTER TABLE task_distribution
    ADD COLUMN task_definition_id BIGINT
        REFERENCES task_definition(id);

CREATE INDEX idx_task_distribution_task_definition_id
    ON task_distribution(task_definition_id);
