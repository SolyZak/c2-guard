--CREATE TABLE premise (
--    id SERIAL PRIMARY KEY,
--    created_by int8 NULL,
--    created_date timestamp(6) NULL,
--    deleted bool NULL,
--    modified_by int8 NULL,
--    modified_date timestamp(6) NULL,
--    name VARCHAR(300) NOT NULL,
--    code VARCHAR(300) UNIQUE NOT NULL
--);

CREATE SEQUENCE premise_id_seq
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NO MAXVALUE
    CACHE 1;

-- public.premise definition

-- Drop table

-- DROP TABLE public.premise;

CREATE TABLE public.premise (
	id serial4 NOT NULL,
	created_by int8 NULL,
	created_date timestamp(6) NULL,
	deleted bool NULL,
	modified_by int8 NULL,
	modified_date timestamp(6) NULL,
	"name" varchar(300) NOT NULL,
	code varchar(300) NOT NULL,
	CONSTRAINT premise_code_key UNIQUE (code),
	CONSTRAINT premise_pkey PRIMARY KEY (id)
);