CREATE SEQUENCE IF NOT EXISTS permission_screen_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS public."permission_screen" (
  id int4 NOT NULL DEFAULT nextval('permission_screen_seq'),

  permission_class_id int4 NOT NULL,
  name_en varchar(100) NULL,
  name_ar varchar(100) NULL,

  deleted bool DEFAULT false,
  created_by int8 NULL,
  created_date timestamp(6) NULL,
  modified_by int8 NULL,
  modified_date timestamp(6) NULL,

  CONSTRAINT permission_screen_pkey PRIMARY KEY (id),
  CONSTRAINT fk_permission_screen_class
    FOREIGN KEY (permission_class_id) REFERENCES public."permission_class"(id)
);

CREATE INDEX IF NOT EXISTS idx_permission_screen_class_id
ON public."permission_screen"(permission_class_id);


ALTER TABLE public."permission"
ADD COLUMN IF NOT EXISTS permission_screen_id int4 NULL;

ALTER TABLE public."permission"
ADD CONSTRAINT fk_permission_permission_screen
FOREIGN KEY (permission_screen_id)
REFERENCES public."permission_screen"(id);

CREATE INDEX IF NOT EXISTS idx_permission_permission_screen_id
ON public."permission"(permission_screen_id);


ALTER TABLE public."permission"
DROP CONSTRAINT IF EXISTS fk_permission_permission_class;

DROP INDEX IF EXISTS public.idx_permission_permission_class_id;

ALTER TABLE public."permission"
DROP COLUMN IF EXISTS permission_class_id;