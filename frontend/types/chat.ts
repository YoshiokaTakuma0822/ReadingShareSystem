import { UserId, Username, User } from './auth'
import { RoomId } from './room'

export type MessageContent = string | { value: string } // チャットメッセージの内容（バックエンドではオブジェクト形式）

export interface ChatMessage {
    id: string // UUID (バックエンドのエンティティに合わせてmessageIdからidに変更)
    roomId: RoomId
    senderUserId: UserId | null // バックエンドのエンティティに合わせてsenderからsenderUserIdに変更、匿名ユーザーの場合null
    senderUsername: Username // 追加: バックエンドから返すユーザー名
    content: MessageContent // バックエンドではMessageContentオブジェクト
    sentAt: string // 送信時刻 (Instant)
}

export interface UserProgress {
    id: string // UUID
    roomId: RoomId
    userId: UserId
    currentPage: number // 現在のページ
    updatedAt: string // 更新時刻 (Instant)
}

export interface SendMessageRequest {
    messageContent: string // バックエンドのDTOに合わせてcontentからmessageContentに変更
    sentAt: string // 送信時刻（ISO8601文字列）を追加
}

export interface RecordProgressRequest {
    roomId: RoomId
    currentPage: number
}
