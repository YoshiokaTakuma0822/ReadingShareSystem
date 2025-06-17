import apiClient from './apiClient'

export async function fetchRoomState(roomId: string) {
    const response = await apiClient.get(`/rooms/${roomId}/states`)
    return response.data
}

export async function updateUserReadingState(roomId: string, userId: string, currentPage: number, comment?: string) {
    const response = await apiClient.post(`/rooms/${roomId}/states/${userId}`, {
        userId,
        currentPage,
        comment
    })
    return response.data
}

export async function turnPage(roomId: string, userId: string, direction: 'next' | 'prev') {
    const response = await apiClient.post(`/progress/${roomId}/record`, {
        userId,
        direction
    })
    return response.data
}
