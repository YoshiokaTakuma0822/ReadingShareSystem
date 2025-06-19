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
            <div
                style={{
                    maxWidth: 700,
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
                <h2 style={{ fontWeight: 'bold', fontSize: 28, marginBottom: 24, color: 'var(--accent)' }}>詳細設定</h2>
                <div style={{ display: 'flex', gap: 32 }}>
                    <div style={{ flex: 1 }}>
                        <div style={{ marginBottom: 16 }}>
                            <label style={{ color: 'var(--text)' }}>部屋名</label>
                            <input type="text" value={roomName} onChange={e => setRoomName(e.target.value)} placeholder="部屋名を入力してください" style={{ width: '100%', padding: 8, marginTop: 4, background: 'var(--input)', color: 'var(--text)', border: '1px solid var(--border)' }} />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label style={{ color: 'var(--text)' }}>本のタイトル</label>
                            <input type="text" value={bookTitle} onChange={e => setBookTitle(e.target.value)} placeholder="本のタイトルを入力してください" style={{ width: '100%', padding: 8, marginTop: 4, background: 'var(--input)', color: 'var(--text)', border: '1px solid var(--border)' }} />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label style={{ color: 'var(--text)' }}>パスワード（オプション）</label>
                            <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力してください" style={{ width: '100%', padding: 8, marginTop: 4, background: 'var(--input)', color: 'var(--text)', border: '1px solid var(--border)' }} />
                        </div>                    </div>
                </div>
                {error && <div style={{ color: 'var(--error)', marginTop: 12 }}>{error}</div>}
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16, marginTop: 32 }}>
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
                        onClick={handleCreate}
                        disabled={loading || !roomName.trim() || !bookTitle.trim()}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid var(--accent)',
                            borderRadius: 8,
                            background: 'var(--accent)',
                            color: 'white',
                            fontSize: 16,
                            cursor: loading || !roomName.trim() || !bookTitle.trim() ? 'not-allowed' : 'pointer',
                            opacity: loading || !roomName.trim() || !bookTitle.trim() ? 0.6 : 1
                        }}
                    >
                        {loading ? '作成中...' : '部屋を作成'}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default RoomCreationModal
