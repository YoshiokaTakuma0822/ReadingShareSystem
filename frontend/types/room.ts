import { UserId, Username, User } from './auth'

export type RoomId = string // 部屋ID (UUID)

export interface Room {
    id: RoomId // バックエンドのエンティティに合わせてroomIdからidに変更
    roomName: string
    bookTitle: string // バックエンドのエンティティから追加
    hostUserId: UserId // バックエンドのエンティティに合わせてhostUserからhostUserIdに変更
    createdAt: string // 部屋作成時刻 (Instant)
    hasPassword: boolean // パスワード保護されているかどうか
}

export interface CreateRoomRequest {
    roomName: string
    bookTitle: string // バックエンドのAPIに合わせて追加
    hostUserId: UserId // バックエンドのAPIに合わせて追加
    password?: string // オプショナル
}

export interface JoinRoomRequest {
    roomId: RoomId
    userId: UserId // バックエンドのAPIに合わせて追加
    roomPassword?: string // バックエンドのAPIに合わせてpasswordからroomPasswordに変更
}

export interface SearchRoomResult {
    rooms: Room[]
}

export interface RoomMember {
    id: string // UUID
    roomId: RoomId
    userId: UserId
    joinedAt: string // Instant
}
