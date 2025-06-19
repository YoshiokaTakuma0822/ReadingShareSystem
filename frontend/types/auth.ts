export type UserId = string // user id: UUID
export type Username = string // username: 文字列, < 100文字
export type Password = string // パスワード (フロントエンドではハッシュ化前のものを扱う)
export type Email = string // メールアドレス (認証に使用する場合)

export interface User {
    id: UserId // バックエンドのエンティティに合わせてuserIdからidに変更
    username: Username
    joinedAt: string // Instant型はISO 8601形式の文字列で送受信されると想定
}

export interface LoginRequest {
    username: Username
    password: Password
}

export interface LoginResponse {
    userId: UserId
    token: string // Bearer Token
    username: Username
}

export interface RegisterUserResponse {
    userId: UserId // 登録時も同様にユーザーIDを返す
}

export interface RegisterUserRequest {
    username: Username
    password: Password
    email?: Email // オプショナルに変更（バックエンドで使われていない）
}
