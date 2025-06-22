ALTER TABLE customers ADD COLUMN timezone varchar(255) NULL;
ALTER TABLE customer_site ADD COLUMN timezone varchar(255) NULL;

-- Update timezone for customers table
UPDATE customers
SET timezone = 'EGYPT';

-- Update timezone for customer_site table
UPDATE customer_site
SET timezone = 'EGYPT';