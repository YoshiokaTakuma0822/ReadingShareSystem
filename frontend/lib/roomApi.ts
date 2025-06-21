import apiClient from './apiClient'
import { Room, CreateRoomRequest, SearchRoomResult, JoinRoomRequest } from '../types/room'

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
    deleteRoom: async (roomId: string): Promise<void> => {
        await apiClient.delete(`/rooms/${roomId}`)
    },
    getRoom: async (roomId: string): Promise<Room> => {
        const response = await apiClient.get<Room>(`/rooms/${roomId}`)
        return response.data
    },
    updateRoom: async (roomId: string, update: { maxPage?: number }) => {
        const response = await apiClient.patch<Room>(`/rooms/${roomId}`, update)
        return response.data
    },
    searchRoomsByGenre: async (genre: string): Promise<{ rooms: Room[] }> => {
        const response = await apiClient.get<Room[]>(`/rooms/genre`, { params: { genre } })
        return { rooms: response.data }
    },
}
