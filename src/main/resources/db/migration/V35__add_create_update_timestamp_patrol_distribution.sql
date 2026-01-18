ALTER TABLE contract_operation_distribution_site_patrol
    ADD COLUMN created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE contract_operation_distribution_site_patrol
    ADD COLUMN updated_at TIMESTAMP WITH TIME ZONE;

UPDATE contract_operation_distribution_site_patrol SET created_at = CURRENT_TIMESTAMP WHERE created_at IS NULL;