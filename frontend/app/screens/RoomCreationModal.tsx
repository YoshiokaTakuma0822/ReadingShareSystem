"use client"

import React, { useState } from 'react'
import { CreateRoomRequest } from '../../types/room'
import { roomApi } from '../../lib/roomApi'

interface RoomCreationModalProps {
    open: boolean
    userId: string // 追加: ホストユーザーID
    onClose: () => void
    onCreated: () => void
}

const RoomCreationModal: React.FC<RoomCreationModalProps> = ({ open, userId, onClose, onCreated }) => {
    const [roomName, setRoomName] = useState('')
    const [bookTitle, setBookTitle] = useState('') // 追加: 本のタイトル
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleCreate = async () => {
        setLoading(true)
        setError(null)
        try {
            const req: CreateRoomRequest = {
                roomName,
                bookTitle,
                hostUserId: userId,
                password: password || undefined,
            }
            await roomApi.createRoom(req)
            onCreated()
        } catch (e) {
            setError('部屋作成に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    if (!open) return null

    return (
        <div style={{ maxWidth: 700, margin: '40px auto', border: '2px solid #388e3c', padding: 32, borderRadius: 8, background: '#f1fdf6', zIndex: 1000, position: 'relative', boxShadow: '0 4px 24px #a5d6a7' }}>
            <h2 style={{ fontWeight: 'bold', fontSize: 28, marginBottom: 24, color: '#388e3c' }}>詳細設定</h2>
            <div style={{ display: 'flex', gap: 32 }}>
                <div style={{ flex: 1 }}>
                    <div style={{ marginBottom: 16 }}>
                        <label>部屋名</label>
                        <input type="text" value={roomName} onChange={e => setRoomName(e.target.value)} placeholder="部屋名を入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                    </div>
                    <div style={{ marginBottom: 16 }}>
                        <label>本のタイトル</label>
                        <input type="text" value={bookTitle} onChange={e => setBookTitle(e.target.value)} placeholder="本のタイトルを入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                    </div>
                    <div style={{ marginBottom: 16 }}>
                        <label>パスワード（オプション）</label>
                        <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                    </div>
                </div>
            </div>
            {error && <div style={{ color: 'red', marginTop: 12 }}>{error}</div>}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16, marginTop: 32 }}>
                <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>キャンセル</button>
                <button onClick={handleCreate} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }} disabled={loading}>
                    {loading ? '作成中...' : '部屋を作成'}
                </button>
            </div>
        </div>
    )
}

export default RoomCreationModal
