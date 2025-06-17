import apiClient from './apiClient'

export interface RecordProgressRequest {
    userId: string
    currentPage: number
    comment?: string
}

export interface UserProgress {
    userId: string
    currentPage: number
    comment?: string
    lastUpdated: string
}

export const progressApi = {
    recordProgress: async (roomId: string, request: RecordProgressRequest): Promise<void> => {
        await apiClient.post(`/progress/${roomId}/record`, request)
    },

    getRoomProgress: async (roomId: string): Promise<UserProgress[]> => {
        const response = await apiClient.get<UserProgress[]>(`/progress/${roomId}`)
        return response.data
    },
}
