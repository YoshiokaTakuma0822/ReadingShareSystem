"use client"

import { useEffect, useState } from 'react'
import { getJwtExpirationTime, isTokenExpiredOrExpiring } from './cookies'
import { refreshTokenIfNeeded } from './authApi'

/**
 * Hook to monitor JWT token expiration and handle automatic refresh
 */
export function useTokenMonitor() {
    const [expirationTime, setExpirationTime] = useState<number | null>(null)
    const [isExpiring, setIsExpiring] = useState(false)
    const [isRefreshing, setIsRefreshing] = useState(false)
    const [lastRefresh, setLastRefresh] = useState<Date | null>(null)

    useEffect(() => {
        const updateTokenInfo = () => {
            const expTime = getJwtExpirationTime()
            setExpirationTime(expTime)
            setIsExpiring(isTokenExpiredOrExpiring(5)) // 5 minutes buffer
        }

        // Update immediately
        updateTokenInfo()

        // Update every 30 seconds
        const interval = setInterval(updateTokenInfo, 30000)

        return () => clearInterval(interval)
    }, [])

    // Attempt to refresh token when it's expiring
    useEffect(() => {
        if (!isExpiring || isRefreshing) return

        const refreshToken = async () => {
            setIsRefreshing(true)
            try {
                await refreshTokenIfNeeded()
                setLastRefresh(new Date())

                // Update token info after refresh
                const expTime = getJwtExpirationTime()
                setExpirationTime(expTime)
                setIsExpiring(isTokenExpiredOrExpiring(5))
            } catch (error) {
                console.error('Token refresh failed:', error)
            } finally {
                setIsRefreshing(false)
            }
        }

        refreshToken()
    }, [isExpiring, isRefreshing])

    /**
     * Manually trigger token refresh
     */
    const manualRefresh = async () => {
        setIsRefreshing(true)
        try {
            await refreshTokenIfNeeded()
            setLastRefresh(new Date())

            // Update token info after refresh
            const expTime = getJwtExpirationTime()
            setExpirationTime(expTime)
            setIsExpiring(isTokenExpiredOrExpiring(5))
        } catch (error) {
            console.error('Manual token refresh failed:', error)
            throw error
        } finally {
            setIsRefreshing(false)
        }
    }

    /**
     * Get time remaining until token expires
     */
    const getTimeUntilExpiry = () => {
        if (!expirationTime) return null

        const now = Date.now()
        const diff = expirationTime - now

        if (diff <= 0) return { expired: true, formatted: '期限切れ' }

        const minutes = Math.floor(diff / (1000 * 60))
        const seconds = Math.floor((diff % (1000 * 60)) / 1000)

        return {
            expired: false,
            milliseconds: diff,
            minutes,
            seconds,
            formatted: `${minutes}分${seconds}秒`
        }
    }

    return {
        expirationTime,
        isExpiring,
        isRefreshing,
        lastRefresh,
        manualRefresh,
        getTimeUntilExpiry
    }
}
