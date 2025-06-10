# コントリビュートガイドライン

このリポジトリには Next.js フロントエンドと Spring Boot バックエンドが含まれています。開発時は以下のルールに従ってください。

## 共通準備

- フロントエンドは **Node 24**、バックエンドは **Java 24** を使用します。詳細は `README.md` を参照してください。
- `docker compose up -d --build` でサービスを起動し、`http://localhost` を開いて動作を確認します。

## フロントエンド

- ソースコードは `frontend` ディレクトリにあります。
- [shadcn/ui](https://ui.shadcn.com) 由来のコンポーネントは `components/ui` に配置します。
- 独自に作成したコンポーネントはファイル名の頭に `my-` を付けて区別してください（例: `my-hover-popover.tsx`）。
- TypeScript と ESLint が有効になっています。コミット前に `npm run lint` を実行してください。
- 開発サーバーは `npm run dev` で起動できます。詳細は `frontend/README.md` を参照してください。

## バックエンド

- ソースコードは `backend` ディレクトリにあります。
- 依存性注入は必ずコンストラクタインジェクションを使用し、テスト以外で `@Autowired` を記述しないでください。
- Maven プロジェクトです。`./mvnw spring-boot:run` でアプリケーションを起動します。
- パッケージ構成やレイヤー分割は `src/main/java` の既存実装を参考にしてください。
- API ドキュメントは `http://localhost:8080/v3/api-docs` で確認できます。その他の開発手順は `backend/README.md` を参照してください。
