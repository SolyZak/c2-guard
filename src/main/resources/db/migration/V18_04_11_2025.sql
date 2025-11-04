-- ===============================
-- TABLE: task_patrol_execution
-- ===============================
CREATE TABLE task_patrol_execution (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_task_patrol_execution_customer FOREIGN KEY (customer_id)
        REFERENCES customers (id)
);

-- ===============================
-- TABLE: task_check_patrol_execution (BASE CLASS)
-- ===============================
CREATE TABLE task_check_patrol_execution (
    id BIGINT PRIMARY KEY,
    name VARCHAR(255),
    evidence BOOLEAN,
    type VARCHAR(31) NOT NULL, -- discriminator column
    task_id BIGINT,
    CONSTRAINT fk_task_check_task_patrol_exec FOREIGN KEY (task_id)
        REFERENCES task_patrol_execution (id)
);

-- ===============================
-- TABLE: task_check_decimal_patrol_execution
-- ===============================
CREATE TABLE task_check_decimal_patrol_execution (
    id BIGINT PRIMARY KEY,
    unit VARCHAR(255),
    operator VARCHAR(255),
    value DOUBLE PRECISION,
    CONSTRAINT fk_decimal_exec_parent FOREIGN KEY (id)
        REFERENCES task_check_patrol_execution (id)
);

-- ===============================
-- TABLE: task_check_number_patrol_execution
-- ===============================
CREATE TABLE task_check_number_patrol_execution (
    id BIGINT PRIMARY KEY,
    unit VARCHAR(255),
    operator VARCHAR(255),
    value INTEGER,
    CONSTRAINT fk_number_exec_parent FOREIGN KEY (id)
        REFERENCES task_check_patrol_execution (id)
);

-- ===============================
-- TABLE: task_check_text_patrol_execution
-- ===============================
CREATE TABLE task_check_text_patrol_execution (
    id BIGINT PRIMARY KEY,
    notes TEXT,
    CONSTRAINT fk_text_exec_parent FOREIGN KEY (id)
        REFERENCES task_check_patrol_execution (id)
);

-- ===============================
-- TABLE: task_check_list_patrol_execution
-- ===============================
CREATE TABLE task_check_list_patrol_execution (
    id BIGINT PRIMARY KEY,
    -- Since listItems is a List<String>, store as text[] (PostgreSQL array)
    list_items TEXT[],
    CONSTRAINT fk_list_exec_parent FOREIGN KEY (id)
        REFERENCES task_check_patrol_execution (id)
);


ALTER TABLE task_check_patrol_execution
ADD COLUMN image TEXT;
