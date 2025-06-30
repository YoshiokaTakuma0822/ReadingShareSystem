import apiClient from './apiClient'
import { Room, CreateRoomRequest, SearchRoomResult, JoinRoomRequest } from '../types/room'

export const roomApi = {
    createRoom: async (request: CreateRoomRequest): Promise<Room> => {
        const response = await apiClient.post<Room>('/rooms', request)
        return response.data
    },
    searchRooms: async (
        keyword: string,
        genre?: string,
        startFrom?: string,
        startTo?: string,
        endFrom?: string,
        endTo?: string,
        pagesMin?: string,
        pagesMax?: string
    ): Promise<{ rooms: Room[] }> => {
        const params: any = { keyword };
        // ジャンル
        if (genre && genre !== '') params.genre = genre;
        // 開始日時範囲
        if (startFrom && startFrom !== '') params.startFrom = startFrom;
        if (startTo && startTo !== '') params.startTo = startTo;
        // 終了日時範囲
        if (endFrom && endFrom !== '') params.endFrom = endFrom;
        if (endTo && endTo !== '') params.endTo = endTo;
        // ページ数範囲（数値型で送信）
        if (pagesMin && pagesMin !== '') params.pagesMin = Number(pagesMin);
        if (pagesMax && pagesMax !== '') params.pagesMax = Number(pagesMax);
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
    getRoomHistory: async (userId: string, limit: number = 10) => {
        const response = await apiClient.get(`/rooms/history`, { params: { userId, limit } });
        return response.data;
    },
    getRooms: async (limit: number = 10): Promise<Room[]> => {
        const response = await apiClient.get<Room[]>('/rooms', { params: { limit } });
        return response.data;
    },
    resetRoomHistory: async (userId: string) => {
        await fetch(`/api/rooms/history?userId=${userId}`, { method: 'DELETE' });
    },
}
