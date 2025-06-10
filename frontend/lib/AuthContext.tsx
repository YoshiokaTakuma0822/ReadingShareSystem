"use client"

import { Account, Member } from '@/types/auth'
import { createContext, ReactNode, useContext, useEffect, useState } from 'react'
import {
    logout as apiLogout,
    getCurrentAccount,
    createTemporaryAccount,
    refreshTokenIfNeeded,
} from './authApi'
import { getJwtExpirationTime } from './cookies'
import { createRoom, getRoom, joinRoom, joinRoomAndGetMember } from './api'

interface AuthContextType {
    account: Account | null
    member: Member | null
    isLoading: boolean
    isAuthenticated: boolean
    isTemporary: boolean
    logout: () => Promise<void>
    error: string | null
}

// 明示的にエクスポートする
export const AuthContext = createContext<AuthContextType | null>(null)

export function useAuth() {
    const context = useContext(AuthContext)
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider')
    }
    return context
}

interface AuthProviderProps {
    children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
    const [account, setAccount] = useState<Account | null>(null)
    const [member, setMember] = useState<Member | null>(null)
    const [isLoading, setIsLoading] = useState(true)
    const [isTemporary, setIsTemporary] = useState(false)
    const [error, setError] = useState<string | null>(null)

    // --- initialization helpers placed in context for clarity ---
    const fetchAccount = async (): Promise<{ account: Account; isTemporary: boolean }> => {
        try {
            let acct = await getCurrentAccount()
            if (acct) {
                return { account: acct, isTemporary: acct.email.startsWith('temp_') }
            }
        } catch (_) {
            // ignore and create temporary below
        }

        const temp = await createTemporaryAccount()
        return { account: temp, isTemporary: true }
    }

    const fetchDefaultRoom = async (memberName: string) => {
        const defaultRoomId = 1
        let room = await getRoom(defaultRoomId)
        if (!room) {
            room = await createRoom({
                name: 'General',
                description: 'デフォルトの部屋 (ID 1)'
            })
        }
        await joinRoom(defaultRoomId, memberName)
        return room
    }

    const fetchMember = async (memberName: string): Promise<Member> => {
        return joinRoomAndGetMember(1, memberName)
    }

    // Set up a timer to check token expiration periodically
    useEffect(() => {
        // Check token expiration
        const expirationTime = getJwtExpirationTime()
        if (!expirationTime) return

        const checkTokenAndRefresh = async () => {
            try {
                await refreshTokenIfNeeded()
                // Update the account with the latest expiration time
                const updatedAccount = await getCurrentAccount()
                if (updatedAccount) {
                    setAccount(updatedAccount)
                }
            } catch (err) {
                console.error('Failed to refresh token:', err)
                setError('認証トークンの更新に失敗しました')
                // Don't logout automatically - let the user decide what to do
            }
        }

        // Check immediately
        checkTokenAndRefresh()

        // Set up periodic checking
        // Check more frequently when token is close to expiring
        const getCheckInterval = () => {
            const now = Date.now()
            const expirationTime = getJwtExpirationTime()
            if (!expirationTime) return 5 * 60 * 1000 // Check every 5 minutes if no expiration time

            const timeUntilExpiry = expirationTime - now

            if (timeUntilExpiry <= 10 * 60 * 1000) { // 10 minutes or less
                return 30 * 1000 // Check every 30 seconds
            } else if (timeUntilExpiry <= 30 * 60 * 1000) { // 30 minutes or less
                return 2 * 60 * 1000 // Check every 2 minutes
            } else {
                return 5 * 60 * 1000 // Check every 5 minutes
            }
        }

        const intervalId = setInterval(checkTokenAndRefresh, getCheckInterval())

        return () => clearInterval(intervalId)
    }, [])

    // Initialize authentication: account -> default room -> member
    useEffect(() => {
        async function initializeAuth() {
            setIsLoading(true)
            setError(null)
            try {
                const { account: acct, isTemporary: temp } = await fetchAccount()
                setAccount(acct)
                setIsTemporary(temp)

                const baseName = acct.email.split('@')[0] || `User${Math.floor(Math.random() * 1000)}`
                await fetchDefaultRoom(baseName)
                const mem = await fetchMember(baseName)
                setMember(mem)
            } catch (err) {
                console.error('Failed to initialize authentication:', err)
                setError('認証の初期化に失敗しました')
            } finally {
                setIsLoading(false)
            }
        }

        initializeAuth()
    }, []) // Empty dependency array to run only once on mount

    // Logout
    const logout = async () => {
        try {
            setIsLoading(true)
            await apiLogout()
            setAccount(null)
            setMember(null)
            setIsTemporary(false)
        } catch (err) {
            console.error('Failed to logout:', err)
            setError('ログアウトに失敗しました')
        } finally {
            setIsLoading(false)
        }
    }

    const value = {
        account,
        member,
        isLoading,
        isAuthenticated: !!account,
        isTemporary,
        logout,
        error
    }

    return <AuthContext.Provider value={value}> {children} </AuthContext.Provider>
}
