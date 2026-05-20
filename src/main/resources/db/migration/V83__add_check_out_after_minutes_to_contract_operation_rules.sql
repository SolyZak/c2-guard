ALTER TABLE public.contract_operation_rules
    ADD COLUMN IF NOT EXISTS check_out_after_minutes INTEGER DEFAULT 10;

UPDATE public.contract_operation_rules
SET check_out_after_minutes = 10
WHERE check_out_after_minutes IS NULL;
