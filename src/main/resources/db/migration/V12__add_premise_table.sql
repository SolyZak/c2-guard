CREATE TABLE premise (
    id SERIAL PRIMARY KEY,
    created_by int8 NULL,
    created_date timestamp(6) NULL,
    deleted bool NULL,
    modified_by int8 NULL,
    modified_date timestamp(6) NULL,
    name VARCHAR(300) NOT NULL,
    code VARCHAR(300) UNIQUE NOT NULL
);
