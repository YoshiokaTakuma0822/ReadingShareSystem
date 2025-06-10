#!/bin/sh
# ───────────────────────────────────────────
# entrypoint.sh
# コンテナ起動時に /jwt_secret.txt の存在をチェックし、
# なければ新規生成してファイルに保存、あればそのまま利用。
# いずれの場合も JWT_SECRET を環境変数にセットし、
# 最後に java プロセスを実行する。
# ───────────────────────────────────────────

# jwt_secret ファイルのパス
SECRET_FILE="/jwt_secret.txt"

# ファイルが存在しない場合は新規生成
if [ ! -f "$SECRET_FILE" ]; then
  openssl rand -base64 32 > "$SECRET_FILE"
fi

# ファイルの中身を環境変数にセット
export JWT_SECRET="$(cat "$SECRET_FILE")"

# アプリケーションを実行
java -jar /app/app.jar
