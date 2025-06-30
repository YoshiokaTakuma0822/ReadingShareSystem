-- Flyway migration: Add genre and start_time columns to rooms table
ALTER TABLE rooms
  ADD COLUMN genre VARCHAR(100);

ALTER TABLE rooms
  ADD COLUMN start_time TIMESTAMP;
