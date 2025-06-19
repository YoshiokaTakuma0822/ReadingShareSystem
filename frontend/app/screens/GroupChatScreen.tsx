"use client"
import React, { useState, useEffect } from 'react'
import SurveyCreationModal from './SurveyCreationModal'
import { chatApi } from '../../lib/chatApi'
import { ChatMessage } from '../../types/chat'

interface Message {
    id: number
    user: string
    text: string
    isCurrentUser: boolean
}

interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
}

const GroupChatScreen: React.FC<GroupChatScreenProps> = ({ roomTitle = "チャットルーム", currentUser = "あなた", roomId }) => {
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState("")
    const [msgId, setMsgId] = useState(1)
    const [showSurveyModal, setShowSurveyModal] = useState(false)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)

    // コンポーネントマウント時にユーザーIDを取得
    useEffect(() => {
        const userId = localStorage.getItem('reading-share-user-id')
        setCurrentUserId(userId)
    }, [])

    // チャット履歴を取得する関数
    const loadChatHistory = async () => {
        if (!roomId) {
            setLoading(false)
            return
        }

        try {
            setLoading(true)
            setError(null)
            const chatHistory = await chatApi.getChatHistory(roomId)

            console.log('取得したチャット履歴:', chatHistory)

            // ChatMessageをMessage形式に変換
            const convertedMessages: Message[] = chatHistory.map((msg, index) => {
                console.log('メッセージ変換:', msg)

                // contentがオブジェクトの場合は.valueを取得、文字列の場合はそのまま使用
                let messageText = ''
                if (typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content) {
                    messageText = String((msg.content as { value: string }).value || '')
                } else {
                    messageText = String(msg.content || '')
                }

                return {
                    id: index + 1,
                    user: String(msg.senderUserId || '匿名ユーザー'),
                    text: messageText,
                    isCurrentUser: msg.senderUserId === currentUserId
                }
            })

            setMessages(convertedMessages)
            setMsgId(convertedMessages.length + 1)
        } catch (err) {
            console.error('チャット履歴の取得に失敗しました:', err)
            console.log('エラー詳細:', err)
            setError('チャット履歴の読み込みに失敗しました')
        } finally {
            setLoading(false)
        }
    }

    // コンポーネントマウント時にチャット履歴を読み込む
    useEffect(() => {
        if (currentUserId !== null) {
            loadChatHistory()
        }
    }, [roomId, currentUserId])

    const handleSend = async () => {
        if (!input.trim() || !roomId) return

        try {
            // サーバーにメッセージを送信
            await chatApi.sendMessage(roomId, { messageContent: input })

            // ローカル状態を更新
            setMessages([...messages, {
                id: msgId,
                user: currentUser,
                text: input,
                isCurrentUser: true
            }])
            setMsgId(msgId + 1)
            setInput("")
        } catch (err) {
            console.error('メッセージ送信に失敗しました:', err)
            // エラーが発生してもローカル状態は更新する（UX向上のため）
            setMessages([...messages, {
                id: msgId,
                user: currentUser,
                text: input,
                isCurrentUser: true
            }])
            setMsgId(msgId + 1)
            setInput("")
        }
    }

    const handleGoToReading = () => {
        if (roomId) {
            window.location.href = `/rooms/${roomId}/reading`
        }
    }

    const handleCreateSurvey = () => {
        setShowSurveyModal(true)
    }

    const handleSurveyCreated = () => {
        setShowSurveyModal(false)
        // サーベイ作成後の処理（必要に応じて）
    }

    return (
        <div style={{ border: '4px solid var(--accent)', margin: 24, padding: 24, background: 'var(--green-bg)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: 'var(--accent)' }}>
                {String(roomTitle)}
            </h2>

            {/* ナビゲーションボタン */}
            <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
                <button
                    onClick={handleGoToReading}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: 'var(--accent)',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    📖 読書画面へ
                </button>
                <button
                    onClick={handleCreateSurvey}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: 'var(--green-main)',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    📊 アンケート作成
                </button>
                <button
                    onClick={() => window.location.href = '/'}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: 'var(--text-muted)',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    🏠 ホームへ
                </button>
            </div>

            {/* エラー表示 */}
            {error && (
                <div style={{
                    background: 'var(--error-background)',
                    color: 'var(--error)',
                    padding: 12,
                    borderRadius: 8,
                    marginBottom: 16,
                    border: '1px solid var(--error)'
                }}>
                    {error}
                    <button
                        onClick={loadChatHistory}
                        style={{
                            marginLeft: 12,
                            background: 'var(--error)',
                            color: 'white',
                            border: 'none',
                            padding: '4px 8px',
                            borderRadius: 4,
                            cursor: 'pointer'
                        }}
                    >
                        再試行
                    </button>
                </div>
            )}

            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 16, marginBottom: 32, minHeight: 200, maxHeight: '60vh', overflowY: 'auto', background: 'var(--card)', borderRadius: 8, padding: 16 }}>
                {loading ? (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: '100%',
                        color: 'var(--text-muted)',
                        fontSize: 16
                    }}>
                        チャット履歴を読み込み中...
                    </div>
                ) : messages.length === 0 ? (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: '100%',
                        color: 'var(--text-muted)',
                        fontSize: 16
                    }}>
                        まだメッセージがありません
                    </div>
                ) : (
                    messages.map(msg => {
                        const isMine = msg.isCurrentUser
                        return (
                            <div
                                key={msg.id}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 8,
                                    justifyContent: isMine ? 'flex-end' : 'flex-start',
                                }}
                            >
                                {!isMine && (
                                    <span style={{ border: '1px solid var(--text)', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--text)', background: 'var(--background)' }}>
                                        {String(msg.user).charAt(0).toUpperCase()}
                                    </span>
                                )}
                                <div
                                    style={{
                                        border: '1px solid var(--border)',
                                        borderRadius: 16,
                                        padding: 8,
                                        background: isMine ? 'var(--green-light)' : 'var(--background)',
                                        color: isMine ? 'white' : 'var(--text)',
                                        maxWidth: 600,
                                        wordBreak: 'break-word',
                                    }}
                                >
                                    {String(msg.text)}
                                </div>
                                {isMine && (
                                    <span style={{ border: '1px solid var(--text)', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'var(--green-light)', color: 'white' }}>
                                        {String(msg.user).charAt(0).toUpperCase()}
                                    </span>
                                )}
                            </div>
                        )
                    })
                )}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32 }}>
                <input
                    type="text"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={e => { if (e.key === 'Enter') handleSend() }}
                    style={{ flex: 1, padding: 12, borderRadius: 8, border: '1px solid var(--border)', fontSize: 18, background: 'var(--input)', color: 'var(--text)' }}
                    placeholder="メッセージを入力..."
                    disabled={loading}
                />
                <button
                    style={{
                        marginLeft: 8,
                        padding: '12px 24px',
                        borderRadius: 8,
                        border: '1px solid var(--border)',
                        fontSize: 18,
                        background: loading ? 'var(--text-muted)' : 'var(--accent)',
                        color: loading ? 'var(--text)' : 'white',
                        cursor: loading ? 'not-allowed' : 'pointer'
                    }}
                    onClick={handleSend}
                    disabled={loading}
                >送信</button>
            </div>

            {/* アンケート作成モーダル */}
            {showSurveyModal && roomId && (
                <SurveyCreationModal
                    open={showSurveyModal}
                    roomId={roomId}
                    onClose={() => setShowSurveyModal(false)}
                    onCreated={handleSurveyCreated}
                />
            )}
        </div>
    )
}

export default GroupChatScreen
