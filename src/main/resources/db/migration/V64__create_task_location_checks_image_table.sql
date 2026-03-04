CREATE SEQUENCE IF NOT EXISTS task_location_checks_image_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE task_location_checks_image (
    id                          BIGINT DEFAULT nextval('task_location_checks_image_seq') PRIMARY KEY,
    task_definition_id          BIGINT NOT NULL,
    location_id                 BIGINT NOT NULL,
    task_check_definition_id    BIGINT NOT NULL,
    customer_id                 BIGINT NOT NULL,
    ref_image                   VARCHAR(500),
    deleted                     BOOLEAN DEFAULT FALSE,
    created_by                  BIGINT,
    modified_by                 BIGINT,
    created_date                TIMESTAMP WITHOUT TIME ZONE,
    modified_date               TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT fk_tlci_task_definition
        FOREIGN KEY (task_definition_id) REFERENCES task_definition (id),
    CONSTRAINT fk_tlci_task_check_definition
        FOREIGN KEY (task_check_definition_id) REFERENCES task_check_definition (id),
    CONSTRAINT fk_tlci_location
        FOREIGN KEY (location_id) REFERENCES location (id),
    CONSTRAINT uq_location_check_definition
        UNIQUE (location_id, task_check_definition_id)
);