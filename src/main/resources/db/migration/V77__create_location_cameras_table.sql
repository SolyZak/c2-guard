CREATE SEQUENCE IF NOT EXISTS location_cameras_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS location_cameras (
    id          BIGINT DEFAULT nextval('location_cameras_seq') PRIMARY KEY,
    location_id BIGINT NOT NULL,
    camera_id   BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,

    CONSTRAINT fk_location_cameras_location FOREIGN KEY (location_id) REFERENCES location(id),
    CONSTRAINT fk_location_cameras_camera   FOREIGN KEY (camera_id)   REFERENCES cameras(id),
    CONSTRAINT fk_location_cameras_customer FOREIGN KEY (customer_id) REFERENCES customers(id),
    CONSTRAINT uq_location_camera           UNIQUE (location_id, camera_id)
);