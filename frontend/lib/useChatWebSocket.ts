import { useCallback, useEffect, useRef, useState } from 'react'
import { Client, ReconnectionTimeMode } from '@stomp/stompjs'
import { Member } from '@/types/auth'

export function useChatWebSocket(
    member: Member | null,
    currentRoomId: number,
    updateMessages: () => Promise<void>,
    updateUsers: () => Promise<void>,
    updateMembers: () => Promise<void>
) {
    const [isConnecting, setIsConnecting] = useState(true)
    const clientRef = useRef<Client | null>(null)
    const lastActiveTimeRef = useRef<number>(Date.now())

    const fetchInitialData = useCallback(async () => {
        await Promise.all([updateMessages(), updateUsers(), updateMembers()])
    }, [updateMessages, updateUsers, updateMembers])

    useEffect(() => {
        if (!member) return
        let isMounted = true

        const handleVisibilityChange = async () => {
            if (document.visibilityState === 'visible') {
                const now = Date.now()
                const timeSinceLastActive = now - lastActiveTimeRef.current
                if (clientRef.current) {
                    clientRef.current.reconnectDelay = 100
                }
                if (timeSinceLastActive > 5000) {
                    await fetchInitialData()
                }
                lastActiveTimeRef.current = now
            } else if (document.visibilityState === 'hidden') {
                lastActiveTimeRef.current = Date.now()
            }
        }

        const setupWebSocket = async (user: Member) => {
            try {
                const protocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
                const brokerURL = process.env.NODE_ENV === 'development'
                    ? `${protocol}://${window.location.hostname}:8080/ws`
                    : `${protocol}://${window.location.host}/ws`

                const client = new Client({
                    brokerURL,
                    connectHeaders: {
                        userId: user.id?.toString() ?? ''
                    },
                    reconnectDelay: 1000,
                    connectionTimeout: 10000,
                    maxReconnectDelay: 60000,
                    reconnectTimeMode: ReconnectionTimeMode.EXPONENTIAL,
                    onConnect: async () => {
                        try {
                            await fetchInitialData()
                            client.subscribe('/topic/chat.messages.update', async () => {
                                await updateMessages()
                            })
                            client.subscribe(`/topic/chat.room.${currentRoomId}.messages.update`, async () => {
                                await updateMessages()
                            })
                            client.subscribe('/topic/chat.users.update', async () => {
                                await updateUsers()
                                await updateMembers()
                            })
                            setIsConnecting(false)
                        } catch (error) {
                            console.error('Failed to fetch initial data:', error)
                            setIsConnecting(false)
                        }
                    },
                    onDisconnect: () => {
                        setIsConnecting(true)
                    },
                    onStompError: (frame) => {
                        console.error('Stomp error:', frame)
                        setIsConnecting(true)
                    }
                })

                client.activate()
                clientRef.current = client
            } catch (error) {
                console.error('Failed to setup WebSocket:', error)
                setIsConnecting(true)
            }
        }

        ;(async () => {
            try {
                if (isMounted) {
                    await fetchInitialData()
                    await setupWebSocket(member)
                    document.addEventListener('visibilitychange', handleVisibilityChange)
                }
            } catch (error) {
                console.error('Failed to setup chat:', error)
                setIsConnecting(false)
            }
        })()

        return () => {
            isMounted = false
            if (clientRef.current) {
                Promise.resolve(clientRef.current.deactivate()).catch(error => {
                    console.error('Failed to deactivate WebSocket:', error)
                })
            }
            document.removeEventListener('visibilitychange', handleVisibilityChange)
        }
    }, [member, currentRoomId, fetchInitialData, updateMessages, updateUsers, updateMembers])

    return { isConnecting }
}
