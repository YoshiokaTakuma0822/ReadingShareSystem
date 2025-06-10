-- Consolidated migration file including V1-V3 with UUID for account IDs

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

-- Chat messages table with indexes and constraints
CREATE TABLE chat_messages (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL REFERENCES members(id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    type VARCHAR(10) NOT NULL
);
CREATE INDEX idx_chat_messages_member_id ON chat_messages(member_id);
CREATE INDEX idx_chat_messages_room_id ON chat_messages(room_id);
CREATE INDEX chat_messages_created_at_id_idx ON chat_messages (created_at DESC, id DESC);

-- Create a system account for default rooms
INSERT INTO accounts (id, email, password) VALUES
    ('00000000-0000-0000-0000-000000000000', 'system@example.com', '$2a$10$dummy.password.hash');

-- Insert default general room with system account as creator
INSERT INTO rooms (name, description, created_by) VALUES
    ('general', 'General chat room', '00000000-0000-0000-0000-000000000000');
