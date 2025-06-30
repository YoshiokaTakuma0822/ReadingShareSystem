"use client"
import React, { useEffect, useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { Survey, SubmitSurveyAnswerRequest } from '../../types/survey'

interface SurveyAnswerModalProps {
    open: boolean
    surveyId: string
    onClose: () => void
}

const SurveyAnswerModal: React.FC<SurveyAnswerModalProps> = ({ open, surveyId, onClose }) => {
    const [survey, setSurvey] = useState<Survey | null>(null)
    const [selected, setSelected] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!open) return
        setLoading(true)
        setError(null)
        surveyApi.getSurveyFormat(surveyId)
            .then(data => {
                setSurvey(data)
            })
            .catch(() => setError('アンケート情報の取得に失敗しました'))
            .finally(() => setLoading(false))
    }, [open, surveyId])

    const handleAnswer = async () => {
        if (!selected || !survey) return
        setLoading(true)
        setError(null)
        try {
            const request: SubmitSurveyAnswerRequest = {
                surveyId,
                answers: new Map([[survey.questions[0].questionText, [selected]]]),
                isAnonymous: false
            }
            await surveyApi.answerSurvey(surveyId, request)
            onClose()
        } catch (e) {
            setError('回答送信に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    if (!open) return null

    const handleBackgroundClick = (e: React.MouseEvent<HTMLDivElement>) => {
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
                    border: '4px solid #388e3c',
                    padding: 32,
                    maxWidth: 600,
                    width: '90%',
                    background: '#f1fdf6',
                    borderRadius: 12,
                    boxShadow: '0 8px 32px rgba(0,0,0,0.2)'
                }}
                onClick={(e) => e.stopPropagation()}
            >
                <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 24, color: '#388e3c' }}>{survey?.title || 'アンケート'}</h2>
                {loading ? (
                    <div>読み込み中...</div>
                ) : error ? (
                    <div style={{ color: 'red', marginBottom: 16 }}>{error}</div>
                ) : survey && survey.questions.length > 0 ? (
                    <div style={{ marginBottom: 32 }}>
                        <h3 style={{ marginBottom: 16 }}>{survey.questions[0].questionText}</h3>
                        {survey.questions[0].options.map((opt: string, idx: number) => (
                            <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 16 }}>
                                <input type="radio" name="survey_option" checked={selected === opt} onChange={() => setSelected(opt)} /> {opt}
                            </div>
                        ))}
                    </div>
                ) : (
                    <div>アンケートの質問がありません</div>
                )}
                <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16 }}>
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
                        onClick={handleAnswer}
                        disabled={!selected || loading}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid #388e3c',
                            borderRadius: 8,
                            background: '#388e3c',
                            color: 'white',
                            fontSize: 16,
                            cursor: !selected || loading ? 'not-allowed' : 'pointer',
                            opacity: !selected || loading ? 0.6 : 1
                        }}
                    >
                        {loading ? '送信中...' : '回答する'}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default SurveyAnswerModal
