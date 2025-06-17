"use client"
import React, { useEffect, useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { SurveyResult } from '../../types/survey'

interface SurveyResultModalProps {
    open: boolean
    surveyId: string
    onClose: () => void
}

const SurveyResultModal: React.FC<SurveyResultModalProps> = ({ open, surveyId, onClose }) => {
    const [surveyResult, setSurveyResult] = useState<SurveyResult | null>(null)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    useEffect(() => {
        if (!open) return
        setLoading(true)
        setError(null)
        surveyApi.getSurveyResult(surveyId)
            .then(data => {
                setSurveyResult(data)
            })
            .catch(() => setError('アンケート結果の取得に失敗しました'))
            .finally(() => setLoading(false))
    }, [open, surveyId])

    if (!open) return null

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 32, maxWidth: 600, background: '#f1fdf6', borderRadius: 8, boxShadow: '0 4px 24px #a5d6a7' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 24, color: '#388e3c' }}>{surveyResult?.title || 'アンケート結果'}</h2>
            {loading ? (
                <div>読み込み中...</div>
            ) : error ? (
                <div style={{ color: 'red', marginBottom: 16 }}>{error}</div>
            ) : surveyResult ? (
                <div style={{ marginBottom: 32 }}>
                    {surveyResult.results.map((result, idx) => (
                        <div key={idx} style={{ marginBottom: 24 }}>
                            <h3 style={{ marginBottom: 12 }}>{result.questionText}</h3>
                            {typeof result.answers === 'object' && !Array.isArray(result.answers) ? (
                                Object.entries(result.answers).map(([option, votes]) => (
                                    <div key={option} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                        <span>{option}</span>
                                        <span>{votes}票</span>
                                    </div>
                                ))
                            ) : Array.isArray(result.answers) ? (
                                result.answers.map((answer, answerIdx) => (
                                    <div key={answerIdx} style={{ marginBottom: 8 }}>
                                        <span>{answer}</span>
                                    </div>
                                ))
                            ) : null}
                        </div>
                    ))}
                </div>
            ) : (
                <div>結果がありません</div>
            )}
            <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>閉じる</button>
            </div>
        </div>
    )
}

export default SurveyResultModal
