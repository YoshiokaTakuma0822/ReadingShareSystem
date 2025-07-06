"use client"

import React, { useState } from 'react'
import CustomDropdown from '../../components/CustomDropdown'
import { roomApi } from '../../lib/roomApi'
import { CreateRoomRequest, Room } from '../../types/room'

interface RoomCreationModalProps {
    open: boolean
    userId: string // 追加: ホストユーザーID
    onClose: () => void
    onCreated: (room: Room) => void
}

const RoomCreationModal: React.FC<RoomCreationModalProps> = ({ open, userId, onClose, onCreated }) => {
    const [roomName, setRoomName] = useState('')
    const [bookTitle, setBookTitle] = useState('') // 追加: 本のタイトル
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [genre, setGenre] = useState('小説')
    const [startTime, setStartTime] = useState('')
    const [endTime, setEndTime] = useState('')
    const [totalPages, setTotalPages] = useState<number>(300) // 追加: 本の全ページ数
    const [passwordType, setPasswordType] = useState<'none' | 'set'>('none')

    const handleCreate = async () => {
        // パスワード設定時のバリデーション: 8～16文字の半角英字＋数字
        if (passwordType === 'set') {
            const passPattern = /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,16}$/
            if (!passPattern.test(password)) {
                setError('パスワードは8文字以上，16文字以下の英字＋数字にしてください.')
                return
            }
        }
        // 入力バリデーション: 部屋名は16文字以下
        if (roomName.length > 16) {
            setError('部屋名は16文字以下にしてください')
            return
        }
        setLoading(true)
        setError(null)
        try {
            const req: CreateRoomRequest = {
                roomName,
                bookTitle,
                hostUserId: userId,
                password: passwordType === 'set' ? password : undefined,
                genre,
                startTime: startTime || undefined,
                endTime: endTime || undefined,
                totalPages: totalPages || undefined, // 追加
            }
            const createdRoom: Room = await roomApi.createRoom(req)
            onCreated(createdRoom)
        } catch (e) {
            setError('部屋作成に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    React.useEffect(() => {
        if (!open) {
            setRoomName('')
            setBookTitle('')
            setPassword('')
            setGenre('小説')
            setStartTime('')
            setEndTime('')
            setTotalPages(300)
            setPasswordType('none')
            setError(null)
        }
    }, [open])

    // 部屋作成完了時にもリセット
    React.useEffect(() => {
        if (!loading && !open) {
            setRoomName('')
            setBookTitle('')
            setPassword('')
            setGenre('小説')
            setStartTime('')
            setEndTime('')
            setTotalPages(300)
            setPasswordType('none')
            setError(null)
        }
    }, [loading, open])

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
                    maxWidth: 900,
                    width: '80vw',
                    minWidth: 600,
                    margin: 'auto',
                    border: '2px solid #388e3c',
                    padding: 32,
                    borderRadius: 18,
                    background: '#f1fdf6',
                    boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
                    display: 'flex',
                    flexDirection: 'column', // カラム方向に戻す
                    minHeight: 320,
                    alignItems: 'center',
                    overflowY: 'visible',
                    maxHeight: 'none',
                    gap: 0,
                }}
                onClick={(e) => e.stopPropagation()}
                onKeyDown={e => {
                    if (e.key === 'Enter' && !loading && roomName.trim() && bookTitle.trim()) {
                        handleCreate()
                    }
                }}
                tabIndex={0}
            >
                <div style={{ display: 'flex', flexDirection: 'row', width: '100%', gap: 32 }}>
                    {/* 左右2カラム */}
                    <div style={{ flex: 1, minWidth: 280 }}>
                        <div style={{ marginBottom: 16 }}>
                            <label>部屋名</label>
                            <input type="text" value={roomName} onChange={e => setRoomName(e.target.value)} placeholder="部屋名を入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>本のタイトル</label>
                            <input type="text" value={bookTitle} onChange={e => setBookTitle(e.target.value)} placeholder="本のタイトルを入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>本のページ数</label>
                            <input type="number" min={1} value={totalPages} onChange={e => setTotalPages(Number(e.target.value))} placeholder="例: 300" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>パスワード設定</label>
                            <CustomDropdown
                                options={[
                                    { value: 'none', label: 'パスワードなし（オープン）' },
                                    { value: 'set', label: 'パスワードあり' }
                                ]}
                                value={passwordType}
                                onChange={(value) => setPasswordType(value as 'none' | 'set')}
                                style={{ marginTop: 4 }}
                            />
                        </div>
                        {passwordType === 'set' && (
                            <div style={{ marginBottom: 16 }}>
                                <label>パスワード</label>
                                <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                            </div>
                        )}
                    </div>
                    <div style={{ flex: 1, minWidth: 280 }}>
                        <div style={{ marginBottom: 16 }}>
                            <label>ジャンル</label>
                            <CustomDropdown
                                options={[
                                    { value: '小説', label: '小説' },
                                    { value: 'ビジネス', label: 'ビジネス' },
                                    { value: '学習', label: '学習' },
                                    { value: 'エッセイ', label: 'エッセイ' },
                                    { value: '漫画', label: '漫画' },
                                    { value: '歴史', label: '歴史' },
                                    { value: '科学', label: '科学' },
                                    { value: 'ライトノベル', label: 'ライトノベル' },
                                    { value: '児童書', label: '児童書' },
                                    { value: '技術書', label: '技術書' },
                                    { value: '趣味・実用', label: '趣味・実用' },
                                    { value: '詩・短歌', label: '詩・短歌' },
                                    { value: '自己啓発', label: '自己啓発' },
                                    { value: '旅行', label: '旅行' },
                                    { value: '料理', label: '料理' },
                                    { value: 'スポーツ', label: 'スポーツ' },
                                    { value: '芸術', label: '芸術' },
                                    { value: '写真集', label: '写真集' },
                                    { value: '伝記', label: '伝記' },
                                    { value: 'ファンタジー', label: 'ファンタジー' },
                                    { value: 'ミステリー', label: 'ミステリー' },
                                    { value: 'ホラー', label: 'ホラー' },
                                    { value: '恋愛', label: '恋愛' },
                                    { value: 'SF', label: 'SF' },
                                    { value: 'ノンフィクション', label: 'ノンフィクション' },
                                    { value: 'その他', label: 'その他' }
                                ]}
                                value={genre}
                                onChange={setGenre}
                                style={{ marginTop: 4 }}
                            />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>開始時刻</label>
                            <input type="datetime-local" value={startTime} onChange={e => setStartTime(e.target.value)} style={{ width: '100%', padding: 8, marginTop: 4 }} />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>終了時刻</label>
                            <input type="datetime-local" value={endTime} onChange={e => setEndTime(e.target.value)} style={{ width: '100%', padding: 8, marginTop: 4 }} />
                        </div>
                    </div>
                </div>
                {/* ボタン類を下部中央に配置 */}
                <div style={{ width: '100%', display: 'flex', justifyContent: 'center', gap: 24, marginTop: 32 }}>
                    <button
                        onClick={onClose}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid #666',
                            borderRadius: 8,
                            background: 'transparent',
                            color: '#666',
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
                            border: '2px solid #388e3c',
                            borderRadius: 8,
                            background: '#388e3c',
                            color: 'white',
                            fontSize: 16,
                            cursor: loading || !roomName.trim() || !bookTitle.trim() ? 'not-allowed' : 'pointer',
                            opacity: loading || !roomName.trim() || !bookTitle.trim() ? 0.6 : 1
                        }}
                    >
                        {loading ? '作成中...' : '部屋を作成'}
                    </button>
                </div>
                {error && <div style={{ color: 'red', marginTop: 12, textAlign: 'center' }}>{error}</div>}
            </div>
        </div>
    )
}

export default RoomCreationModal
