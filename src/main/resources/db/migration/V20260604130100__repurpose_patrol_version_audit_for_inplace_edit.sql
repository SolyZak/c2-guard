-- Patrol edit no longer creates a new patrol version, so an audit row now
-- references a single patrol (edited in place) rather than a previous->new
-- pair. Repurpose patrol_version_audit:
--   * previous_patrol_id / new_patrol_id become nullable (legacy rows keep
--     their values; in-place edits leave them NULL).
--   * patrol_id  -> the patrol that was edited.
--   * changes    -> human-readable field-level delta (name/frequency changed,
--                   tasks/locations added/removed). before_snapshot /
--                   after_snapshot still hold the full definitions.

ALTER TABLE patrol_version_audit
    ALTER COLUMN previous_patrol_id DROP NOT NULL,
    ALTER COLUMN new_patrol_id      DROP NOT NULL;

ALTER TABLE patrol_version_audit
    ADD COLUMN patrol_id BIGINT,
    ADD COLUMN changes   JSONB NOT NULL DEFAULT '{}'::jsonb;

-- Backfill patrol_id for any pre-existing (copy-on-edit era) rows so the
-- column is meaningful across the whole table.
UPDATE patrol_version_audit
SET patrol_id = new_patrol_id
WHERE patrol_id IS NULL;

CREATE INDEX idx_pva_patrol_id ON patrol_version_audit (patrol_id);
