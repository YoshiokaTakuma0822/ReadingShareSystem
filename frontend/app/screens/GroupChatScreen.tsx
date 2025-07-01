"use client"
import { useRouter } from 'next/navigation'
import React, { useEffect, useState } from 'react'
import { chatApi } from '../../lib/chatApi'
import { roomApi } from '../../lib/roomApi'
import { surveyApi } from '../../lib/surveyApi'
import { Room } from '../../types/room'
import { Survey } from '../../types/survey'
import ReadingScreenOverlay from './ReadingScreenOverlay'
import SurveyAnswerModal from './SurveyAnswerModal'
import SurveyCreationModal from './SurveyCreationModal'
import SurveyResultModal from './SurveyResultModal'

interface Message {
    id: number
    user: string
    text: string
    isCurrentUser: boolean
    sentAt?: string
    messageType?: string // 追加: メッセージタイプ
    surveyId?: string // 追加: アンケートID
}

interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
}

// SurveyMessageCardコンポーネント
interface SurveyMessageCardProps {
    msg: Message
    isMine: boolean
    currentUserId: string | null
    onAnswerClick: (surveyId: string) => void
    onResultClick: (surveyId: string) => void
}

const SurveyMessageCard: React.FC<SurveyMessageCardProps> = ({ msg, isMine, currentUserId, onAnswerClick, onResultClick }) => {
    const [surveyData, setSurveyData] = useState<Survey | null>(null)
    const [loading, setLoading] = useState(true)
    const [hasAnswered, setHasAnswered] = useState(false)
    const [error, setError] = useState<string | null>(null)

    // アンケート情報を取得
    useEffect(() => {
        if (msg.surveyId) {
            surveyApi.getSurveyFormat(msg.surveyId)
                .then(data => {
                    setSurveyData(data)
                    setLoading(false)
                })
                .catch(() => {
                    setError('アンケート情報の取得に失敗しました')
                    setLoading(false)
                })
        }
    }, [msg.surveyId])

    // 回答状態を確認
    useEffect(() => {
        if (msg.surveyId && currentUserId) {
            surveyApi.hasAnswered(msg.surveyId, currentUserId)
                .then(answered => {
                    setHasAnswered(answered)
                })
                .catch(() => {
                    setHasAnswered(false)
                })
        }
    }, [msg.surveyId, currentUserId])

    return (
        <div
            style={{
                display: 'flex',
                alignItems: 'flex-start',
                gap: 8,
                justifyContent: isMine ? 'flex-end' : 'flex-start',
                marginBottom: 12
            }}
        >
            {!isMine && (
                <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                </span>
            )}
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: isMine ? 'flex-end' : 'flex-start', gap: 4 }}>
                {/* タイムスタンプとユーザー名 */}
                <div style={{ fontSize: '0.8em', color: '#888', display: 'flex', gap: 8 }}>
                    <span>{msg.user}</span>
                    {msg.sentAt && <span>{new Date(msg.sentAt).toLocaleTimeString()}</span>}
                </div>
                {/* アンケートカード */}
                <div
                    style={{
                        border: '2px solid #2196f3',
                        borderRadius: 12,
                        padding: 16,
                        background: 'linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%)',
                        maxWidth: 500,
                        minWidth: 300,
                        boxShadow: '0 2px 8px rgba(33, 150, 243, 0.2)'
                    }}
                >
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
                        <span style={{ fontSize: 20 }}>📊</span>
                        <span style={{ fontWeight: 'bold', color: '#1976d2' }}>新しいアンケート</span>
                    </div>

                    {loading ? (
                        <div style={{ color: '#666', fontStyle: 'italic' }}>アンケート情報を読み込み中...</div>
                    ) : error ? (
                        <div style={{ color: '#d32f2f' }}>{error}</div>
                    ) : surveyData ? (
                        <div>
                            <div style={{ marginBottom: 12 }}>
                                <strong style={{ color: '#1976d2' }}>タイトル:</strong> {surveyData.title}
                            </div>
                            {surveyData.questions.map((q, qi) => (
                                <div key={qi} style={{ marginBottom: 12 }}>
                                    <div style={{ fontWeight: 'bold', marginBottom: 4, color: '#333' }}>{q.questionText}</div>
                                    <div style={{ fontSize: '0.9em', color: '#555' }}>
                                        <strong>選択肢:</strong>
                                        <ul style={{ margin: '4px 0', paddingLeft: 20 }}>
                                            {q.options.map((opt, oi) => (
                                                <li key={oi} style={{ marginBottom: 2 }}>{opt}</li>
                                            ))}
                                        </ul>
                                    </div>
                                </div>
                            ))}
                            {msg.surveyId && (
                                <div style={{ display: 'flex', gap: 8, width: '100%' }}>
                                    {hasAnswered ? (
                                        <button
                                            onClick={() => onResultClick(msg.surveyId!)}
                                            style={{
                                                flex: 1,
                                                padding: '12px 24px',
                                                borderRadius: 8,
                                                border: 'none',
                                                fontSize: 16,
                                                background: '#4caf50',
                                                color: 'white',
                                                cursor: 'pointer',
                                                fontWeight: 'bold',
                                                boxShadow: '0 2px 4px rgba(76, 175, 80, 0.3)'
                                            }}
                                        >
                                            📊 結果を表示
                                        </button>
                                    ) : (
                                        <button
                                            onClick={() => onAnswerClick(msg.surveyId!)}
                                            style={{
                                                flex: 1,
                                                padding: '12px 24px',
                                                borderRadius: 8,
                                                border: 'none',
                                                fontSize: 16,
                                                background: '#2196f3',
                                                color: 'white',
                                                cursor: 'pointer',
                                                fontWeight: 'bold',
                                                boxShadow: '0 2px 4px rgba(33, 150, 243, 0.3)'
                                            }}
                                        >
                                            📝 アンケートに回答する
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    ) : (
                        <div style={{ color: '#666', fontStyle: 'italic' }}>アンケート情報がありません</div>
                    )}
                </div>
            </div>
            {isMine && (
                <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>
                    {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                </span>
            )}
        </div>
    )
}

// グローバルwindowに型を追加
declare global {
    interface Window {
        updateGroupChatScreen?: (data: any) => void
    }
}

const GroupChatScreen: React.FC<GroupChatScreenProps> = ({ roomTitle = "チャットルーム", currentUser = "あなた", roomId }) => {
    const router = useRouter()
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState("")
    const [msgId, setMsgId] = useState(1) // 追加
    const [showSurveyModal, setShowSurveyModal] = useState(false)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)
    const [showReadingOverlay, setShowReadingOverlay] = useState(false)
    const [roomName, setRoomName] = useState<string>(roomTitle)
    // アンケート回答モーダル制御
    const [showAnswerModal, setShowAnswerModal] = useState(false)
    const [answerSurveyId, setAnswerSurveyId] = useState<string | null>(null)
    // アンケート結果モーダル制御
    const [showResultModal, setShowResultModal] = useState(false)
    const [resultSurveyId, setResultSurveyId] = useState<string | null>(null)

    // 追加: ユーザーID→ユーザー名のマッピングを保持
    const [userIdToName, setUserIdToName] = useState<Record<string, string>>({})

    // スクロール用ref
    const messagesEndRef = React.useRef<HTMLDivElement | null>(null)

    // コンポーネントマウント時にユーザーIDを取得
    useEffect(() => {
        let userId = localStorage.getItem('reading-share-user-id')
        if (!userId) {
            alert('ユーザー情報が見つかりません。再ログインしてください。')
            window.location.href = '/login' // ログイン画面へリダイレクト
            return
        }
        // ハイフン除去・小文字化して保存
        userId = userId.replace(/-/g, '').toLowerCase()
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
                let messageText = ''

                // SUVEYタイプのメッセージの場合は、textを空文字にする（カードで表示するため）
                if (msg.messageType === 'SURVEY') {
                    messageText = ''
                } else {
                    if (typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content) {
                        messageText = String((msg.content as { value: string }).value || '')
                    } else {
                        messageText = String(msg.content || '')
                    }
                }

                // 厳密な自分判定（ハイフン除去・小文字化）
                const senderId = (msg.senderUserId ?? '').replace(/-/g, '').toLowerCase()
                // currentUserIdはすでに整形済み
                const myId = currentUserId ?? ''
                // msg.senderUserIdがnullの場合は空文字でアクセスしない
                const username = senderId && msg.senderUserId && userIdToName[msg.senderUserId] ? userIdToName[msg.senderUserId] : (msg.senderUserId || '匿名ユーザー')
                return {
                    id: index + 1,
                    user: username,
                    text: messageText,
                    isCurrentUser: !!(senderId && myId && senderId === myId),
                    sentAt: msg.sentAt,
                    messageType: msg.messageType || 'TEXT', // 追加: メッセージタイプ
                    surveyId: msg.surveyId // 追加: アンケートID
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
            // サーバーにメッセージを送信（WebSocket経由で全員に配信されるのを待つ）
            await chatApi.sendMessage(roomId, { messageContent: input })
            // ローカル状態はWebSocket受信時のみ更新する（ここでは更新しない）
            setInput("")
        } catch (err) {
            console.error('メッセージ送信に失敗しました:', err)
            // エラー時のみローカルに一時的に表示（任意）
        }
    }

    const handleGoToReading = () => {
        setShowReadingOverlay(true)
    }

    const handleCreateSurvey = () => {
        setShowSurveyModal(true)
    }

    // アンケート作成後はモーダルを閉ち、回答モーダルを開く
    const handleSurveyCreated = (surveyId: string) => {
        setShowSurveyModal(false)
        setAnswerSurveyId(surveyId)
        setShowAnswerModal(true)
    }

    // アンケート回答後のコールバック
    const handleSurveyAnswered = () => {
        setShowAnswerModal(false)
        // チャット履歴を再読み込みして回答状態を更新
        loadChatHistory()
    }

    // 通知受信時にチャット履歴を再取得して更新
    useEffect(() => {
        if (!roomId) return
        const ws = new WebSocket(`ws://localhost:8080/ws/chat/notifications/${roomId}`)
        ws.onmessage = () => {
            console.log('通知受信: チャット履歴を再読み込み')
            loadChatHistory()
        }
        return () => ws.close()
    }, [roomId])

    // メッセージ追加時に自動スクロール
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' })
        }
    }, [messages])

    // 部屋名取得
    useEffect(() => {
        if (roomId) {
            roomApi.getRoom(roomId).then((room: Room) => {
                setRoomName(room.roomName)
            }).catch(() => {
                setRoomName(roomTitle) // 取得失敗時はデフォルト
            })
        }
    }, [roomId])

    // 部屋メンバー一覧を取得してユーザー名マッピングを作成
    useEffect(() => {
        if (!roomId) return
        roomApi.getRoomMembers(roomId).then((members: any[]) => {
            const map: Record<string, string> = {}
            members.forEach(m => {
                if (m.userId && m.username) map[m.userId] = m.username
            })
            setUserIdToName(map)
        })
    }, [roomId])

    // メッセージ表示コンポーネント
    const renderMessage = (msg: Message) => {
        const isMine = msg.isCurrentUser

        // アンケートメッセージの場合
        if (msg.messageType === 'SURVEY') {
            return <SurveyMessageCard
                key={msg.id}
                msg={msg}
                isMine={isMine}
                currentUserId={currentUserId}
                onAnswerClick={(surveyId: string) => {
                    setAnswerSurveyId(surveyId)
                    setShowAnswerModal(true)
                }}
                onResultClick={(surveyId: string) => {
                    setResultSurveyId(surveyId)
                    setShowResultModal(true)
                }}
            />
        }

        // 通常のテキストメッセージ
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
                    <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                    </span>
                )}
                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                    {/* 自分のメッセージはタイムスタンプを左側に */}
                    {isMine && msg.sentAt && (
                        <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'right' }}>
                            {new Date(msg.sentAt).toLocaleTimeString()}
                        </span>
                    )}
                    <div
                        style={{
                            border: '1px solid #222',
                            borderRadius: 16,
                            padding: 8,
                            background: isMine ? '#e0f7fa' : '#fff',
                            maxWidth: 600,
                            wordBreak: 'break-word',
                        }}
                    >
                        {String(msg.text)}
                    </div>
                    {/* 他人のメッセージはタイムスタンプを右側に */}
                    {!isMine && msg.sentAt && (
                        <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'left' }}>
                            {new Date(msg.sentAt).toLocaleTimeString()}
                        </span>
                    )}
                </div>
                {isMine && (
                    <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>
                        {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                    </span>
                )}
            </div>
        )
    }

    // userIdToNameまたはcurrentUserIdが更新されたら履歴を再生成
    useEffect(() => {
        if (!roomId || !currentUserId || Object.keys(userIdToName).length === 0) return;
        (async () => {
            setLoading(true)
            setError(null)
            try {
                const chatHistory = await chatApi.getChatHistory(roomId)
                const convertedMessages: Message[] = chatHistory.map((msg, index) => {
                    let messageText = ''
                    if (typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content) {
                        messageText = String((msg.content as { value: string }).value || '')
                    } else {
                        messageText = String(msg.content || '')
                    }

                    // SURVEYメッセージの場合はテキストを空にする（カードで表示するため）
                    if (msg.messageType === 'SURVEY') {
                        messageText = ''
                    }

                    const senderId = (msg.senderUserId ?? '').replace(/-/g, '').toLowerCase()
                    const myId = currentUserId ?? ''
                    const username = senderId && msg.senderUserId && userIdToName[msg.senderUserId] ? userIdToName[msg.senderUserId] : (msg.senderUserId || '匿名ユーザー')
                    return {
                        id: index + 1,
                        user: username,
                        text: messageText,
                        isCurrentUser: !!(senderId && myId && senderId === myId),
                        sentAt: msg.sentAt,
                        messageType: msg.messageType || 'TEXT', // 追加: メッセージタイプ
                        surveyId: msg.surveyId // 追加: アンケートID
                    }
                })
                setMessages(convertedMessages)
                setMsgId(convertedMessages.length + 1)
            } catch (err) {
                setError('チャット履歴の読み込みに失敗しました')
            } finally {
                setLoading(false)
            }
        })()
    }, [roomId, currentUserId, userIdToName])

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 24, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh', position: 'relative' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: '#388e3c' }}>
                {roomName}
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
                    📖 読書画面をオーバーレイ表示
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

            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 16, marginBottom: 32, minHeight: 200, maxHeight: '60vh', overflowY: 'auto', background: 'rgba(255,255,255,0.7)', borderRadius: 8, padding: 16 }}>
                {loading ? (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: '100%',
                        color: '#666',
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
                        color: '#999',
                        fontSize: 16
                    }}>
                        まだメッセージがありません
                    </div>
                ) : (
                    <>
                        {messages.map(msg => {
                            const isMine = msg.isCurrentUser

                            // アンケートメッセージの場合は専用のカードコンポーネントを表示
                            if (msg.messageType === 'SURVEY') {
                                return (
                                    <SurveyMessageCard
                                        key={msg.id}
                                        msg={msg}
                                        isMine={isMine}
                                        currentUserId={currentUserId}
                                        onAnswerClick={(surveyId) => {
                                            setAnswerSurveyId(surveyId)
                                            setShowAnswerModal(true)
                                        }}
                                        onResultClick={(surveyId) => {
                                            setResultSurveyId(surveyId)
                                            setShowResultModal(true)
                                        }}
                                    />
                                )
                            }

                            // 通常のテキストメッセージの場合
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
                                        <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                            {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                                        </span>
                                    )}
                                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                        {/* 自分のメッセージはタイムスタンプを左側に */}
                                        {isMine && msg.sentAt && (
                                            <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'right' }}>
                                                {new Date(msg.sentAt).toLocaleTimeString()}
                                            </span>
                                        )}
                                        <div
                                            style={{
                                                border: '1px solid #222',
                                                borderRadius: 16,
                                                padding: 8,
                                                background: isMine ? '#e0f7fa' : '#fff',
                                                maxWidth: 600,
                                                wordBreak: 'break-word',
                                            }}
                                        >
                                            {String(msg.text)}
                                        </div>
                                        {/* 他人のメッセージはタイムスタンプを右側に */}
                                        {!isMine && msg.sentAt && (
                                            <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'left' }}>
                                                {new Date(msg.sentAt).toLocaleTimeString()}
                                            </span>
                                        )}
                                    </div>
                                    {isMine && (
                                        <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>
                                            {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                                        </span>
                                    )}
                                </div>
                            )
                        })}
                        <div ref={messagesEndRef} />
                    </>
                )}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32 }}>
                <input
                    type="text"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={e => { if (e.key === 'Enter') handleSend() }}
                    style={{ flex: 1, padding: 12, borderRadius: 8, border: '1px solid #222', fontSize: 18 }}
                    placeholder="メッセージを入力..."
                    disabled={loading}
                />
                <button
                    style={{
                        marginLeft: 8,
                        padding: '12px 24px',
                        borderRadius: 8,
                        border: '1px solid #222',
                        fontSize: 18,
                        background: loading ? '#ccc' : 'white',
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
            <ReadingScreenOverlay roomId={roomId} open={showReadingOverlay} onClose={() => setShowReadingOverlay(false)} />
            {/* アンケート回答モーダル */}
            {showAnswerModal && answerSurveyId && (
                <SurveyAnswerModal
                    open={showAnswerModal}
                    surveyId={answerSurveyId!}
                    onClose={() => setShowAnswerModal(false)}
                    onAnswered={handleSurveyAnswered}
                />
            )}

            {/* アンケート結果モーダル */}
            {showResultModal && resultSurveyId && (
                <SurveyResultModal
                    open={showResultModal}
                    surveyId={resultSurveyId!}
                    onClose={() => setShowResultModal(false)}
                />
            )}
        </div>
    )

} // GroupChatScreen 関数を閉じる

export default GroupChatScreen
