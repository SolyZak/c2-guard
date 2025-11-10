----- 28/10/2025------
-- 1️⃣ Drop any foreign key constraints referencing other tables
ALTER TABLE IF EXISTS location_patrol_detail
DROP CONSTRAINT IF EXISTS fk_patrol_detail;

ALTER TABLE IF EXISTS location_patrol_detail
DROP CONSTRAINT IF EXISTS fk_location_patrol;

-- 2️⃣ Drop the old join table (safe even if it’s empty)
DROP TABLE IF EXISTS location_patrol_detail CASCADE;


-- 1️⃣ Drop foreign key constraints (names may differ in your database)
ALTER TABLE IF EXISTS task_patrol_detail
DROP CONSTRAINT IF EXISTS task_patrol_detail_patrol_detail_fk;

ALTER TABLE IF EXISTS task_patrol_detail
DROP CONSTRAINT IF EXISTS task_patrol_detail_task_fk;

-- 2️⃣ Drop the old join table
DROP TABLE IF EXISTS task_patrol_detail CASCADE;


-- ===============================================
-- Create sequence for patrol_assignment IDs
-- ===============================================
CREATE SEQUENCE IF NOT EXISTS patrol_assignment_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    OWNED BY NONE;

-- ===============================================
-- Create patrol_assignment table
-- ===============================================
CREATE TABLE IF NOT EXISTS patrol_assignment (
    id BIGINT NOT NULL DEFAULT nextval('patrol_assignment_seq') PRIMARY KEY,
    patrol_detail_id BIGINT NOT NULL,

    -- Optionally add audit columns if your BaseEntity includes them, for example:
    -- created_by VARCHAR(255),
    -- created_date TIMESTAMP,
    -- updated_by VARCHAR(255),
    -- updated_date TIMESTAMP,

    CONSTRAINT fk_patrol_assignment_patrol_detail
        FOREIGN KEY (patrol_detail_id)
        REFERENCES patrol_detail (id)
        ON DELETE CASCADE
);

-- ===============================================
-- Optional: Index for faster joins
-- ===============================================
CREATE INDEX IF NOT EXISTS idx_patrol_assignment_detail
    ON patrol_assignment (patrol_detail_id);



-- 2️⃣ Add new foreign key columns to patrol_detail
ALTER TABLE patrol_detail
ADD COLUMN location_id BIGINT,
ADD COLUMN task_id BIGINT;

-- 3️⃣ Add foreign key constraints
ALTER TABLE patrol_detail
ADD CONSTRAINT fk_patrol_detail_location
FOREIGN KEY (location_id) REFERENCES location(id);

ALTER TABLE patrol_detail
ADD CONSTRAINT fk_patrol_detail_task
FOREIGN KEY (task_id) REFERENCES task(id);

ALTER TABLE contract_operation_site_distribution
ADD CONSTRAINT uq_contract_operation_site_distribution_contract_and_service
UNIQUE (customer_contract_id, customer_contract_service_id, operation_site_id);

-- 1. Drop the old JSONB column since it's no longer used
ALTER TABLE contract_operation_distribution_site_patrol
DROP COLUMN IF EXISTS locations;

-- 2. Add new columns for locationId and taskId
ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN IF NOT EXISTS location_id BIGINT,
ADD COLUMN IF NOT EXISTS task_id BIGINT;

-- 3. Add the new time columns (OffsetTime corresponds to TIME WITH TIME ZONE)
ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN IF NOT EXISTS from_time TIME WITH TIME ZONE,
ADD COLUMN IF NOT EXISTS to_time TIME WITH TIME ZONE;


-- 1️⃣ Add the new column to store the foreign key
ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN customer_id BIGINT;

-- 2️⃣ Add a foreign key constraint referencing the Customer table
ALTER TABLE contract_operation_distribution_site_patrol
ADD CONSTRAINT fk_contract_operation_site_patrol_customer
FOREIGN KEY (customer_id) REFERENCES customers(id);

-- (Optional) 3️⃣ If you want to improve performance on lookups by customer
CREATE INDEX idx_contract_operation_site_patrol_customer
    ON contract_operation_distribution_site_patrol(customer_id);

ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN status VARCHAR(20);