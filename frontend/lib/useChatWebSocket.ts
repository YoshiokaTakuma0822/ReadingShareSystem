import { useEffect, useRef } from 'react'

type MessageHandler = (event: MessageEvent) => void

const sockets: Record<string, WebSocket> = {}

/**
 * WebSocket を部屋ごとに一元管理し、イベントを購読できるカスタムフック
 * @param roomId ルームID
 * @param onMessage WebSocket の message イベントハンドラ
 */
export function useChatWebSocket(
    roomId: string | undefined,
    onMessage: MessageHandler
) {
    const savedHandler = useRef<MessageHandler | undefined>(undefined)
    useEffect(() => {
        savedHandler.current = onMessage
    }, [onMessage])

    useEffect(() => {
        if (!roomId) return
        let ws = sockets[roomId]
        const wsUrl =
            process.env.NODE_ENV === 'production'
                ? `/ws/chat/notifications/${roomId}`
                : `ws://localhost:8080/ws/chat/notifications/${roomId}`

        // 初回接続または切断済みなら再接続
        if (!ws || ws.readyState === WebSocket.CLOSED) {
            ws = new WebSocket(wsUrl)
            sockets[roomId] = ws
            ws.onopen = () => {
                setInterval(() => {
                    if (ws.readyState === WebSocket.OPEN) {
                        ws.send(JSON.stringify({ type: 'ping' }))
                    }
                }, 45000)
            }
        }

        const listener = (event: MessageEvent) => {
            savedHandler.current?.(event)
        }
        ws.addEventListener('message', listener)

        // ページが再度表示されたときに接続を保証
        const handleVisibility = () => {
            if (document.visibilityState === 'visible') {
                const current = sockets[roomId]
                if (!current || current.readyState === WebSocket.CLOSED) {
                    const newWs = new WebSocket(wsUrl)
                    sockets[roomId] = newWs
                    newWs.onopen = ws.onopen
                    newWs.addEventListener('message', listener)
                    ws = newWs
                }
            }
        }
        document.addEventListener('visibilitychange', handleVisibility)

        return () => {
            ws.removeEventListener('message', listener)
            document.removeEventListener('visibilitychange', handleVisibility)
        }
    }, [roomId])
}
