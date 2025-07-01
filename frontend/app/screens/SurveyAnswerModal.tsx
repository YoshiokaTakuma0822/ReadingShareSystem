"use client"
import React, { useEffect, useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { Survey, SubmitSurveyAnswerRequest } from '../../types/survey'

interface SurveyAnswerModalProps {
    open: boolean
    surveyId: string
    onClose: () => void
    onAnswered?: () => void // 回答完了時のコールバックを追加
}

const SurveyAnswerModal: React.FC<SurveyAnswerModalProps> = ({ open, surveyId, onClose, onAnswered }) => {
    const [survey, setSurvey] = useState<Survey | null>(null)
    const [selected, setSelected] = useState<Record<string, string[]>>({})
    const [newOption, setNewOption] = useState<Record<string, string>>({})
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [isAnonymous, setIsAnonymous] = useState(false)

    useEffect(() => {
        if (!open) return
        setLoading(true)
        setError(null)
        surveyApi.getSurveyFormat(surveyId)
            .then(data => {
                setSurvey(data)
                // 初期化
                const sel: Record<string, string[]> = {}
                data.questions.forEach(q => sel[q.questionText] = [])
                setSelected(sel)
                setNewOption({})
                setIsAnonymous(false)
            })
            .catch(() => setError('アンケート情報の取得に失敗しました'))
            .finally(() => setLoading(false))
    }, [open, surveyId])

    const handleSelect = (q: string, opt: string, multi: boolean) => {
        setSelected(prev => {
            if (multi) {
                const arr = prev[q] || []
                if (arr.includes(opt)) {
                    return { ...prev, [q]: arr.filter(o => o !== opt) }
                } else {
                    return { ...prev, [q]: [...arr, opt] }
                }
            } else {
                return { ...prev, [q]: [opt] }
            }
        })
    }

    const handleAddOption = (q: string) => {
        const val = (newOption[q] || '').trim()
        if (!val) return
        setSelected(prev => ({ ...prev, [q]: [...(prev[q] || []), val] }))
        setNewOption(prev => ({ ...prev, [q]: '' }))
    }

    const handleAnswer = async () => {
        if (!survey) return
        setLoading(true)
        setError(null)
        try {
            // userId取得
            let userId = localStorage.getItem('reading-share-user-id');
            if (!userId) {
                setError('ユーザー情報が見つかりません。再ログインしてください。')
                setLoading(false)
                return;
            }
            // そのままuserIdを使う
            // 必須チェック
            for (const q of survey.questions) {
                if ((selected[q.questionText]?.length || 0) === 0) {
                    setError('すべての質問に回答してください')
                    setLoading(false)
                    return
                }
                if (q.questionType === 'SINGLE_CHOICE' && selected[q.questionText].length > 1) {
                    setError('単一選択の質問には1つだけ選択してください')
                    setLoading(false)
                    return
                }
            }
            const request: SubmitSurveyAnswerRequest = {
                surveyId,
                userId,
                answers: selected,
                isAnonymous
            }
            await surveyApi.answerSurvey(surveyId, request)
            onClose()
            if (onAnswered) onAnswered();
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
                        {survey.questions.map((q, idx) => (
                            <div key={idx} style={{ marginBottom: 24 }}>
                                <h3 style={{ marginBottom: 16 }}>{q.questionText}</h3>
                                {q.options.map((opt: string, i: number) => (
                                    <div key={i} style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 8 }}>
                                        <input
                                            type={q.questionType === 'MULTIPLE_CHOICE' ? 'checkbox' : 'radio'}
                                            name={q.questionText}
                                            checked={selected[q.questionText]?.includes(opt) || false}
                                            onChange={() => handleSelect(q.questionText, opt, q.questionType === 'MULTIPLE_CHOICE')}
                                        /> {opt}
                                    </div>
                                ))}
                                {q.allowAddOptions && (
                                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
                                        <input
                                            type="text"
                                            value={newOption[q.questionText] || ''}
                                            onChange={e => setNewOption(prev => ({ ...prev, [q.questionText]: e.target.value }))}
                                            placeholder="新しい選択肢を追加"
                                            style={{ flex: 1, padding: 6, fontSize: 16, border: '1px solid #ccc' }}
                                        />
                                        <button
                                            type="button"
                                            onClick={() => handleAddOption(q.questionText)}
                                            style={{ padding: '6px 16px', borderRadius: 6, border: '1px solid #388e3c', background: '#e8f5e9', color: '#388e3c', fontWeight: 600, cursor: 'pointer' }}
                                        >追加</button>
                                    </div>
                                )}
                            </div>
                        ))}
                        <label style={{ color: '#388e3c', fontWeight: 500 }}>
                            <input type="checkbox" checked={isAnonymous} onChange={e => setIsAnonymous(e.target.checked)} /> 匿名で回答する
                        </label>
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
                        disabled={loading}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid #388e3c',
                            borderRadius: 8,
                            background: '#388e3c',
                            color: 'white',
                            fontSize: 16,
                            cursor: loading ? 'not-allowed' : 'pointer',
                            opacity: loading ? 0.6 : 1
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
