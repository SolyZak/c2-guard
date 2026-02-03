ALTER TABLE public.premise
    ADD COLUMN IF NOT EXISTS longitude NUMERIC(13,10),
    ADD COLUMN IF NOT EXISTS latitude  NUMERIC(13,10);

--(old and not correct in business logic)
ALTER TABLE public.premise
    DROP CONSTRAINT IF EXISTS premise_code_key;

-- Added (per-customer) unique constraints
ALTER TABLE public.premise
    ADD CONSTRAINT premise_customer_id_code_key UNIQUE (customer_id, code);

ALTER TABLE public.premise
    ADD CONSTRAINT premise_customer_id_name_key UNIQUE (customer_id, "name");