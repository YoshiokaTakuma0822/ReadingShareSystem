#!/usr/bin/env bash

# sh backend/b.sh

# --- 設定: ユーザー認証情報と部屋IDを変数にセット ---
# ランダムなユーザー名と固定パスワードを使用
USERNAME="user_$(date +%s)"
PASSWORD="password123"
ROOM_ID="replace_with_room_uuid"  # 対象の部屋ID (UUID) に置き換えてください

# --- 新規ユーザー登録（ランダムユーザー名） ---
echo "[STEP] 新規ユーザー登録中..."
REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"'$USERNAME'","password":"'$PASSWORD'"}')
USER_ID=$(echo "$REGISTER_RESPONSE" | jq -r .)
echo "[STEP] 登録されたユーザーID: $USER_ID"
echo "[RESPONSE] REGISTER_RESPONSE: $REGISTER_RESPONSE"

# --- ログインしてBearerトークンを取得 ---
echo "[STEP] ログイン中..."
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"'$USERNAME'","password":"'$PASSWORD'"}')
echo "[RESPONSE] LOGIN_RESPONSE: $LOGIN_RESPONSE"

# --- レスポンスからトークン抽出 ---
TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r .token)
echo "[STEP] 取得したトークン: $TOKEN"

echo "[STEP] 部屋作成中..."
# 開始・終了時刻を生成 (UTC)
START_TIME=$(date -u +"%Y-%m-%dT%H:%M")
# macOS の date で1時間後
END_TIME=$(date -u -v+1H +"%Y-%m-%dT%H:%M")
ROOM_RESPONSE=$(curl -s -X POST http://localhost:8080/api/rooms \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "roomName": "room_'$START_TIME'",
    "hostUserId": "'$USER_ID'",
    "bookTitle": "Test Book",
    "password": "",
    "totalPages": 100,
    "genre": "テスト",
    "startTime": "'$START_TIME'",
    "endTime": "'$END_TIME'"
  }')
echo "[RESPONSE] ROOM_RESPONSE: $ROOM_RESPONSE"
ROOM_ID=$(echo "$ROOM_RESPONSE" | jq -r .id)
echo "[STEP] 作成された部屋ID: $ROOM_ID"

# --- アンケート作成 ---
echo "[STEP] アンケート作成中..."
CREATE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/surveys \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "roomId":"'$ROOM_ID'",
    "title":"テストアンケート",
    "questions":[
      {
        "questionText":"本日の満足度はいかがですか？",
        "options":["良い","普通","悪い"],
        "questionType":"SINGLE_CHOICE",
        "allowAnonymous":false,
        "allowAddOptions":false
      }
    ]
  }')
echo "[RESPONSE] CREATE_RESPONSE: $CREATE_RESPONSE"
# --- 回答送信 ---
SURVEY_ID=$(echo "$CREATE_RESPONSE" | jq -r .)
echo "[STEP] アンケートID: $SURVEY_ID への回答送信中..."
ANSWER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/surveys/$SURVEY_ID/answers \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "userId":"'$USER_ID'",
    "answers":{
      "本日の満足度はいかがですか？":["良い"]
    },
    "isAnonymous":false
  }')
echo "[RESPONSE] ANSWER_RESPONSE: $ANSWER_RESPONSE"
echo "[RESULT] 回答送信完了"
