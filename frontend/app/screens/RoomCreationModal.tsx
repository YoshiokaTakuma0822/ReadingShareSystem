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
    const [genre, setGenre] = useState('小説');
    const [startTime, setStartTime] = useState('');
    const [endTime, setEndTime] = useState('');
    const [totalPages, setTotalPages] = useState<number>(300); // 追加: 本の全ページ数

    const handleCreate = async () => {
        setLoading(true);
        setError(null);
        try {
            const req: CreateRoomRequest = {
                roomName,
                bookTitle,
                hostUserId: userId,
                password: password || undefined,
                genre,
                startTime: startTime || undefined,
                endTime: endTime || undefined,
                totalPages: totalPages || undefined, // 追加
            };
            await roomApi.createRoom(req);
            onCreated();
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
                onClick={(e) => e.stopPropagation()} // モーダル内のクリックで閉じるのを防ぐ
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
                            <label>パスワード（オプション）</label>
                            <input type="password" value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
                        </div>
                    </div>
                    <div style={{ flex: 1, minWidth: 280 }}>
                        <div style={{ marginBottom: 16 }}>
                            <label>ジャンル</label>
                            <select value={genre} onChange={e => setGenre(e.target.value)} style={{ width: '100%', padding: 8, marginTop: 4 }}>
                                <option value="小説">小説</option>
                                <option value="ビジネス">ビジネス</option>
                                <option value="学習">学習</option>
                                <option value="エッセイ">エッセイ</option>
                                <option value="漫画">漫画</option>
                                <option value="歴史">歴史</option>
                                <option value="科学">科学</option>
                                <option value="ライトノベル">ライトノベル</option>
                                <option value="児童書">児童書</option>
                                <option value="技術書">技術書</option>
                                <option value="趣味・実用">趣味・実用</option>
                                <option value="詩・短歌">詩・短歌</option>
                                <option value="自己啓発">自己啓発</option>
                                <option value="旅行">旅行</option>
                                <option value="料理">料理</option>
                                <option value="スポーツ">スポーツ</option>
                                <option value="芸術">芸術</option>
                                <option value="写真集">写真集</option>
                                <option value="伝記">伝記</option>
                                <option value="ファンタジー">ファンタジー</option>
                                <option value="ミステリー">ミステリー</option>
                                <option value="ホラー">ホラー</option>
                                <option value="恋愛">恋愛</option>
                                <option value="SF">SF</option>
                                <option value="ノンフィクション">ノンフィクション</option>
                                <option value="その他">その他</option>
                            </select>
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
