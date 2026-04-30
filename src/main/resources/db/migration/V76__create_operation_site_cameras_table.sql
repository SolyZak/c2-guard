-- Create operation_site_cameras join table
CREATE TABLE operation_site_cameras (
    id BIGSERIAL PRIMARY KEY,
    camera_id BIGINT NOT NULL,
    operation_site_id BIGINT NOT NULL,
    CONSTRAINT fk_operation_site_cameras_camera_id FOREIGN KEY (camera_id) REFERENCES cameras(id),
    CONSTRAINT fk_operation_site_cameras_operation_site_id FOREIGN KEY (operation_site_id) REFERENCES customer_site(id),
    CONSTRAINT uk_operation_site_cameras_unique UNIQUE (camera_id, operation_site_id)
);

-- Create sequence for operation_site_cameras table
CREATE SEQUENCE operation_site_cameras_seq START WITH 1 INCREMENT BY 1;

