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
    getRoom: async (roomId: string): Promise<Room> => {
        const response = await apiClient.get<Room>(`/rooms/${roomId}`)
        return response.data
    },
    joinRoom: async (request: JoinRoomRequest) => {
        const response = await apiClient.post('/rooms/join', request)
        return response.data
    },
    deleteRoom: async (roomId: string) => {
        await apiClient.delete(`/rooms/${roomId}`);
    },
    getMyRooms: async (): Promise<Room[]> => {
        const response = await apiClient.get<Room[]>('/rooms/my-rooms');
        return response.data;
    },
    getRoomMembers: async (roomId: string) => {
        const response = await apiClient.get(`/rooms/${roomId}/members`);
        return response.data;
    },
    updateTotalPages: async (roomId: string, totalPages: number): Promise<Room> => {
        const response = await apiClient.put<Room>(`/rooms/${roomId}`, { totalPages });
        return response.data;
    },
}
