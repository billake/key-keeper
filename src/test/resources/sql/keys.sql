CREATE TABLE keys(
    id uuid DEFAULT gen_random_uuid(),
    name text NOT NULL,
    password text NOT NULL,
    description text NOT NULL,
    created TIMESTAMP DEFAULT now(),
    deleted TIMESTAMP DEFAULT NULL
);

ALTER TABLE keys
ADD CONSTRAINT pk_keys PRIMARY KEY (id);

CREATE INDEX index_name
ON keys(name);

INSERT INTO keys (
        id,
        name,
        password,
        description,
        created,
        deleted
      ) VALUES (
        'a9ec8f5e-2b60-4d54-add7-d7e87a673d7a',
        'some_key_name',
        'some_key_password_123',
        'some_key_description',
        '2024-11-09 12:00:00',
        NULL
      );