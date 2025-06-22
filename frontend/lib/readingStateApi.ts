import apiClient from './apiClient'
import {
    RoomReadingState,
    UpdateUserReadingStateRequest,
    RoomReadingStateResponse
} from '../types/readingState'

export const readingStateApi = {
    updateUserReadingState: async (roomId: string, memberId: string, request: UpdateUserReadingStateRequest): Promise<void> => {
        await apiClient.post(`/rooms/${roomId}/states/${memberId}`, request)
    },

    getRoomReadingState: async (roomId: string, memberId: string): Promise<RoomReadingStateResponse> => {
        const response = await apiClient.get(`/rooms/${roomId}/states/${memberId}`)
        return response.data
    },
}
