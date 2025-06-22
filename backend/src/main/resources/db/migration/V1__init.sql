-- ユーザーテーブル
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    joined_at TIMESTAMP NOT NULL
);

-- 読書部屋テーブル
CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    book_title VARCHAR(200) NOT NULL,
    host_user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    room_password_hash VARCHAR(255),
    max_page INTEGER,
    genre VARCHAR(100),
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    page_speed INTEGER
);

-- 部屋メンバーテーブル
CREATE TABLE room_members (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id),
    user_id UUID NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    CONSTRAINT unique_room_member UNIQUE (room_id, user_id)
);

-- チャットメッセージテーブル
CREATE TABLE chat_messages (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id),
    sender_user_id UUID NOT NULL,
    message_content TEXT NOT NULL,
    sent_at TIMESTAMP NOT NULL
);

-- ユーザー読書進捗テーブル
CREATE TABLE user_progress (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id),
    user_id UUID NOT NULL,
    current_page INTEGER NOT NULL CHECK (current_page >= 0),
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT unique_user_progress UNIQUE (room_id, user_id)
);

-- アンケートテーブル
CREATE TABLE surveys (
    id UUID PRIMARY KEY,
    room_id UUID NOT NULL REFERENCES rooms(id),
    title VARCHAR(255) NOT NULL,
    questions JSONB NOT NULL,
    created_at TIMESTAMP NOT NULL
);

-- アンケート回答テーブル
CREATE TABLE survey_answers (
    id UUID PRIMARY KEY,
    survey_id UUID NOT NULL REFERENCES surveys(id),
    user_id UUID NOT NULL,
    answers JSONB NOT NULL,
    answered_at TIMESTAMP NOT NULL,
    is_anonymous BOOLEAN DEFAULT FALSE,
    CONSTRAINT unique_survey_answer UNIQUE (survey_id, user_id)
);

-- 認証トークンテーブル
CREATE TABLE auth_tokens (
    id UUID PRIMARY KEY,
    token_value VARCHAR(128) UNIQUE NOT NULL,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_used_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT true
);

-- インデックスの作成
CREATE INDEX idx_rooms_host_user_id ON rooms(host_user_id);
CREATE INDEX idx_chat_messages_room_id ON chat_messages(room_id);
CREATE INDEX idx_chat_messages_sent_at ON chat_messages(sent_at);
CREATE INDEX idx_user_progress_room_id ON user_progress(room_id);
CREATE INDEX idx_surveys_room_id ON surveys(room_id);
CREATE INDEX idx_survey_answers_survey_id ON survey_answers(survey_id);
CREATE INDEX idx_survey_answers_is_anonymous ON survey_answers(is_anonymous);
CREATE INDEX idx_auth_tokens_token_value ON auth_tokens(token_value);
CREATE INDEX idx_auth_tokens_user_id ON auth_tokens(user_id);
CREATE INDEX idx_auth_tokens_expires_at ON auth_tokens(expires_at);
CREATE INDEX idx_auth_tokens_active_expires ON auth_tokens(is_active, expires_at) WHERE is_active = true;

-- コメント
COMMENT ON TABLE auth_tokens IS '認証トークンテーブル - Bearer Token認証のためのopaqueトークンを管理';
COMMENT ON COLUMN auth_tokens.id IS 'トークンのユニークID';
COMMENT ON COLUMN auth_tokens.token_value IS 'opaqueトークン値（Base64エンコード）';
COMMENT ON COLUMN auth_tokens.user_id IS 'トークンの所有者ユーザーID';
COMMENT ON COLUMN auth_tokens.created_at IS 'トークン作成時刻';
COMMENT ON COLUMN auth_tokens.expires_at IS 'トークン有効期限';
COMMENT ON COLUMN auth_tokens.last_used_at IS '最終使用時刻';
COMMENT ON COLUMN auth_tokens.is_active IS 'トークンが有効かどうか';
