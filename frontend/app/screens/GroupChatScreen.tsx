"use client"

import { useRouter } from 'next/navigation'
import React, { useEffect, useState } from 'react'
import MessageList from '../../components/MessageList'
import { chatApi } from '../../lib/chatApi'
import { roomApi } from '../../lib/roomApi'
import { Room } from '../../types/room'
import ReadingScreenOverlay from './ReadingScreenOverlay'
import SurveyCreationModal from './SurveyCreationModal'


interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
}

const GroupChatScreen: React.FC<GroupChatScreenProps> = ({ roomTitle = "チャットルーム", currentUser = "あなた", roomId }) => {
    const router = useRouter()
    const [input, setInput] = useState("")
    const [showSurveyModal, setShowSurveyModal] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)
    const [showReadingOverlay, setShowReadingOverlay] = useState(false)
    const [roomName, setRoomName] = useState<string>(roomTitle)
    const [testRadioValue, setTestRadioValue] = useState<string>("")
    // アンケート回答モーダル制御
    // アンケート回答・結果はSurveyMessageCard内で処理

    // 追加: ユーザーID→ユーザー名のマッピングを保持
    const [userIdToName, setUserIdToName] = useState<Record<string, string>>({})

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
        // 何もしない（メッセージリスト側で処理）
    }, [])

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

    // メッセージ送信ハンドラ
    const handleSendMessage = async () => {
        if (!input.trim() || !roomId) return
        setLoading(true)
        setError(null)
        try {
            await chatApi.sendMessage(roomId, { messageContent: input })
            setInput("")
        } catch {
            setError('メッセージ送信に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 24, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh', position: 'relative' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: '#388e3c' }}>
                {roomName}
            </h2>

            {/* ナビゲーションボタン */}
            <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
                <button
                    onClick={() => setShowReadingOverlay(true)}
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
                    onClick={() => setShowSurveyModal(true)}
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

            {/* テスト用ラジオボタン - バグ検証 */}
            <div style={{
                background: '#fff3e0',
                border: '2px solid #ff9800',
                borderRadius: 8,
                padding: 16,
                marginBottom: 16
            }}>
                <h3 style={{ margin: '0 0 12px 0', color: '#e65100' }}>🧪 テスト用ラジオボタン</h3>
                <p style={{ margin: '0 0 12px 0', fontSize: 14, color: '#666' }}>
                    現在の選択: <strong>{testRadioValue || "未選択"}</strong>
                </p>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                        <input
                            type="radio"
                            name="testRadio"
                            value="option1"
                            checked={testRadioValue === "option1"}
                            onChange={(e) => {
                                console.log('Test radio changed:', e.target.value)
                                setTestRadioValue(e.target.value)
                            }}
                            style={{ marginRight: 8 }}
                        />
                        オプション1
                    </label>
                    <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                        <input
                            type="radio"
                            name="testRadio"
                            value="option2"
                            checked={testRadioValue === "option2"}
                            onChange={(e) => {
                                console.log('Test radio changed:', e.target.value)
                                setTestRadioValue(e.target.value)
                            }}
                            style={{ marginRight: 8 }}
                        />
                        オプション2
                    </label>
                    <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                        <input
                            type="radio"
                            name="testRadio"
                            value="option3"
                            checked={testRadioValue === "option3"}
                            onChange={(e) => {
                                console.log('Test radio changed:', e.target.value)
                                setTestRadioValue(e.target.value)
                            }}
                            style={{ marginRight: 8 }}
                        />
                        オプション3
                    </label>
                </div>
                <div style={{ marginTop: 12, display: 'flex', gap: 8 }}>
                    <button
                        onClick={() => {
                            console.log('Reset clicked')
                            setTestRadioValue("")
                        }}
                        style={{
                            padding: '6px 12px',
                            fontSize: 12,
                            background: '#ff9800',
                            color: 'white',
                            border: 'none',
                            borderRadius: 4,
                            cursor: 'pointer'
                        }}
                    >
                        リセット
                    </button>
                    <button
                        onClick={() => console.log('Current state:', testRadioValue)}
                        style={{
                            padding: '6px 12px',
                            fontSize: 12,
                            background: '#2196f3',
                            color: 'white',
                            border: 'none',
                            borderRadius: 4,
                            cursor: 'pointer'
                        }}
                    >
                        状態確認
                    </button>
                </div>
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
                        onClick={() => { /* 再試行ロジックはMessageListに移譲 */ }}
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

            {/* メッセージリスト */}
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
                {/* チャット取得・スクロールはMessageListに移譲 */}
                <MessageList roomId={roomId} />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32 }}>
                <input
                    type="text"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={e => { if (e.key === 'Enter') { handleSendMessage() } }}
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
                    onClick={handleSendMessage}
                    disabled={loading}
                >送信</button>
            </div>

            {/* アンケート作成モーダル */}
            {showSurveyModal && roomId && (
                <SurveyCreationModal
                    open={showSurveyModal}
                    roomId={roomId}
                    onClose={() => setShowSurveyModal(false)}
                    onCreated={() => setShowSurveyModal(false)} // 作成後はモーダルを閉じる
                />
            )}
            <ReadingScreenOverlay roomId={roomId} open={showReadingOverlay} onClose={() => setShowReadingOverlay(false)} />

        </div>
    )

} // GroupChatScreen 関数を閉じる

export default GroupChatScreen
