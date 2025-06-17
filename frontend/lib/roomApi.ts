import apiClient from './apiClient'
import { Room, CreateRoomRequest, SearchRoomResult } from '../types/room'

export const roomApi = {
    createRoom: async (request: CreateRoomRequest & { password?: string }): Promise<Room> => {
        const response = await apiClient.post<Room>('/rooms', request)
        return response.data
    },
    searchRooms: async (keyword: string): Promise<{ rooms: Room[] }> => {
        const response = await apiClient.get<Room[]>('/rooms/search', { params: { keyword } })
        return { rooms: response.data }
    },
    joinRoom: async (roomId: string, password?: string) => {
        const response = await apiClient.post('/rooms/join', { roomId, roomPassword: password })
        return response.data
    },
}
