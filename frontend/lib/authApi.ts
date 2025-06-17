import apiClient from './apiClient'

export const authApi = {
    login: async (username: string, password: string) => {
        const response = await apiClient.post('/auth/login', { username, password })
        return response.data
    },
    register: async (username: string, password: string) => {
        const response = await apiClient.post('/auth/register', { username, password })
        return response.data
    },
}
