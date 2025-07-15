# ReadingShareSystem
ReadingShareSystemは、読書コミュニティ向けのプラットフォームです。ユーザーは読書ルームを作成・参加して、進捗を共有したり、グループチャットやアンケート機能を通じてディスカッションを行えます。

## 主な機能
- 読書ルームの作成・参加
- リアルタイムチャット (グループチャット)
- 読書進捗の管理・共有
- アンケート作成・回答
- 認証・会員管理

## 技術スタック
| コンポーネント | 技術                                               |
|---------------|----------------------------------------------------|
| フロントエンド | Next.js (React, TypeScript)、Tailwind CSS          |
| バックエンド   | Spring Boot (Java)、Maven                         |
| APIクライアント| Axios、React Query                                 |
| データベース   | PostgreSQL                                         |
| インフラ       | Docker、Docker Compose、NGINX                     |

## プロジェクト構成
```
ReadingShareSystem/
├─ backend/        # Spring Boot アプリケーション
├─ frontend/       # Next.js フロントエンド
└─ nginx/          # リバースプロキシ設定
```

## セットアップと実行方法
ローカル開発環境では Docker Compose を使用します。

1. Docker イメージのビルドとコンテナ起動
   ```bash
   docker compose up --build
   ```
2. ブラウザで http://localhost を開く

## 開発
- フロントエンド: `cd frontend && npm install && npm run dev`
- バックエンド: `cd backend && ./mvnw spring-boot:run`
