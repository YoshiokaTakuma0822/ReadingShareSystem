import apiClient from './apiClient'
import { ChatMessage, SendMessageRequest, ChatStreamItem } from '../types/chat'

export const chatApi = {
    sendMessage: async (roomId: string, request: SendMessageRequest): Promise<void> => {
        await apiClient.post(`/chat/${roomId}/message`, request)
    },

    getChatHistory: async (roomId: string): Promise<ChatMessage[]> => {
        const response = await apiClient.get<ChatMessage[]>(`/chat/${roomId}/history`)
        return response.data
    },

    getChatStream: async (roomId: string): Promise<ChatStreamItem[]> => {
        const response = await apiClient.get<ChatStreamItem[]>(`/rooms/${roomId}/stream`)
        return response.data
    },
}
