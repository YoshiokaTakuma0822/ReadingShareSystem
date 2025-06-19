"use client"
import React, { useState } from 'react'
import SurveyCreationModal from './SurveyCreationModal'

interface Message {
    id: number
    user: string
    text: string
}

interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
}

const GroupChatScreen: React.FC<GroupChatScreenProps> = ({ roomTitle = "チャットルーム", currentUser = "あなた", roomId }) => {
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState("")
    const [msgId, setMsgId] = useState(1)
    const [showSurveyModal, setShowSurveyModal] = useState(false)

    const handleSend = () => {
        if (!input.trim()) return
        setMessages([...messages, { id: msgId, user: currentUser, text: input }])
        setMsgId(msgId + 1)
        setInput("")
    }

    const handleGoToReading = () => {
        if (roomId) {
            window.location.href = `/rooms/${roomId}/reading`
        }
    }

    const handleCreateSurvey = () => {
        setShowSurveyModal(true)
    }

    const handleSurveyCreated = () => {
        setShowSurveyModal(false)
        // サーベイ作成後の処理（必要に応じて）
    }

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 24, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: '#388e3c' }}>{roomTitle}</h2>

            {/* ナビゲーションボタン */}
            <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
                <button
                    onClick={handleGoToReading}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#4caf50',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    📖 読書画面へ
                </button>
                <button
                    onClick={handleCreateSurvey}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#2196f3',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    📊 アンケート作成
                </button>
                <button
                    onClick={() => window.location.href = '/'}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#757575',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    🏠 ホームへ
                </button>
            </div>
            <div style={{ flex: 1, display: 'flex', flexDirection: 'column', gap: 16, marginBottom: 32, minHeight: 200, maxHeight: '60vh', overflowY: 'auto', background: 'rgba(255,255,255,0.7)', borderRadius: 8, padding: 16 }}>
                {messages.map(msg => {
                    const isMine = msg.user === currentUser
                    return (
                        <div
                            key={msg.id}
                            style={{
                                display: 'flex',
                                alignItems: 'center',
                                gap: 8,
                                justifyContent: isMine ? 'flex-end' : 'flex-start',
                            }}
                        >
                            {!isMine && (
                                <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{msg.user.charAt(0).toUpperCase()}</span>
                            )}
                            <div
                                style={{
                                    border: '1px solid #222',
                                    borderRadius: 16,
                                    padding: 8,
                                    background: isMine ? '#e0f7fa' : '#fff',
                                    maxWidth: 600,
                                    wordBreak: 'break-word',
                                }}
                            >
                                {msg.text}
                            </div>
                            {isMine && (
                                <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>{msg.user.charAt(0).toUpperCase()}</span>
                            )}
                        </div>
                    )
                })}
            </div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32 }}>
                <input
                    type="text"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={e => { if (e.key === 'Enter') handleSend() }}
                    style={{ flex: 1, padding: 12, borderRadius: 8, border: '1px solid #222', fontSize: 18 }}
                    placeholder="メッセージを入力..."
                />
                <button
                    style={{ marginLeft: 8, padding: '12px 24px', borderRadius: 8, border: '1px solid #222', fontSize: 18 }}
                    onClick={handleSend}
                >送信</button>
            </div>

            {/* アンケート作成モーダル */}
            {showSurveyModal && roomId && (
                <SurveyCreationModal
                    open={showSurveyModal}
                    roomId={roomId}
                    onClose={() => setShowSurveyModal(false)}
                    onCreated={handleSurveyCreated}
                />
            )}
        </div>
    )
}

export default GroupChatScreen
