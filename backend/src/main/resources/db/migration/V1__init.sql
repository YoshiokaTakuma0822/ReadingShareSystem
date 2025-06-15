-- Enable pgcrypto extension for gen_random_uuid()
CREATE EXTENSION IF NOT EXISTS pgcrypto;

-- Accounts table for authentication
CREATE TABLE accounts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    refresh_token TEXT
);

-- Rooms table for chat organization
CREATE TABLE rooms (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by UUID REFERENCES accounts(id) ON DELETE SET NULL
);
CREATE INDEX idx_rooms_name ON rooms(name);

-- Members table for chat participation
CREATE TABLE members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    account_id UUID NOT NULL REFERENCES accounts(id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    CONSTRAINT uk_members_account_room UNIQUE (account_id, room_id)
);
CREATE INDEX idx_members_name ON members(name);

-- Create a system account for default rooms
INSERT INTO accounts (id, email, password) VALUES
    ('00000000-0000-0000-0000-000000000000', 'system@example.com', '$2a$10$dummy.password.hash');

-- Insert default general room with system account as creator
INSERT INTO rooms (name, description, created_by) VALUES
    ('general', 'General chat room', '00000000-0000-0000-0000-000000000000');
