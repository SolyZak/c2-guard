-- ============================================================
-- V79: Migrate task_id → task_definition_id in
--      contract_operation_distribution_site_patrol
-- ============================================================

-- 1. Drop any FK constraint referencing the old task table
DO
$$
DECLARE
    r RECORD;
BEGIN
    FOR r IN (
        SELECT con.conname AS constraint_name
        FROM pg_constraint con
        JOIN pg_class rel ON rel.oid = con.conrelid
        JOIN pg_attribute att ON att.attrelid = con.conrelid
            AND att.attnum = ANY(con.conkey)
        WHERE rel.relname = 'contract_operation_distribution_site_patrol'
          AND con.contype = 'f'
          AND att.attname = 'task_id'
    )
    LOOP
        EXECUTE format(
            'ALTER TABLE contract_operation_distribution_site_patrol '
            || 'DROP CONSTRAINT %I',
            r.constraint_name
        );
    END LOOP;
END
$$
;

-- 2. Drop the old task_id column
ALTER TABLE contract_operation_distribution_site_patrol
    DROP COLUMN IF EXISTS task_id;

-- 3. Add new task_definition_id column
ALTER TABLE contract_operation_distribution_site_patrol
    ADD COLUMN task_definition_id BIGINT;

-- 4. Add FK to the new task_definition table
ALTER TABLE contract_operation_distribution_site_patrol
    ADD CONSTRAINT fk_patrol_dist_task_definition
    FOREIGN KEY (task_definition_id)
    REFERENCES task_definition(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

-- 5. Add index for performance
CREATE INDEX idx_patrol_dist_task_definition_id
    ON contract_operation_distribution_site_patrol(task_definition_id);