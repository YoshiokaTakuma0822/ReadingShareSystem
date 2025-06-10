import { useState, useCallback } from 'react'
import { sendMessageToRoom, getRecentMessagesFromRoom } from './api'
import { useAuth } from './auth'
import { useChatData } from './useChatData'
import { useChatWebSocket } from './useChatWebSocket'

export function useChat() {
    const { member } = useAuth()
    const [currentRoomId, setCurrentRoomId] = useState<number>(1)

    const {
        messages,
        activeUsers,
        members,
        updateMessages,
        updateUsers,
        updateMembers,
        loadOlderMessages,
        setMessages
    } = useChatData(currentRoomId)

    const { isConnecting } = useChatWebSocket(
        member,
        currentRoomId,
        updateMessages,
        updateUsers,
        updateMembers
    )

    const sendMessage = async (content: string) => {
        if (!member) return
        try {
            await sendMessageToRoom(currentRoomId, content)
            await updateMessages()
        } catch (error) {
            console.error('Failed to send message:', error)
        }
    }

    const switchRoom = useCallback(async (roomId: number) => {
        if (roomId === currentRoomId) return
        setCurrentRoomId(roomId)
        setMessages([])
        try {
            const roomMessages = await getRecentMessagesFromRoom(roomId)
            setMessages(roomMessages.sort((a, b) => (a.id ?? 0) - (b.id ?? 0)))
            await updateMembers()
        } catch (error) {
            console.error('Failed to fetch room messages:', error)
        }
    }, [currentRoomId, updateMembers, setMessages])

    return {
        messages,
        isConnecting,
        sendMessage,
        activeUsers,
        members,
        loadOlderMessages,
        currentUser: member,
        currentRoomId,
        switchRoom
    }
}
