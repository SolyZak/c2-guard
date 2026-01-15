ALTER TABLE trigger
    ADD COLUMN code VARCHAR(255),
    ADD COLUMN name_ar VARCHAR(255);

UPDATE trigger
SET code = name,
    name_ar = name;

ALTER TABLE trigger
    ALTER COLUMN name_ar SET NOT NULL;

ALTER TABLE trigger
    ALTER COLUMN code SET NOT NULL;

ALTER TABLE trigger
    ADD CONSTRAINT trigger_code_unique UNIQUE (code);



ALTER TABLE service_platform
    ADD COLUMN name_ar VARCHAR(255);

UPDATE service_platform
SET name_ar = name;

ALTER TABLE service_platform
    ALTER COLUMN name_ar SET NOT NULL;

ALTER TABLE service_platform
    ADD CONSTRAINT service_platform_code_unique UNIQUE (code);

