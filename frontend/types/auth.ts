export type UserId = number; // user id: 整数, >0
export type Username = string; // username: 文字列, < 100文字
export type Password = string; // パスワード (フロントエンドではハッシュ化前のものを扱う)
export type Email = string; // メールアドレス (認証に使用する場合)

export interface User {
  userId: UserId;
  username: Username;
  // contents: string; // JSON形式で管理される内容。具体的な用途不明なため一旦省略
  joinedAt: string; // Instant型はISO 8601形式の文字列で送受信されると想定
}

export interface LoginRequest {
  username: Username;
  password: Password;
}

export interface LoginResponse {
  token: string; // 認証トークン
  user: User;
}

export interface RegisterUserRequest {
  username: Username;
  password: Password;
  email: Email; // 会員登録時に入力する場合
}
