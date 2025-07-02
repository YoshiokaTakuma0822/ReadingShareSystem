"use client"

import React, { useCallback, useEffect, useRef, useState } from 'react'
import { chatApi } from '../lib/chatApi'
import { roomApi } from '../lib/roomApi'
import { Message } from '../types/message'
import ChatMessageCard from './ChatMessageCard'
import SurveyMessageCard from './SurveyMessageCard'

interface MessageListProps {
    roomId?: string
}

const MessageList: React.FC<MessageListProps> = ({ roomId }) => {
    const [messages, setMessages] = useState<Message[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)
    const [userIdToName, setUserIdToName] = useState<Record<string, string>>({})
    const [surveyLoadingStates, setSurveyLoadingStates] = useState<Record<number, boolean>>({})
    const [shouldScrollToBottom, setShouldScrollToBottom] = useState(false)
    const initialLoadRef = useRef(true)
    const containerRef = useRef<HTMLDivElement | null>(null)

    const instantScrollToBottom = useCallback(() => {
        if (containerRef.current) {
            containerRef.current.scrollTo({ top: containerRef.current.scrollHeight, behavior: 'auto' })
        }
    }, [])

    const smoothScrollToBottom = useCallback(() => {
        if (containerRef.current) {
            containerRef.current.scrollTo({ top: containerRef.current.scrollHeight, behavior: 'smooth' })
        }
    }, [])

    const handleSurveyLoadingComplete = useCallback((messageId: number) => {
        setSurveyLoadingStates(prev => {
            const newStates = { ...prev, [messageId]: true }
            const surveyMsgs = messages.filter(m => m.messageType === 'SURVEY')
            const allLoaded = surveyMsgs.every(m => newStates[m.id])
            if (allLoaded && shouldScrollToBottom) {
                setTimeout(() => {
                    if (initialLoadRef.current) {
                        instantScrollToBottom()
                        initialLoadRef.current = false
                    } else {
                        smoothScrollToBottom()
                    }
                    setShouldScrollToBottom(false)
                }, 100)
            }
            return newStates
        })
    }, [messages, shouldScrollToBottom, instantScrollToBottom, smoothScrollToBottom])

    const loadChatHistory = async () => {
        if (!roomId) return setLoading(false)
        setLoading(true)
        setError(null)
        try {
            const chatHistory = await chatApi.getChatHistory(roomId)
            // ソート: sentAtを主キー、idを副キーとして
            chatHistory.sort((a, b) => {
                if (a.sentAt < b.sentAt) return -1
                if (a.sentAt > b.sentAt) return 1
                return a.id < b.id ? -1 : a.id > b.id ? 1 : 0
            })
            // Message[] へ変換して state にセット
            const converted: Message[] = chatHistory.map((msg, idx) => {
                let messageText = ''
                if (msg.messageType === 'SURVEY') {
                    messageText = ''
                } else if (typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content) {
                    messageText = String((msg.content as { value: string }).value || '')
                } else {
                    messageText = String(msg.content || '')
                }
                const senderId = msg.senderId ?? ''
                const myId = currentUserId ?? ''
                const username = msg.senderName || (msg.senderId && userIdToName[msg.senderId]
                    ? userIdToName[msg.senderId]
                    : '匿名ユーザー')
                return {
                    id: idx + 1,
                    uuid: msg.id,
                    user: username,
                    text: messageText,
                    isCurrentUser: senderId === myId,
                    sentAt: msg.sentAt,
                    messageType: msg.messageType || 'TEXT',
                    surveyId: msg.surveyId
                }
            })
            // 差分のみ追加: 既存メッセージには手を付けず、新着だけ追加
            setMessages(prev => {
                if (prev.length === 0) {
                    return converted
                }
                const existing = new Set(prev.map(m => m.uuid))
                const newOnly = converted.filter(m => !existing.has(m.uuid))
                return newOnly.length > 0 ? [...prev, ...newOnly] : prev
            })
        } catch {
            setError('チャット履歴の読み込みに失敗しました')
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        const uid = localStorage.getItem('reading-share-user-id')
        if (uid) {
            setCurrentUserId(uid)
        }
    }, [])

    useEffect(() => {
        if (roomId) {
            roomApi.getRoomMembers(roomId).then(members => {
                const map: Record<string, string> = {}
                members.forEach((m: any) => { if (m.userId && m.username) map[m.userId] = m.username })
                setUserIdToName(map)
            })
        }
    }, [roomId])

    // 初期ロード: currentUserIdとroomIdが揃ったらチャット履歴を取得
    useEffect(() => { if (currentUserId && roomId) loadChatHistory() }, [currentUserId, roomId])
    // WebSocket通知: currentUserIdも含めて再接続
    useEffect(() => {
        if (!roomId || !currentUserId) return

        const ws = new WebSocket(`ws://localhost:8080/ws/chat/notifications/${roomId}`)

        // メッセージ受信時のイベントハンドラ
        ws.onmessage = () => {
            loadChatHistory()
        }

        // クリーンアップ関数
        return () => {
            ws.close()
        }
    }, [roomId, currentUserId])

    useEffect(() => {
        // メッセージ追加時
        const surveyMsgs = messages.filter(m => m.messageType === 'SURVEY')
        const initialStates: Record<number, boolean> = {}
        surveyMsgs.forEach(m => initialStates[m.id] = false)
        setSurveyLoadingStates(initialStates)
        setShouldScrollToBottom(true)

        // サーベイメッセージがない場合はすぐにスクロール
        if (!surveyMsgs.length) {
            if (initialLoadRef.current) {
                instantScrollToBottom()
                initialLoadRef.current = false
            } else {
                smoothScrollToBottom()
            }
        }
    }, [messages, instantScrollToBottom, smoothScrollToBottom])

    // 初回ロード時のみローディング表示（既存メッセージがある場合は差分取得中も既存表示を維持）
    if (loading && messages.length === 0) return <div>チャット履歴を読み込み中...</div>
    if (error) return <div>{error}</div>
    if (!messages.length) {
        return (
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', color: '#999', fontSize: 16 }}>
                <div>まだメッセージがありません</div>
                <button onClick={loadChatHistory} style={{ marginTop: 8, padding: '8px 16px', borderRadius: 4, border: '1px solid #222', background: 'white', cursor: 'pointer' }}>
                    再読み込み
                </button>
            </div>
        )
    }

    return (
        <div ref={containerRef} style={{
            flex: 1,
            overflowY: 'auto',
            display: 'flex',
            flexDirection: 'column',
            gap: 16,
            scrollBehavior: 'smooth'
        }}>
            {messages.map(msg => {
                const isMine = msg.isCurrentUser
                return msg.messageType === 'SURVEY'
                    ? <SurveyMessageCard key={msg.uuid} msg={msg} isMine={isMine} currentUserId={currentUserId} onLoadingComplete={() => handleSurveyLoadingComplete(msg.id)} />
                    : <ChatMessageCard key={msg.uuid} msg={msg} isMine={isMine} />
            })}
        </div>
    )
}

export default MessageList
