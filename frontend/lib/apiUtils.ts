import { fetchWithAuth } from './authApi'

/**
 * Fetch wrapper for API calls that require authentication
 * Automatically handles token refresh when needed
 *
 * @param url API endpoint URL
 * @param options Fetch options
 * @returns Response from fetch
 */
export async function fetchWithTokenRefresh(
    url: string,
    options: RequestInit = {}
): Promise<Response> {
    try {
        // Use the improved fetchWithAuth that handles token refresh
        return fetchWithAuth(url, options)
    } catch (error) {
        console.error(`API request failed: ${url}`, error)
        throw error
    }
}
