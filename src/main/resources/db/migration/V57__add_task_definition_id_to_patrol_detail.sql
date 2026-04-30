-- [TASK-MIGRATION] NEW: task_definition_id for patrol_detail, mirroring V56 for task_distribution.
--
-- CLEANUP (Phase E):
--   1. ALTER TABLE patrol_detail ALTER COLUMN task_definition_id SET NOT NULL;
--   2. ALTER TABLE patrol_detail DROP COLUMN task_id;
--   3. DROP INDEX idx_patrol_detail_task_id;

ALTER TABLE patrol_detail
    ALTER COLUMN task_id DROP NOT NULL;

ALTER TABLE patrol_detail
    ADD COLUMN task_definition_id BIGINT
        REFERENCES task_definition(id);

CREATE INDEX idx_patrol_detail_task_definition_id
    ON patrol_detail(task_definition_id);
