-- Audit row per US2 (per-service assignment) save. tasks_before / tasks_after
-- capture the full {locationId -> [taskDefId]} snapshot; delta records the
-- atomic add/remove operations for human-readable history queries.

CREATE TABLE patrol_assignment_audit (
    id                BIGSERIAL    PRIMARY KEY,
    edit_session_id   UUID         NOT NULL,
    service_id        BIGINT       NOT NULL,
    patrol_id         BIGINT       NOT NULL,
    service_time_id   BIGINT       NOT NULL,
    site_id           BIGINT       NOT NULL,
    actor_user_id     BIGINT       NOT NULL,
    actor_user_name   VARCHAR(255) NOT NULL,
    occurred_at       TIMESTAMPTZ  NOT NULL DEFAULT now(),
    cutoff_date       DATE         NOT NULL,
    tasks_before      JSONB        NOT NULL,
    tasks_after       JSONB        NOT NULL,
    delta             JSONB        NOT NULL
);

CREATE INDEX idx_paa_assignment_time
    ON patrol_assignment_audit (service_id, patrol_id, service_time_id, site_id, occurred_at DESC);
CREATE INDEX idx_paa_session ON patrol_assignment_audit (edit_session_id);
