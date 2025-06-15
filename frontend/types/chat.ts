import { UserId, Username, User } from './auth';
import { RoomId } from './room';

export type MessageContent = string; // チャットメッセージの内容

export interface ChatMessage {
  messageId: string; // UUIDなど
  roomId: RoomId;
  sender: User; // 送信ユーザー情報
  content: MessageContent;
  sentAt: string; // 送信時刻 (Instant)
}

export interface UserProgress {
  userId: UserId;
  username: Username;
  currentPage: number; // 現在のページ
  totalPage: number; // 総ページ数 (本の情報として持つか？)
  updatedAt: string; // 更新時刻 (Instant)
}

export interface SendMessageRequest {
  roomId: RoomId;
  // senderId: UserId; // バックエンドでセッションから取得
  content: MessageContent;
}

export interface RecordProgressRequest {
  roomId: RoomId;
  // userId: UserId; // バックエンドでセッションから取得
  currentPage: number;
}
