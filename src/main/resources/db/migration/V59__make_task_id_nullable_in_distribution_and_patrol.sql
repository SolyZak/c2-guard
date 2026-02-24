-- V59: Make task_id nullable in task_distribution and patrol_detail.
--
-- WHY THIS EXISTS:
--   V43 set task_id NOT NULL on task_distribution.
--   V56 already contains this same DROP NOT NULL, but V56 also adds
--   REFERENCES task_definition(id) which requires the task_management tables
--   (V52–V55) to be applied first. If those are pending, V56 blocks and
--   task_id stays NOT NULL, preventing the new taskDefinitionId path from working.
--
--   This migration fixes the blocker independently — it has zero dependencies
--   on any other table. It is safe to run even if V56/V57 already applied
--   the same change (DROP NOT NULL on an already-nullable column is a no-op
--   in PostgreSQL).
--
-- WHAT THIS DOES:
--   Allows task_distribution.task_id and patrol_detail.task_id to be NULL so
--   that new records created via the taskDefinitionId path do not require a
--   legacy task reference.

-- task_distribution: task_id must accept NULL for new-path distributions
ALTER TABLE task_distribution
    ALTER COLUMN task_id DROP NOT NULL;

-- patrol_detail: task_id must accept NULL for new-path patrol details
ALTER TABLE patrol_detail
    ALTER COLUMN task_id DROP NOT NULL;
