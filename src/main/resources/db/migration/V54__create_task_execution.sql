CREATE TABLE IF NOT EXISTS task_execution
(
    id           BIGSERIAL   NOT NULL,
    workforce_id BIGINT      NOT NULL,
    customer_id  BIGINT      NOT NULL,
    created_at   TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT pk_task_execution PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_task_execution_workforce_id ON task_execution (workforce_id);
CREATE INDEX IF NOT EXISTS idx_task_execution_customer_id ON task_execution (customer_id);
