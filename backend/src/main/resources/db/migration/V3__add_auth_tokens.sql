-- 認証トークンテーブルの作成
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
CREATE INDEX idx_auth_tokens_token_value ON auth_tokens(token_value);
CREATE INDEX idx_auth_tokens_user_id ON auth_tokens(user_id);
CREATE INDEX idx_auth_tokens_expires_at ON auth_tokens(expires_at);
CREATE INDEX idx_auth_tokens_active_expires ON auth_tokens(is_active, expires_at) WHERE is_active = true;

-- コメントの追加
COMMENT ON TABLE auth_tokens IS '認証トークンテーブル - Bearer Token認証のためのopaqueトークンを管理';
COMMENT ON COLUMN auth_tokens.id IS 'トークンのユニークID';
COMMENT ON COLUMN auth_tokens.token_value IS 'opaqueトークン値（Base64エンコード）';
COMMENT ON COLUMN auth_tokens.user_id IS 'トークンの所有者ユーザーID';
COMMENT ON COLUMN auth_tokens.created_at IS 'トークン作成時刻';
COMMENT ON COLUMN auth_tokens.expires_at IS 'トークン有効期限';
COMMENT ON COLUMN auth_tokens.last_used_at IS '最終使用時刻';
COMMENT ON COLUMN auth_tokens.is_active IS 'トークンが有効かどうか';
