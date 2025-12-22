ALTER TABLE public."location"
    ALTER COLUMN longitude TYPE NUMERIC(13,10)
        USING longitude::NUMERIC(13,10),
    ALTER COLUMN latitude  TYPE NUMERIC(13,10)
        USING latitude::NUMERIC(13,10);