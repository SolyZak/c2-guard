CREATE SEQUENCE IF NOT EXISTS permission_class_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS public."permission_class" (
  id int4 NOT NULL DEFAULT nextval('permission_class_seq'),
  name_en varchar(100) NULL,
  name_ar varchar(100) NULL,

  deleted bool DEFAULT false,
  created_by int8 NULL,
  created_date timestamp(6) NULL,
  modified_by int8 NULL,
  modified_date timestamp(6) NULL,

  CONSTRAINT permission_class_pkey PRIMARY KEY (id)
);
ALTER TABLE public."permission"
ADD COLUMN IF NOT EXISTS permission_class_id int4 NULL;

ALTER TABLE public."permission"
ADD CONSTRAINT fk_permission_permission_class
FOREIGN KEY (permission_class_id)
REFERENCES public."permission_class"(id);

CREATE INDEX IF NOT EXISTS idx_permission_permission_class_id
ON public."permission"(permission_class_id);