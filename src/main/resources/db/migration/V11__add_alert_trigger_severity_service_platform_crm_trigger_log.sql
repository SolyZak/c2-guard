CREATE TABLE alert_trigger_severity (
    id SERIAL PRIMARY KEY,
    alert_id INT NOT NULL,
    trigger_id INT NOT NULL,
    service_platform_id INT NOT NULL,
    severity TEXT NOT NULL DEFAULT 'LOW',
    db_version INT NOT NULL DEFAULT 0
);

INSERT INTO alert_trigger_severity (id, alert_id, trigger_id, service_platform_id, severity, db_version)
VALUES
    (1, 1, 1, 1, 'HIGH', 0),
    (1, 1, 1, 1, 'LOW', 0);

CREATE TABLE service_platform (
    id SERIAL PRIMARY KEY,
    name TEXT NOT NULL UNIQUE,
    code TEXT NOT NULL UNIQUE
);

INSERT INTO service_platform (id, name, code)
VALUES
    (1,'ATTENDANCE', 'ATTENDANCE'),
    (2, 'PATROLS', 'PATROLS'),
    (3, 'VISITORS', 'VISITORS'),
    (4, 'INCIDENTS', 'INCIDENTS');

CREATE TABLE crm_trigger_log (
    id SERIAL PRIMARY KEY,
    service_trigger_event_id INT NOT NULL,
    trigger_id INT NOT NULL,
    trigger_name TEXT NOT NULL,
    workforce_id INT NOT NULL,
    operation_site_id INT NOT NULL,
    customer_id INT NOT NULL,
    longitude DECIMAL NOT NULL,
    latitude DECIMAL NOT NULL,
    event_time TIME WITH TIME ZONE NOT NULL,
    event_date DATE NOT NULL,
    service_platform_id INT NOT NULL,
    description TEXT
);