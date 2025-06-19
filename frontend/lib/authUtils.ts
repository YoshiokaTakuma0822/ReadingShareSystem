import React from 'react'
import { UserId } from '../types/auth'
import { authApi } from './authApi'

const USER_ID_KEY = 'reading-share-user-id'
const TOKEN_KEY = 'authToken'

// ブラウザ環境かどうかを確認する関数
const isBrowser = (): boolean => {
    return typeof window !== 'undefined' && typeof localStorage !== 'undefined'
}

export const authStorage = {
    // ユーザーIDをローカルストレージに保存
    setUserId: (userId: UserId): void => {
        if (isBrowser()) {
            localStorage.setItem(USER_ID_KEY, userId)
        }
    },

    // ローカルストレージからユーザーIDを取得
    getUserId: (): UserId | null => {
        if (isBrowser()) {
            return localStorage.getItem(USER_ID_KEY)
        }
        return null
    },

    // ローカルストレージからユーザーIDを削除（ログアウト）
    clearUserId: (): void => {
        if (isBrowser()) {
            localStorage.removeItem(USER_ID_KEY)
        }
    },

    // トークンをローカルストレージに保存
    setToken: (token: string): void => {
        if (isBrowser()) {
            localStorage.setItem(TOKEN_KEY, token)
        }
    },

    // ローカルストレージからトークンを取得
    getToken: (): string | null => {
        if (isBrowser()) {
            return localStorage.getItem(TOKEN_KEY)
        }
        return null
    },

    // ローカルストレージからトークンを削除
    clearToken: (): void => {
        if (isBrowser()) {
            localStorage.removeItem(TOKEN_KEY)
        }
    },

    // ログイン状態かどうかを確認（トークンベース）
    isLoggedIn: (): boolean => {
        if (isBrowser()) {
            return !!localStorage.getItem(TOKEN_KEY)
        }
        return false
    }
}

/**
 * ユーザーがログインしているかチェックし、ログインしていない場合はログインページにリダイレクトする
 * @param redirectPath リダイレクト先のパス（デフォルト: '/login'）
 * @returns ログインしている場合はtrue、リダイレクトした場合はfalse
 */
export const requireAuth = (redirectPath: string = '/login'): boolean => {
    if (typeof window === 'undefined') {
        // サーバーサイドレンダリング時は何もしない
        return true
    }

    if (!authStorage.isLoggedIn()) {
        window.location.href = redirectPath
        return false
    }

    return true
}

/**
 * 現在のユーザーのトークンを取得する
 * @returns トークン文字列またはnull
 */
export const getAuthToken = (): string | null => {
    return authStorage.getToken()
}

/**
 * ログアウト処理を実行する
 */
export const logout = async (): Promise<void> => {
    await authApi.logout()
    window.location.href = '/login'
}

// ダミーユーザーIDを取得（開発用）
export const getDummyUserId = (): UserId => {
    // サーバーサイドでは固定のダミーIDを返す
    if (!isBrowser()) {
        return '00000000-0000-0000-0000-000000000001'
    }

    const userId = authStorage.getUserId()
    if (userId) {
        return userId
    }

    // ログインしていない場合はダミーユーザーIDを使用
    const dummyUserId = '00000000-0000-0000-0000-000000000001'
    authStorage.setUserId(dummyUserId)
    return dummyUserId
}

// React Hooks用：クライアントサイドでのみユーザーIDを取得
export const useUserId = (): UserId | null => {
    const [userId, setUserId] = React.useState<UserId | null>(null)

    React.useEffect(() => {
        // クライアントサイドでのみ実行
        setUserId(authStorage.getUserId())
    }, [])

    return userId
}
