"use client"

import React, { useState } from 'react'
import { Room } from '../../types/room'
import { roomApi } from '../../lib/roomApi'
import RoomCreationModal from './RoomCreationModal'
import SurveyAnswerModal from './SurveyAnswerModal'
import SurveyResultModal from './SurveyResultModal'

const HomeScreen: React.FC = () => {
    const [tab, setTab] = useState<'create' | 'search'>('create')
    const [searchText, setSearchText] = useState('')
    const [rooms, setRooms] = useState<Room[]>([])
    const [showCreateModal, setShowCreateModal] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [showSurveyAnswerModal, setShowSurveyAnswerModal] = useState(false)
    const [showSurveyResultModal, setShowSurveyResultModal] = useState(false)

    // サンプル用のダミーsurveyId
    // const dummySurveyId = 'sample-survey-id-1'
    const dummySurveyId = '00000000-0000-0000-0000-000000000001' // 例: 固定のダミーID
    // サンプル用のダミーuserId (実際のアプリでは認証から取得)
    // const dummyUserId = 'sample-user-id-1'
    const dummyUserId = '00000000-0000-0000-0000-000000000001' // 例: 固定のダミーID

    // 部屋検索API
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

    // 初期表示で全件取得
    React.useEffect(() => {
        if (tab === 'search') handleSearch()
    }, [tab])
    // 部屋作成後のリスト再取得
    const handleRoomCreated = () => {
        setShowCreateModal(false)
        setTab('search')
        handleSearch()
    }

    return (
        <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
            {/* 各W画面の確認方法案内 */}
            <div style={{ background: 'var(--green-bg)', border: '1px solid var(--border)', borderRadius: 10, padding: 24, marginBottom: 32 }}>
                <h2 style={{ color: 'var(--accent)', fontSize: 22, marginBottom: 12 }}>各W画面の確認方法</h2>
                <ul style={{ lineHeight: 2, fontSize: 16, color: 'var(--accent)', marginLeft: 16 }}>
                    <li><a href="/screens/HomeScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W1: ホーム画面</a></li>
                    <li><a href="/screens/LoginScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W2: ログイン画面</a></li>
                    <li><a href="/screens/RegisterScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W3: 会員登録画面</a></li>
                    <li><a href="/screens/RoomCreationModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W4: 部屋作成画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/RoomJoinModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W5: 部屋参加画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/GroupChatScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W6: グループチャット画面</a></li>
                    <li><a href="/screens/SurveyCreationModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W7: アンケート作成画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/SurveyAnswerModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W8: アンケート回答画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/SurveyResultModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W9: アンケート結果画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/ReadingScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W10: 読書画面</a></li>
                </ul>
                <div style={{ marginTop: 12, color: 'var(--accent)', fontWeight: 'bold' }}>
                    全W画面サンプル集は現在ご利用いただけません
                </div>
                <div style={{ marginTop: 20, color: '#388e3c', fontWeight: 500, fontSize: 15 }}>
                    {/* アンケート作成テンプレート（サンプル）削除 */}
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
                    <button onClick={() => setShowCreateModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid var(--text-main)' }}>
                        部屋を作成する
                    </button>
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
                    />
                    <button onClick={handleSearch} style={{ padding: '8px 16px' }}>検索</button>
                </div>
            )}
            {loading ? (
                <div>読み込み中...</div>
            ) : error ? (
                <div style={{ color: 'red' }}>{error}</div>
            ) : (
                <div style={{ border: '2px solid var(--text-main)', padding: 24 }}>
                    <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24 }}>
                        {rooms.length === 0 ? (
                            <div style={{ color: '#b0b8c9', fontSize: 20, width: '100%', textAlign: 'center', padding: '32px 0' }}>
                                部屋はありません
                            </div>
                        ) : (
                            rooms.map((room) => (
                                <div key={room.id} style={{ border: '1px solid var(--text-main)', width: 200, height: 100, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20 }}>
                                    {room.roomName}
                                </div>
                            ))
                        )}
                    </div>
                </div>
            )}
            {showCreateModal && (
                <RoomCreationModal open={showCreateModal} userId={dummyUserId} onClose={() => setShowCreateModal(false)} onCreated={handleRoomCreated} />
            )}
            {showSurveyAnswerModal && (
                <SurveyAnswerModal open={showSurveyAnswerModal} surveyId={dummySurveyId} onClose={() => setShowSurveyAnswerModal(false)} onAnswered={() => { setShowSurveyAnswerModal(false); alert('回答送信完了（ダミー）') }} />
            )}
            {showSurveyResultModal && (
                <SurveyResultModal open={showSurveyResultModal} surveyId={dummySurveyId} onClose={() => setShowSurveyResultModal(false)} />
            )}
            <div style={{ marginTop: 32, display: 'flex', gap: 16, flexWrap: 'wrap' }}>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/RoomCreationSample', '_blank')}>部屋作成サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/RoomJoinSample', '_blank')}>部屋参加サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/SurveyCreationSample', '_blank')}>アンケート作成サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/SurveyAnswerSample', '_blank')}>アンケート回答サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/SurveyResultSample', '_blank')}>アンケート結果サンプル（ウィンドウ型）</button>
            </div>
        </div>
    )
}

export default HomeScreen
