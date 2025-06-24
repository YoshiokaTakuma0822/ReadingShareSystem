import apiClient from './apiClient'
import { Room, CreateRoomRequest, SearchRoomResult, JoinRoomRequest } from '../types/room'

export const roomApi = {
    createRoom: async (request: CreateRoomRequest): Promise<Room> => {
        const response = await apiClient.post<Room>('/rooms', request)
        return response.data
    },
    searchRooms: async (
        keyword: string,
        roomType?: string,
        genre?: string,
        minPages?: string,
        maxPages?: string,
        startTimeFrom?: string,
        startTimeTo?: string,
        endTimeFrom?: string,
        endTimeTo?: string
    ): Promise<{ rooms: Room[] }> => {
        const params: any = { keyword };
        if (roomType && roomType !== 'all') params.roomType = roomType;
        if (genre) params.genre = genre;
        if (minPages) params.minPages = minPages;
        if (maxPages) params.maxPages = maxPages;
        if (startTimeFrom) params.startTimeFrom = startTimeFrom;
        if (startTimeTo) params.startTimeTo = startTimeTo;
        if (endTimeFrom) params.endTimeFrom = endTimeFrom;
        if (endTimeTo) params.endTimeTo = endTimeTo;
        const response = await apiClient.get<Room[]>('/rooms/search', { params });
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
    deleteRoom: async (roomId: string): Promise<void> => {
        await apiClient.delete(`/rooms/${roomId}`)
    },
    updateRoom: async (roomId: string, update: { maxPage?: number }) => {
        const response = await apiClient.patch<Room>(`/rooms/${roomId}`, update)
        return response.data
    },
    searchRoomsByGenre: async (genre: string): Promise<{ rooms: Room[] }> => {
        const response = await apiClient.get<Room[]>(`/rooms/genre`, { params: { genre } })
        return { rooms: response.data }
    },
    getRoomMembers: async (roomId: string) => {
        const response = await apiClient.get(`/rooms/${roomId}/members`);
        return response.data;
    },
}
