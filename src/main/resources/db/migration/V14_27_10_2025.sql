-- Sequence
CREATE SEQUENCE contract_operation_site_distribution_patrol_seq
    START WITH 1
    INCREMENT BY 1;

-- Main table
CREATE TABLE contract_operation_distribution_site_patrol (
    id BIGINT PRIMARY KEY DEFAULT nextval('contract_operation_site_distribution_patrol_seq'),
    patrol_id BIGINT,
    site_id BIGINT,
    start_date DATE,
    locations JSONB
);

-- 1️⃣ Add the new foreign key columns
ALTER TABLE contract_operation_distribution_site_patrol
    ADD COLUMN customer_service_id BIGINT NOT NULL,
    ADD COLUMN customer_contract_id BIGINT NOT NULL;

-- 2️⃣ Add the foreign key constraints
ALTER TABLE contract_operation_distribution_site_patrol
    ADD CONSTRAINT fk_contract_op_site_patrol_customer_service
        FOREIGN KEY (customer_service_id)
        REFERENCES customer_service (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;

ALTER TABLE contract_operation_distribution_site_patrol
    ADD CONSTRAINT fk_contract_op_site_patrol_customer_contract
        FOREIGN KEY (customer_contract_id)
        REFERENCES customer_contract (id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT;


-- 1️⃣ Drop the old foreign key constraint
ALTER TABLE contract_operation_distribution_site_patrol
DROP CONSTRAINT IF EXISTS fk_contract_op_site_patrol_customer_service;

-- 2️⃣ Rename the column (optional if you want to keep the old name)
ALTER TABLE contract_operation_distribution_site_patrol
RENAME COLUMN customer_service_id TO customer_contract_service_id;

-- 3️⃣ Adjust column type to match the new referenced table's primary key type
-- (assuming LKCustomerContractService.id is BIGINT)
ALTER TABLE contract_operation_distribution_site_patrol
ALTER COLUMN customer_contract_service_id TYPE BIGINT USING customer_contract_service_id::BIGINT;

-- 4️⃣ Add the new foreign key constraint
ALTER TABLE contract_operation_distribution_site_patrol
ADD CONSTRAINT fk_contract_operation_distribution_site_patrol_customer_contract_service
FOREIGN KEY (customer_contract_service_id)
REFERENCES customer_contract_service(id);