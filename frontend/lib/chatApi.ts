import apiClient from './apiClient'

export interface ChatMessage {
    id: string
    roomId: string
    userId: string
    message: string
    timestamp: string
}

export interface SendMessageRequest {
    userId: string
    message: string
}

export const chatApi = {
    sendMessage: async (roomId: string, request: SendMessageRequest): Promise<void> => {
        await apiClient.post(`/chat/${roomId}/message`, request)
    },

    getChatHistory: async (roomId: string): Promise<ChatMessage[]> => {
        const response = await apiClient.get<ChatMessage[]>(`/chat/${roomId}/history`)
        return response.data
    },
}
