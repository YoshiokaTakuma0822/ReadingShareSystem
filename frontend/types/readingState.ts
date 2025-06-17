import { UserId } from './auth'
import { RoomId } from './room'

export interface UserReadingState {
    userId: UserId
    currentPage: number
    comment: string
}

export interface RoomReadingState {
    roomId: RoomId
    userStates: UserReadingState[]
}

export interface UpdateUserReadingStateRequest {
    userId: UserId
    currentPage: number
    comment: string
}

export interface UserReadingStateResponse {
    userId: UserId
    currentPage: number
    comment: string
}

export interface RoomReadingStateResponse {
    roomId: RoomId
    userStates: UserReadingStateResponse[]
}
