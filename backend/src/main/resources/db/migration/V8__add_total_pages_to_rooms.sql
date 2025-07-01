-- V8__add_total_pages_to_rooms.sql
-- 旧V6__add_total_pages_to_rooms.sql の内容を移動
ALTER TABLE rooms ADD COLUMN total_pages INT;
