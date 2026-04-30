CREATE SEQUENCE IF NOT EXISTS task_check_comparison_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE task_check_comparison
(
    id                        BIGINT           NOT NULL DEFAULT nextval('task_check_comparison_id_seq'),
    task_check_definition_id  BIGINT           NOT NULL,
    task_check_execution_id   BIGINT           NOT NULL,
    matching                  BOOLEAN          NOT NULL,
    ratio                     DOUBLE PRECISION,
    created_date              TIMESTAMP        NOT NULL,
    CONSTRAINT pk_task_check_comparison PRIMARY KEY (id),
    CONSTRAINT fk_task_check_comparison_definition FOREIGN KEY (task_check_definition_id) REFERENCES task_check_definition (id),
    CONSTRAINT fk_task_check_comparison_execution FOREIGN KEY (task_check_execution_id) REFERENCES task_check_execution (id)
);