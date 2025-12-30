-- =====================================================
-- REVERT SCRIPT: Change time columns back to timetz
-- Table: contract_operation_site_distribution_details
-- =====================================================

ALTER TABLE public.contract_operation_site_distribution_details
    ALTER COLUMN from_time TYPE timetz
    USING from_time::timetz;

ALTER TABLE public.contract_operation_site_distribution_details
    ALTER COLUMN to_time TYPE timetz
    USING to_time::timetz;