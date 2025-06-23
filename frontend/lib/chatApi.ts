/**
 * チャットの送信、履歴取得、ストリーム機能を提供するAPIです。
 *
 * @author 02001
 * @componentId C1
 * @moduleName チャットAPI
 * @see apiClient
 * @packageDocumentation
 */

import { ChatMessage, ChatStreamItem, SendMessageRequest } from '../types/chat'
import apiClient from './apiClient'

/**
 * チャットAPIを提供するモジュールです。
 * チャットメッセージの送信、チャット履歴の取得、チャットストリームの取得を行います。
 */
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
