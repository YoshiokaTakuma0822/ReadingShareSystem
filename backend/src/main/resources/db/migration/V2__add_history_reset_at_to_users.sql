-- V2__add_history_reset_at_to_users.sql
ALTER TABLE users ADD COLUMN history_reset_at TIMESTAMP;
