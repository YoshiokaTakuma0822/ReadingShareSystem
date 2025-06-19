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
    border: '2px solid var(--border)',
    borderRadius: 6,
    fontSize: 16,
    boxSizing: 'border-box' as const,
    outline: 'none',
    background: 'var(--input)',
    color: 'var(--text)',
}

const RoomJoinModal: React.FC<RoomJoinModalProps> = ({ open, room, userId, onClose, onJoined }) => {
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleJoin = async () => {
        setLoading(true)
        setError(null)
        try {
            const request: JoinRoomRequest = {
                roomId: room.id,
                userId,
                roomPassword: password || undefined
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
            setError('部屋参加に失敗しました')
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
                zIndex: 1000
            }}
            onClick={handleBackgroundClick}
        >
            <div style={{
                maxWidth: 500,
                width: '90%',
                margin: 'auto',
                border: '2px solid var(--accent)',
                padding: 32,
                borderRadius: 12,
                background: 'var(--background)',
                boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
                color: 'var(--text)'
            }}
                onClick={(e) => e.stopPropagation()} // モーダル内のクリックで閉じるのを防ぐ
            >
                <h2 style={{ fontWeight: 'bold', fontSize: 24, marginBottom: 24, color: 'var(--accent)' }}>部屋に参加</h2>

                {/* 部屋情報表示 */}
                <div style={{
                    background: 'var(--card)',
                    padding: 16,
                    borderRadius: 8,
                    marginBottom: 20,
                    border: '1px solid var(--border)'
                }}>
                    <h3 style={{ fontSize: 18, fontWeight: 'bold', color: 'var(--accent)', marginBottom: 8 }}>
                        {room.roomName}
                    </h3>
                    <p style={{ color: 'var(--text)', marginBottom: 4 }}>
                        <strong>本:</strong> {room.bookTitle}
                    </p>
                    <p style={{ color: 'var(--text-muted)', fontSize: 14 }}>
                        <strong>作成日:</strong> {new Date(room.createdAt).toLocaleDateString()}
                    </p>
                </div>

                {room.hasPassword && (
                    <div style={{ marginBottom: 16 }}>
                        <label style={{ color: 'var(--text)', fontWeight: 'bold' }}>パスワード</label>
                        <input
                            type="password"
                            value={password}
                            onChange={e => setPassword(e.target.value)}
                            style={inputStyle}
                            placeholder="パスワードを入力してください"
                        />
                    </div>
                )}

                {!room.hasPassword && (
                    <div style={{ marginBottom: 16, color: 'var(--accent)', fontSize: 14 }}>
                        ✓ この部屋はパスワード保護されていません
                    </div>
                )}

                {error && <div style={{ color: 'var(--error)', marginBottom: 12, padding: 8, background: 'var(--error-background)', borderRadius: 4 }}>{error}</div>}
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16, marginTop: 24 }}>
                    <button
                        onClick={onClose}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid var(--border)',
                            borderRadius: 8,
                            background: 'transparent',
                            color: 'var(--text)',
                            fontSize: 16,
                            cursor: 'pointer'
                        }}
                    >
                        キャンセル
                    </button>
                    <button
                        onClick={handleJoin}
                        disabled={loading || (room.hasPassword && !password.trim())}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid var(--accent)',
                            borderRadius: 8,
                            background: 'var(--accent)',
                            color: 'white',
                            fontSize: 16,
                            cursor: loading || (room.hasPassword && !password.trim()) ? 'not-allowed' : 'pointer',
                            opacity: loading || (room.hasPassword && !password.trim()) ? 0.6 : 1
                        }}
                    >
                        {loading ? '参加中...' : '部屋に参加'}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default RoomJoinModal
