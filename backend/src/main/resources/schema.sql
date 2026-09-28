CREATE TABLE IF NOT EXISTS saved_scripts (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(80) NOT NULL,
    language VARCHAR(20) NOT NULL,
    code CLOB NOT NULL,
    updated_at VARCHAR(40) NOT NULL
);