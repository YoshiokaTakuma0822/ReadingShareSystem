"use client"

import React, { useState } from 'react'
import AuthGuard from '../../components/AuthGuard'
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from '../../components/ui/card'
import { HoverPopover } from '../../components/ui/my-hover-popover'
import { getDummyUserId, logout } from '../../lib/authUtils'
import { roomApi } from '../../lib/roomApi'
import type { RoomHistoryDto } from '../../types/room'
import { Room } from '../../types/room'
import RoomCreationModal from './RoomCreationModal'
import RoomJoinModal from './RoomJoinModal'

const HomeScreen: React.FC = () => {
    const [tab, setTab] = useState<'create' | 'search'>('create') // デフォルトを部屋作成に変更
    const [searchText, setSearchText] = useState('')
    const [rooms, setRooms] = useState<Room[]>([])

    const [showCreateModal, setShowCreateModal] = useState(false)
    const [showJoinModal, setShowJoinModal] = useState(false)
    const [selectedRoom, setSelectedRoom] = useState<Room | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string>('00000000-0000-0000-0000-000000000001')

    // クライアントサイドでユーザーIDを初期化
    React.useEffect(() => {
        setCurrentUserId(getDummyUserId())
    }, [])

    const [creatorMap, setCreatorMap] = useState<{ [roomId: string]: string }>({})

    const [roomType, setRoomType] = useState<string>('all') // 部屋タイプ: all, open, closed

    // ジャンル、ページ数範囲、開始/終了時刻範囲
    const [genre, setGenre] = useState<string>('')
    const [minPages, setMinPages] = useState<string>('')
    const [maxPages, setMaxPages] = useState<string>('')
    const [startTimeFrom, setStartTimeFrom] = useState<string>('')
    const [startTimeTo, setStartTimeTo] = useState<string>('')
    const [endTimeFrom, setEndTimeFrom] = useState<string>('')
    const [endTimeTo, setEndTimeTo] = useState<string>('')

    // 部屋一覧取得（作成タブ用）
    const handleGetRooms = async () => {
        setLoading(true)
        setError(null)
        try {
            const roomsList = await roomApi.getRooms(100) // limit 100
            setRooms(roomsList)
            // 部屋ごとに作成者名を取得
            const map: { [roomId: string]: string } = {}
            await Promise.all(roomsList.map(async (room) => {
                try {
                    const members = await roomApi.getRoomMembers(room.id)
                    const creator = members.find((m: any) => (m.userId || '').replace(/-/g, '').toLowerCase() === (room.hostUserId || '').replace(/-/g, '').toLowerCase())
                    map[room.id] = creator ? creator.username : ''
                } catch {
                    map[room.id] = ''
                }
            }))
            setCreatorMap(map)
        } catch (e) {
            setError('部屋の取得に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    // 部屋検索（検索タブ用）
    const handleSearch = async () => {
        setLoading(true)
        setError(null)
        try {
            // 検索タブでは複数条件検索
            const result = await roomApi.searchRooms(
                searchText,
                genre,
                startTimeFrom,
                startTimeTo,
                endTimeFrom,
                endTimeTo,
                minPages,
                maxPages,
                roomType === 'open',
                roomType === 'closed'
            )
            const found = result.rooms
            setRooms(found)
            // 部屋ごとに作成者名を取得
            const map: { [roomId: string]: string } = {}
            await Promise.all(found.map(async (room) => {
                try {
                    const members = await roomApi.getRoomMembers(room.id)
                    const creator = members.find((m: any) => (m.userId || '').replace(/-/g, '').toLowerCase() === (room.hostUserId || '').replace(/-/g, '').toLowerCase())
                    map[room.id] = creator ? creator.username : ''
                } catch {
                    map[room.id] = ''
                }
            }))
            setCreatorMap(map)
        } catch (e) {
            setError('部屋の検索に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    // 初期表示とタブ変更時の部屋取得
    // 初期マウントで全件取得
    React.useEffect(() => {
        if (tab === 'create') {
            handleGetRooms()
        } else {
            handleSearch()
        }
    }, [])
    // タブ変更時の部屋取得
    React.useEffect(() => {
        // タブを切り替えたときは常に部屋情報を取得
        if (tab === 'create') {
            handleGetRooms()
        } else {
            handleSearch()
        }
    }, [tab])

    // 検索テキスト変更時のリアルタイム検索（デバウンス）
    React.useEffect(() => {
        if (tab === 'search') {
            const timeoutId = setTimeout(() => {
                handleSearch()
            }, 300)
            return () => clearTimeout(timeoutId)
        }
    }, [searchText, tab])

    // --- ここから下の onChange で handleSearch を呼ばないように修正 ---
    // 検索フォームの各条件の onChange で handleSearch を呼ばず、state のみ更新
    // 検索ボタン押下時のみ handleSearch を呼ぶ

    // ユーザープロフィール情報
    const [userName, setUserName] = useState<string>('')
    const [loginTime, setLoginTime] = useState<Date | null>(null)

    // ログイン時刻とユーザー名をlocalStorageから取得
    React.useEffect(() => {
        if (typeof window !== 'undefined') {
            setUserName(localStorage.getItem('reading-share-user-name') || 'ゲスト')
            const loginTimestamp = localStorage.getItem('reading-share-login-time')
            if (loginTimestamp) {
                setLoginTime(new Date(Number(loginTimestamp)))
            } else {
                const now = Date.now()
                localStorage.setItem('reading-share-login-time', String(now))
                setLoginTime(new Date(now))
            }
        }
    }, [])

    // 経過時間を計算
    const [elapsed, setElapsed] = useState('')
    React.useEffect(() => {
        if (!loginTime) return
        const update = () => {
            const now = new Date()
            const diff = Math.floor((now.getTime() - loginTime.getTime()) / 1000)
            const h = Math.floor(diff / 3600)
            const m = Math.floor((diff % 3600) / 60)
            const s = diff % 60
            setElapsed(`${h}時間${m}分${s}秒`)
        }
        update()
        const timer = setInterval(update, 1000)
        return () => clearInterval(timer)
    }, [loginTime])

    const [roomHistory, setRoomHistory] = useState<RoomHistoryDto[]>([])

    // 履歴リセット状態（localStorageで永続化）
    const [historyReset, setHistoryReset] = useState(false)
    // localStorageから初期値を復元
    React.useEffect(() => {
        if (typeof window !== 'undefined') {
            setHistoryReset(localStorage.getItem('reading-share-history-reset') === '1')
        }
    }, [])

    // 履歴リセットボタン
    const handleResetHistory = async () => {
        if (!currentUserId) return
        if (!window.confirm('本当に履歴を削除しますか？')) return
        try {
            await roomApi.resetRoomHistory(currentUserId)
            setRoomHistory([])
            setHistoryReset(true)
            localStorage.setItem('reading-share-history-reset', '1')
        } catch {
            alert('履歴のリセットに失敗しました')
        }
    }

    // 履歴リセット状態を初期化（ユーザーIDが変わった時や明示的に解除したい場合）
    React.useEffect(() => {
        if (typeof window !== 'undefined' && !historyReset) {
            localStorage.removeItem('reading-share-history-reset')
        }
    }, [currentUserId, historyReset])

    // 履歴取得（リセット後は取得しない）
    React.useEffect(() => {
        if (!currentUserId || historyReset) return
        roomApi.getRoomHistory(currentUserId, 10)
            .then(setRoomHistory)
            .catch(() => { })
    }, [currentUserId, historyReset])

    // 部屋クリック時の処理
    const handleRoomClick = (room: Room) => {
        setSelectedRoom(room)
        setShowJoinModal(true)
    }

    // 部屋作成後のリスト再取得
    const handleRoomCreated = async (_room: Room) => {
        setShowCreateModal(false)
        // 作成タブに戻す
        setTab('create')
        // 部屋リストを更新（作成タブなので全件取得）
        await handleGetRooms()
    }

    // 部屋参加後の処理
    const handleRoomJoined = async () => {
        setShowJoinModal(false)
        // 履歴リセット状態なら解除（再参加で履歴を再開）
        if (historyReset) {
            setHistoryReset(false)
            if (typeof window !== 'undefined') {
                localStorage.removeItem('reading-share-history-reset')
            }
        }
        if (selectedRoom && currentUserId) {
            try {
                const history = await roomApi.getRoomHistory(currentUserId, 10)
                setRoomHistory(history)
            } catch { }
            window.location.href = `/rooms/${selectedRoom.id}/chat`
        }
    }

    return (
        <AuthGuard>
            <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
                <div style={{ marginBottom: 32 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                        <h1 style={{ color: 'var(--green-dark)', fontSize: 32, margin: 0 }}>読書共有システム</h1>
                        {/* ユーザープロフィール表示 */}
                        <HoverPopover
                            align="end"
                            side="bottom"
                            content={
                                <Card className="w-64">
                                    <CardHeader>
                                        <CardTitle className="text-lg">プロフィール</CardTitle>
                                        <CardDescription>ユーザー情報</CardDescription>
                                    </CardHeader>
                                    <CardContent>
                                        <div className="space-y-3">
                                            <div>
                                                <div className="text-sm font-medium text-gray-700">ユーザー名</div>
                                                <div className="text-sm text-gray-900">{userName}</div>
                                            </div>
                                            <div>
                                                <div className="text-sm font-medium text-gray-700">ログイン経過</div>
                                                <div className="text-sm text-gray-900">
                                                    {loginTime
                                                        ? `${Math.floor((Date.now() - loginTime.getTime()) / 60000)}分`
                                                        : '不明'
                                                    }
                                                </div>
                                            </div>
                                        </div>
                                    </CardContent>
                                    <CardFooter>
                                        <button
                                            onClick={logout}
                                            className="w-full px-4 py-2 bg-red-600 hover:bg-red-700 text-white text-sm font-medium rounded-md transition-colors"
                                        >
                                            ログアウト
                                        </button>
                                    </CardFooter>
                                </Card>
                            }
                        >
                            <div
                                style={{
                                    background: '#388e3c',
                                    color: '#fff',
                                    borderRadius: 24,
                                    padding: '8px 20px',
                                    fontWeight: 'bold',
                                    fontSize: 16,
                                    cursor: 'pointer',
                                    position: 'relative',
                                    marginRight: 16
                                }}
                            >
                                {userName}
                            </div>
                        </HoverPopover>
                    </div>
                    <p style={{ color: 'var(--text-main)', fontSize: 16 }}>友達と一緒に読書を楽しもう</p>
                    <div style={{ marginTop: 16 }}>
                        <a
                            href="/debug"
                            style={{
                                color: '#666',
                                fontSize: 12,
                                textDecoration: 'underline',
                                display: process.env.NODE_ENV === 'development' ? 'inline' : 'none'
                            }}
                        >
                            開発者向けデバッグ画面
                        </a>
                    </div>
                </div>
                <div style={{ display: 'flex', gap: 16, marginBottom: 24 }}>
                    <button
                        style={{ flex: 1, padding: 16, background: tab === 'create' ? 'var(--green-light)' : 'var(--white)', borderBottom: tab === 'create' ? '2px solid var(--green-dark)' : '1px solid var(--green-light)', color: 'var(--green-dark)', fontWeight: 'bold' }}
                        onClick={() => setTab('create')}
                    >部屋作成</button>
                    <button
                        style={{ flex: 1, padding: 16, background: tab === 'search' ? 'var(--green-light)' : 'var(--white)', borderBottom: tab === 'search' ? '2px solid var(--green-dark)' : '1px solid var(--green-light)', color: 'var(--green-dark)', fontWeight: 'bold' }}
                        onClick={() => setTab('search')}
                    >検索</button>
                </div>
                {tab === 'create' && (
                    <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center', gap: 12 }}>
                        <button onClick={() => setShowCreateModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid var(--text-main)', background: 'var(--green-main)', color: 'var(--white)', fontWeight: 'bold' }}>
                            新しい部屋を作成する
                        </button>
                        <button
                            onClick={handleGetRooms}
                            style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #2196f3', background: '#2196f3', color: '#fff', fontWeight: 'bold', fontSize: 16, cursor: 'pointer' }}
                        >部屋一覧を更新</button>
                    </div>
                )}
                {tab === 'search' && (
                    <div style={{ marginBottom: 24, display: 'flex', flexDirection: 'column', gap: 8 }}>
                        {/* 上段：キーワード・部屋タイプ・ジャンル・ボタン */}
                        <div style={{ display: 'flex', alignItems: 'flex-end', gap: 12, flexWrap: 'wrap' }}>
                            <div style={{ display: 'flex', gap: 12, flexWrap: 'wrap' }}>
                                <div style={{ display: 'flex', flexDirection: 'column', minWidth: 180 }}>
                                    <label style={{ fontSize: 13, color: '#388e3c', marginBottom: 2 }}>部屋名・本のタイトル</label>
                                    <input
                                        type="text"
                                        value={searchText}
                                        onChange={e => setSearchText(e.target.value)}
                                        placeholder="部屋名・本のタイトルで検索"
                                        style={{ padding: 12, borderRadius: 8, border: '1px solid #ccc', fontSize: 18, minWidth: 180 }}
                                    />
                                </div>
                                <div style={{ display: 'flex', flexDirection: 'column' }}>
                                    <label style={{ fontSize: 13, color: '#388e3c', marginBottom: 2 }}>部屋の公開範囲</label>
                                    <select
                                        value={roomType}
                                        onChange={e => setRoomType(e.target.value)}
                                        style={{ padding: 12, borderRadius: 8, border: '1px solid #ccc', fontSize: 18, height: '56px' }}
                                    >
                                        <option value="all">すべて</option>
                                        <option value="open">オープン</option>
                                        <option value="closed">クローズ</option>
                                    </select>
                                </div>
                                {/* ジャンル＋ボタンを横並びでまとめる */}
                                <div style={{ display: 'flex', alignItems: 'flex-end', gap: 8 }}>
                                    <div style={{ display: 'flex', flexDirection: 'column' }}>
                                        <label style={{ fontSize: 13, color: '#388e3c', marginBottom: 2 }}>ジャンル</label>
                                        <select
                                            value={genre}
                                            onChange={e => setGenre(e.target.value)}
                                            style={{ padding: 12, borderRadius: 8, border: '1px solid #ccc', fontSize: 18, height: '56px' }}
                                        >
                                            <option value="">ジャンル指定なし</option>
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
                                    {/* 右にスペースを追加（検索ボタン1個分） */}
                                    <div style={{ width: 120 }} />
                                    <button
                                        onClick={handleSearch}
                                        style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #388e3c', background: '#fff', color: '#388e3c', fontWeight: 'bold', fontSize: 16, cursor: 'pointer' }}
                                    >検索</button>
                                    <button
                                        onClick={handleSearch}
                                        style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #2196f3', background: '#2196f3', color: '#fff', fontWeight: 'bold', fontSize: 16, cursor: 'pointer' }}
                                    >部屋一覧を更新</button>
                                </div>
                            </div>
                        </div>
                        {/* 下段：範囲指定 */}
                        <div style={{ display: 'flex', alignItems: 'flex-end', gap: 12, flexWrap: 'wrap', marginTop: 4 }}>
                            <div style={{ display: 'flex', flexDirection: 'column', border: '1px solid #388e3c', borderRadius: 8, padding: 8, minWidth: 180 }}>
                                <label style={{ fontSize: 13, color: '#388e3c', marginBottom: 2 }}>ページ数範囲</label>
                                <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                                    <input
                                        type="number"
                                        value={minPages}
                                        onChange={e => setMinPages(e.target.value)}
                                        placeholder="最小"
                                        style={{ width: 70, padding: 8, borderRadius: 8, border: '1px solid #ccc', fontSize: 16 }}
                                    />
                                    <span style={{ color: '#888' }}>～</span>
                                    <input
                                        type="number"
                                        value={maxPages}
                                        onChange={e => setMaxPages(e.target.value)}
                                        placeholder="最大"
                                        style={{ width: 70, padding: 8, borderRadius: 8, border: '1px solid #ccc', fontSize: 16 }}
                                    />
                                </div>
                            </div>
                            <div style={{ display: 'flex', flexDirection: 'column', border: '1px solid #388e3c', borderRadius: 8, padding: 8, minWidth: 220 }}>
                                <label style={{ fontSize: 13, color: '#388e3c', marginBottom: 2 }}>開始時刻範囲</label>
                                <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                                    <input
                                        type="datetime-local"
                                        value={startTimeFrom}
                                        onChange={e => setStartTimeFrom(e.target.value)}
                                        style={{ padding: 8, borderRadius: 8, border: '1px solid #ccc', fontSize: 14, minWidth: 90 }}
                                    />
                                    <span style={{ color: '#888' }}>～</span>
                                    <input
                                        type="datetime-local"
                                        value={startTimeTo}
                                        onChange={e => setStartTimeTo(e.target.value)}
                                        style={{ padding: 8, borderRadius: 8, border: '1px solid #ccc', fontSize: 14, minWidth: 90 }}
                                    />
                                </div>
                            </div>
                            <div style={{ display: 'flex', flexDirection: 'column', border: '1px solid #388e3c', borderRadius: 8, padding: 8, minWidth: 220 }}>
                                <label style={{ fontSize: 13, color: '#388e3c', marginBottom: 2 }}>終了時刻範囲</label>
                                <div style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                                    <input
                                        type="datetime-local"
                                        value={endTimeFrom}
                                        onChange={e => setEndTimeFrom(e.target.value)}
                                        style={{ padding: 8, borderRadius: 8, border: '1px solid #ccc', fontSize: 14, minWidth: 90 }}
                                    />
                                    <span style={{ color: '#888' }}>～</span>
                                    <input
                                        type="datetime-local"
                                        value={endTimeTo}
                                        onChange={e => setEndTimeTo(e.target.value)}
                                        style={{ padding: 8, borderRadius: 8, border: '1px solid #ccc', fontSize: 14, minWidth: 90 }}
                                    />
                                </div>
                            </div>
                        </div>
                    </div>
                )}

                {/* 部屋一覧表示（両方のタブで共通） */}
                {loading ? (
                    <div>読み込み中...</div>
                ) : error ? (
                    <div style={{ color: 'red' }}>{error}</div>
                ) : (
                    <div style={{ border: '2px solid var(--text-main)', padding: '24px 16px', maxHeight: 400, overflow: 'auto' }}>
                        <div
                            style={{
                                display: 'grid',
                                gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))',
                                gap: 20,
                                width: '100%',
                                boxSizing: 'border-box',
                            }}
                        >
                            {tab === 'search' &&
                                roomType === 'all' && !searchText && !genre && !minPages && !maxPages && !startTimeFrom && !startTimeTo && !endTimeFrom && !endTimeTo ? (
                                <div style={{ color: '#b0b8c9', fontSize: 18, width: '100%', textAlign: 'center', padding: '32px 0' }}>
                                    検索された部屋はここに表示されます
                                </div>
                            ) : rooms.length === 0 ? (
                                <div style={{ color: '#b0b8c9', fontSize: 20, width: '100%', textAlign: 'center', padding: '32px 0' }}>
                                    部屋はありません
                                </div>
                            ) : (
                                rooms.map((room) => (
                                    <div
                                        key={room.id}
                                        style={{
                                            border: '1px solid var(--text-main)',
                                            borderRadius: 8,
                                            width: '100%',
                                            minWidth: 0,
                                            minHeight: 140,
                                            maxHeight: 180,
                                            padding: 16,
                                            cursor: 'pointer',
                                            transition: 'all 0.2s ease',
                                            background: 'var(--white)',
                                            display: 'flex',
                                            flexDirection: 'column',
                                            justifyContent: 'space-between',
                                            position: 'relative',
                                            overflowWrap: 'break-word',
                                            wordBreak: 'break-word',
                                        }}
                                        onClick={() => {
                                            handleRoomClick(room)
                                        }}
                                        onMouseEnter={(e) => {
                                            e.currentTarget.style.background = 'var(--green-light)'
                                            e.currentTarget.style.transform = 'translateY(-2px)'
                                            e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)'
                                            // 既存ツールチップを削除
                                            document.querySelectorAll(`[id^='room-tooltip-']`).forEach(el => el.remove())
                                            // ツールチップ生成
                                            const tooltip = document.createElement('div')
                                            tooltip.id = `room-tooltip-${room.id}`
                                            tooltip.style.position = 'fixed'
                                            const rect = e.currentTarget.getBoundingClientRect()
                                            tooltip.style.left = `${rect.left + rect.width / 2}px`
                                            tooltip.style.top = `${rect.top - 12}px`
                                            tooltip.style.transform = 'translate(-50%, -100%)'
                                            tooltip.style.background = '#fff'
                                            tooltip.style.color = '#333'
                                            tooltip.style.padding = '14px 24px'
                                            tooltip.style.borderRadius = '12px'
                                            tooltip.style.boxShadow = '0 2px 12px rgba(0,0,0,0.18)'
                                            tooltip.style.whiteSpace = 'nowrap'
                                            tooltip.style.zIndex = '9999'
                                            tooltip.style.fontSize = '15px'
                                            tooltip.innerHTML = `
                                                <b>部屋名:</b> ${room.roomName}<br/>
                                                <b>本タイトル:</b> ${room.bookTitle}<br/>
                                                <b>作成者:</b> ${creatorMap[room.id] || '-'}<br/>
                                                <b>作成日:</b> ${new Date(room.createdAt).toLocaleString()}<br/>
                                                <b>ページ数:</b> ${room.totalPages ?? '-'}<br/>
                                                <b>パスワード:</b> ${room.hasPassword ? 'あり' : 'なし'}
                                            `
                                            document.body.appendChild(tooltip)
                                        }}
                                        onMouseLeave={(e) => {
                                            e.currentTarget.style.background = 'var(--white)'
                                            e.currentTarget.style.transform = 'translateY(0)'
                                            e.currentTarget.style.boxShadow = 'none'
                                            // すべてのツールチップを確実に削除
                                            document.querySelectorAll(`[id^='room-tooltip-']`).forEach(el => el.remove())
                                        }}
                                    >
                                        <div>
                                            <h3 style={{ color: 'var(--green-dark)', fontSize: 18, fontWeight: 'bold', marginBottom: 8, overflowWrap: 'break-word', wordBreak: 'break-word' }}>
                                                {room.roomName}
                                            </h3>
                                            <p style={{ color: 'var(--text-main)', fontSize: 14, marginBottom: 8, overflowWrap: 'break-word', wordBreak: 'break-word' }}>
                                                本: {room.bookTitle}<br />
                                                作成者: {creatorMap[room.id] || '-'}
                                            </p>
                                        </div>
                                        <div style={{ fontSize: 12, color: '#666', display: 'flex', justifyContent: 'space-between' }}>
                                            <span>作成日: {new Date(room.createdAt).toLocaleDateString()}</span>
                                            <span>{room.hasPassword ? '🔒 パスワード有' : '🔓 オープン'}</span>
                                        </div>
                                        {currentUserId && room.hostUserId && currentUserId.replace(/-/g, '').toLowerCase() === room.hostUserId.replace(/-/g, '').toLowerCase() && (
                                            <button
                                                style={{
                                                    position: 'absolute',
                                                    top: 8,
                                                    right: 8,
                                                    background: '#dc3545',
                                                    color: 'white',
                                                    border: 'none',
                                                    borderRadius: 4,
                                                    padding: '4px 8px',
                                                    fontSize: 12,
                                                    cursor: 'pointer',
                                                }}
                                                onClick={e => {
                                                    e.stopPropagation()
                                                    if (window.confirm('本当にこの部屋を削除しますか？')) {
                                                        roomApi.deleteRoom(room.id)
                                                            .then(() => {
                                                                // 現在のタブに応じて適切な更新処理を実行
                                                                if (tab === 'create') {
                                                                    handleGetRooms()
                                                                } else {
                                                                    handleSearch()
                                                                }
                                                            })
                                                            .catch(err => {
                                                                let msg = '削除に失敗しました'
                                                                if (err && err.response && err.response.data) {
                                                                    msg += '\n' + JSON.stringify(err.response.data)
                                                                } else if (err && err.message) {
                                                                    msg += '\n' + err.message
                                                                }
                                                                alert(msg)
                                                                console.error('deleteRoom error:', err)
                                                            })
                                                    }
                                                }}
                                            >削除</button>
                                        )}
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                )}
                {/* 最近参加した部屋（最新10件） */}
                <div style={{ marginTop: 32, padding: 16, background: 'var(--white)', borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.1)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                        <h2 style={{ margin: '0 0 12px', fontSize: 20, color: 'var(--green-dark)' }}>最近参加した部屋</h2>
                        <button
                            onClick={handleResetHistory}
                            style={{ padding: '6px 16px', borderRadius: 6, border: '1px solid #dc3545', background: '#fff', color: '#dc3545', fontWeight: 'bold', fontSize: 14, cursor: 'pointer', marginLeft: 12 }}
                        >履歴をリセット</button>
                    </div>
                    {roomHistory.filter(h => {
                        if (!h.room) return true
                        // 作成しただけの部屋: joinedAtとcreatedAtが完全一致かつ自分がホスト
                        const isHost = (h.room.hostUserId || '').replace(/-/g, '').toLowerCase() === currentUserId.replace(/-/g, '').toLowerCase()
                        const joined = new Date(h.joinedAt).getTime()
                        const created = new Date(h.room.createdAt).getTime()
                        if (isHost && joined === created) return false
                        return true
                    }).length === 0 ? (
                        <div style={{ color: '#666' }}>まだ参加した部屋がありません</div>
                    ) : (
                        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16 }}>
                            {roomHistory.filter(h => {
                                if (!h.room) return true
                                const isHost = (h.room.hostUserId || '').replace(/-/g, '').toLowerCase() === currentUserId.replace(/-/g, '').toLowerCase()
                                const joined = new Date(h.joinedAt).getTime()
                                const created = new Date(h.room.createdAt).getTime()
                                if (isHost && joined === created) return false
                                return true
                            }).map(h => (
                                <div
                                    key={h.roomId}
                                    style={{ position: 'relative', background: 'var(--white)', padding: 16, borderRadius: 8, boxShadow: '0 2px 8px rgba(0,0,0,0.1)', cursor: h.deleted ? 'default' : 'pointer', transition: '0.3s', textAlign: 'center', color: h.deleted ? 'red' : 'inherit' }}
                                    onClick={() => { if (!h.deleted && h.room) { handleRoomClick(h.room) } }}
                                >
                                    {h.deleted ? (
                                        <div style={{ fontSize: 16, fontWeight: 'bold', padding: '32px 0', color: 'red' }}>
                                            この部屋は既に削除されています
                                        </div>
                                    ) : (
                                        <>
                                            <div>
                                                <h3 style={{ color: 'var(--green-dark)', fontSize: 18, fontWeight: 'bold', marginBottom: 8, overflowWrap: 'break-word', wordBreak: 'break-word' }}>
                                                    {h.room!.roomName}
                                                </h3>
                                                <p style={{ color: 'var(--text-main)', fontSize: 14, marginBottom: 8, overflowWrap: 'break-word', wordBreak: 'break-word' }}>
                                                    本: {h.room!.bookTitle}<br />
                                                    作成者: {creatorMap[h.roomId] || '-'}
                                                </p>
                                            </div>
                                            <div style={{ fontSize: 12, color: '#999', display: 'flex', justifyContent: 'space-between' }}>
                                                <span>参加: {new Date(h.joinedAt).toLocaleDateString()}</span>
                                                <span>{h.room!.hasPassword ? '🔒' : '🔓'}</span>
                                            </div>
                                        </>
                                    )}
                                </div>
                            ))}
                        </div>
                    )}
                </div>
            </div>

            {/* モーダル類 */}
            {showCreateModal && (
                <RoomCreationModal
                    open={showCreateModal}
                    userId={currentUserId}
                    onClose={() => setShowCreateModal(false)}
                    onCreated={handleRoomCreated}
                />
            )}
            {showJoinModal && selectedRoom && (
                <RoomJoinModal
                    open={showJoinModal}
                    room={selectedRoom}
                    userId={currentUserId}
                    onClose={() => {
                        setShowJoinModal(false)
                        setSelectedRoom(null)
                    }}
                    onJoined={handleRoomJoined}
                />
            )}
        </AuthGuard>
    )
}

export default HomeScreen
