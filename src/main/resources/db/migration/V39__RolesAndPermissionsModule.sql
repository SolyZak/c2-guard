CREATE SEQUENCE IF NOT EXISTS permission_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS public."role" (
  id int4 NOT NULL DEFAULT nextval('role_seq'),
  name varchar(300) NOT NULL,
  description varchar(500) NULL,

  deleted bool DEFAULT false,
  created_by int8 NULL,
  created_date timestamp(6) NULL,
  modified_by int8 NULL,
  modified_date timestamp(6) NULL,

  CONSTRAINT role_pkey PRIMARY KEY (id),
  CONSTRAINT role_name_uk UNIQUE (name)
);

CREATE TABLE IF NOT EXISTS public."permission" (
  id int8 NOT NULL DEFAULT nextval('permission_seq'),
  keycloak_role_name varchar(255) NOT NULL,
  name_en varchar(100) NULL,
  name_ar varchar(100) NULL,

  deleted bool DEFAULT false,
  created_by int8 NULL,
  created_date timestamp(6) NULL,
  modified_by int8 NULL,
  modified_date timestamp(6) NULL,

  CONSTRAINT permission_pkey PRIMARY KEY (id),
  CONSTRAINT permission_keycloak_role_name_uk UNIQUE (keycloak_role_name)
);

CREATE TABLE IF NOT EXISTS public.role_permissions (
  permission_id int8 NOT NULL,
  role_id int4 NOT NULL,
  CONSTRAINT permission_role_pkey PRIMARY KEY (permission_id, role_id),
  CONSTRAINT fk_permission FOREIGN KEY (permission_id) REFERENCES public."permission"(id),
  CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES public."role"(id)
);

CREATE INDEX IF NOT EXISTS idx_permission_keycloak_role_name
ON public."permission"(keycloak_role_name);


ALTER TABLE public.customer_users
ADD COLUMN IF NOT EXISTS role_id int4 NULL;

ALTER TABLE public.customer_users
ADD CONSTRAINT fk_customer_users_role
FOREIGN KEY (role_id) REFERENCES public."role"(id);