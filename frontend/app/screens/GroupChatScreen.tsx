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
    sentAt: string // 送信時刻を追加
}

interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
}

// Surveyメッセージ用の型
interface SurveyMessage {
    id: string;
    surveyId: string;
    type: 'survey';
    title: string;
    createdAt: string;
}

const GroupChatScreen: React.FC<GroupChatScreenProps> = ({ roomTitle = "チャットルーム", currentUser = "あなた", roomId }) => {
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState("")
    const [msgId, setMsgId] = useState(1)
    const [showSurveyModal, setShowSurveyModal] = useState(false)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)
    const [surveyMessages, setSurveyMessages] = useState<SurveyMessage[]>([])
    const [showSurveyAnswerModal, setShowSurveyAnswerModal] = useState(false);
    const [dummySurveyId, setDummySurveyId] = useState<string | null>(null);

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
        setLoading(true)
        setError(null)
        try {
            // チャット履歴取得
            const chatHistory = await chatApi.getChatHistory(roomId)
            setMessages(chatHistory.map((msg, idx) => ({
                id: idx, // 連番でnumber型に変換
                user: msg.senderUsername || '匿名', // ユーザー名を表示
                text: typeof msg.content === 'string' ? msg.content : msg.content.value,
                isCurrentUser: String(msg.senderUserId) === String(currentUserId),
                sentAt: msg.sentAt // 送信時刻を格納
            })))
            // Surveyメッセージ取得（例: チャット履歴からSurvey関連を抽出）
            const surveyMsgs = chatHistory.filter(msg => {
                if (typeof msg.content === 'object' && msg.content.value.startsWith('[SURVEY]')) {
                    return true
                }
                return false
            }).map(msg => {
                const value = typeof msg.content === 'object' ? msg.content.value : ''
                // [SURVEY]surveyId:title 形式を想定
                const match = value.match(/^\[SURVEY\](.*?):(.*)$/)
                return match ? {
                    id: msg.id,
                    surveyId: match[1],
                    type: 'survey',
                    title: match[2],
                    createdAt: msg.sentAt,
                } : null
            }).filter(Boolean) as SurveyMessage[]
            setSurveyMessages(surveyMsgs)
        } catch (e) {
            setError('チャット履歴の取得に失敗しました')
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

    // チャット自動更新（5秒ごと）
    useEffect(() => {
        if (!roomId || !currentUserId) return;
        const interval = setInterval(() => {
            loadChatHistory();
        }, 5000);
        return () => clearInterval(interval);
    }, [roomId, currentUserId])

    const handleSend = async () => {
        if (!input.trim() || !roomId) return

        try {
            // サーバーにメッセージを送信（送信時刻を付与）
            await chatApi.sendMessage(roomId, {
                messageContent: input,
                sentAt: new Date().toISOString(),
            })
            // 送信直後に全員のチャット履歴を即時再取得
            await loadChatHistory()
            setInput("")
        } catch (err) {
            console.error('メッセージ送信に失敗しました:', err)
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

    // 1. チャットのユーザーアイコンを頭文字一文字に
    const getUserInitial = (user: string) => user.charAt(0).toUpperCase();

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 24, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: '#388e3c' }}>
                {String(roomTitle)}
            </h2>

            {/* ナビゲーションボタン */}
            <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
                <button
                    onClick={handleGoToReading}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#4caf50',
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
                        background: '#2196f3',
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
                        background: '#757575',
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
                    background: '#ffebee',
                    color: '#c62828',
                    padding: 12,
                    borderRadius: 8,
                    marginBottom: 16,
                    border: '1px solid #ef5350'
                }}>
                    {error}
                    <button
                        onClick={loadChatHistory}
                        style={{
                            marginLeft: 12,
                            background: '#c62828',
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

            {/* チャット欄 */}
            <div style={{ flex: 1, overflowY: 'auto', marginBottom: 16, background: '#fff', borderRadius: 8, padding: 16, border: '1px solid #b0b8c9', display: 'flex', flexDirection: 'column', gap: 12 }}>
                {messages.map((msg, idx) => (
                    <div key={msg.id} style={{ display: 'flex', alignItems: 'center', gap: 12, flexDirection: msg.isCurrentUser ? 'row-reverse' : 'row', justifyContent: msg.isCurrentUser ? 'flex-end' : 'flex-start' }}>
                        {/* ユーザーアイコン（頭文字） */}
                        <div style={{ width: 36, height: 36, borderRadius: '50%', background: '#e0e0e0', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: 18, color: '#388e3c', marginLeft: msg.isCurrentUser ? 0 : 4, marginRight: msg.isCurrentUser ? 4 : 0 }}>
                            {getUserInitial(msg.user)}
                        </div>
                        <div style={{ flex: 1, textAlign: msg.isCurrentUser ? 'right' : 'left' }}>
                            <div style={{ fontWeight: msg.isCurrentUser ? 'bold' : 'normal', color: msg.isCurrentUser ? '#388e3c' : '#333' }}>{msg.user}</div>
                            <div style={{ fontSize: 16, display: 'inline-block', background: msg.isCurrentUser ? '#c8e6c9' : '#fff', borderRadius: 8, padding: '8px 16px', margin: msg.isCurrentUser ? '0 0 0 24px' : '0 24px 0 0' }}>{msg.text}</div>
                            {/* タイムスタンプ */}
                            <div style={{ fontSize: 12, color: '#888' }}>{msg.sentAt ? new Date(msg.sentAt).toLocaleTimeString() : ''}</div>
                        </div>
                    </div>
                ))}
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

            {/* チャット欄の下部にSurveyメッセージを表示 */}
            <div style={{ marginTop: 24 }}>
                {surveyMessages.length > 0 && (
                    <div style={{ background: '#e3f2fd', borderRadius: 8, padding: 12, marginBottom: 8 }}>
                        <b>アンケート:</b>
                        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                            {surveyMessages.map(s => (
                                <li key={s.id} style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 8 }}>
                                    <span style={{ color: '#1976d2', fontWeight: 500 }}>アンケート名：</span>
                                    <span style={{ color: '#1976d2', fontWeight: 500 }}>{s.title}</span>
                                    <span style={{ marginLeft: 8, fontSize: 12, color: '#888' }}>{new Date(s.createdAt).toLocaleString()}</span>
                                    <button
                                        style={{ marginLeft: 'auto', padding: '6px 16px', background: '#388e3c', color: 'white', border: 'none', borderRadius: 6, cursor: 'pointer', fontSize: 14 }}
                                        onClick={() => {
                                            setShowSurveyAnswerModal(true);
                                            setDummySurveyId(s.surveyId);
                                        }}
                                    >
                                        回答する
                                    </button>
                                </li>
                            ))}
                        </ul>
                    </div>
                )}
            </div>

            {/* チャット入力欄 */}
            <form
                onSubmit={e => { e.preventDefault(); handleSend(); }}
                style={{ display: 'flex', gap: 12, marginTop: 8 }}
            >
                <input
                    type="text"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    placeholder="メッセージを入力..."
                    style={{ flex: 1, padding: 12, fontSize: 16, borderRadius: 8, border: '1px solid #b0b8c9' }}
                    onKeyDown={e => { if (e.key === 'Enter' && !e.shiftKey) { handleSend(); e.preventDefault(); } }}
                />
                <button
                    type="submit"
                    disabled={!input.trim()}
                    style={{ padding: '12px 24px', fontSize: 16, borderRadius: 8, background: '#388e3c', color: 'white', border: 'none', fontWeight: 'bold', cursor: !input.trim() ? 'not-allowed' : 'pointer', opacity: !input.trim() ? 0.6 : 1 }}
                >
                    送信
                </button>
            </form>
        </div>
    )
}

export default GroupChatScreen
