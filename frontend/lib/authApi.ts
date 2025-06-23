/**
 * ユーザーのログイン、登録、ログアウト認証機能を提供するAPIです。
 *
 * @author 02001
 * @componentId C1
 * @moduleName 認証API
 * @packageDocumentation
 */

import { LoginRequest, LoginResponse, RegisterUserRequest, RegisterUserResponse } from '../types/auth'
import apiClient from './apiClient'

export const authApi = {
    login: async (request: LoginRequest): Promise<LoginResponse> => {
        const response = await apiClient.post('/auth/login', request)
        // トークンとユーザーIDをローカルストレージに保存
        if (response.data.token) {
            localStorage.setItem('authToken', response.data.token)
        }
        if (response.data.userId) {
            localStorage.setItem('reading-share-user-id', response.data.userId)
        }
        return response.data
    },
    register: async (request: RegisterUserRequest): Promise<RegisterUserResponse> => {
        const response = await apiClient.post('/auth/register', request)
        // バックエンドはユーザーIDのみ返すので、適切な形式に変換
        return { userId: response.data }
    },
    logout: async (): Promise<void> => {
        try {
            await apiClient.post('/auth/logout')
        } finally {
            // サーバーリクエストの成功/失敗に関わらずローカルトークンを削除
            localStorage.removeItem('authToken')
            localStorage.removeItem('reading-share-user-id')
        }
    },
    isAuthenticated: (): boolean => {
        return !!localStorage.getItem('authToken')
    },
}
