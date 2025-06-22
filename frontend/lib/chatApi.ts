import apiClient from './apiClient'
import { ChatMessage, SendMessageRequest } from '../types/chat'

export const chatApi = {
    sendMessage: async (roomId: string, request: SendMessageRequest): Promise<void> => {
        await apiClient.post(`/chat/${roomId}/message`, request)
    },

    getChatHistory: async (roomId: string): Promise<ChatMessage[]> => {
        const response = await apiClient.get<ChatMessage[]>(`/chat/${roomId}/history`)
        return response.data
    },
}
