-- Patrol edit is now in-place (no copy-on-edit versioning). Drop the
-- versioning scaffolding added in V85: the per-chain "current version"
-- unique index, the previous-version FK + index, the valid-window index,
-- and the valid_from / valid_to / previous_patrol_id columns themselves.
-- History of edits is captured in patrol_version_audit instead.

DROP INDEX IF EXISTS uniq_current_patrol_per_chain;
DROP INDEX IF EXISTS idx_patrol_previous_id;
DROP INDEX IF EXISTS idx_patrol_valid_window;

ALTER TABLE patrol
    DROP CONSTRAINT IF EXISTS fk_patrol_previous;

ALTER TABLE patrol
    DROP COLUMN IF EXISTS previous_patrol_id,
    DROP COLUMN IF EXISTS valid_to,
    DROP COLUMN IF EXISTS valid_from;
