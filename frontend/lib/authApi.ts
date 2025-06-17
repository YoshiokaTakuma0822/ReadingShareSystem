import apiClient from './apiClient'
import { LoginRequest, LoginResponse, RegisterUserRequest, User } from '../types/auth'

export const authApi = {
    login: async (request: LoginRequest): Promise<LoginResponse> => {
        const response = await apiClient.post('/auth/login', request)
        return response.data
    },
    register: async (request: RegisterUserRequest): Promise<User> => {
        const response = await apiClient.post('/auth/register', request)
        return response.data
    },
}
