import { UserId, Username, User } from './auth'

export type RoomId = string // 部屋ID (UUID)

export interface Room {
    id: RoomId // バックエンドのエンティティに合わせてroomIdからidに変更
    roomName: string
    bookTitle: string // バックエンドのエンティティから追加
    hostUserId: UserId // バックエンドのエンティティに合わせてhostUserからhostUserIdに変更
    hostUsername: string // 部屋作成者のユーザー名を追加
    createdAt: string // 部屋作成時刻 (Instant)
    hasPassword: boolean // パスワード保護されているかどうか
    genre?: string
    startTime?: string
    endTime?: string
    maxPage?: number // 本の最大ページ数
    pageSpeed?: number // ページごとの読書速度
}

export interface CreateRoomRequest {
    roomName: string
    bookTitle: string // バックエンドのAPIに合わせて追加
    hostUserId: UserId // バックエンドのAPIに合わせて追加
    password?: string // オプショナル
    genre?: string
    startTime?: string
    endTime?: string
    maxPage?: number // 本の最大ページ数
    pageSpeed?: number // ページごとの読書速度
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
    username: string // ユーザー名を追加
}

export interface RoomHistoryDto {
    roomId: string;
    room: Room | null; // nullなら削除済み
    deleted: boolean;
    joinedAt: string;
}
