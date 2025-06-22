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
                zIndex: 1000,
            }}
            onClick={handleBackgroundClick}
        >
            <div style={{ background: '#fff', borderRadius: 16, padding: 32, minWidth: 400, minHeight: 320, boxShadow: '0 8px 32px rgba(0,0,0,0.2)', position: 'relative' }}>
                <h2 style={{ textAlign: 'center', marginBottom: 16, color: '#388e3c' }}>部屋に参加</h2>
                {/* 部屋情報表示（ジャンル・開始終了時刻など） */}
                <div style={{ marginBottom: 16, fontSize: 15, color: '#333' }}>
                    <div><b>部屋名:</b> {room.roomName}</div>
                    <div><b>本:</b> {room.bookTitle}</div>
                    {room.genre && <div><b>ジャンル:</b> {room.genre}</div>}
                    {room.startTime && <div><b>開始時刻:</b> {room.startTime}</div>}
                    {room.endTime && <div><b>終了時刻:</b> {room.endTime}</div>}
                    {room.maxPage && <div><b>ページ数:</b> {room.maxPage}</div>}
                    {room.pageSpeed && <div><b>めくり速度:</b> {room.pageSpeed}秒</div>}
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
