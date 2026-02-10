CREATE TABLE trigger (
     id SERIAL PRIMARY KEY,
     db_version INT NOT NULL DEFAULT 0,
     creation_type TEXT NOT NULL DEFAULT 'HUMAN',
     name VARCHAR UNIQUE NOT NULL
);