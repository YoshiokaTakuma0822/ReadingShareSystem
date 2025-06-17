"use client"
import React, { useState } from 'react'
import { roomApi } from '../../lib/roomApi'
import { JoinRoomRequest } from '../../types/room'

interface RoomJoinModalProps {
    open: boolean
    roomId: string
    userId: string // 追加: ユーザーID
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

const RoomJoinModal: React.FC<RoomJoinModalProps> = ({ open, roomId, userId, onClose, onJoined }) => {
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleJoin = async () => {
        setLoading(true)
        setError(null)
        try {
            const request: JoinRoomRequest = {
                roomId,
                userId,
                roomPassword: password || undefined
            }
            await roomApi.joinRoom(request)
            onJoined()
        } catch (e) {
            setError('部屋参加に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    if (!open) return null

    return (
        <div style={{ maxWidth: 400, margin: '40px auto', border: '2px solid #388e3c', padding: 32, borderRadius: 8, background: '#f1fdf6', zIndex: 1000, position: 'relative', boxShadow: '0 4px 24px #a5d6a7' }}>
            <h2 style={{ fontWeight: 'bold', fontSize: 24, marginBottom: 24, color: '#388e3c' }}>部屋に参加</h2>
            <div style={{ marginBottom: 16 }}>
                <label>パスワード（必要な場合のみ）</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} />
            </div>
            {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16, marginTop: 32 }}>
                <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>キャンセル</button>
                <button onClick={handleJoin} disabled={loading} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>{loading ? '参加中...' : '部屋に参加'}</button>
            </div>
        </div>
    )
}

export default RoomJoinModal
