-- ユーザーテーブル
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    contents JSON,
    joined_at TIMESTAMP NOT NULL
);

-- 読書部屋テーブル
CREATE TABLE rooms (
    id UUID PRIMARY KEY,
    room_name VARCHAR(100) NOT NULL,
    book_title VARCHAR(200) NOT NULL,
    host_user_id UUID NOT NULL,
    created_at TIMESTAMP NOT NULL,
    room_password_hash VARCHAR(255)
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
    CONSTRAINT unique_survey_answer UNIQUE (survey_id, user_id)
);

-- インデックスの作成
CREATE INDEX idx_rooms_host_user_id ON rooms(host_user_id);
CREATE INDEX idx_chat_messages_room_id ON chat_messages(room_id);
CREATE INDEX idx_chat_messages_sent_at ON chat_messages(sent_at);
CREATE INDEX idx_user_progress_room_id ON user_progress(room_id);
CREATE INDEX idx_surveys_room_id ON surveys(room_id);
CREATE INDEX idx_survey_answers_survey_id ON survey_answers(survey_id);
