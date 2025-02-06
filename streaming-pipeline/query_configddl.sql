CREATE TABLE query_config (
    topic_code VARCHAR2(50) PRIMARY KEY,
    base_query CLOB,
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);