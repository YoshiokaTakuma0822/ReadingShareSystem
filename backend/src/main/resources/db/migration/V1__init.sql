-- backend/src/main/resources/db/migration/V1__init.sql
-- Initial migration (V1): users/auth_tokens/rooms/room_members/chat_messages/user_progress/surveys/survey_answers を一括作成

-- ユーザーテーブル
CREATE TABLE users (
    id             UUID PRIMARY KEY,
    username       VARCHAR(255) NOT NULL UNIQUE,
    password_hash  VARCHAR(255) NOT NULL,
    joined_at      TIMESTAMP WITH TIME ZONE NOT NULL,
    history_reset_at TIMESTAMP WITH TIME ZONE
);

-- 認証トークンテーブル
CREATE TABLE auth_tokens (
    id           UUID PRIMARY KEY,
    token_value  VARCHAR(128) UNIQUE NOT NULL,
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    last_used_at TIMESTAMP WITH TIME ZONE,
    is_active    BOOLEAN NOT NULL DEFAULT true
);

-- 読書部屋テーブル
CREATE TABLE rooms (
    id             UUID PRIMARY KEY,
    room_name      VARCHAR(100) NOT NULL,
    book_title     VARCHAR(200) NOT NULL,
    host_user_id   UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    end_time       TIMESTAMP WITH TIME ZONE,
    total_pages    INTEGER NOT NULL DEFAULT 0,
    password_hash  VARCHAR(255),
    genre          VARCHAR(100),
    start_time     TIMESTAMP WITH TIME ZONE
);

-- 部屋メンバーテーブル
CREATE TABLE room_members (
    id           UUID PRIMARY KEY,
    room_id      UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    joined_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT unique_room_member UNIQUE (room_id, user_id)
);

-- チャットメッセージテーブル
CREATE TABLE chat_messages (
    id              UUID PRIMARY KEY,
    room_id         UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    sender_user_id  UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    message_content TEXT NOT NULL,
    sent_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    message_type    VARCHAR(20) NOT NULL,
    survey_id       UUID REFERENCES surveys(id) ON DELETE SET NULL
);

-- ユーザー読書進捗テーブル
CREATE TABLE user_progress (
    id           UUID PRIMARY KEY,
    room_id      UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    user_id      UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    current_page INTEGER NOT NULL CHECK (current_page >= 0),
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT unique_user_progress UNIQUE (room_id, user_id)
);

-- アンケートテーブル
CREATE TABLE surveys (
    id         UUID PRIMARY KEY,
    room_id    UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    title      VARCHAR(255) NOT NULL,
    questions  JSONB NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

-- アンケート回答テーブル
CREATE TABLE survey_answers (
    id          UUID PRIMARY KEY,
    survey_id   UUID NOT NULL REFERENCES surveys(id) ON DELETE CASCADE,
    user_id     UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    answers     JSONB NOT NULL,
    answered_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT unique_survey_answer UNIQUE (survey_id, user_id)
);

-- インデックス
CREATE INDEX idx_users_username               ON users(username);
CREATE INDEX idx_auth_tokens_token_value      ON auth_tokens(token_value);
CREATE INDEX idx_auth_tokens_user_id          ON auth_tokens(user_id);
CREATE INDEX idx_auth_tokens_expires_at       ON auth_tokens(expires_at);
CREATE INDEX idx_auth_tokens_active_expires   ON auth_tokens(is_active, expires_at) WHERE is_active = true;
CREATE INDEX idx_rooms_host_user_id           ON rooms(host_user_id);
CREATE INDEX idx_chat_messages_room_id        ON chat_messages(room_id);
CREATE INDEX idx_chat_messages_sent_at        ON chat_messages(sent_at);
CREATE INDEX idx_chat_messages_survey_id      ON chat_messages(survey_id);
CREATE INDEX idx_chat_messages_message_type   ON chat_messages(message_type);
CREATE INDEX idx_user_progress_room_id        ON user_progress(room_id);
CREATE INDEX idx_surveys_room_id              ON surveys(room_id);
CREATE INDEX idx_survey_answers_survey_id     ON survey_answers(survey_id);
