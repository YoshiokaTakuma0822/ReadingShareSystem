"use client"

import React, { useState } from 'react'
import { Room } from '../../types/room'
import { RoomHistoryDto } from '../../types/room';
import { roomApi } from '../../lib/roomApi'
import { getDummyUserId, logout } from '../../lib/authUtils'
import RoomCreationModal from './RoomCreationModal'
import RoomJoinModal from './RoomJoinModal'
import SurveyAnswerModal from './SurveyAnswerModal'
import SurveyResultModal from './SurveyResultModal'
import AuthGuard from '../../components/AuthGuard'

const HomeScreen: React.FC = () => {
    const [tab, setTab] = useState<'create' | 'search'>('create') // デフォルトを部屋作成に変更
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

    // クライアントサイドでユーザーIDを初期化
    React.useEffect(() => {
        setCurrentUserId(getDummyUserId())
    }, [])

    const [creatorMap, setCreatorMap] = useState<{ [roomId: string]: string }>({})

    // 部屋検索API（空文字の場合は全件取得）
    const handleSearch = async () => {
        setLoading(true)
        setError(null)
        try {
            const result = await roomApi.searchRooms(searchText)
            setRooms(result.rooms || [])
            // 部屋ごとに作成者名を取得
            const map: { [roomId: string]: string } = {};
            await Promise.all((result.rooms || []).map(async (room) => {
                try {
                    const members = await roomApi.getRoomMembers(room.id);
                    console.log('room:', room, 'members:', members); // デバッグ出力
                    console.log('members detail:', JSON.stringify(members)); // 詳細デバッグ
                    const creator = members.find((m: any) => (m.userId || '').replace(/-/g, '').toLowerCase() === (room.hostUserId || '').replace(/-/g, '').toLowerCase());
                    map[room.id] = creator ? creator.username : '';
                } catch {
                    map[room.id] = '';
                }
            }));
            setCreatorMap(map);
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
    const handleRoomJoined = async () => {
        setShowJoinModal(false)
        if (selectedRoom) {
            // 履歴を即時再取得
            if (currentUserId) {
                try {
                    const history = await roomApi.getRoomHistory(currentUserId, 10);
                    setRoomHistory(history);
                } catch {}
            }
            // グループチャット画面へ移動
            window.location.href = `/rooms/${selectedRoom.id}/chat`
        }
    }

    // ユーザープロフィール情報
    const [userName, setUserName] = useState<string>('');
    const [loginTime, setLoginTime] = useState<Date | null>(null);

    // ログイン時刻とユーザー名をlocalStorageから取得
    React.useEffect(() => {
        if (typeof window !== 'undefined') {
            setUserName(localStorage.getItem('reading-share-user-name') || 'ゲスト');
            const loginTimestamp = localStorage.getItem('reading-share-login-time');
            if (loginTimestamp) {
                setLoginTime(new Date(Number(loginTimestamp)));
            } else {
                const now = Date.now();
                localStorage.setItem('reading-share-login-time', String(now));
                setLoginTime(new Date(now));
            }
        }
    }, []);

    // 経過時間を計算
    const [elapsed, setElapsed] = useState('');
    React.useEffect(() => {
        if (!loginTime) return;
        const update = () => {
            const now = new Date();
            const diff = Math.floor((now.getTime() - loginTime.getTime()) / 1000);
            const h = Math.floor(diff / 3600);
            const m = Math.floor((diff % 3600) / 60);
            const s = diff % 60;
            setElapsed(`${h}時間${m}分${s}秒`);
        };
        update();
        const timer = setInterval(update, 1000);
        return () => clearInterval(timer);
    }, [loginTime]);

    const [roomHistory, setRoomHistory] = useState<RoomHistoryDto[]>([]);
    // 履歴取得
    React.useEffect(() => {
        if (!currentUserId) return;
        console.log('履歴取得: currentUserId =', currentUserId);
        roomApi.getRoomHistory(currentUserId, 10)
            .then((res) => {
                console.log('roomApi.getRoomHistory response:', res);
                setRoomHistory(res);
            })
            .catch(() => setRoomHistory([]));
    }, [currentUserId]);

    return (
        <AuthGuard>
            <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
                <div style={{ marginBottom: 32 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                        <h1 style={{ color: 'var(--accent)', fontSize: 32, margin: 0 }}>読書共有システム</h1>
                        {/* ユーザープロフィール表示 */}
                        <div
                            style={{
                                background: '#388e3c', color: '#fff', borderRadius: 24, padding: '8px 20px', fontWeight: 'bold', fontSize: 16, cursor: 'pointer', position: 'relative', marginRight: 16
                            }}
                            title={``}
                            onMouseEnter={e => {
                                const tooltip = document.createElement('div');
                                tooltip.id = 'user-profile-tooltip';
                                tooltip.style.position = 'absolute';
                                tooltip.style.top = '110%';
                                tooltip.style.left = '50%';
                                tooltip.style.transform = 'translateX(-50%)';
                                tooltip.style.background = '#fff';
                                tooltip.style.color = '#333';
                                tooltip.style.padding = '12px 20px';
                                tooltip.style.borderRadius = '12px';
                                tooltip.style.boxShadow = '0 2px 8px rgba(0,0,0,0.18)';
                                tooltip.style.whiteSpace = 'nowrap';
                                tooltip.style.zIndex = '9999';
                                tooltip.innerHTML = `<b>ユーザー名:</b> ${userName}<br/><b>ログイン経過:</b> ${elapsed}`;
                                e.currentTarget.appendChild(tooltip);
                            }}
                            onMouseLeave={e => {
                                const tooltip = document.getElementById('user-profile-tooltip');
                                if (tooltip) tooltip.remove();
                            }}
                        >
                            {userName}
                        </div>
                        <button
                            onClick={logout}
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
                    <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center', gap: 12 }}>
                        <button onClick={() => setShowCreateModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid var(--text-main)', background: 'var(--accent)', color: 'var(--white)', fontWeight: 'bold' }}>
                            新しい部屋を作成する
                        </button>
                        <button
                            onClick={handleSearch}
                            style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #2196f3', background: '#2196f3', color: '#fff', fontWeight: 'bold', fontSize: 16, cursor: 'pointer' }}
                        >部屋一覧を更新</button>
                    </div>
                )}
                {tab === 'search' && (
                    <div style={{ marginBottom: 24, display: 'flex', alignItems: 'center', gap: 12 }}>
                        <input
                            type="text"
                            value={searchText}
                            onChange={e => setSearchText(e.target.value)}
                            placeholder="部屋名・本のタイトルで検索"
                            style={{ padding: 12, borderRadius: 8, border: '1px solid #ccc', fontSize: 18, flex: 1 }}
                        />
                        <button
                            onClick={handleSearch}
                            style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #388e3c', background: '#fff', color: '#388e3c', fontWeight: 'bold', fontSize: 16, cursor: 'pointer' }}
                        >検索</button>
                        <button
                            onClick={handleSearch}
                            style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #2196f3', background: '#2196f3', color: '#fff', fontWeight: 'bold', fontSize: 16, cursor: 'pointer' }}
                        >部屋一覧を更新</button>
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
                                gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', // 240pxに調整
                                gap: 20,
                                width: '100%',
                                boxSizing: 'border-box',
                            }}
                        >
                            {rooms.length === 0 ? (
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
                                            document.querySelectorAll(`[id^='room-tooltip-']`).forEach(el => el.remove());
                                            // ツールチップ生成
                                            const tooltip = document.createElement('div');
                                            tooltip.id = `room-tooltip-${room.id}`;
                                            tooltip.style.position = 'fixed';
                                            const rect = e.currentTarget.getBoundingClientRect();
                                            tooltip.style.left = `${rect.left + rect.width / 2}px`;
                                            tooltip.style.top = `${rect.top - 12}px`;
                                            tooltip.style.transform = 'translate(-50%, -100%)';
                                            tooltip.style.background = '#fff';
                                            tooltip.style.color = '#333';
                                            tooltip.style.padding = '14px 24px';
                                            tooltip.style.borderRadius = '12px';
                                            tooltip.style.boxShadow = '0 2px 12px rgba(0,0,0,0.18)';
                                            tooltip.style.whiteSpace = 'nowrap';
                                            tooltip.style.zIndex = '9999';
                                            tooltip.style.fontSize = '15px';
                                            tooltip.innerHTML = `
                                                <b>部屋名:</b> ${room.roomName}<br/>
                                                <b>本タイトル:</b> ${room.bookTitle}<br/>
                                                <b>作成者:</b> ${creatorMap[room.id] || '-'}<br/>
                                                <b>作成日:</b> ${new Date(room.createdAt).toLocaleString()}<br/>
                                                <b>ページ数:</b> ${room.totalPages ?? '-'}<br/>
                                                <b>パスワード:</b> ${room.hasPassword ? 'あり' : 'なし'}
                                            `;
                                            document.body.appendChild(tooltip);
                                        }}
                                        onMouseLeave={(e) => {
                                            e.currentTarget.style.background = 'var(--white)'
                                            e.currentTarget.style.transform = 'translateY(0)'
                                            e.currentTarget.style.boxShadow = 'none'
                                            // すべてのツールチップを確実に削除
                                            document.querySelectorAll(`[id^='room-tooltip-']`).forEach(el => el.remove());
                                        }}
                                    >
                                        <div>
                                            <h3 style={{ color: 'var(--accent)', fontSize: 18, fontWeight: 'bold', marginBottom: 8, overflowWrap: 'break-word', wordBreak: 'break-word' }}>
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
                                                    e.stopPropagation();
                                                    if (window.confirm('本当にこの部屋を削除しますか？')) {
                                                        roomApi.deleteRoom(room.id)
                                                            .then(handleSearch)
                                                            .catch(err => {
                                                                let msg = '削除に失敗しました';
                                                                if (err && err.response && err.response.data) {
                                                                    msg += '\n' + JSON.stringify(err.response.data);
                                                                } else if (err && err.message) {
                                                                    msg += '\n' + err.message;
                                                                }
                                                                alert(msg);
                                                                console.error('deleteRoom error:', err);
                                                            });
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
                        onAnswered={() => { setShowSurveyAnswerModal(false); alert('回答送信完了（ダミー）'); }} 
                    />
                )}
                {showSurveyResultModal && (
                    <SurveyResultModal 
                        open={showSurveyResultModal} 
                        surveyId={dummySurveyId} 
                        onClose={() => setShowSurveyResultModal(false)} 
                    />
                )}
                {/* 履歴表示 */}
                <div style={{ margin: '32px 0 16px 0', padding: 16, background: '#f7f7f7', borderRadius: 8, border: '1px solid #ccc' }}>
                    <h2 style={{ fontSize: 18, color: 'var(--accent)', margin: 0, marginBottom: 8 }}>自分が参加したことのある部屋の履歴（最新10件）</h2>
                    {roomHistory.length === 0 ? (
                        <div style={{ color: '#888', fontSize: 15 }}>履歴がありません</div>
                    ) : (
                        <ul style={{ listStyle: 'none', padding: 0, margin: 0 }}>
                            {roomHistory.map((h) => (
                                <li key={h.roomId} style={{ marginBottom: 8, padding: 8, background: '#fff', borderRadius: 6, border: '1px solid #eee', fontSize: 15 }}>
                                    {h.deleted || !h.room ? (
                                        <span style={{ color: '#b71c1c' }}>この部屋は既に削除されています</span>
                                    ) : (
                                        <>
                                            <span style={{ fontWeight: 'bold', color: 'var(--accent)' }}>{h.room.roomName}</span>
                                            <span style={{ marginLeft: 8, color: '#666' }}>（{h.room.bookTitle}）</span>
                                            <span style={{ marginLeft: 8, color: '#888', fontSize: 13 }}>作成日: {new Date(h.room.createdAt).toLocaleDateString()}</span>
                                            <button style={{ marginLeft: 16, padding: '2px 10px', fontSize: 13, borderRadius: 4, border: '1px solid #388e3c', background: '#e8f5e9', color: '#388e3c', cursor: 'pointer' }} onClick={() => window.location.href = `/rooms/${h.roomId}/chat`}>チャットへ</button>
                                        </>
                                    )}
                                    <span style={{ float: 'right', color: '#aaa', fontSize: 12 }}>参加日: {new Date(h.joinedAt).toLocaleDateString()}</span>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </AuthGuard>
    )
}

export default HomeScreen
