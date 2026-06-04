CREATE SEQUENCE IF NOT EXISTS role_lifecycle_id_seq START 1 INCREMENT 1;

CREATE TABLE role_lifecycle (
    id               BIGINT       PRIMARY KEY DEFAULT nextval('role_lifecycle_id_seq'),
    role_id          INTEGER      NOT NULL REFERENCES role(id),
    user_id          BIGINT       NOT NULL REFERENCES customer_users(id),
    customer_id      BIGINT       NOT NULL REFERENCES customers(id),
    start_date       TIMESTAMP(6) NOT NULL,
    end_date         TIMESTAMP(6) NULL,

    created_by       BIGINT       NULL,
    created_date     TIMESTAMP(6) NULL,
    modified_by      BIGINT       NULL,
    modified_date    TIMESTAMP(6) NULL,
    created_by_user  VARCHAR(255) NULL,
    modified_by_user VARCHAR(255) NULL,
    deleted          BOOLEAN      DEFAULT FALSE
);

CREATE INDEX idx_role_lifecycle_user_open
    ON role_lifecycle(user_id) WHERE end_date IS NULL;
CREATE INDEX idx_role_lifecycle_user     ON role_lifecycle(user_id);
CREATE INDEX idx_role_lifecycle_customer ON role_lifecycle(customer_id);
CREATE INDEX idx_role_lifecycle_role     ON role_lifecycle(role_id);
