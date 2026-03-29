-- Create vendors table for cameras management
CREATE TABLE vendors (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create sequence for vendors table
CREATE SEQUENCE vendors_seq START WITH 1 INCREMENT BY 1;

