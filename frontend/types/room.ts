import { UserId, Username, User } from './auth';

export type RoomId = string; // 部屋ID (UUIDなど)

export interface Room {
  roomId: RoomId;
  roomName: string;
  hostUser: User; // ホストユーザー情報
  maxMembers: number; // 最大参加人数
  createdAt: string; // 部屋作成時刻 (Instant)
}

export interface CreateRoomRequest {
  roomName: string;
  maxMembers: number;
  password?: string;
  comment?: string;
}

export interface JoinRoomRequest {
  roomId: RoomId;
}

export interface SearchRoomResult {
  rooms: Room[];
}

export interface RoomMember {
  name: string;
  page: number;
  color?: string;
}
