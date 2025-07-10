import React, { useEffect, useRef, useState } from 'react'
import { chatApi } from '../../lib/chatApi'
import { useChatWebSocket } from '../../lib/useChatWebSocket'
import ChatNotification from './ChatNotification'
import ReadingScreen from './ReadingScreen'
import styles from './ReadingScreenOverlay.module.css'

interface ReadingScreenOverlayProps {
    roomId?: string
    open: boolean
    onClose: () => void
}

const ReadingScreenOverlay: React.FC<ReadingScreenOverlayProps> = ({ roomId, open, onClose }) => {
    const [notification, setNotification] = useState<string | null>(null)
    const [badgeCount, setBadgeCount] = useState(0)
    const [badgeSenders, setBadgeSenders] = useState<string[]>([])
    const [badgeSenderCounts, setBadgeSenderCounts] = useState<{ [sender: string]: number }>({}) // 送信者ごとの件数を管理
    const [visible, setVisible] = useState(false)
    const [showNewMessageBanner, setShowNewMessageBanner] = useState(false)
    const userIdRef = useRef<string | null>(null)

    // ユーザーID取得
    useEffect(() => {
        if (typeof window !== 'undefined') {
            userIdRef.current = localStorage.getItem('reading-share-user-id')
        }
    }, [])

    // 通知を一定時間で消す
    useEffect(() => {
        if (notification) {
            setVisible(true)
            const timer = setTimeout(() => {
                setVisible(false)
            }, 4000)
            return () => clearTimeout(timer)
        }
    }, [notification])

    // WebSocketでリアルタイム通知
    useChatWebSocket(roomId, (event) => {
        const data = JSON.parse(event.data)
        const receivedRoomId = String(data.roomId || '').trim().toLowerCase()
        const currentRoomId = String(roomId || '').trim().toLowerCase()
        if (data.senderId && userIdRef.current && data.senderId !== userIdRef.current && receivedRoomId === currentRoomId) {
            setShowNewMessageBanner(true)
            setTimeout(() => setShowNewMessageBanner(false), 3000)
            setNotification('他のメンバーから新しいメッセージが届きました')
            setBadgeCount(c => c + 1)
            setBadgeSenders(prev => prev.includes(data.senderName) ? prev : [...prev, data.senderName])
            setBadgeSenderCounts(prev => ({
                ...prev,
                [data.senderName]: (prev[data.senderName] || 0) + 1
            }))
        }
    })

    // ユーザー情報取得
    const userId = userIdRef.current || ""
    const userName = (typeof window !== 'undefined' && localStorage.getItem("reading-share-user-name")) || ""
    // メッセージ送信: REST API で実行
    const sendMessage = async (content: string) => {
        if (!roomId) return
        try {
            await chatApi.sendMessage(roomId, { messageContent: content })
        } catch (err) {
            console.error('メッセージ送信失敗:', err)
        }
    }

    // オーバーレイを閉じる時にバッジと送信者リストをリセット
    const handleClose = () => {
        setBadgeCount(0)
        setBadgeSenders([])
        onClose()
    }

    if (!open) return null
    return (
        <div className={styles.overlay} onClick={handleClose}>
            {/* 新着メッセージバナー */}
            {showNewMessageBanner && (
                <div style={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    width: '100vw',
                    background: '#1976d2',
                    color: '#fff',
                    textAlign: 'center',
                    fontWeight: 'bold',
                    fontSize: 18,
                    padding: '12px 0',
                    zIndex: 4000,
                    boxShadow: '0 2px 8px rgba(0,0,0,0.12)'
                }}>
                    新着メッセージがあります
                </div>
            )}
            {/* 通知バッジ */}
            {badgeCount > 0 && (
                <div
                    style={{
                        position: 'fixed',
                        top: 18,
                        left: 18,
                        zIndex: 3001,
                        background: '#d32f2f',
                        color: '#fff',
                        borderRadius: '50%',
                        width: 32,
                        height: 32,
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontWeight: 'bold',
                        fontSize: 18,
                        boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                        cursor: 'pointer',
                    }}
                    title={
                        Object.keys(badgeSenderCounts).length > 0
                            ? Object.entries(badgeSenderCounts)
                                .map(([name, count]) => `${name}(${count})`).join(', ')
                            : ''
                    }
                >
                    {badgeCount}
                </div>
            )}
            {/* 通知表示 */}
            <ChatNotification message={notification || ''} visible={visible} onClose={() => setNotification(null)} />
            <div className={styles.modal} onClick={e => e.stopPropagation()}>
                <button className={styles.closeButton} onClick={handleClose} aria-label="Close">×</button>
                <ReadingScreen roomId={roomId} />
            </div>
        </div>
    )
}

export default ReadingScreenOverlay
