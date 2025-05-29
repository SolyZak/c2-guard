CREATE TABLE public.customers (
	id int8 NOT NULL,
	created_by int8 NULL,
	created_date timestamp(6) NULL,
	deleted bool NULL,
	modified_by int8 NULL,
	modified_date timestamp(6) NULL,
	active bool DEFAULT false NOT NULL,
	address varchar(255) NULL,
	code varchar(255) NULL,
	country_code varchar(255) NULL,
	email varchar(255) NULL,
	"name" varchar(255) NULL,
	phone varchar(255) NULL,
	registration_number varchar(255) NULL,
	CONSTRAINT customers_pkey PRIMARY KEY (id)
);

CREATE TABLE public.customer_contract (
	id int8 NOT NULL,
	created_by_user varchar(255) NULL,
	created_date timestamp(6) NULL,
	modified_by_user varchar(255) NULL,
	modified_date timestamp(6) NULL,
	is_deleted int4 NOT NULL,
	agreement_name varchar(255) NULL,
	agreement_number varchar(255) NULL,
	currency int8 NULL,
	currency_code varchar(255) NULL,
	currency_name varchar(255) NULL,
	end_agreement_date date NULL,
	security_company_id int8 NULL,
	security_company_name varchar(255) NULL,
	start_agreement_date date NULL,
	status varchar(255) NULL,
	customer_id int8 NULL,
	CONSTRAINT customer_contract_pkey PRIMARY KEY (id),
	CONSTRAINT customer_contract_status_check CHECK (((status)::text = ANY ((ARRAY['SAVED'::character varying, 'SENT'::character varying, 'DISTRIBUTED'::character varying, 'ON_DISTRIBUTE'::character varying, 'APPROVED_BY_SECURITY_COMPANY'::character varying])::text[]))),
	CONSTRAINT uknymnedmbn1lfbcb1wtp62jqq2 UNIQUE (agreement_number)
);

CREATE TABLE public.customer_service (
	id int8 NOT NULL,
	created_by_user varchar(255) NULL,
	created_date timestamp(6) NULL,
	modified_by_user varchar(255) NULL,
	modified_date timestamp(6) NULL,
	is_deleted int4 NOT NULL,
	multi_site bool NOT NULL,
	service_name varchar(255) NULL,
	unit varchar(255) NULL,
	customer_id int8 NULL,
	CONSTRAINT customer_service_pkey PRIMARY KEY (id),
	CONSTRAINT customer_service_unit_check CHECK (((unit)::text = ANY ((ARRAY['PERSON'::character varying, 'PRODUCT'::character varying])::text[])))
);

CREATE TABLE public.customer_service_activities (
	customer_service_id int8 NOT NULL,
	activities varchar(255) NULL,
	CONSTRAINT customer_service_activities_activities_check CHECK (((activities)::text = ANY ((ARRAY['ATTENDANCE'::character varying, 'PATROLS'::character varying, 'VISITORS'::character varying, 'INCIDENTS'::character varying])::text[])))
);

CREATE TABLE public.customer_service_details (
	id int8 NOT NULL,
	created_by_user varchar(255) NULL,
	created_date timestamp(6) NULL,
	modified_by_user varchar(255) NULL,
	modified_date timestamp(6) NULL,
	is_deleted int4 NOT NULL,
	days int8 NULL,
	hours int8 NULL,
	customer_service_id int8 NULL,
	CONSTRAINT customer_service_details_pkey PRIMARY KEY (id)
);

CREATE TABLE public.customer_site (
	id int8 NOT NULL,
	created_by int8 NULL,
	created_date timestamp(6) NULL,
	deleted bool NULL,
	modified_by int8 NULL,
	modified_date timestamp(6) NULL,
	active bool NULL,
	latitude float8 NULL,
	longitude float8 NULL,
	"name" varchar(255) NOT NULL,
	tolerance float8 NULL,
	customer_id int8 NOT NULL,
	CONSTRAINT customer_site_pkey PRIMARY KEY (id)
);

CREATE TABLE public.complaint (
	id int4 NOT NULL,
	created_by int8 NULL,
	created_date timestamp(6) NULL,
	deleted bool NULL,
	modified_by int8 NULL,
	modified_date timestamp(6) NULL,
	description varchar(1500) NULL,
	evidences_paths _varchar NULL,
	customer_id int8 NULL,
	customer_site_id int8 NULL,
	CONSTRAINT complaint_pkey PRIMARY KEY (id)
);

CREATE TABLE public.contract_operation_rules (
	id int8 NOT NULL,
	allow_check_in_after bool NOT NULL,
	allow_check_in_before bool NOT NULL,
	allow_check_out_after bool NOT NULL,
	check_in_after_minutes int4 NULL,
	check_in_before_minutes int4 NULL,
	check_out_after_minutes int4 NULL,
	presence_mode varchar(255) NULL,
	customer_contract_id int8 NULL,
	CONSTRAINT contract_operation_rules_pkey PRIMARY KEY (id),
	CONSTRAINT contract_operation_rules_presence_mode_check CHECK (((presence_mode)::text = ANY ((ARRAY['CHECK_IN_ONLY'::character varying, 'CHECK_OUT_ONLY'::character varying, 'BOTH'::character varying])::text[]))),
	CONSTRAINT uk6p65yxjjxjgy3vwri0qvaf67f UNIQUE (customer_contract_id)
);

CREATE TABLE public.customer_contract_service (
	id int8 NOT NULL,
	created_by_user varchar(255) NULL,
	created_date timestamp(6) NULL,
	modified_by_user varchar(255) NULL,
	modified_date timestamp(6) NULL,
	is_deleted int4 NOT NULL,
	quantity int8 NULL,
	unit_price float8 NULL,
	customer_contract_id int8 NOT NULL,
	service_details_id int8 NULL,
	CONSTRAINT customer_contract_service_pkey PRIMARY KEY (id)
);

CREATE TABLE public.contract_operation_site_distribution (
	id int8 NOT NULL,
	created_by_user varchar(255) NULL,
	created_date timestamp(6) NULL,
	modified_by_user varchar(255) NULL,
	modified_date timestamp(6) NULL,
	is_deleted int4 NOT NULL,
	customer_contract_id int8 NOT NULL,
	customer_contract_service_id int8 NOT NULL,
	operation_site_id int8 NULL,
	CONSTRAINT contract_operation_site_distribution_pkey PRIMARY KEY (id)
);

CREATE TABLE public.contract_operation_site_distribution_activities (
	operation_site_distribution_id int8 NOT NULL,
	activities varchar(255) NULL,
	CONSTRAINT contract_operation_site_distribution_activitie_activities_check CHECK (((activities)::text = ANY ((ARRAY['ATTENDANCE'::character varying, 'PATROLS'::character varying, 'VISITORS'::character varying, 'INCIDENTS'::character varying])::text[])))
);

CREATE TABLE public.contract_operation_site_distribution_details (
	id int8 NOT NULL,
	created_by_user varchar(255) NULL,
	created_date timestamp(6) NULL,
	modified_by_user varchar(255) NULL,
	modified_date timestamp(6) NULL,
	is_deleted int4 NOT NULL,
	days _varchar NULL,
	from_time time(6) NULL,
	quantity int8 NULL,
	to_time time(6) NULL,
	contract_operation_site_distribution_id int8 NOT NULL,
	CONSTRAINT contract_operation_site_distribution_details_pkey PRIMARY KEY (id)
);