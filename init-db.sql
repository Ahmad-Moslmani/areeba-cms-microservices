-- ==========================================================
-- Author: Ahmad Mouslimani
-- AREEBA CHALLENGE: DATABASE INITIALIZATION SCRIPT
-- RUN: psql -U postgres -f init-db.sql
-- ==========================================================

-- 1. Create Databases
CREATE DATABASE fraud_db;
CREATE DATABASE transaction_db;
CREATE DATABASE card_db;
CREATE DATABASE account_db;

-- 2. Setup Fraud Service Schemas
\c fraud_db;
CREATE SCHEMA IF NOT EXISTS fraud_test;
COMMENT ON SCHEMA fraud_test IS 'Isolated schema for Fraud Service Unit/Integration Tests';

-- 3. Setup Transaction Service Schemas
\c transaction_db;
CREATE SCHEMA IF NOT EXISTS transaction_test;
COMMENT ON SCHEMA transaction_test IS 'Isolated schema for Transaction Service Unit/Integration Tests';

-- 4. Setup Card Service Schemas
\c card_db;
CREATE SCHEMA IF NOT EXISTS card_test;
COMMENT ON SCHEMA card_test IS 'Isolated schema for Card Service Unit/Integration Tests';

-- 5. Setup Account Service Schemas
\c account_db;
CREATE SCHEMA IF NOT EXISTS account_test;
COMMENT ON SCHEMA account_test IS 'Isolated schema for Account Service Unit/Integration Tests';