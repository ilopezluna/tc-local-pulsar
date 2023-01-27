CREATE TABLE notes
(
    id bigserial NOT NULL,
    text       CHARACTER VARYING(255)   NOT NULL,
    PRIMARY KEY (id)
);