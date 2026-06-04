-- Per-patrol edit lock. One row per patrol being edited. If present and not
-- expired, only the holder can call lock-guarded endpoints for that patrol.
-- Multiple patrols can be edited in parallel by different users.
-- Hard TTL (default 30 min); FE shows iteration banners cosmetically.

CREATE TABLE patrol_edit_lock (
    patrol_id         BIGINT       PRIMARY KEY REFERENCES patrol(id) ON DELETE CASCADE,
    holder_user_id    BIGINT       NOT NULL,
    holder_user_name  VARCHAR(255) NOT NULL,
    acquired_at       TIMESTAMPTZ  NOT NULL,
    expires_at        TIMESTAMPTZ  NOT NULL
);

CREATE INDEX idx_patrol_edit_lock_expires_at ON patrol_edit_lock (expires_at);
