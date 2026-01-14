ALTER TABLE alert_trigger_severity
    DROP COLUMN alert_id,
    DROP COLUMN trigger_id,
    DROP COLUMN service_platform_id;

ALTER TABLE crm_trigger_log
    ALTER COLUMN service_trigger_event_id    TYPE BIGINT USING service_trigger_event_id::BIGINT,
    ALTER COLUMN trigger_id                  TYPE BIGINT USING trigger_id::BIGINT,
    ALTER COLUMN workforce_id                TYPE BIGINT USING workforce_id::BIGINT,
    ALTER COLUMN operation_site_id           TYPE BIGINT USING operation_site_id::BIGINT,
    ALTER COLUMN customer_id                 TYPE BIGINT USING customer_id::BIGINT,
    ALTER COLUMN service_platform_id         TYPE BIGINT USING service_platform_id::BIGINT;

ALTER TABLE crm_trigger_log
    ADD CONSTRAINT crm_trigger_log_service_platform_id_fkey FOREIGN KEY (service_platform_id) REFERENCES service_platform(id);

CREATE TABLE alert_trigger (
    id SERIAL PRIMARY KEY,
    db_version BIGINT NOT NULL DEFAULT 0,
    alert_id BIGINT NOT NULL,
    trigger_id BIGINT NOT NULL,
    service_platform_id BIGINT NOT NULL,

    CONSTRAINT alert_trigger_service_platform_id_fkey FOREIGN KEY (service_platform_id) REFERENCES service_platform(id)
);


ALTER TABLE alert_trigger_severity
    ADD COLUMN alert_trigger_id BIGINT NOT NULL,
    ADD CONSTRAINT alert_trigger_severity_alert_trigger_id_fkey FOREIGN KEY (alert_trigger_id) REFERENCES alert_trigger(id);