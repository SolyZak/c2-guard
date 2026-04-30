CREATE TABLE IF NOT EXISTS task_definition
(
    id          BIGSERIAL    NOT NULL,
    name        VARCHAR(255) NOT NULL,
    severity    VARCHAR(50)  NOT NULL,
    customer_id BIGINT       NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ,
    deleted_at  TIMESTAMPTZ,
    CONSTRAINT pk_task_definition PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_task_definition_customer_id ON task_definition (customer_id);
