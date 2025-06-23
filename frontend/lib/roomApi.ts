/**
 * ルームの作成、検索、参加を行うためのAPIです。
 *
 * @author 02001
 * @componentId C1
 * @moduleName ルームAPI
 * @see apiClient
 * @packageDocumentation
 */

import { CreateRoomRequest, JoinRoomRequest, Room } from '../types/room'
import apiClient from './apiClient'

/**
 * ルームAPIを提供するモジュールです。
 */
export const roomApi = {
    createRoom: async (request: CreateRoomRequest): Promise<Room> => {
        const response = await apiClient.post<Room>('/rooms', request)
        return response.data
    },
    searchRooms: async (keyword: string): Promise<{ rooms: Room[] }> => {
        const response = await apiClient.get<Room[]>('/rooms/search', { params: { keyword } })
        return { rooms: response.data }
    },
    joinRoom: async (request: JoinRoomRequest) => {
        const response = await apiClient.post('/rooms/join', request)
        return response.data
    },
}
