"use client"

import React, { useState } from 'react'
import { Room } from '../../types/room'
import { roomApi } from '../../lib/roomApi'
import { getDummyUserId, requireAuth, logout } from '../../lib/authUtils'
import RoomCreationModal from './RoomCreationModal'
import RoomJoinModal from './RoomJoinModal'
import SurveyAnswerModal from './SurveyAnswerModal'
import SurveyResultModal from './SurveyResultModal'

const HomeScreen: React.FC = () => {
    const [tab, setTab] = useState<'create' | 'search'>('search') // 初期表示を検索タブに変更
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
    }, [tab])

    // 検索テキスト変更時のリアルタイム検索（デバウンス）
    React.useEffect(() => {
        if (tab === 'search') {
            const timeoutId = setTimeout(() => {
                handleSearch()
            }, 300) // 300ms後に検索実行

            return () => clearTimeout(timeoutId)
        }
    }, [searchText, tab])
    // 部屋作成後のリスト再取得
    const handleRoomCreated = () => {
        setShowCreateModal(false)
        setTab('search')
        handleSearch()
    }

    // 部屋クリック時の処理
    const handleRoomClick = (room: Room) => {
        setSelectedRoom(room)
        setShowJoinModal(true)
    }

    // 部屋参加後の処理
    const handleRoomJoined = () => {
        setShowJoinModal(false)
        if (selectedRoom) {
            // グループチャット画面へ移動
            window.location.href = `/rooms/${selectedRoom.id}/chat`
        }
    }

    return (
        <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
            <div style={{ marginBottom: 32 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                    <h1 style={{ color: 'var(--accent)', fontSize: 32, margin: 0 }}>読書共有システム</h1>
                    <button
                        onClick={logout}
                        style={{
                            padding: '8px 16px',
                            background: 'var(--accent)',
                            color: 'var(--white)',
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
                            color: 'var(--text-accent)',
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
                    style={{ flex: 1, padding: 16, background: tab === 'create' ? 'var(--green-light)' : 'var(--white)', borderBottom: tab === 'create' ? '2px solid var(--accent)' : '1px solid var(--green-light)', color: 'var(--accent)', fontWeight: 'bold' }}
                    onClick={() => setTab('create')}
                >部屋作成</button>
                <button
                    style={{ flex: 1, padding: 16, background: tab === 'search' ? 'var(--green-light)' : 'var(--white)', borderBottom: tab === 'search' ? '2px solid var(--accent)' : '1px solid var(--green-light)', color: 'var(--accent)', fontWeight: 'bold' }}
                    onClick={() => setTab('search')}
                >検索</button>
            </div>
            {tab === 'create' && (
                <div style={{ marginBottom: 24 }}>
                    <button onClick={() => setShowCreateModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid var(--text-main)', background: 'var(--accent)', color: 'var(--white)', fontWeight: 'bold' }}>
                        新しい部屋を作成する
                    </button>
                    <div style={{ marginTop: 16, fontSize: 14, color: 'var(--text-main)' }}>
                        または既存の部屋に参加：
                    </div>
                </div>
            )}
            {tab === 'search' && (
                <div style={{ marginBottom: 24, display: 'flex', gap: 8 }}>
                    <input
                        type="text"
                        value={searchText}
                        onChange={e => setSearchText(e.target.value)}
                        placeholder="部屋名で検索"
                        style={{ flex: 1, padding: 8, fontSize: 16 }}
                        onKeyPress={(e) => {
                            if (e.key === 'Enter') {
                                handleSearch()
                            }
                        }}
                    />
                    <button onClick={handleSearch} style={{ padding: '8px 16px', background: 'var(--accent)', color: 'var(--white)', border: 'none', borderRadius: 4, cursor: 'pointer' }}>検索</button>
                </div>
            )}

            {/* 部屋一覧表示（両方のタブで共通） */}
            {loading ? (
                <div>読み込み中...</div>
            ) : error ? (
                <div style={{ color: 'red' }}>{error}</div>
            ) : (
                <div style={{ border: '2px solid var(--text-main)', padding: 24 }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24 }}>
                        {rooms.length === 0 ? (
                            <div style={{ color: 'var(--text-accent)', fontSize: 20, width: '100%', textAlign: 'center', padding: '32px 0' }}>
                                部屋はありません
                            </div>
                        ) : (
                            rooms.map((room) => (
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
                                    </div>
                                    <div style={{ fontSize: 12, color: 'var(--text-accent)', display: 'flex', justifyContent: 'space-between' }}>
                                        <span>作成日: {new Date(room.createdAt).toLocaleDateString()}</span>
                                        <span>{room.hasPassword ? '🔒 パスワード有' : '🔓 オープン'}</span>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
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
                <SurveyAnswerModal open={showSurveyAnswerModal} surveyId={dummySurveyId} onClose={() => setShowSurveyAnswerModal(false)} onAnswered={() => { setShowSurveyAnswerModal(false); alert('回答送信完了（ダミー）') }} />
            )}
            {showSurveyResultModal && (
                <SurveyResultModal open={showSurveyResultModal} surveyId={dummySurveyId} onClose={() => setShowSurveyResultModal(false)} />
            )}
        </div>
    )
}

export default HomeScreen
