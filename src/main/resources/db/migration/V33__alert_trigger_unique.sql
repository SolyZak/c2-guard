ALTER TABLE alert_trigger
    DROP CONSTRAINT alert_trigger_service_platform_id_fkey,
    ADD CONSTRAINT alert_trigger_service_platform_id_fkey FOREIGN KEY (service_platform_id)
    REFERENCES service_platform (id)
    ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE crm_trigger_log
    DROP CONSTRAINT crm_trigger_log_service_platform_id_fkey,
    ADD CONSTRAINT crm_trigger_log_service_platform_id_fkey FOREIGN KEY (service_platform_id)
    REFERENCES service_platform(id)
    ON DELETE CASCADE ON UPDATE CASCADE;

ALTER TABLE alert_trigger_severity
    DROP CONSTRAINT alert_trigger_severity_alert_trigger_id_fkey,
    ADD CONSTRAINT alert_trigger_severity_alert_trigger_id_fkey FOREIGN KEY (alert_trigger_id)
    REFERENCES alert_trigger(id)
    ON DELETE CASCADE ON UPDATE CASCADE;


ALTER TABLE alert_trigger
    ADD CONSTRAINT alert_trigger_alert_id_trigger_id_service_platform_id_unique UNIQUE (alert_id, trigger_id, service_platform_id);

ALTER TABLE alert_trigger_severity
    ADD CONSTRAINT alert_trigger_severity_alert_trigger_id_customer_id_unique UNIQUE (alert_trigger_id, customer_id);
