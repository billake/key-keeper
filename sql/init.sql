CREATE DATABASE board;
\c board;

CREATE TABLE keys(
    id uuid DEFAULT gen_random_uuid(),
    name text NOT NULL,
    password text NOT NULL,
    description text NOT NULL,
    created bigint NOT NULL,
    deleted bigint DEFAULT NULL
);

ALTER TABLE keys
ADD CONSTRAINT pk_keys PRIMARY KEY (id);

CREATE INDEX index_name
ON keys(name);