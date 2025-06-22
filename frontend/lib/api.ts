import apiClient from './apiClient'
import { RoomReadingStateResponse, UpdateUserReadingStateRequest } from '../types/readingState'

export async function fetchRoomState(roomId: string): Promise<RoomReadingStateResponse> {
    const response = await apiClient.get(`/rooms/${roomId}/states`)
    return response.data
}

export async function updateUserReadingState(roomId: string, userId: string, request: UpdateUserReadingStateRequest): Promise<void> {
    const response = await apiClient.post(`/rooms/${roomId}/states/${userId}`, request)
    return response.data
}

export async function turnPage(roomId: string, userId: string, direction: 'next' | 'prev'): Promise<void> {
    const response = await apiClient.post(`/progress/${roomId}/record`, {
        userId,
        direction
    })
    return response.data
}
