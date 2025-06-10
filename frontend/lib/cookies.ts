/**
 * Utility functions for handling cookies on the client side
 */

/**
 * Get a cookie value by name
 */
export function getCookie(name: string): string | null {
    if (typeof document === 'undefined') {
        return null // SSR safety
    }

    const value = `; ${document.cookie}`
    const parts = value.split(`; ${name}=`)

    if (parts.length === 2) {
        const cookieValue = parts.pop()?.split(';').shift()
        return cookieValue || null
    }

    return null
}

/**
 * Get the JWT expiration time from the jwt-expires-at cookie
 * @returns Unix timestamp in milliseconds, or null if cookie doesn't exist
 */
export function getJwtExpirationTime(): number | null {
    const expiresAtStr = getCookie('jwt-expires-at')

    if (!expiresAtStr) {
        return null
    }

    // Try parsing as ISO 8601 format first (backend now returns Instant.toString())
    try {
        const isoDate = new Date(expiresAtStr)
        if (!isNaN(isoDate.getTime())) {
            return isoDate.getTime()
        }
    } catch (error) {
        // Fall back to parsing as Unix timestamp for backward compatibility
    }

    // Fallback: try parsing as Unix timestamp (milliseconds)
    const timestamp = parseInt(expiresAtStr, 10)
    return isNaN(timestamp) ? null : timestamp
}

/**
 * Check if the JWT token is expired or will expire soon
 * @param bufferMinutes Minutes before expiration to consider as "about to expire" (default: 5)
 * @returns true if token is expired or about to expire
 */
export function isTokenExpiredOrExpiring(bufferMinutes: number = 5): boolean {
    const expirationTime = getJwtExpirationTime()

    if (!expirationTime) {
        return true // No expiration time means no token
    }

    const now = Date.now()
    const bufferMs = bufferMinutes * 60 * 1000

    return now >= (expirationTime - bufferMs)
}
