BEGIN;

-- 0) Normalize deleted column (important for partial unique index correctness)
UPDATE public."role"
SET deleted = false
WHERE deleted IS NULL;

ALTER TABLE public."role"
  ALTER COLUMN deleted SET DEFAULT false;

ALTER TABLE public."role"
  ALTER COLUMN deleted SET NOT NULL;

--  Add tenant scope + keycloak technical name columns
ALTER TABLE public."role"
  ADD COLUMN IF NOT EXISTS customer_id int8,
  ADD COLUMN IF NOT EXISTS keycloak_role_name varchar(400);

--  Backfill existing rows (adjust default customer_id as you like)
UPDATE public."role"
SET customer_id = 1
WHERE customer_id IS NULL;

-- Backfill keycloak_role_name for existing rows using a deterministic legacy format
-- (new roles will use: C{customerId}__{roleSlug}__{uuidSuffix})
UPDATE public."role"
SET keycloak_role_name = concat('C', customer_id, '__RID', id);

ALTER TABLE public."role"
  ALTER COLUMN customer_id SET NOT NULL,
  ALTER COLUMN keycloak_role_name SET NOT NULL;

-- Drop old global unique constraint on name
ALTER TABLE public."role"
  DROP CONSTRAINT IF EXISTS role_name_uk;

-- Unique per tenant, case-insensitive, excluding deleted roles
CREATE UNIQUE INDEX IF NOT EXISTS role_customer_name_uk
ON public."role"(customer_id, lower("name"))
WHERE deleted = false;

-- Unique Keycloak role name globally
CREATE UNIQUE INDEX IF NOT EXISTS role_keycloak_role_name_uk
ON public."role"(keycloak_role_name);

COMMIT;