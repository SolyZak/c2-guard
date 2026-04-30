-- Step 1: Drop the existing global unique constraint
ALTER TABLE public.customer_contract
    DROP CONSTRAINT IF EXISTS uknymnedmbn1lfbcb1wtp62jqq2;

-- Step 2: Add composite unique constraint scoped to customer_id
ALTER TABLE public.customer_contract
    ADD CONSTRAINT uk_customer_contract_agreement_number_per_customer
        UNIQUE (agreement_number, customer_id);