ALTER TABLE customer_site ADD COLUMN created_by_user varchar(255) NULL;
ALTER TABLE customer_site ADD COLUMN modified_by_user varchar(255) NULL;

ALTER TABLE customer_users ADD COLUMN created_by_user varchar(255) NULL;
ALTER TABLE customer_users ADD COLUMN modified_by_user varchar(255) NULL;

ALTER TABLE complaint ADD COLUMN created_by_user varchar(255) NULL;
ALTER TABLE complaint ADD COLUMN modified_by_user varchar(255) NULL;

ALTER TABLE customers ADD COLUMN created_by_user varchar(255) NULL;
ALTER TABLE customers ADD COLUMN modified_by_user varchar(255) NULL;

ALTER TABLE customer_contract ADD COLUMN created_by int8 NULL;
ALTER TABLE customer_contract ADD COLUMN modified_by int8 NULL;

ALTER TABLE customer_service ADD COLUMN created_by int8 NULL;
ALTER TABLE customer_service ADD COLUMN modified_by int8 NULL;

ALTER TABLE customer_service_activities ADD COLUMN created_by int8 NULL;
ALTER TABLE customer_service_activities ADD COLUMN modified_by int8 NULL;

ALTER TABLE customer_service_details ADD COLUMN created_by int8 NULL;
ALTER TABLE customer_service_details ADD COLUMN modified_by int8 NULL;

ALTER TABLE contract_operation_rules ADD COLUMN created_by int8 NULL;
ALTER TABLE contract_operation_rules ADD COLUMN modified_by int8 NULL;

ALTER TABLE customer_contract_service ADD COLUMN created_by int8 NULL;
ALTER TABLE customer_contract_service ADD COLUMN modified_by int8 NULL;

ALTER TABLE contract_operation_site_distribution ADD COLUMN created_by int8 NULL;
ALTER TABLE contract_operation_site_distribution ADD COLUMN modified_by int8 NULL;

ALTER TABLE contract_operation_site_distribution_activities ADD COLUMN created_by int8 NULL;
ALTER TABLE contract_operation_site_distribution_activities ADD COLUMN modified_by int8 NULL;

ALTER TABLE contract_operation_site_distribution_details ADD COLUMN created_by int8 NULL;
ALTER TABLE contract_operation_site_distribution_details ADD COLUMN modified_by int8 NULL;