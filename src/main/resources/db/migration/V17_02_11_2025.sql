-- Add new column "end_date"
ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN end_date date;

-- Drop the old "status" column
ALTER TABLE contract_operation_distribution_site_patrol
DROP COLUMN IF EXISTS status;

ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN patrol_frequency_type VARCHAR(50);

ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN status VARCHAR(255);

ALTER TABLE contract_operation_distribution_site_patrol
ADD COLUMN unique_id VARCHAR(255);


