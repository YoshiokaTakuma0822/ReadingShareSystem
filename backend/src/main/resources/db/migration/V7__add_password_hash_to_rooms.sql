-- V7__add_password_hash_to_rooms.sql
ALTER TABLE rooms ADD COLUMN password_hash VARCHAR(255);
