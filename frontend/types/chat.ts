export type MessageType = 'CHAT' | 'JOIN' | 'LEAVE'

// Room interface
export interface Room {
    id: number
    name: string
    description: string
}

export interface MessageResponse {
    id: number
    senderId: number
    roomId: number
    content: string
    createdAt: string
    type: MessageType
}

// Member lookup map type for efficient name resolution
export interface MemberMap {
    [memberId: number]: {
        id: number
        name: string
    }
}
