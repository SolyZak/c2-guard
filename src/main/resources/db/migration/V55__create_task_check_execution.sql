CREATE TABLE IF NOT EXISTS task_check_execution
(
    id                       BIGSERIAL    NOT NULL,
    task_check_definition_id BIGINT       NOT NULL,
    task_execution_id        BIGINT       NOT NULL,
    check_type               VARCHAR(50)  NOT NULL,
    check_values             JSONB        NOT NULL,
    evidence_image_path      VARCHAR(500),
    comment                  VARCHAR(1000),
    customer_id              BIGINT       NOT NULL,
    created_at               TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT pk_task_check_execution PRIMARY KEY (id),
    CONSTRAINT fk_task_check_execution_check_definition
        FOREIGN KEY (task_check_definition_id) REFERENCES task_check_definition (id) ON DELETE RESTRICT,
    CONSTRAINT fk_task_check_execution_task_execution
        FOREIGN KEY (task_execution_id) REFERENCES task_execution (id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_task_check_execution_check_definition_id ON task_check_execution (task_check_definition_id);
CREATE INDEX IF NOT EXISTS idx_task_check_execution_task_execution_id ON task_check_execution (task_execution_id);
CREATE INDEX IF NOT EXISTS idx_task_check_execution_customer_id ON task_check_execution (customer_id);
