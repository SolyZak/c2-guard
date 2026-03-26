-- Create cameras table with foreign key to vendors
CREATE TABLE cameras (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    ip VARCHAR(20) NOT NULL,
    vendor_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    CONSTRAINT fk_cameras_vendor_id FOREIGN KEY (vendor_id) REFERENCES vendors(id),
    CONSTRAINT fk_cameras_customer_id FOREIGN KEY (customer_id) REFERENCES customers(id)
);

-- Create sequence for cameras table
CREATE SEQUENCE cameras_seq START WITH 1 INCREMENT BY 1;

