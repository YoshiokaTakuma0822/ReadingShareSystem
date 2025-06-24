/**
 * ルーム内のユーザー読書状態を更新および取得するためのAPIです。
 *
 * @author 02001
 * @componentId C1
 * @moduleName 読書状態API
 * @see apiClient
 * @packageDocumentation
 */

import {
    RoomReadingStateResponse,
    UpdateUserReadingStateRequest
} from '../types/readingState'
import apiClient from './apiClient'

/**
 * 読書状態APIを提供するモジュールです。
 * ルーム内のユーザーの読書状態を更新および取得する機能を提供します。
 *
 * @returns updateUserReadingState, getRoomReadingState メソッドを含むオブジェクト
 */
export const readingStateApi = {
    updateUserReadingState: async (roomId: string, memberId: string, request: UpdateUserReadingStateRequest): Promise<void> => {
        await apiClient.post(`/rooms/${roomId}/states/${memberId}`, request)
    },

    getRoomReadingState: async (roomId: string, memberId: string): Promise<RoomReadingStateResponse> => {
        const response = await apiClient.get(`/rooms/${roomId}/states/${memberId}`)
        return response.data
    },
}
