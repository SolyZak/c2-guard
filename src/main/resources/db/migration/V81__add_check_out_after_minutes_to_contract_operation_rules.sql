ALTER TABLE public.contract_operation_rules
    ADD COLUMN IF NOT EXISTS check_out_after_minutes INTEGER;
