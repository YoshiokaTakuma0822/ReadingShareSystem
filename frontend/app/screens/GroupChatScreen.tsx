"use client"

import { useRouter } from 'next/navigation'
import React, { useEffect, useState } from 'react'
import MessageList from '../../components/MessageList'
import { chatApi } from '../../lib/chatApi'
import { roomApi } from '../../lib/roomApi'
import { Message } from '../../types/message'
import { Room } from '../../types/room'
import ReadingScreenOverlay from './ReadingScreenOverlay'
import SurveyAnswerModal from './SurveyAnswerModal'
import SurveyCreationModal from './SurveyCreationModal'
import SurveyResultModal from './SurveyResultModal'


interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
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

    // 追加: アンケートメッセージのローディング状態を追跡
    const [surveyLoadingStates, setSurveyLoadingStates] = useState<Record<number, boolean>>({})

    // 追加: スクロールが必要かどうかのフラグ
    const [shouldScrollToBottom, setShouldScrollToBottom] = useState(false)

    // スクロール用ref
    const messagesContainerRef = React.useRef<HTMLDivElement | null>(null)

    // 初回ロード判定用ref
    const initialLoadRef = React.useRef(true)
    // 即時スクロール関数
    const instantScrollToBottom = React.useCallback(() => {
        if (messagesContainerRef.current) {
            messagesContainerRef.current.scrollTo({
                top: messagesContainerRef.current.scrollHeight,
                behavior: 'auto'
            })
        }
    }, [])

    // なめらかなスクロール関数
    const smoothScrollToBottom = React.useCallback(() => {
        if (messagesContainerRef.current) {
            messagesContainerRef.current.scrollTo({
                top: messagesContainerRef.current.scrollHeight,
                behavior: 'smooth'
            })
        }
    }, [])

    // アンケートメッセージのローディング完了コールバック
    const handleSurveyLoadingComplete = React.useCallback((messageId: number) => {
        setSurveyLoadingStates(prev => {
            const newStates = {
                ...prev,
                [messageId]: true
            }

            // すべてのアンケートメッセージのローディングが完了したかチェック
            const surveyMessages = messages.filter(msg => msg.messageType === 'SURVEY')
            const allLoaded = surveyMessages.every(msg => newStates[msg.id] === true)

            if (allLoaded && shouldScrollToBottom) {
                // 次のレンダリング後にスクロールを実行
                setTimeout(() => {
                    if (initialLoadRef.current) {
                        instantScrollToBottom()
                        initialLoadRef.current = false
                    } else {
                        smoothScrollToBottom()
                    }
                    setShouldScrollToBottom(false)
                }, 100) // 少し遅延を追加してレンダリング完了を確実にする
            }

            return newStates
        })
    }, [messages, shouldScrollToBottom])

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
            // ソート: sentAtを主キー、idを副キーとして
            chatHistory.sort((a, b) => {
                if (a.sentAt < b.sentAt) return -1
                if (a.sentAt > b.sentAt) return 1
                // sentAt が同じなら UUID を辞書式に比較
                return a.id < b.id ? -1 : a.id > b.id ? 1 : 0
            })

            console.log('取得したチャット履歴:', chatHistory)

            // ChatMessageをMessage形式に変換
            const convertedMessages: Message[] = chatHistory.map((msg, index) => {
                console.log('メッセージ変換:', { senderUserId: msg.senderUserId, senderName: msg.senderName, content: msg.content })
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
                // バックエンドから返されるsenderNameを優先的に使用、ない場合のみfallback
                const username = msg.senderName || (senderId && msg.senderUserId && userIdToName[msg.senderUserId] ? userIdToName[msg.senderUserId] : '匿名ユーザー')
                return {
                    id: index + 1,
                    uuid: msg.id, // 元のメッセージ UUID を保持
                    user: username,
                    text: messageText,
                    isCurrentUser: !!(senderId && myId && senderId === myId),
                    sentAt: msg.sentAt,
                    messageType: msg.messageType || 'TEXT', // 追加: メッセージタイプ
                    surveyId: msg.surveyId // 追加: アンケートID
                }
            })

            // 差分のみ追加し、既存メッセージは保持
            if (messages.length > 0) {
                const existingUuids = new Set(messages.map(m => m.uuid))
                const newOnly = convertedMessages.filter(m => !existingUuids.has(m.uuid))
                if (newOnly.length > 0) {
                    const combined = [...messages, ...newOnly]
                    combined.sort((a, b) => {
                        if ((a.sentAt ?? '') < (b.sentAt ?? '')) return -1
                        if ((a.sentAt ?? '') > (b.sentAt ?? '')) return 1
                        return a.uuid < b.uuid ? -1 : a.uuid > b.uuid ? 1 : 0
                    })
                    setMessages(combined)
                    setMsgId(combined.length + 1)
                }
            } else {
                setMessages(convertedMessages)
                setMsgId(convertedMessages.length + 1)
            }
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

    /*
    // メッセージ追加時にスクロール処理を設定
    useEffect(() => {
        // アンケートメッセージのローディング状態を初期化
        const surveyMessages = messages.filter(msg => msg.messageType === 'SURVEY')
        const initialLoadingStates: Record<number, boolean> = {}
        surveyMessages.forEach(msg => {
            initialLoadingStates[msg.id] = false
        })
        setSurveyLoadingStates(initialLoadingStates)

        // スクロールフラグを設定
        setShouldScrollToBottom(true)

        // アンケートメッセージがない場合は即座にスクロール
        if (surveyMessages.length === 0) {
            setTimeout(() => {
                if (initialLoadRef.current) {
                    instantScrollToBottom()
                    initialLoadRef.current = false
                } else {
                    smoothScrollToBottom()
                }
                setShouldScrollToBottom(false)
            }, 100) // 少し遅延を追加してレンダリング完了を確実にする
        }
    }, [messages])
    */

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

    // userIdToNameまたはcurrentUserIdが更新されたら差分フェッチで履歴を更新
    useEffect(() => {
        if (!roomId || !currentUserId || Object.keys(userIdToName).length === 0) return
        // 全フェッチを差分追加する共通ロジックを利用
        loadChatHistory()
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

            <div ref={messagesContainerRef} style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                gap: 16,
                marginBottom: 32,
                minHeight: 200,
                maxHeight: '60vh',
                overflowY: 'auto',
                background: 'rgba(255,255,255,0.7)',
                borderRadius: 8,
                padding: 16,
                scrollBehavior: 'smooth' // なめらかなスクロールを追加
            }}>
                <MessageList
                    messages={messages}
                    loading={loading}
                    currentUserId={currentUserId}
                    onAnswerClick={(surveyId) => {
                        setAnswerSurveyId(surveyId)
                        setShowAnswerModal(true)
                    }}
                    onResultClick={(surveyId) => {
                        setResultSurveyId(surveyId)
                        setShowResultModal(true)
                    }}
                    onLoadingComplete={handleSurveyLoadingComplete}
                />
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
