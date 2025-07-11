"use client"

import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"
import React, { useState } from 'react'
import { roomApi } from '../../lib/roomApi'
import { CreateRoomRequest, Room } from '../../types/room'

interface RoomCreationModalProps {
    open: boolean
    userId: string // 追加: ホストユーザーID
    onClose: () => void
    onCreated: (room: Room) => void
}

const RoomCreationModal: React.FC<RoomCreationModalProps> = ({ open, userId, onClose, onCreated }) => {
    // 日付を 'YYYY-MM-DDTHH:mm' 形式のローカル日時文字列に変換
    const toDatetimeLocal = (date: Date) => {
        const tzOffset = date.getTimezoneOffset() * 60000
        return new Date(date.getTime() - tzOffset).toISOString().slice(0, 16)
    }
    // 現在時刻のローカル日時文字列を取得
    const getCurrentDateTimeLocal = () => toDatetimeLocal(new Date())
    // 一週間後のローカル日時文字列を取得
    const getOneWeekLaterDateTimeLocal = () => toDatetimeLocal(new Date(Date.now() + 7 * 24 * 3600 * 1000))

    const [roomName, setRoomName] = useState('')
    const [bookTitle, setBookTitle] = useState('') // 追加: 本のタイトル
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [genre, setGenre] = useState('小説')
    const [startTime, setStartTime] = useState(() => getCurrentDateTimeLocal()) // 初期化: 現在時刻
    const [endTime, setEndTime] = useState(() => getOneWeekLaterDateTimeLocal()) // 初期化: 一週間後
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

        // 時刻バリデーション
        if (startTime && endTime) {
            const startDate = new Date(startTime)
            const endDate = new Date(endTime)
            const now = new Date()

            // 開始時刻 < 終了時刻 のチェック
            if (startDate >= endDate) {
                setError('開始時刻は終了時刻よりも前に設定してください')
                return
            }

            // 現在時刻 < 終了時刻 のチェック
            if (endDate <= now) {
                setError('終了時刻は現在時刻よりも後に設定してください')
                return
            }
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
                startTime: startTime ? new Date(startTime).toISOString() : undefined,
                endTime: endTime ? new Date(endTime).toISOString() : undefined,
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
            setStartTime(getCurrentDateTimeLocal()) // 初期化: 現在時刻
            setEndTime(getOneWeekLaterDateTimeLocal()) // 初期化: 一週間後
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
            setStartTime(getCurrentDateTimeLocal()) // 初期化: 現在時刻
            setEndTime(getOneWeekLaterDateTimeLocal()) // 初期化: 一週間後
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
                            <input
                                type="text"
                                value={roomName}
                                onChange={e => setRoomName(e.target.value)}
                                placeholder="部屋名を入力してください"
                                style={{ width: '100%', padding: 8, marginTop: 4 }}
                                tabIndex={1}
                            />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>本のタイトル</label>
                            <input
                                type="text"
                                value={bookTitle}
                                onChange={e => setBookTitle(e.target.value)}
                                placeholder="本のタイトルを入力してください"
                                style={{ width: '100%', padding: 8, marginTop: 4 }}
                                tabIndex={2}
                            />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>本のページ数</label>
                            <input
                                type="number"
                                min={1}
                                value={totalPages}
                                onChange={e => setTotalPages(Number(e.target.value))}
                                placeholder="例: 300"
                                style={{ width: '100%', padding: 8, marginTop: 4 }}
                                tabIndex={3}
                            />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>パスワード設定</label>
                            <Select value={passwordType} onValueChange={(value) => setPasswordType(value as 'none' | 'set')}>
                                <SelectTrigger style={{ width: '100%', marginTop: 4 }} tabIndex={4}>
                                    <SelectValue placeholder="パスワード設定を選択" />
                                </SelectTrigger>
                                <SelectContent style={{ zIndex: 1001 }}>
                                    <SelectItem value="none">パスワードなし（オープン）</SelectItem>
                                    <SelectItem value="set">パスワードあり</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        {passwordType === 'set' && (
                            <div style={{ marginBottom: 16 }}>
                                <label>パスワード</label>
                                <input
                                    type="password"
                                    value={password}
                                    onChange={e => setPassword(e.target.value)}
                                    placeholder="パスワードを入力してください"
                                    style={{ width: '100%', padding: 8, marginTop: 4 }}
                                    tabIndex={5}
                                />
                            </div>
                        )}
                    </div>
                    <div style={{ flex: 1, minWidth: 280 }}>
                        <div style={{ marginBottom: 16 }}>
                            <label>ジャンル</label>
                            <Select value={genre} onValueChange={setGenre}>
                                <SelectTrigger style={{ width: '100%', marginTop: 4 }} tabIndex={6}>
                                    <SelectValue placeholder="ジャンルを選択" />
                                </SelectTrigger>
                                <SelectContent style={{ zIndex: 1001 }}>
                                    <SelectItem value="小説">小説</SelectItem>
                                    <SelectItem value="ビジネス">ビジネス</SelectItem>
                                    <SelectItem value="学習">学習</SelectItem>
                                    <SelectItem value="エッセイ">エッセイ</SelectItem>
                                    <SelectItem value="漫画">漫画</SelectItem>
                                    <SelectItem value="歴史">歴史</SelectItem>
                                    <SelectItem value="科学">科学</SelectItem>
                                    <SelectItem value="ライトノベル">ライトノベル</SelectItem>
                                    <SelectItem value="児童書">児童書</SelectItem>
                                    <SelectItem value="技術書">技術書</SelectItem>
                                    <SelectItem value="趣味・実用">趣味・実用</SelectItem>
                                    <SelectItem value="詩・短歌">詩・短歌</SelectItem>
                                    <SelectItem value="自己啓発">自己啓発</SelectItem>
                                    <SelectItem value="旅行">旅行</SelectItem>
                                    <SelectItem value="料理">料理</SelectItem>
                                    <SelectItem value="スポーツ">スポーツ</SelectItem>
                                    <SelectItem value="芸術">芸術</SelectItem>
                                    <SelectItem value="写真集">写真集</SelectItem>
                                    <SelectItem value="伝記">伝記</SelectItem>
                                    <SelectItem value="ファンタジー">ファンタジー</SelectItem>
                                    <SelectItem value="ミステリー">ミステリー</SelectItem>
                                    <SelectItem value="ホラー">ホラー</SelectItem>
                                    <SelectItem value="恋愛">恋愛</SelectItem>
                                    <SelectItem value="SF">SF</SelectItem>
                                    <SelectItem value="ノンフィクション">ノンフィクション</SelectItem>
                                    <SelectItem value="その他">その他</SelectItem>
                                </SelectContent>
                            </Select>
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>開始時刻</label>
                            <input
                                type="datetime-local"
                                value={startTime}
                                onChange={e => setStartTime(e.target.value)}
                                style={{ width: '100%', padding: 8, marginTop: 4 }}
                                tabIndex={7}
                            />
                        </div>
                        <div style={{ marginBottom: 16 }}>
                            <label>終了時刻</label>
                            <input
                                type="datetime-local"
                                value={endTime}
                                onChange={e => setEndTime(e.target.value)}
                                style={{ width: '100%', padding: 8, marginTop: 4 }}
                                tabIndex={8}
                            />
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
                        tabIndex={9}
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
                        tabIndex={10}
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
