import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export const authService = {
  login: async (id: string, password: string) => {
    const response = await axios.post(`${API_BASE_URL}/auth/login`, { id, password });
    return response.data;
  },
  register: async (id: string, password: string) => {
    const response = await axios.post(`${API_BASE_URL}/auth/register`, { id, password });
    return response.data;
  },
};
