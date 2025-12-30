ALTER TABLE public.contract_operation_site_distribution_details
    ALTER COLUMN from_time TYPE time
    USING from_time::time;

ALTER TABLE public.contract_operation_site_distribution_details
    ALTER COLUMN to_time TYPE time
    USING to_time::time;