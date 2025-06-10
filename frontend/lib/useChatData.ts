import { useState, useCallback } from 'react'
import { getActiveUsers, getMessagesBeforeFromRoom, getRecentMessagesFromRoom, getRoomMembers } from './api'
import { Member } from '@/types/auth'
import { MessageResponse, MemberMap } from '@/types/chat'

export function useChatData(currentRoomId: number) {
    const [messages, setMessages] = useState<MessageResponse[]>([])
    const [activeUsers, setActiveUsers] = useState<Member[]>([])
    const [members, setMembers] = useState<MemberMap>({})

    const updateMessages = useCallback(async () => {
        try {
            const recentMessages = await getRecentMessagesFromRoom(currentRoomId)
            setMessages(prevMessages => {
                const newOnes = recentMessages.filter(m => !prevMessages.some(p => p.id === m.id))
                const merged = [...prevMessages, ...newOnes]
                return merged.sort((a, b) => (a.id ?? 0) - (b.id ?? 0))
            })
        } catch (error) {
            console.error('Failed to fetch messages:', error)
        }
    }, [currentRoomId])

    const updateUsers = useCallback(async () => {
        try {
            const users = await getActiveUsers()
            setActiveUsers(users)
        } catch (error) {
            console.error('Failed to fetch users:', error)
        }
    }, [])

    const updateMembers = useCallback(async () => {
        try {
            const roomMembers = await getRoomMembers(currentRoomId)
            const memberMap: MemberMap = {}
            roomMembers.forEach(m => {
                memberMap[m.id] = { id: m.id, name: m.name }
            })
            setMembers(memberMap)
        } catch (error) {
            console.error('Failed to fetch members:', error)
        }
    }, [currentRoomId])

    const loadOlderMessages = useCallback(async (limit: number = 20): Promise<void> => {
        try {
            if (messages.length === 0) return
            const oldestId = messages[0].id
            if (oldestId === undefined) return
            const olderMessages = await getMessagesBeforeFromRoom(currentRoomId, oldestId, limit)
            if (olderMessages.length === 0) return
            setMessages(prevMessages => {
                const newOnes = olderMessages.filter(m => !prevMessages.some(p => p.id === m.id))
                const merged = [...prevMessages, ...newOnes]
                return merged.sort((a, b) => (a.id ?? 0) - (b.id ?? 0))
            })
        } catch (error) {
            console.error('Failed to load older messages:', error)
        }
    }, [messages, currentRoomId])

    return {
        messages,
        activeUsers,
        members,
        updateMessages,
        updateUsers,
        updateMembers,
        loadOlderMessages,
        setMessages
    }
}
