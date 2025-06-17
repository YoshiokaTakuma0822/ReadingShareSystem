"use client"
import React, { useEffect, useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { Survey, SubmitSurveyAnswerRequest } from '../../types/survey'

interface SurveyAnswerModalProps {
    open: boolean
    surveyId: string
    onClose: () => void
    onAnswered: () => void
}

const SurveyAnswerModal: React.FC<SurveyAnswerModalProps> = ({ open, surveyId, onClose, onAnswered }) => {
    const [survey, setSurvey] = useState<Survey | null>(null)
    const [selected, setSelected] = useState<string | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!open) return
        setLoading(true)
        setError(null)
        surveyApi.getSurvey(surveyId)
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
            onAnswered()
        } catch (e) {
            setError('回答送信に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    if (!open) return null

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 32, maxWidth: 600, background: '#f1fdf6', borderRadius: 8, boxShadow: '0 4px 24px #a5d6a7' }}>
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
                <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>キャンセル</button>
                <button onClick={handleAnswer} disabled={!selected || loading} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>{loading ? '送信中...' : '回答する'}</button>
            </div>
        </div>
    )
}

export default SurveyAnswerModal
