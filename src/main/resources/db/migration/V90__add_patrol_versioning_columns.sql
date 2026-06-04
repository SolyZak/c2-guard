-- Patrol versioning: copy-on-edit chain.
-- Each Patrol row is one immutable version. An "edit" inserts a new row that
-- supersedes the previous via previous_patrol_id. Active version per chain has
-- valid_to IS NULL.

ALTER TABLE patrol
    ADD COLUMN valid_from         DATE   NOT NULL DEFAULT DATE '1970-01-01',
    ADD COLUMN valid_to           DATE   NULL,
    ADD COLUMN previous_patrol_id BIGINT NULL;

ALTER TABLE patrol
    ADD CONSTRAINT fk_patrol_previous
        FOREIGN KEY (previous_patrol_id) REFERENCES patrol(id) ON DELETE SET NULL;

-- Exactly one current version per chain (i.e. per root patrol id).
-- A root patrol's chain is identified by COALESCE(previous_patrol_id, id) at the head.
CREATE UNIQUE INDEX uniq_current_patrol_per_chain
    ON patrol ((COALESCE(previous_patrol_id, id)))
    WHERE valid_to IS NULL;

CREATE INDEX idx_patrol_previous_id  ON patrol (previous_patrol_id);
CREATE INDEX idx_patrol_valid_window ON patrol (valid_from, valid_to);
