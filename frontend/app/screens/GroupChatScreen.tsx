"use client"

import { useRouter } from 'next/navigation'
import React, { useEffect, useRef, useState } from 'react'
import { MdHome, MdMenuBook, MdPoll } from 'react-icons/md'
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
    // アンケート回答モーダル制御
    // アンケート回答・結果はSurveyMessageCard内で処理

    // 追加: ユーザーID→ユーザー名のマッピングを保持
    const [userIdToName, setUserIdToName] = useState<Record<string, string>>({})
    const [scrollTrigger, setScrollTrigger] = useState(0)
    const [roomStatus, setRoomStatus] = useState<{ isActive: boolean; startTime?: string; endTime?: string } | null>(null)

    // input要素のrefを追加
    const inputRef = useRef<HTMLInputElement>(null)

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

    // 部屋の活動時間ステータスを取得
    useEffect(() => {
        if (!roomId) return
        const fetchRoomStatus = async () => {
            try {
                const status = await roomApi.getRoomStatus(roomId)
                setRoomStatus(status)
            } catch (error) {
                console.error('Failed to fetch room status:', error)
                // エラーの場合は活動可能として扱う（既存機能を壊さないため）
                setRoomStatus({ isActive: true })
            }
        }
        fetchRoomStatus()
        // 定期的にステータスを更新（1分おき）
        const interval = setInterval(fetchRoomStatus, 60000)
        return () => clearInterval(interval)
    }, [roomId])

    // メッセージ送信ハンドラ
    const handleSendMessage = async () => {
        // メッセージ長チェック: 10000文字以内
        if (input.length > 10000) {
            setError('メッセージは10000文字以下にしてください.')
            return
        }
        if (!input.trim() || !roomId) return

        // 部屋の活動時間チェック
        if (roomStatus && !roomStatus.isActive) {
            setError('部屋の活動時間外のため、メッセージを送信できません')
            return
        }

        setLoading(true)
        setError(null)
        try {
            await chatApi.sendMessage(roomId, { messageContent: input })
            setInput("")
            // メッセージ送信後にスクロールをトリガー
            setScrollTrigger(prev => prev + 1)
            // フォーカスを入力欄に戻す（Chrome対応）
            setTimeout(() => {
                inputRef.current?.focus()
            }, 100)
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
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        gap: 8
                    }}
                >
                    <MdMenuBook style={{ fontSize: 25, marginTop: -3.5 }} />
                    本を読む
                </button>
                <button
                    onClick={() => {
                        if (roomStatus && !roomStatus.isActive) {
                            setError('部屋の活動時間外のため、アンケートを作成できません')
                            return
                        }
                        setShowSurveyModal(true)
                    }}
                    disabled={roomStatus ? !roomStatus.isActive : false}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: (roomStatus && !roomStatus.isActive) ? '#ccc' : '#2196f3',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: (roomStatus && !roomStatus.isActive) ? 'not-allowed' : 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        gap: 8,
                        opacity: (roomStatus && !roomStatus.isActive) ? 0.6 : 1
                    }}
                >
                    <MdPoll style={{ fontSize: 25, marginTop: -1.5 }} />
                    アンケートを作る
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
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                        display: 'flex',
                        alignItems: 'center',
                        gap: 8
                    }}
                >
                    <MdHome style={{ fontSize: 25, marginTop: -1.5 }} />
                    ホームへ
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
                </div>
            )}

            {/* メッセージリスト */}
            <div style={{
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                marginBottom: 32,
                minHeight: 200,
                maxHeight: '60vh',
                background: 'rgba(255,255,255,0.7)',
                borderRadius: 8,
                padding: 16
            }}>
                {/* チャット取得・スクロールはMessageListに移譲 */}
                <MessageList roomId={roomId} scrollTrigger={scrollTrigger} />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32 }}>
                <input
                    ref={inputRef}
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
                        background: (loading || (roomStatus && !roomStatus.isActive)) ? '#ccc' : 'white',
                        cursor: (loading || (roomStatus && !roomStatus.isActive)) ? 'not-allowed' : 'pointer',
                        opacity: (loading || (roomStatus && !roomStatus.isActive)) ? 0.6 : 1
                    }}
                    onClick={handleSendMessage}
                    disabled={loading || (roomStatus ? !roomStatus.isActive : false)}
                >送信</button>
            </div>

            {/* アンケート作成モーダル */}
            {showSurveyModal && roomId && (
                <SurveyCreationModal
                    open={showSurveyModal}
                    roomId={roomId}
                    onClose={() => setShowSurveyModal(false)}
                    onCreated={() => {
                        setShowSurveyModal(false) // 作成後はモーダルを閉じる
                        setScrollTrigger(prev => prev + 1) // スクロールをトリガー
                    }}
                />
            )}
            <ReadingScreenOverlay roomId={roomId} open={showReadingOverlay} onClose={() => setShowReadingOverlay(false)} />

        </div>
    )

} // GroupChatScreen 関数を閉じる

export default GroupChatScreen
