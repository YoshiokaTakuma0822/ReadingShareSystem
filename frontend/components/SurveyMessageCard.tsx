"use client"

import React, { useEffect, useState } from 'react'
import { surveyApi } from '../lib/surveyApi'
import { Message } from '../types/message'
import { Survey } from '../types/survey'

interface SurveyMessageCardProps {
    msg: Message
    isMine: boolean
    currentUserId: string | null
    onAnswerClick: (surveyId: string) => void
    onResultClick: (surveyId: string) => void
    onLoadingComplete?: () => void
}

const SurveyMessageCard: React.FC<SurveyMessageCardProps> = ({ msg, isMine, currentUserId, onAnswerClick, onResultClick, onLoadingComplete }) => {
    const [surveyData, setSurveyData] = useState<Survey | null>(null)
    const [loading, setLoading] = useState(true)
    const [hasAnswered, setHasAnswered] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (msg.surveyId) {
            surveyApi.getSurveyFormat(msg.surveyId)
                .then(data => {
                    setSurveyData(data)
                    setLoading(false)
                    onLoadingComplete?.()
                })
                .catch(() => {
                    setError('アンケート情報の取得に失敗しました')
                    setLoading(false)
                    onLoadingComplete?.()
                })
        }
    }, [msg.surveyId, onLoadingComplete])

    useEffect(() => {
        if (msg.surveyId && currentUserId) {
            surveyApi.hasAnswered(msg.surveyId, currentUserId)
                .then(answered => setHasAnswered(answered))
                .catch(() => setHasAnswered(false))
        }
    }, [msg.surveyId, currentUserId])

    return (
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8, justifyContent: isMine ? 'flex-end' : 'flex-start', marginBottom: 12 }}>
            {!isMine && (
                <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                </span>
            )}
            <div style={{ display: 'flex', flexDirection: 'column', alignItems: isMine ? 'flex-end' : 'flex-start', gap: 4 }}>
                <div style={{ fontSize: '0.8em', color: '#888', display: 'flex', gap: 8 }}>
                    <span>{msg.user}</span>
                    {msg.sentAt && <span>{new Date(msg.sentAt).toLocaleTimeString()}</span>}
                </div>
                <div style={{ border: '2px solid #2196f3', borderRadius: 12, padding: 16, background: 'linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%)', maxWidth: 500, minWidth: 300, boxShadow: '0 2px 8px rgba(33,150,243,0.2)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
                        <span style={{ fontSize: 20 }}>📊</span>
                        <span style={{ fontWeight: 'bold', color: '#1976d2' }}>新しいアンケート</span>
                    </div>
                    {loading ? (
                        <div style={{ color: '#666', fontStyle: 'italic' }}>アンケート情報を読み込み中...</div>
                    ) : error ? (
                        <div style={{ color: '#d32f2f' }}>{error}</div>
                    ) : surveyData ? (
                        <div>
                            <div style={{ marginBottom: 12 }}>
                                <strong style={{ color: '#1976d2' }}>タイトル:</strong> {surveyData.title}
                            </div>
                            {surveyData.questions.map((q, qi) => (
                                <div key={qi} style={{ marginBottom: 12 }}>
                                    <div style={{ fontWeight: 'bold', marginBottom: 4, color: '#333' }}>{q.questionText}</div>
                                    <ul style={{ margin: '4px 0', paddingLeft: 20, fontSize: '0.9em', color: '#555' }}>
                                        {q.options.map((opt, oi) => (
                                            <li key={oi} style={{ marginBottom: 2 }}>{opt}</li>
                                        ))}
                                    </ul>
                                </div>
                            ))}
                            {msg.surveyId && (
                                <div style={{ display: 'flex', gap: 8, width: '100%' }}>
                                    {hasAnswered ? (
                                        <button onClick={() => onResultClick(msg.surveyId!)} style={{ flex: 1, padding: '12px 24px', borderRadius: 8, border: 'none', fontSize: 16, background: '#4caf50', color: 'white', cursor: 'pointer', fontWeight: 'bold', boxShadow: '0 2px 4px rgba(76,175,80,0.3)' }}>
                                            📊 結果を表示
                                        </button>
                                    ) : (
                                        <button onClick={() => onAnswerClick(msg.surveyId!)} style={{ flex: 1, padding: '12px 24px', borderRadius: 8, border: 'none', fontSize: 16, background: '#2196f3', color: 'white', cursor: 'pointer', fontWeight: 'bold', boxShadow: '0 2px 4px rgba(33,150,243,0.3)' }}>
                                            📝 アンケートに回答する
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    ) : (
                        <div style={{ color: '#666', fontStyle: 'italic' }}>アンケート情報がありません</div>
                    )}
                </div>
            </div>
            {isMine && (
                <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>
                    {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                </span>
            )}
        </div>
    )
}

export default SurveyMessageCard
