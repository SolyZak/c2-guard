ALTER TABLE alert_trigger
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

ALTER SEQUENCE alert_trigger_id_seq AS BIGINT;

ALTER TABLE alert_trigger_severity
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

ALTER SEQUENCE alert_trigger_severity_id_seq AS BIGINT;

ALTER TABLE crm_trigger_log
    ALTER COLUMN id TYPE BIGINT USING id::BIGINT;

ALTER SEQUENCE crm_trigger_log_id_seq AS BIGINT;
