ALTER TABLE public.customer_site
ADD COLUMN premise_id int8 NULL;

ALTER TABLE public.customer_site
ADD CONSTRAINT customer_site_premise_fk
FOREIGN KEY (premise_id)
REFERENCES public.premise(id);

-- ============================================================
-- Sequence for Location primary key
-- ============================================================
CREATE SEQUENCE location_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

-- ============================================================
-- Table: location
-- ============================================================
CREATE TABLE location (
    id BIGINT NOT NULL DEFAULT nextval('location_seq'),
    premise_id BIGINT,
    name VARCHAR(255),
    access_type VARCHAR(255),
    longitude DOUBLE PRECISION,
    latitude DOUBLE PRECISION,

    -- From BaseEntity
    deleted BOOLEAN DEFAULT FALSE,
    modified_date TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    created_date TIMESTAMP,

    -- From BaseAuditEntity
    modified_by_user VARCHAR(255),
    created_by_user VARCHAR(255),

    CONSTRAINT pk_location PRIMARY KEY (id),
    CONSTRAINT fk_location_premise FOREIGN KEY (premise_id)
        REFERENCES premise (id) ON DELETE SET NULL
);


ALTER TABLE location
ADD COLUMN qr_image BYTEA;

ALTER TABLE location
ALTER COLUMN qr_image TYPE oid USING lo_from_bytea(0, qr_image);



-- ==========================================================
-- 1️⃣ Sequences
-- ==========================================================
CREATE SEQUENCE task_id_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE task_check_id_seq START WITH 1 INCREMENT BY 1;

-- ==========================================================
-- 2️⃣ Task Table
-- ==========================================================
CREATE TABLE task (
    id BIGINT PRIMARY KEY DEFAULT nextval('task_id_seq'),
    name VARCHAR(255) NOT NULL,

    -- Common audit fields from BaseEntity
    deleted BOOLEAN DEFAULT FALSE,
    modified_date TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    created_date TIMESTAMP
);

-- ==========================================================
-- 3️⃣ Base TaskCheck Table (Parent)
-- ==========================================================
CREATE TABLE task_check (
    id BIGINT PRIMARY KEY DEFAULT nextval('task_check_id_seq'),

    name VARCHAR(255),
    evidence BOOLEAN,
    type VARCHAR(31) NOT NULL,  -- Discriminator column (for JOINED inheritance)

    -- Link to Task (One-to-Many relationship)
    task_id BIGINT REFERENCES task(id) ON DELETE CASCADE,

    -- Common audit fields from BaseEntity
    deleted BOOLEAN DEFAULT FALSE,
    modified_date TIMESTAMP,
    created_by BIGINT,
    modified_by BIGINT,
    created_date TIMESTAMP
);

-- ==========================================================
-- 4️⃣ Subclass Tables (Joined Inheritance)
-- ==========================================================

-- TaskCheckDecimal
CREATE TABLE task_check_decimal (
    id BIGINT PRIMARY KEY REFERENCES task_check(id) ON DELETE CASCADE,
    unit VARCHAR(255),
    operator VARCHAR(255),
    value DOUBLE PRECISION
);

-- TaskCheckNumber
CREATE TABLE task_check_number (
    id BIGINT PRIMARY KEY REFERENCES task_check(id) ON DELETE CASCADE,
    unit VARCHAR(255),
    operator VARCHAR(255),
    value INTEGER
);

-- TaskCheckText
CREATE TABLE task_check_text (
    id BIGINT PRIMARY KEY REFERENCES task_check(id) ON DELETE CASCADE,
    notes TEXT
);

-- TaskCheckList (listItems as text array)
CREATE TABLE task_check_list (
    id BIGINT PRIMARY KEY REFERENCES task_check(id) ON DELETE CASCADE,
    list_items TEXT[]
);

-- ==========================================================
-- ✅ Indexes and Constraints
-- ==========================================================
CREATE INDEX idx_task_check_task_id ON task_check(task_id);
CREATE INDEX idx_task_check_type ON task_check(type);




-----------19/10/2025-------------------------
-- ==========================
-- Table: public.patrol
-- ==========================

-- Drop existing table (optional, only if you need a fresh recreate)
-- DROP TABLE IF EXISTS public.patrol CASCADE;

-- Create the sequence (for @SequenceGenerator)
CREATE SEQUENCE IF NOT EXISTS public.patrol_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create the table
CREATE TABLE public.patrol (
    id BIGINT NOT NULL DEFAULT nextval('public.patrol_seq'),
    name VARCHAR(255),
	frequency VARCHAR(255),
    frequency_rate VARCHAR(255),
    CONSTRAINT patrol_pkey PRIMARY KEY (id)
);

-- Ownership (optional)
-- ALTER SEQUENCE public.patrol_seq OWNED BY public.patrol.id;



-- ==========================
-- Table: public.patrol_detail
-- ==========================

-- Drop existing table (optional)
-- DROP TABLE IF EXISTS public.patrol_detail CASCADE;

-- Create the sequence (for @SequenceGenerator)
CREATE SEQUENCE IF NOT EXISTS public.patrol_detail_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- Create the table
CREATE TABLE public.patrol_detail (
    id BIGINT NOT NULL DEFAULT nextval('public.patrol_detail_seq'),
    patrol_id BIGINT,
    CONSTRAINT patrol_detail_pkey PRIMARY KEY (id),
    CONSTRAINT patrol_detail_patrol_fk FOREIGN KEY (patrol_id)
        REFERENCES public.patrol(id)
);

-- Ownership (optional)
-- ALTER SEQUENCE public.patrol_detail_seq OWNED BY public.patrol_detail.id;



ALTER TABLE public.task
ADD COLUMN patrol_detail_id BIGINT;

ALTER TABLE public.task
ADD CONSTRAINT task_patrol_detail_fk
FOREIGN KEY (patrol_detail_id)
REFERENCES public.patrol_detail(id);


ALTER TABLE public.location
ADD COLUMN patrol_detail_id BIGINT;

ALTER TABLE public.location
ADD CONSTRAINT location_patrol_detail_fk
FOREIGN KEY (patrol_detail_id)
REFERENCES public.patrol_detail(id);



-- 1. Drop the old foreign key and column
ALTER TABLE location DROP CONSTRAINT IF EXISTS fk_location_patrol_detail;
ALTER TABLE location DROP COLUMN IF EXISTS patrol_detail_id;

-- 2. Create the new join table for the many-to-many relationship
CREATE TABLE location_patrol_detail (
    location_id BIGINT NOT NULL,
    patrol_detail_id BIGINT NOT NULL,
    PRIMARY KEY (location_id, patrol_detail_id),
    CONSTRAINT fk_location_patrol FOREIGN KEY (location_id)
        REFERENCES location (id) ON DELETE CASCADE,
    CONSTRAINT fk_patrol_detail FOREIGN KEY (patrol_detail_id)
        REFERENCES patrol_detail (id) ON DELETE CASCADE
);

-- 1️⃣ Drop old foreign key and column
ALTER TABLE public.task DROP CONSTRAINT IF EXISTS task_patrol_detail_id_fkey;
ALTER TABLE public.task DROP COLUMN IF EXISTS patrol_detail_id;

-- 2️⃣ Create join table for many-to-many relation
CREATE TABLE public.task_patrol_detail (
    task_id BIGINT NOT NULL,
    patrol_detail_id BIGINT NOT NULL,
    CONSTRAINT task_patrol_detail_pk PRIMARY KEY (task_id, patrol_detail_id),
    CONSTRAINT task_patrol_detail_task_fk FOREIGN KEY (task_id) REFERENCES public.task(id),
    CONSTRAINT task_patrol_detail_patrol_detail_fk FOREIGN KEY (patrol_detail_id) REFERENCES public.patrol_detail(id)
);



-- 🔹 Add BaseEntity common audit columns
ALTER TABLE public.patrol
    ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS modified_date TIMESTAMP,
    ADD COLUMN IF NOT EXISTS created_by BIGINT,
    ADD COLUMN IF NOT EXISTS modified_by BIGINT,
    ADD COLUMN IF NOT EXISTS created_date TIMESTAMP;

-- 🔹 Add customer relationship (foreign key)
ALTER TABLE public.patrol
    ADD COLUMN IF NOT EXISTS customer_id BIGINT NOT NULL,
    ADD CONSTRAINT patrol_customer_fk FOREIGN KEY (customer_id)
        REFERENCES public.customers (id);
-- :small_blue_diamond: Add customer_id column and foreign key constraint to task table
ALTER TABLE public.task
    ADD COLUMN IF NOT EXISTS customer_id BIGINT NOT NULL,
    ADD CONSTRAINT task_customer_fk FOREIGN KEY (customer_id)
        REFERENCES public.customers (id);
-- :small_blue_diamond: Add customer_id column and foreign key constraint to location table
ALTER TABLE public.location
    ADD COLUMN IF NOT EXISTS customer_id BIGINT NOT NULL,
    ADD CONSTRAINT location_customer_fk FOREIGN KEY (customer_id)
        REFERENCES public.customers (id);

-- :small_blue_diamond: Add customer_id column and foreign key constraint to premise table
ALTER TABLE public.premise
    ADD COLUMN IF NOT EXISTS customer_id BIGINT NOT NULL,
    ADD CONSTRAINT premise_customer_fk FOREIGN KEY (customer_id)
        REFERENCES public.customers (id);