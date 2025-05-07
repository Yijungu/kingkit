-- V1__create_refresh_token_table.sql

CREATE TABLE refresh_tokens (
    email VARCHAR(255) PRIMARY KEY,
    token VARCHAR(1000) NOT NULL
);
