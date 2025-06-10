-- Add last_token_refresh_at column to accounts table
-- This column tracks when the JWT token was last refreshed to prevent too frequent refreshes

ALTER TABLE accounts ADD COLUMN last_token_refresh_at TIMESTAMP WITH TIME ZONE;
