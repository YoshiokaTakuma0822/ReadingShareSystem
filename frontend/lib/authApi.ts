import { Account, LoginRequest, Member, RegisterRequest } from "@/types/auth"
import { getJwtExpirationTime, isTokenExpiredOrExpiring } from "./cookies"
import { getCurrentMemberInRoom, joinRoomAndGetMember } from "./api"

// Store the current account in memory for quick access
let currentAccount: Account | null = null

// Store the expiration time separately from the account
let currentTokenExpiresAt: Date | null = null

// Mutex to prevent multiple refresh attempts
let refreshPromise: Promise<void> | null = null

// Mutex to prevent multiple member creation attempts
let createMemberPromises = new Map<string, Promise<Member>>()

/**
 * Initialize token expiration time from cookie at startup
 */
function initializeTokenExpiration(): Date | null {
    const expirationTime = getJwtExpirationTime()
    currentTokenExpiresAt = expirationTime ? new Date(expirationTime) : null
    return currentTokenExpiresAt
}

/**
 * Update stored token expiration time
 */
function updateTokenExpiration(): void {
    const expirationTime = getJwtExpirationTime()
    currentTokenExpiresAt = expirationTime ? new Date(expirationTime) : null
}

/**
 * Get current token expiration time
 */
export function getCurrentTokenExpiration(): Date | null {
    if (currentTokenExpiresAt === null) {
        return initializeTokenExpiration()
    }
    return currentTokenExpiresAt
}

/**
 * Refresh the JWT token using the refresh token cookie
 */
export async function refreshToken(): Promise<void> {
    try {
        const response = await fetch("/api/accounts/refresh", {
            method: "POST",
            credentials: "include"
        })

        if (!response.ok) {
            // If refresh token is missing, invalid, or any other error, create a new temporary account
            if (response.status === 400 || response.status === 401 || response.status === 500) {
                console.log(`Refresh failed with status ${response.status}, creating temporary account...`)
                await createTemporaryAccount()
                return
            }
            throw new Error(`Failed to refresh token: ${response.status}`)
        }

        // Token refresh successful, the new tokens are set as cookies by the backend
        updateTokenExpiration()
        console.log("Token refreshed successfully")
    } catch (error) {
        console.error("Token refresh failed:", error)
        // Clear the current account since refresh failed
        currentAccount = null
        currentTokenExpiresAt = null

        // If it's a network error or any other error, also try to create a temporary account
        try {
            console.log("Creating temporary account due to refresh error...")
            await createTemporaryAccount()
        } catch (tempError) {
            console.error("Failed to create temporary account:", tempError)
            throw error // Re-throw the original error
        }
    }
}

/**
 * Check if token needs refreshing and refresh if necessary
 */
export async function refreshTokenIfNeeded(): Promise<void> {
    // If there's already a refresh in progress, wait for it
    if (refreshPromise) {
        await refreshPromise
        return
    }

    // Check if token is expired or expiring soon (5 minutes buffer)
    const expirationTime = getJwtExpirationTime()
    if (!expirationTime) {
        // No token exists, create a temporary account
        console.log("No token found, creating temporary account...")
        refreshPromise = (async () => {
            await createTemporaryAccount()
        })()
    } else if (isTokenExpiredOrExpiring(5)) {
        // Token exists but is expired or expiring soon
        console.log("Token is expired or expiring soon, refreshing...")
        refreshPromise = refreshToken()
    } else {
        return // Token is still valid
    }

    try {
        await refreshPromise
        // Wait a brief moment for the token to be fully available
        await new Promise(resolve => setTimeout(resolve, 50))
    } finally {
        refreshPromise = null
    }
}

/**
 * Wrapper for API requests that need authentication
 * Automatically refreshes token if needed before making the request
 */
export async function fetchWithAuth(
    url: string,
    options: RequestInit = {}
): Promise<Response> {
    try {
        // Check if we need to refresh the token before making the request
        await refreshTokenIfNeeded()

        // Make the request with credentials
        const response = await fetch(url, {
            ...options,
            credentials: "include" // Ensure cookies are included
        })

        // If we get a 401, try refreshing once more
        if (response.status === 401) {
            console.log("Got 401, attempting token refresh...")
            await refreshToken()

            // Wait a brief moment for the new token to be available
            await new Promise(resolve => setTimeout(resolve, 100))

            // Retry the original request
            return fetch(url, {
                ...options,
                credentials: "include"
            })
        }

        return response
    } catch (error) {
        console.error("Authentication request failed:", error)
        throw error
    }
}

/**
 * Register a new account
 */
export async function registerAccount(request: RegisterRequest): Promise<Account> {
    try {
        const response = await fetch("/api/accounts/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(request),
            credentials: "include" // Include cookies
        })
        if (!response.ok) {
            const errorData = await response.text()
            throw new Error(`Failed to register: ${errorData}`)
        }

        const registerResponse = await response.json()

        // Convert register response to Account format
        const account: Account = {
            id: registerResponse.id,
            email: registerResponse.email
        }

        return account
    } catch (error) {
        console.error("Registration failed:", error)
        throw error
    }
}

/**
 * Login with email and password
 */
export async function login(request: LoginRequest): Promise<Account> {
    try {
        const response = await fetch("/api/accounts/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(request),
            credentials: "include" // Include cookies
        })
        if (!response.ok) {
            const errorData = await response.text()
            throw new Error(`Failed to login: ${errorData}`)
        }

        // Backend returns a simple string message but sets cookies
        // We need to wait a bit for cookies to be set, then read the expiration time
        await new Promise(resolve => setTimeout(resolve, 100))

        // Update stored token expiration time
        updateTokenExpiration()

        const account: Account = {
            email: request.email
        }

        currentAccount = account // Store account for quick access
        return account
    } catch (error) {
        console.error("Login failed:", error)
        throw error
    }
}

/**
 * Get current authenticated account
 */
export async function getCurrentAccount(): Promise<Account | null> {
    try {
        const response = await fetchWithAuth("/api/accounts/me")
        if (!response.ok) return null
        const meResponse = await response.json()

        // Update stored token expiration time
        updateTokenExpiration()

        // Convert the /me response to Account format
        const account: Account = {
            id: meResponse.id,
            email: meResponse.email
        }

        currentAccount = account // Store account for quick access
        return account
    } catch (error) {
        console.error("Failed to get current account:", error)

        // If getting current account fails, try to refresh/create account
        try {
            const hadTokenBefore = getJwtExpirationTime() !== null
            await refreshTokenIfNeeded()

            // Only wait if we didn't have a token before (creating new temporary account)
            if (!hadTokenBefore) {
                await new Promise(resolve => setTimeout(resolve, 200))
            }

            // Try again after refresh
            const response = await fetch("/api/accounts/me", {
                credentials: "include"
            })
            if (response.ok) {
                const meResponse = await response.json()
                updateTokenExpiration()

                const account: Account = {
                    id: meResponse.id,
                    email: meResponse.email
                }

                currentAccount = account
                return account
            }
        } catch (retryError) {
            console.error("Failed to get current account after retry:", retryError)
        }

        return null
    }
}

/**
 * Logout current account
 */
export async function logout(): Promise<void> {
    const response = await fetchWithAuth("/api/accounts/logout", {
        method: "POST"
    })
    if (!response.ok) throw new Error(`Failed to logout: ${response.status}`)
    currentAccount = null // Clear stored account data
    currentTokenExpiresAt = null // Clear stored token expiration

    // Clear any pending member creation promises
    createMemberPromises.clear()
}

// createMember function removed - use joinRoom from api.ts instead


/**
 * Create a temporary account and login automatically
 */
export async function createTemporaryAccount(): Promise<Account> {
    try {
        const email = `temp_${Math.floor(Math.random() * 1000000)}@example.com`
        const password = `pass_${Math.floor(Math.random() * 1000000)}`

        console.log("Creating temporary account with email:", email)

        // Register the account
        await registerAccount({ email, password })

        // Login with the new account
        const account = await login({ email, password })

        console.log("Temporary account created and logged in successfully")
        return account
    } catch (error) {
        console.error("Failed to create temporary account:", error)
        throw new Error("一時アカウントの作成に失敗しました。しばらくしてからもう一度お試しください。")
    }
}

/**
 * Create a member and join the room
 * Since backend generates IDs automatically, we don't need to check for existing members by ID
 */
export async function createMember(name: string, roomId: number = 1): Promise<Member> {
    // For temporary users, we can simplify since each call creates a unique account
    // But we still keep a basic guard for edge cases (same name collision, etc.)
    const memberKey = `${name}-${roomId}`

    // If there's already a request in progress for the same name+room, wait for it
    if (createMemberPromises.has(memberKey)) {
        console.log(`Create member already in progress for ${name} in room ${roomId}, waiting...`)
        return createMemberPromises.get(memberKey)!
    }

    // Create the promise and store it
    const memberPromise = (async (): Promise<Member> => {
        try {
            console.log(`Creating new member with name: ${name} in room ${roomId}`)

            // Create a new member using the consolidated joinRoomAndGetMember function
            return await joinRoomAndGetMember(roomId, name)
        } catch (error) {
            console.error("Create member failed:", error)
            throw error
        } finally {
            // Clean up the promise from the map
            createMemberPromises.delete(memberKey)
        }
    })()

    // Store the promise
    createMemberPromises.set(memberKey, memberPromise)

    return memberPromise
}

/**
 * Initialize authentication: fetch/account, ensure default room, and create/get member
 */
