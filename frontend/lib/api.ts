import { MessageResponse, Room } from "@/types/chat"
import { Member } from "@/types/auth"
import { fetchWithTokenRefresh } from "./apiUtils"

/**
 * Fetch recent chat messages
 */
export async function getRecentMessages(): Promise<MessageResponse[]> {
    // Default to room 1 (general room) for backwards compatibility
    return getRecentMessagesFromRoom(1)
}

/**
 * Fetch recent chat messages from a specific room
 */
export async function getRecentMessagesFromRoom(roomId: number): Promise<MessageResponse[]> {
    const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/messages`)
    if (!response.ok) throw new Error(`Failed to fetch messages: ${response.status}`)
    return response.json()
}

/**
 * Fetch active users
 */
export async function getActiveUsers(): Promise<Member[]> {
    // Use room 1 (general room) members as fallback
    const response = await fetchWithTokenRefresh("/api/rooms/1/members")
    if (!response.ok) throw new Error(`Failed to fetch active users: ${response.status}`)
    return response.json()
}

/**
 * Fetch all available rooms
 */
export async function getRooms(): Promise<Room[]> {
    const response = await fetchWithTokenRefresh("/api/rooms")
    if (!response.ok) throw new Error(`Failed to fetch rooms: ${response.status}`)
    return response.json()
}

/**
 * Send a new chat message to a specific room
 */
export async function sendMessageToRoom(roomId: number, content: string): Promise<MessageResponse> {
    const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/messages`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ content })
    })
    if (!response.ok) throw new Error(`Failed to send message: ${response.status}`)
    return response.json()
}

/**
 * Send a new chat message
 */
export async function sendMessage(content: string): Promise<MessageResponse> {
    // Default to room 1 (general room) for backwards compatibility
    return sendMessageToRoom(1, content)
}

/**
 * Fetch older chat messages before a specific message ID
 */
export async function getMessagesBefore(
    beforeId: number,
    limit: number = 20
): Promise<MessageResponse[]> {
    // Default to room 1 (general room) for backwards compatibility
    return getMessagesBeforeFromRoom(1, beforeId, limit)
}

/**
 * Fetch older chat messages before a specific message ID from a specific room
 */
export async function getMessagesBeforeFromRoom(
    roomId: number,
    beforeId: number,
    limit: number = 20
): Promise<MessageResponse[]> {
    const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/messages?beforeId=${beforeId}&limit=${limit}`)
    if (!response.ok) throw new Error(`Failed to fetch older messages: ${response.status}`)
    return response.json()
}

/**
 * Refresh authentication token
 *
 * Call this function when the current token is expired or about to expire
 */
export async function refreshToken(): Promise<{ accessToken: string; expiresAt?: number; refreshToken?: string }> {
    // This request should not use fetchWithTokenRefresh to avoid infinite loops
    const response = await fetch("/api/accounts/refresh", {
        method: "POST",
        credentials: "include"
    })
    if (!response.ok) throw new Error(`Failed to refresh token: ${response.status}`)
    return response.json()
}

/**
 * Create a new room
 */
export async function createRoom(roomData: { name: string; description?: string }): Promise<Room> {
    const response = await fetchWithTokenRefresh("/api/rooms", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(roomData)
    })
    if (!response.ok) throw new Error(`Failed to create room: ${response.status}`)
    return response.json()
}

/**
 * Join a specific room
 * Checks for existing membership before joining to avoid 409 conflicts
 */
export async function joinRoom(roomId: number, memberName: string): Promise<void> {
    // First check if user is already a member using GET /api/rooms/{roomId}/members/me
    console.log(`Checking existing membership in room ${roomId} before joining...`)
    const existingMember = await getCurrentMemberInRoom(roomId)

    if (existingMember) {
        console.log(`Already a member of room ${roomId}:`, existingMember.name)
        return // Already a member, no need to join again
    }

    // If not a member, join the room
    console.log(`Joining room ${roomId} as ${memberName}...`)
    const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/members`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: memberName })
    })
    if (!response.ok) throw new Error(`Failed to join room: ${response.status}`)
}

/**
 * Join a specific room and return the member object
 * Similar to joinRoom but returns the created/existing member
 * Ensures room exists before attempting to join
 */
export async function joinRoomAndGetMember(roomId: number, memberName: string): Promise<Member> {
    // First ensure the room exists
    console.log(`Checking if room ${roomId} exists...`)
    const room = await getRoom(roomId)
    if (!room) {
        throw new Error(`Room ${roomId} does not exist. Cannot create member in non-existent room.`)
    }

    // Check if user is already a member using GET /api/rooms/{roomId}/members/me
    console.log(`Checking existing membership in room ${roomId} before joining...`)
    const existingMember = await getCurrentMemberInRoom(roomId)

    if (existingMember) {
        console.log(`Already a member of room ${roomId}:`, existingMember.name)
        return existingMember // Already a member, return existing member
    }

    // If not a member, join the room and return the created member
    console.log(`Joining room ${roomId} as ${memberName}...`)
    const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/members`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ name: memberName })
    })
    if (!response.ok) throw new Error(`Failed to join room: ${response.status}`)

    // Return the created member from the response
    return response.json()
}

/**
 * Get a specific room by ID
 */
export async function getRoom(roomId: number): Promise<Room | null> {
    try {
        const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}`)
        if (response.status === 404) return null
        if (!response.ok) throw new Error(`Failed to fetch room: ${response.status}`)
        return response.json()
    } catch (error) {
        console.error(`Error fetching room ${roomId}:`, error)
        return null
    }
}

/**
 * Get all members in a specific room
 */
export async function getRoomMembers(roomId: number): Promise<Member[]> {
    const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/members`)
    if (!response.ok) throw new Error(`Failed to fetch room members: ${response.status}`)
    return response.json()
}

/**
 * Check if current member is already in a specific room
 */
export async function isMemberInRoom(roomId: number): Promise<boolean> {
    try {
        // Use the dedicated membership check endpoint
        const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/members/me`)

        // If we get 200, we're a member
        // If we get 404, we're not a member
        if (response.status === 404) {
            return false
        }

        if (!response.ok) {
            console.warn(`Unexpected response when checking room membership: ${response.status}`)
            return false
        }

        // If we successfully got the member details, we're in the room
        return true
    } catch (error) {
        console.error("Error checking room membership:", error)
        return false
    }
}

/**
 * Get current user's member information for a specific room
 */
export async function getCurrentMemberInRoom(roomId: number): Promise<Member | null> {
    try {
        const response = await fetchWithTokenRefresh(`/api/rooms/${roomId}/members/me`)

        if (response.status === 404) {
            return null
        }

        if (!response.ok) {
            throw new Error(`Failed to get current member: ${response.status}`)
        }

        return response.json()
    } catch (error) {
        console.error("Error getting current member:", error)
        return null
    }
}
