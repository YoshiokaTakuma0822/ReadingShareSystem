"use client"

import React, { useState, useEffect } from 'react'
import { Room } from '../../types/room'
import { roomApi } from '../../lib/roomApi'
import { getDummyUserId, requireAuth, logout } from '../../lib/authUtils'
import RoomCreationModal from './RoomCreationModal'
import RoomJoinModal from './RoomJoinModal'
import SurveyAnswerModal from './SurveyAnswerModal'
import SurveyResultModal from './SurveyResultModal'

const HomeScreen: React.FC = () => {
    const [searchText, setSearchText] = useState('')
    const [rooms, setRooms] = useState<Room[]>([])
    const [showCreateModal, setShowCreateModal] = useState(false)
    const [showJoinModal, setShowJoinModal] = useState(false)
    const [selectedRoom, setSelectedRoom] = useState<Room | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [showSurveyAnswerModal, setShowSurveyAnswerModal] = useState(false)
    const [showSurveyResultModal, setShowSurveyResultModal] = useState(false)
    const [currentUserId, setCurrentUserId] = useState<string>('00000000-0000-0000-0000-000000000001')

    // サンプル用のダミーsurveyId
    const dummySurveyId = '00000000-0000-0000-0000-000000000001'

    // 認証チェック
    React.useEffect(() => {
        requireAuth()
    }, [])

    // クライアントサイドでユーザーIDを初期化
    React.useEffect(() => {
        setCurrentUserId(getDummyUserId())
    }, [])

    // 部屋検索API（空文字の場合は全件取得）
    const handleSearch = async () => {
        setLoading(true)
        setError(null)
        try {
            const result = await roomApi.searchRooms(searchText)
            setRooms(result.rooms || [])
        } catch (e) {
            setError('部屋の取得に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    // 初期表示とタブ変更時の部屋取得
    React.useEffect(() => {
        handleSearch() // 初期表示時とタブ変更時に部屋を取得
    }, [])

    // 検索テキスト変更時のリアルタイム検索（デバウンス）
    React.useEffect(() => {
        const timeoutId = setTimeout(() => {
            handleSearch()
        }, 300) // 300ms後に検索実行

        return () => clearTimeout(timeoutId)
    }, [searchText])
    // 部屋作成後のリスト再取得
    const handleRoomCreated = () => {
        setShowCreateModal(false)
        handleSearch() // 即時更新
    }

    // 部屋クリック時の処理
    const handleRoomClick = (room: Room) => {
        setSelectedRoom(room)
        setShowJoinModal(true)
    }

    // 参加履歴（最新10件）をlocalStorageで管理
    const [history, setHistory] = useState<Room[]>([])
    // 履歴の取得
    useEffect(() => {
        const raw = localStorage.getItem('joinedRoomHistory')
        if (raw) {
            try {
                setHistory(JSON.parse(raw))
            } catch {}
        }
    }, [])
    // 部屋参加時に履歴を追加
    const handleRoomJoined = () => {
        setShowJoinModal(false)
        if (selectedRoom) {
            // 履歴追加
            const newHistory = [selectedRoom, ...history.filter(r => r.id !== selectedRoom.id)].slice(0, 10)
            setHistory(newHistory)
            localStorage.setItem('joinedRoomHistory', JSON.stringify(newHistory))
            // グループチャット画面へ移動
            window.location.href = `/rooms/${selectedRoom.id}/chat`
        }
    }

    // 部屋削除後のリスト再取得
    const handleRoomDeleted = () => {
        handleSearch() // 即時更新
    }

    // 部屋削除API呼び出し
    const handleDeleteRoom = async (roomId: string) => {
        if (!window.confirm('本当にこの部屋を削除しますか？')) return
        try {
            await import('../../lib/roomApi').then(({ roomApi }) => roomApi.deleteRoom(roomId))
            handleRoomDeleted()
        } catch (e) {
            alert('部屋の削除に失敗しました')
        }
    }

    // 部屋一覧の特徴ごとにプルダウンで絞り込み
    const [roomType, setRoomType] = useState<'all' | 'open' | 'closed'>('all')
    // ジャンル選択肢（RoomCreationModalと共通化推奨）
    const genreOptions = [
        '小説', 'ビジネス', '漫画', 'エッセイ', '専門書', 'ライトノベル', '児童書', 'その他'
    ];
    const [searchGenre, setSearchGenre] = useState('')

    // パスワード有無選択肢
    const [searchPasswordType, setSearchPasswordType] = useState('all'); // all, open, closed

    // 2段階プルダウンUI
    // 1段目: ジャンル, パスワード有無
    const [mainFilter, setMainFilter] = useState<'genre' | 'password'>('genre');

    // 部屋一覧の特徴ごとに絞り込み
    const filteredRooms = rooms.filter(room => {
        const keyword = searchText.toLowerCase();
        const match =
            room.roomName.toLowerCase().includes(keyword) ||
            (room.bookTitle && room.bookTitle.toLowerCase().includes(keyword)) ||
            (room.genre && room.genre.toLowerCase().includes(keyword)) ||
            (room.hostUserId && room.hostUserId.toLowerCase().includes(keyword));
        let genreMatch = true;
        let passwordMatch = true;
        if (mainFilter === 'genre') {
            genreMatch = !searchGenre || room.genre === searchGenre;
        }
        if (mainFilter === 'password') {
            if (searchPasswordType === 'open') passwordMatch = !room.hasPassword;
            else if (searchPasswordType === 'closed') passwordMatch = room.hasPassword;
        }
        if (roomType === 'all') return match && genreMatch && passwordMatch;
        if (roomType === 'open') return !room.hasPassword && match && genreMatch && passwordMatch;
        if (roomType === 'closed') return room.hasPassword && match && genreMatch && passwordMatch;
        return match && genreMatch && passwordMatch;
    })

    // 履歴欄で現存する部屋のみ入室可能にし、削除済みはグレーアウト＋警告表示。
    // 履歴欄の部屋が現存するか判定するため、最新roomsリストのidで判定
    const isRoomAlive = (roomId: string) => rooms.some(r => r.id === roomId)

    // 日付を「YYYY/MM/DD HH:mm」形式で表示する関数
    const formatDateTime = (iso: string | undefined) => {
        if (!iso) return '未設定';
        const d = new Date(iso);
        if (isNaN(d.getTime())) return '未設定';
        return `${d.getFullYear()}/${d.getMonth() + 1}/${d.getDate()} ${d.getHours().toString().padStart(2, '0')}:${d.getMinutes().toString().padStart(2, '0')}`;
    };

    return (
        <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
            <div style={{ marginBottom: 32 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                    <h1 style={{ color: 'var(--accent)', fontSize: 32, margin: 0 }}>読書共有システム</h1>
                    <button
                        onClick={async () => { await logout(); }}
                        style={{
                            padding: '8px 16px',
                            background: '#dc3545',
                            color: 'white',
                            border: 'none',
                            borderRadius: 4,
                            cursor: 'pointer',
                            fontSize: 14
                        }}
                    >
                        ログアウト
                    </button>
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

            {/* 部屋作成ボタン＋検索バーを横並びに配置 */}
            <div style={{ display: 'flex', gap: 16, marginBottom: 24, alignItems: 'center' }}>
                <button onClick={() => setShowCreateModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid var(--text-main)', background: 'var(--accent)', color: 'var(--white)', fontWeight: 'bold', whiteSpace: 'nowrap' }}>
                    新しい部屋を作成する
                </button>
                <div style={{ display: 'flex', gap: 8, marginLeft: 'auto', alignItems: 'center', minWidth: 0 }}>
                    <input
                        type="text"
                        value={searchText}
                        onChange={e => setSearchText(e.target.value)}
                        placeholder="検索"
                        style={{ width: 340, padding: 16, fontSize: 20, border: '2px solid #388e3c', borderRadius: 8, minWidth: 0 }}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                                handleSearch()
                            }
                        }}
                    />
                    <select value={roomType} onChange={e => setRoomType(e.target.value as any)} style={{ padding: 12, borderRadius: 8, border: '2px solid #388e3c', fontSize: 18 }}>
                        <option value="all">全て</option>
                        <option value="open">オープン（パスワードなし）</option>
                        <option value="closed">クローズ（パスワード有）</option>
                    </select>
                </div>
            </div>

            {/* 部屋一覧表示 */}
            <div style={{ display: 'flex', alignItems: 'flex-start', marginBottom: 12, gap: 0, justifyContent: 'flex-end' }}>
                {/* 絞り込み系を右側にまとめて配置 */}
                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-end', gap: 8 }}>
                    <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                        <select value={mainFilter} onChange={e => setMainFilter(e.target.value as 'genre' | 'password')} style={{ padding: 8, borderRadius: 6, border: '1px solid #b0b8c9', fontSize: 16 }}>
                            <option value="genre">ジャンルで絞り込み</option>
                            <option value="password">パスワード有無で絞り込み</option>
                        </select>
                        {mainFilter === 'genre' ? (
                            <select value={searchGenre} onChange={e => setSearchGenre(e.target.value)} style={{ padding: 8, borderRadius: 6, border: '1px solid #b0b8c9', fontSize: 16 }}>
                                <option value="">すべてのジャンル</option>
                                {genreOptions.map(opt => (
                                    <option key={opt} value={opt}>{opt}</option>
                                ))}
                            </select>
                        ) : (
                            <select value={searchPasswordType} onChange={e => setSearchPasswordType(e.target.value)} style={{ padding: 8, borderRadius: 6, border: '1px solid #b0b8c9', fontSize: 16 }}>
                                <option value="all">すべて</option>
                                <option value="open">オープン（パスワードなし）</option>
                                <option value="closed">クローズ（パスワード有）</option>
                            </select>
                        )}
                    </div>
                    {/* 更新ボタンをその右の下に配置 */}
                    <button onClick={handleSearch} style={{ padding: '6px 18px', borderRadius: 8, background: '#388e3c', color: '#fff', border: 'none', fontWeight: 'bold', fontSize: 16, marginTop: 4, alignSelf: 'flex-end' }}>更新</button>
                </div>
                <span style={{ color: '#888', fontSize: 14, marginLeft: 16, alignSelf: 'flex-end' }}>部屋情報を最新に更新</span>
            </div>
            {loading ? (
                <div>読み込み中...</div>
            ) : error ? (
                <div style={{ color: 'red' }}>{error}</div>
            ) : (
                <div style={{ border: '2px solid var(--text-main)', padding: 24 }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24, maxHeight: 500, overflowY: 'auto' }}>
                        {filteredRooms.length === 0 ? (
                            <div style={{ color: '#b0b8c9', fontSize: 20, width: '100%', textAlign: 'center', padding: '32px 0' }}>
                                部屋はありません
                            </div>
                        ) : (
                            <div style={{ display: 'flex', flexWrap: 'wrap', gap: 40, justifyContent: 'center', alignItems: 'center', maxHeight: 400, overflowY: 'auto', padding: 8 }}>
                                {filteredRooms.map((room) => (
                                    <div
                                        key={room.id}
                                        style={{
                                            border: '1px solid var(--text-main)',
                                            borderRadius: 8,
                                            width: 280,
                                            minHeight: 120,
                                            padding: 16,
                                            cursor: 'pointer',
                                            transition: 'all 0.2s ease',
                                            background: 'var(--white)',
                                            display: 'flex',
                                            flexDirection: 'column',
                                            justifyContent: 'space-between'
                                        }}
                                        onClick={() => {
                                            handleRoomClick(room)
                                        }}
                                        onMouseEnter={(e) => {
                                            e.currentTarget.style.background = 'var(--green-light)'
                                            e.currentTarget.style.transform = 'translateY(-2px)'
                                            e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.1)'
                                        }}
                                        onMouseLeave={(e) => {
                                            e.currentTarget.style.background = 'var(--white)'
                                            e.currentTarget.style.transform = 'translateY(0)'
                                            e.currentTarget.style.boxShadow = 'none'
                                        }}
                                    >
                                        <div>
                                            <h3 style={{ color: 'var(--accent)', fontSize: 18, fontWeight: 'bold', marginBottom: 8 }}>
                                                {room.roomName}
                                            </h3>
                                            <p style={{ color: 'var(--text-main)', fontSize: 14, marginBottom: 8 }}>
                                                本: {room.bookTitle}
                                            </p>
                                            <p style={{ color: '#388e3c', fontSize: 13, marginBottom: 4 }}>
                                                ジャンル: {room.genre || '未設定'}
                                            </p>
                                            <p style={{ color: '#1976d2', fontSize: 13, marginBottom: 4 }}>
                                                開始時刻: {formatDateTime(room.startTime)}
                                            </p>
                                            <p style={{ color: '#1976d2', fontSize: 13, marginBottom: 4 }}>
                                                終了時刻: {formatDateTime(room.endTime)}
                                            </p>
                                            <p style={{ color: '#8d6e63', fontSize: 13, marginBottom: 4 }}>
                                                作成者: {room.hostUsername || '不明'}
                                            </p>
                                        </div>
                                        <div style={{ fontSize: 12, color: '#666', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                            {/* <span>作成日: {formatDateTime(room.createdAt)}</span> */}
                                            <span>{room.hasPassword ? '🔒 パスワード有' : '🔓 オープン'}</span>
                                            {/* ホストのみ削除ボタン表示 */}
                                            {room.hostUserId === currentUserId && (
                                                <button
                                                    onClick={e => { e.stopPropagation(); handleDeleteRoom(room.id) }}
                                                    style={{ marginLeft: 8, padding: '2px 8px', background: '#dc3545', color: 'white', border: 'none', borderRadius: 4, cursor: 'pointer', fontSize: 12 }}
                                                >削除</button>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            )}
            {/* 履歴欄を部屋一覧の下に追加 */}
            <div style={{ marginTop: 40 }}>
                <h2 style={{ fontSize: 20, color: '#388e3c', marginBottom: 12 }}>参加済みの部屋履歴</h2>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24 }}>
                    {history.length === 0 ? (
                        <div style={{ color: '#b0b8c9', fontSize: 16 }}>履歴はありません</div>
                    ) : (
                        history.map(room => {
                            const alive = isRoomAlive(room.id)
                            return (
                                <div key={room.id}
                                    style={{
                                        border: '1px solid #b0b8c9',
                                        borderRadius: 8,
                                        padding: 12,
                                        minWidth: 220,
                                        background: alive ? '#f1fdf6' : '#f5f5f5',
                                        color: alive ? undefined : '#aaa',
                                        cursor: alive ? 'pointer' : 'not-allowed',
                                        opacity: alive ? 1 : 0.5
                                    }}
                                    onClick={() => alive && handleRoomClick(room)}
                                >
                                    <div style={{ fontWeight: 'bold', color: alive ? '#388e3c' : '#aaa', fontSize: 16 }}>{room.roomName}</div>
                                    <div style={{ fontSize: 13, color: alive ? '#1976d2' : '#aaa' }}>本: {room.bookTitle}</div>
                                    <div style={{ fontSize: 12, color: '#888' }}>作成日: {new Date(room.createdAt).toLocaleDateString()}</div>
                                    <div style={{ fontSize: 12, color: '#888' }}>ジャンル: {room.genre || '未設定'}</div>
                                    <div style={{ fontSize: 12, color: '#888' }}>終了時刻: {room.endTime ? new Date(room.endTime).toLocaleString() : '未設定'}</div>
                                    {!alive && <div style={{ color: '#e53935', fontSize: 12, marginTop: 4 }}>この部屋は削除されています</div>}
                                </div>
                            )
                        })
                    )}
                </div>
            </div>
            {showCreateModal && (
                <RoomCreationModal open={showCreateModal} userId={currentUserId} onClose={() => setShowCreateModal(false)} onCreated={handleRoomCreated} />
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
            {showSurveyAnswerModal && (
                <SurveyAnswerModal 
                    open={showSurveyAnswerModal} 
                    surveyId={dummySurveyId} 
                    onClose={() => setShowSurveyAnswerModal(false)} 
                    onAnswered={() => { 
                        setShowSurveyAnswerModal(false); 
                        alert('回答送信完了（ダミー）'); 
                    }} 
                />
            )}
        </div>
    );
}

export default HomeScreen
