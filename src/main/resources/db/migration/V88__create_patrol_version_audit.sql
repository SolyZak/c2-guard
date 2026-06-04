-- Audit row per US1 (patrol-definition) save. Captures the copy-on-edit pair
-- previous_patrol_id -> new_patrol_id, the full before/after definition
-- snapshots, and the list of services whose schedules were regenerated.

CREATE TABLE patrol_version_audit (
    id                  BIGSERIAL    PRIMARY KEY,
    edit_session_id     UUID         NOT NULL,
    previous_patrol_id  BIGINT       NOT NULL,
    new_patrol_id       BIGINT       NOT NULL,
    customer_id         BIGINT       NOT NULL,
    actor_user_id       BIGINT       NOT NULL,
    actor_user_name     VARCHAR(255) NOT NULL,
    occurred_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    cutoff_date         DATE         NOT NULL,
    before_snapshot     JSONB        NOT NULL,
    after_snapshot      JSONB        NOT NULL,
    affected_services   JSONB        NOT NULL
);

CREATE INDEX idx_pva_customer_time ON patrol_version_audit (customer_id, occurred_at DESC);
CREATE INDEX idx_pva_new_patrol    ON patrol_version_audit (new_patrol_id);
CREATE INDEX idx_pva_session       ON patrol_version_audit (edit_session_id);
