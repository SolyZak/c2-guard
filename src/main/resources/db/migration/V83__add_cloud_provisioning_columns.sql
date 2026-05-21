-- Cloud management provisioning columns
ALTER TABLE public.customers ADD COLUMN IF NOT EXISTS cloud_customer_id BIGINT;
ALTER TABLE public.customers ADD COLUMN IF NOT EXISTS sub_cloud_account_id BIGINT;
ALTER TABLE public.customers ADD COLUMN IF NOT EXISTS global_customer_uuid UUID;
ALTER TABLE public.customers ADD COLUMN IF NOT EXISTS cloud_operator_id BIGINT;

CREATE UNIQUE INDEX IF NOT EXISTS uk_customers_global_customer_uuid
    ON public.customers(global_customer_uuid) WHERE global_customer_uuid IS NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS uk_customers_cloud_customer_id
    ON public.customers(cloud_customer_id) WHERE cloud_customer_id IS NOT NULL;
