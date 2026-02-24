CREATE TABLE IF NOT EXISTS task_check_definition
(
    id                 BIGSERIAL    NOT NULL,
    task_definition_id BIGINT       NOT NULL,
    name               VARCHAR(255) NOT NULL,
    severity           VARCHAR(50)  NOT NULL,
    check_type         VARCHAR(50)  NOT NULL,
    check_settings     JSONB        NOT NULL,
    has_evidence       BOOLEAN      NOT NULL DEFAULT false,
    has_comment        BOOLEAN      NOT NULL DEFAULT false,
    customer_id        BIGINT       NOT NULL,
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ,
    deleted_at         TIMESTAMPTZ,
    CONSTRAINT pk_task_check_definition PRIMARY KEY (id),
    CONSTRAINT fk_task_check_definition_task_definition
        FOREIGN KEY (task_definition_id) REFERENCES task_definition (id) ON DELETE RESTRICT
);

CREATE INDEX IF NOT EXISTS idx_task_check_definition_task_definition_id ON task_check_definition (task_definition_id);
CREATE INDEX IF NOT EXISTS idx_task_check_definition_customer_id ON task_check_definition (customer_id);
