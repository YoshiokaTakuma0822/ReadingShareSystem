"use client"

import React, { useState, useEffect } from 'react'
import { Room } from '../../../types/room'
import { roomApi } from '../../../lib/roomApi'

interface RoomDetailPageProps {
    params: Promise<{
        roomId: string
    }>
}

const RoomDetailPage: React.FC<RoomDetailPageProps> = ({ params }) => {
    const { roomId } = React.use(params)
    const [room, setRoom] = useState<Room | null>(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [password, setPassword] = useState('')
    const [showPasswordInput, setShowPasswordInput] = useState(false)

    useEffect(() => {
        fetchRoomDetails()
    }, [roomId])

    const fetchRoomDetails = async () => {
        try {
            setLoading(true)
            // 実際のAPIがない場合の仮実装
            // const roomData = await roomApi.getRoomById(params.roomId)
            // setRoom(roomData)

            // 仮のデータ
            setRoom({
                id: roomId,
                roomName: `サンプル部屋 ${roomId}`,
                bookTitle: 'サンプル本のタイトル',
                hostUserId: 'sample-host-id',
                createdAt: new Date().toISOString(),
                hasPassword: Math.random() > 0.5 // ランダムでパスワード有無を決定
            })
        } catch (e) {
            setError('部屋の情報を取得できませんでした')
        } finally {
            setLoading(false)
        }
    }

    const handleJoinRoom = async () => {
        if (!room) return

        try {
            const dummyUserId = '00000000-0000-0000-0000-000000000001'

            if (room.hasPassword && !password) {
                setShowPasswordInput(true)
                return
            }

            // 部屋参加のAPI呼び出し
            await roomApi.joinRoom({
                roomId: room.id,
                userId: dummyUserId,
                roomPassword: room.hasPassword ? password : undefined
            })

            // 読書画面へ移動
            window.location.href = `/reading/${room.id}`
        } catch (e) {
            alert('部屋への参加に失敗しました')
        }
    }

    if (loading) {
        return (
            <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
                <div style={{ textAlign: 'center', marginTop: 100 }}>
                    <div style={{ color: 'var(--accent)', fontSize: 18 }}>読み込み中...</div>
                </div>
            </div>
        )
    }

    if (error || !room) {
        return (
            <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
                <div style={{ textAlign: 'center', marginTop: 100 }}>
                    <div style={{ color: 'red', fontSize: 18, marginBottom: 16 }}>
                        {error || '部屋が見つかりません'}
                    </div>
                    <button
                        onClick={() => window.location.href = '/'}
                        style={{ padding: '12px 24px', fontSize: 16, borderRadius: 8 }}
                    >
                        ホームに戻る
                    </button>
                </div>
            </div>
        )
    }

    return (
        <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
            <div style={{ maxWidth: 600, margin: '0 auto' }}>
                <div style={{ marginBottom: 24 }}>
                    <button
                        onClick={() => window.location.href = '/'}
                        style={{
                            padding: '8px 16px',
                            fontSize: 14,
                            background: 'transparent',
                            border: '1px solid var(--accent)',
                            color: 'var(--accent)',
                            borderRadius: 4,
                            cursor: 'pointer'
                        }}
                    >
                        ← ホームに戻る
                    </button>
                </div>

                <div style={{
                    background: 'var(--white)',
                    padding: 32,
                    borderRadius: 12,
                    border: '1px solid var(--border)'
                }}>
                    <h1 style={{ color: 'var(--accent)', fontSize: 28, marginBottom: 24 }}>
                        {room.roomName}
                    </h1>

                    <div style={{ marginBottom: 24 }}>
                        <div style={{ marginBottom: 12 }}>
                            <strong style={{ color: 'var(--accent)' }}>読む本:</strong>
                            <span style={{ marginLeft: 8, color: 'var(--text-main)' }}>{room.bookTitle}</span>
                        </div>
                        <div style={{ marginBottom: 12 }}>
                            <strong style={{ color: 'var(--accent)' }}>作成日時:</strong>
                            <span style={{ marginLeft: 8, color: 'var(--text-main)' }}>
                                {new Date(room.createdAt).toLocaleString()}
                            </span>
                        </div>
                        <div style={{ marginBottom: 12 }}>
                            <strong style={{ color: 'var(--accent)' }}>パスワード保護:</strong>
                            <span style={{ marginLeft: 8, color: 'var(--text-main)' }}>
                                {room.hasPassword ? 'あり' : 'なし'}
                            </span>
                        </div>
                    </div>

                    {showPasswordInput && room.hasPassword && (
                        <div style={{ marginBottom: 24 }}>
                            <label style={{ display: 'block', marginBottom: 8, color: 'var(--accent)', fontWeight: 'bold' }}>
                                パスワードを入力してください:
                            </label>
                            <input
                                type="password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                style={{
                                    width: '100%',
                                    padding: 12,
                                    fontSize: 16,
                                    border: '1px solid var(--border)',
                                    borderRadius: 8,
                                    marginBottom: 12
                                }}
                                placeholder="パスワードを入力"
                            />
                        </div>
                    )}

                    <div style={{ display: 'flex', gap: 16, justifyContent: 'center' }}>
                        <button
                            onClick={handleJoinRoom}
                            style={{
                                padding: '16px 32px',
                                fontSize: 18,
                                background: 'var(--accent)',
                                color: 'var(--white)',
                                border: 'none',
                                borderRadius: 8,
                                cursor: 'pointer',
                                fontWeight: 'bold'
                            }}
                        >
                            部屋に参加する
                        </button>
                        <button
                            onClick={() => window.location.href = `/chat/${room.id}`}
                            style={{
                                padding: '16px 32px',
                                fontSize: 18,
                                background: 'transparent',
                                color: 'var(--accent)',
                                border: '2px solid var(--accent)',
                                borderRadius: 8,
                                cursor: 'pointer',
                                fontWeight: 'bold'
                            }}
                        >
                            チャットに参加
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}

export default RoomDetailPage
