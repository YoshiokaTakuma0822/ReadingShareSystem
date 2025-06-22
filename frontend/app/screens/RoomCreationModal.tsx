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

    // 部屋作成カテゴリ（ジャンル・開始終了時刻・ページ数）追加
    const [genre, setGenre] = useState('')
    const [startTime, setStartTime] = useState('')
    const [endTime, setEndTime] = useState('')
    const [maxPage, setMaxPage] = useState('')

    // ジャンル選択肢（例）
    const genreOptions = [
        '小説', 'ビジネス', '漫画', 'エッセイ', '専門書', 'ライトノベル', '児童書', 'その他'
    ];

    // ジャンル・パスワード有無の2段階プルダウンUI用state
    const [mainFilter, setMainFilter] = useState<'genre' | 'password'>('genre');
    const [selectedGenre, setSelectedGenre] = useState('');
    const [passwordType, setPasswordType] = useState<'open' | 'closed'>('open');

    const toIsoStringWithSeconds = (s: string) => {
        if (!s) return undefined;
        // 例: "2025-06-21T10:56" → "2025-06-21T10:56:00+09:00"（ローカルタイムゾーン）
        if (s.length === 16) {
            const date = new Date(s + ':00');
            return date.toISOString();
        }
        return s;
    };

    const handleCreate = async () => {
        setLoading(true)
        setError(null)
        try {
            const req: CreateRoomRequest = {
                roomName,
                bookTitle,
                hostUserId: userId,
                password: (mainFilter === 'password' && passwordType === 'closed') ? password : undefined,
                genre: mainFilter === 'genre' ? selectedGenre : '',
                startTime: toIsoStringWithSeconds(startTime),
                endTime: toIsoStringWithSeconds(endTime),
                maxPage: maxPage ? Number(maxPage) : undefined,
                pageSpeed: 60, // 1ページごとに1分固定
            }
            await roomApi.createRoom(req)
            onCreated()
        } catch (e: any) {
            const apiMsg = e?.response?.data?.message || e?.message || ''
            setError('部屋作成に失敗しました' + (apiMsg ? `: ${apiMsg}` : ''))
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
                    maxWidth: 650,
                    width: '98%',
                    margin: 'auto',
                    border: '2px solid #388e3c',
                    padding: 18,
                    borderRadius: 12,
                    background: '#f1fdf6',
                    boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
                    maxHeight: '90vh',
                    overflowY: 'auto'
                }}
                onClick={(e) => e.stopPropagation()} // モーダル内のクリックで閉じるのを防ぐ
            >
                <h2 style={{ fontWeight: 'bold', fontSize: 22, marginBottom: 18, color: '#388e3c' }}>詳細設定</h2>
                <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                    <div style={{ flex: 1 }}>
                        <div style={{ marginBottom: 10 }}>
                            <label>部屋名</label>
                            <input type="text" value={roomName} onChange={e => setRoomName(e.target.value)} placeholder="部屋名を入力してください" style={{ width: '100%', padding: 6, marginTop: 4 }} />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            <label>本のタイトル</label>
                            <input type="text" value={bookTitle} onChange={e => setBookTitle(e.target.value)} placeholder="本のタイトルを入力してください" style={{ width: '100%', padding: 6, marginTop: 4 }} />
                        </div>
                        <div style={{ marginBottom: 10 }}>
                            {/* 1段目: ジャンル/パスワード有無の切り替え */}
                            <label style={{ marginRight: 8 }}>部屋タイプ</label>
                            <select value={mainFilter} onChange={e => setMainFilter(e.target.value as 'genre' | 'password')} style={{ padding: 8, borderRadius: 6, border: '1px solid #b0b8c9', fontSize: 16, marginRight: 8 }}>
                                <option value="genre">ジャンルで選択</option>
                                <option value="password">パスワード有無で選択</option>
                            </select>
                            {/* 2段目: サブフィルタ */}
                            {mainFilter === 'genre' ? (
                                <select id="genre" value={selectedGenre} onChange={e => setSelectedGenre(e.target.value)} style={{ padding: 8, borderRadius: 6, border: '1px solid #b0b8c9', fontSize: 16 }}>
                                    <option value="">ジャンルを選択してください</option>
                                    {genreOptions.map(opt => (
                                        <option key={opt} value={opt}>{opt}</option>
                                    ))}
                                </select>
                            ) : (
                                <select value={passwordType} onChange={e => setPasswordType(e.target.value as 'open' | 'closed')} style={{ padding: 8, borderRadius: 6, border: '1px solid #b0b8c9', fontSize: 16 }}>
                                    <option value="open">パスワードなし</option>
                                    <option value="closed">パスワードあり</option>
                                </select>
                            )}
                        </div>
                        {/* パスワード有り選択時のみ入力欄を表示 */}
                        {mainFilter === 'password' && passwordType === 'closed' && (
                            <div style={{ marginBottom: 10 }}>
                                <label>パスワード</label>
                                <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力してください" style={{ width: '100%', padding: 6, marginTop: 4 }} />
                            </div>
                        )}
                        <div style={{ marginBottom: 8 }}>
                            <label>開始時刻</label>
                            <input type="datetime-local" value={startTime} onChange={e => setStartTime(e.target.value)} style={{ width: '100%', padding: 6, border: '1px solid #888', borderRadius: 6 }} />
                        </div>
                        <div style={{ marginBottom: 8 }}>
                            <label>終了時刻</label>
                            <input type="datetime-local" value={endTime} onChange={e => setEndTime(e.target.value)} style={{ width: '100%', padding: 6, border: '1px solid #888', borderRadius: 6 }} />
                        </div>
                        <div style={{ marginBottom: 8 }}>
                            <label>本のページ数</label>
                            <input type="number" value={maxPage} onChange={e => setMaxPage(e.target.value)} style={{ width: '100%', padding: 6, border: '1px solid #888', borderRadius: 6 }} />
                        </div>
                    </div>
                </div>
                {error && <div style={{ color: 'red', marginTop: 12 }}>{error}</div>}
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16, marginTop: 32 }}>
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
            </div>
        </div>
    )
}

export default RoomCreationModal
