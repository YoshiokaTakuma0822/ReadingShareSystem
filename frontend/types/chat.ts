import { UserId, Username, User } from './auth'
import { RoomId } from './room'

export type MessageContent = string // チャットメッセージの内容

export interface ChatMessage {
    id: string // UUID (バックエンドのエンティティに合わせてmessageIdからidに変更)
    roomId: RoomId
    senderUserId: UserId | null // バックエンドのエンティティに合わせてsenderからsenderUserIdに変更、匿名ユーザーの場合null
    content: MessageContent // バックエンドではMessageContentオブジェクトだが、フロントエンドでは文字列として扱う
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
}

export interface RecordProgressRequest {
    roomId: RoomId
    currentPage: number
}
