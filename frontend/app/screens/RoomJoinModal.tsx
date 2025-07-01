"use client"
import React, { useState } from 'react'
import { roomApi } from '../../lib/roomApi'
import { JoinRoomRequest, Room } from '../../types/room'

interface RoomJoinModalProps {
    open: boolean
    room: Room
    userId: string
    onClose: () => void
    onJoined: () => void
}

const inputStyle = {
    width: '100%',
    padding: 8,
    marginTop: 4,
    border: '2px solid #888',
    borderRadius: 6,
    fontSize: 16,
    boxSizing: 'border-box' as const,
    outline: 'none',
}

const RoomJoinModal: React.FC<RoomJoinModalProps> = ({ open, room, userId, onClose, onJoined }) => {
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleJoin = async () => {
        setLoading(true)
        setError(null)
        try {
            // パスワードが必要な場合のみroomPasswordを設定
            const request: JoinRoomRequest = {
                roomId: room.id,
                userId,
                roomPassword: password.trim() || undefined
            }
            await roomApi.joinRoom(request)
            onJoined()
        } catch (e: any) {
            // 既に部屋のメンバーの場合は、そのまま成功として扱う
            const errorMessage = e.response?.data?.message || e.message || ''
            if (errorMessage.includes('既に部屋のメンバーです')) {
                onJoined()
                return
            }
            // エラー時はonJoinedを絶対に呼ばない
            if (errorMessage.includes('パスワードが正しくありません')) {
                setError('パスワードが正しくありません')
            } else if (errorMessage.includes('パスワードが必要です')) {
                setError('パスワードを入力してください')
            } else {
                setError('部屋参加に失敗しました')
            }
            // モーダルは閉じない
        } finally {
            setLoading(false)
        }
    }

    if (!open) return null

    const handleBackgroundClick = (e: React.MouseEvent<HTMLDivElement>) => {
        // クリックされた要素が背景の場合のみ閉じる
        if (e.target === e.currentTarget) {
            onClose()
        }
    }

    return (
        <div
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background: 'rgba(0,0,0,0.5)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 1000,
            }}
            onClick={handleBackgroundClick}
        >
            <div style={{
                maxWidth: 500,
                width: '90%',
                margin: 'auto',
                border: '2px solid #388e3c',
                padding: 32,
                borderRadius: 12,
                background: '#f1fdf6',
                boxShadow: '0 8px 32px rgba(0,0,0,0.2)'
            }}
                onClick={(e) => e.stopPropagation()}
                onKeyDown={e => {
                    if (e.key === 'Enter' && !loading && (!room.hasPassword || password.trim())) {
                        handleJoin();
                    }
                }}
                tabIndex={0}
            >
                <h2 style={{ fontWeight: 'bold', fontSize: 24, marginBottom: 24, color: '#388e3c' }}>部屋に参加</h2>

                {/* 部屋情報表示 */}
                <div style={{
                    background: 'rgba(255,255,255,0.8)',
                    padding: 16,
                    borderRadius: 8,
                    marginBottom: 20,
                    border: '1px solid #c8e6c9'
                }}>
                    <h3 style={{ fontSize: 18, fontWeight: 'bold', color: '#2e7d32', marginBottom: 8 }}>
                        {room.roomName}
                    </h3>
                    <p style={{ color: '#424242', marginBottom: 4 }}>
                        <strong>本:</strong> {room.bookTitle}
                    </p>
                    <p style={{ color: '#424242', marginBottom: 4 }}>
                        <strong>ジャンル:</strong> {room.genre || '未設定'}
                    </p>
                    <p style={{ color: '#424242', marginBottom: 4 }}>
                        <strong>開始時刻:</strong> {room.startTime ? new Date(room.startTime).toLocaleString() : '未設定'}
                    </p>
                    <p style={{ color: '#424242', marginBottom: 4 }}>
                        <strong>終了時刻:</strong> {room.endTime ? new Date(room.endTime).toLocaleString() : '未設定'}
                    </p>
                    <p style={{ color: '#424242', fontSize: 14 }}>
                        <strong>作成日:</strong> {new Date(room.createdAt).toLocaleDateString()}
                    </p>
                </div>
                {/* ...既存のパスワード入力・参加ボタン... */}
                <div style={{ marginBottom: 16 }}>
                    {room.hasPassword && (
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            placeholder="パスワード"
                            style={inputStyle}
                        />
                    )}
                </div>
                {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
                <button onClick={handleJoin} disabled={loading} style={{ width: '100%', padding: 12, fontSize: 18, borderRadius: 8, border: '1px solid #222', background: '#388e3c', color: '#fff', fontWeight: 'bold' }}>{loading ? '参加中...' : '参加'}</button>
                <button onClick={onClose} style={{ position: 'absolute', top: 16, right: 16, background: '#888', color: '#fff', border: 'none', borderRadius: 8, padding: '6px 16px', cursor: 'pointer' }}>閉じる</button>
            </div>
        </div>
    )
}

export default RoomJoinModal
