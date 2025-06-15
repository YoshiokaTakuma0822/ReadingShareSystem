import axios from 'axios';
import { Room, CreateRoomRequest, SearchRoomResult } from '../types/room';

const API_BASE_URL = 'http://localhost:8080/api';

export const roomService = {
  createRoom: async (request: CreateRoomRequest & { password?: string }) : Promise<Room> => {
    const response = await axios.post<Room>(`${API_BASE_URL}/rooms`, request);
    return response.data;
  },
  searchRooms: async (keyword: string): Promise<{ rooms: Room[] }> => {
    const response = await axios.get<Room[]>(`${API_BASE_URL}/rooms/search`, { params: { keyword } });
    return { rooms: response.data };
  },
  joinRoom: async (roomId: string, password?: string) => {
    const response = await axios.post(`${API_BASE_URL}/rooms/join`, { roomId, roomPassword: password });
    return response.data;
  },
};
