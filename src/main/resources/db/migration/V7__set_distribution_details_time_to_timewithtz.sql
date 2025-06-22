ALTER TABLE contract_operation_site_distribution_details
    ALTER COLUMN from_time TYPE TIME WITH TIME ZONE USING from_time::TIME WITH TIME ZONE;

ALTER TABLE contract_operation_site_distribution_details
    ALTER COLUMN to_time TYPE TIME WITH TIME ZONE USING to_time::TIME WITH TIME ZONE;
