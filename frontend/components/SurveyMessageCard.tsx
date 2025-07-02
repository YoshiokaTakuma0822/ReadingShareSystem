"use client"

import React, { useCallback, useEffect, useState } from 'react'
import { surveyApi } from '../lib/surveyApi'
import { Message } from '../types/message'
import { SubmitSurveyAnswerRequest, Survey, SurveyResult } from '../types/survey'

interface SurveyMessageCardProps {
    msg: Message
    isMine: boolean
    currentUserId: string | null
    onLoadingComplete?: () => void
}

const SurveyMessageCard: React.FC<SurveyMessageCardProps> = ({ msg, isMine, currentUserId, onLoadingComplete }) => {
    console.log('SurveyMessageCard render:', msg.surveyId)
    useEffect(() => {
        return () => {
            console.log('SurveyMessageCard unmount:', msg.surveyId)
        }
    }, [msg.surveyId])

    const [surveyData, setSurveyData] = useState<Survey | null>(null)
    const [loading, setLoading] = useState(true)
    const [hasAnswered, setHasAnswered] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [showingResults, setShowingResults] = useState(false)
    const [answers, setAnswers] = useState<Record<string, string[]>>({})
    const [isAnonymous, setIsAnonymous] = useState(false)
    const [submitting, setSubmitting] = useState(false)
    const [results, setResults] = useState<SurveyResult | null>(null)
    const [surveyTestVal, setSurveyTestVal] = useState<string>('')

    // ローカルストレージに回答状態を保存するキーを生成
    const localStorageKey = useCallback(() => {
        if (!msg.surveyId) return null
        return `survey_answers_${msg.surveyId}`
    }, [msg.surveyId])

    // 回答をローカルストレージから読み込む
    useEffect(() => {
        if (!msg.surveyId) return
        const key = localStorageKey()
        if (!key) return

        const savedAnswers = localStorage.getItem(key)
        if (savedAnswers) {
            try {
                const parsed = JSON.parse(savedAnswers)
                console.log('Loaded answers from storage:', parsed)
                setAnswers(parsed)
            } catch (e) {
                console.error('Failed to parse saved answers:', e)
            }
        }
    }, [msg.surveyId, localStorageKey])

    // 回答をローカルストレージに保存
    useEffect(() => {
        if (!msg.surveyId || Object.keys(answers).length === 0) return
        const key = localStorageKey()
        if (!key) return

        localStorage.setItem(key, JSON.stringify(answers))
        console.log('Saved answers to storage:', answers)
    }, [answers, msg.surveyId, localStorageKey])

    useEffect(() => {
        if (msg.surveyId) {
            console.log('Loading survey format for', msg.surveyId)
            surveyApi.getSurveyFormat(msg.surveyId)
                .then(data => {
                    setSurveyData(data)
                    // 回答の初期化（ローカルストレージから読み込むため不要）
                    // const initAnswers: Record<string, string[]> = {}
                    // data.questions.forEach(q => {
                    //     initAnswers[q.questionText] = []
                    // })
                    // setAnswers(initAnswers)
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

    const handleShowResults = useCallback(async () => {
        if (!msg.surveyId) return

        try {
            const result = await surveyApi.getSurveyResult(msg.surveyId)
            setResults(result)
            setShowingResults(true)
        } catch (error) {
            console.error('結果取得エラー:', error)
            alert('結果の取得に失敗しました')
        }
    }, [msg.surveyId])

    useEffect(() => {
        if (msg.surveyId && currentUserId) {
            surveyApi.hasAnswered(msg.surveyId, currentUserId)
                .then(answered => {
                    setHasAnswered(answered)
                    // 回答済みなら自動的に結果を取得
                    if (answered) {
                        handleShowResults()
                    }
                })
                .catch(() => setHasAnswered(false))
        }
    }, [msg.surveyId, currentUserId, handleShowResults])

    const handleAnswerSelect = useCallback((questionText: string, option: string, isMultiple: boolean) => {
        console.log('handleAnswerSelect called:', { questionText, option, isMultiple })
        setAnswers(prev => {
            const currentAnswers = prev[questionText] || []
            let newAnswers: string[]

            if (isMultiple) {
                if (currentAnswers.includes(option)) {
                    newAnswers = currentAnswers.filter(a => a !== option)
                } else {
                    newAnswers = [...currentAnswers, option]
                }
            } else {
                newAnswers = [option]
            }

            const result = { ...prev, [questionText]: newAnswers }
            console.log('New answers state:', result)
            // ローカルストレージに即時保存（状態の永続化）
            const key = localStorageKey()
            if (key) {
                try {
                    localStorage.setItem(key, JSON.stringify(result))
                    console.log('Saved answers on change:', result)
                } catch (e) {
                    console.error('Failed to save answers:', e)
                }
            }
            return result
        })
    }, [localStorageKey])

    const handleSubmitAnswer = async () => {
        if (!msg.surveyId || !currentUserId) return

        setSubmitting(true)
        try {
            const request: SubmitSurveyAnswerRequest = {
                userId: currentUserId,
                answers,
                isAnonymous
            }
            await surveyApi.answerSurvey(msg.surveyId, request)
            setHasAnswered(true)
            // 回答送信後に結果を取得
            handleShowResults()

            // 回答送信に成功したら、ローカルストレージからも削除
            const key = localStorageKey()
            if (key) {
                localStorage.removeItem(key)
                console.log('Removed saved answers after submission')
            }
        } catch (error) {
            console.error('回答送信エラー:', error)
            alert('回答の送信に失敗しました')
        } finally {
            setSubmitting(false)
        }
    }

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
                        <span style={{ fontWeight: 'bold', color: '#1976d2' }}>アンケート</span>
                    </div>

                    {loading ? (
                        <div style={{ color: '#666', fontStyle: 'italic' }}>アンケート情報を読み込み中...</div>
                    ) : error ? (
                        <div style={{ color: '#d32f2f' }}>{error}</div>
                    ) : surveyData ? (
                        <div>
                            <div style={{ marginBottom: 16 }}>
                                <strong style={{ color: '#1976d2' }}>タイトル:</strong> {surveyData.title}
                            </div>

                            {/* 結果表示モード */}
                            {(showingResults || hasAnswered) && results ? (
                                <div>
                                    <div style={{ marginBottom: 12, fontSize: 14, color: '#666' }}>
                                        回答者数: {results.totalRespondents}人
                                    </div>
                                    {results.results.map((questionResult, qIndex) => (
                                        <div key={qIndex} style={{ marginBottom: 16, padding: 12, border: '1px solid #e0e0e0', borderRadius: 8, background: 'white' }}>
                                            <h4 style={{ marginBottom: 12, color: '#333', fontSize: 14 }}>{questionResult.questionText}</h4>
                                            <div style={{ fontSize: 12 }}>
                                                {Object.entries(questionResult.votes).map(([option, count]) => {
                                                    const percentage = results.totalRespondents > 0 ? ((count / results.totalRespondents) * 100).toFixed(1) : '0'
                                                    return (
                                                        <div key={option} style={{ marginBottom: 6 }}>
                                                            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 2 }}>
                                                                <span>{option}</span>
                                                                <span>{count}票 ({percentage}%)</span>
                                                            </div>
                                                            <div style={{ background: '#f0f0f0', borderRadius: 2, height: 16, overflow: 'hidden' }}>
                                                                <div style={{ background: '#4caf50', height: '100%', width: `${percentage}%`, transition: 'width 0.3s ease' }}></div>
                                                            </div>
                                                        </div>
                                                    )
                                                })}
                                            </div>
                                        </div>
                                    ))}
                                    {!hasAnswered && (
                                        <button onClick={() => setShowingResults(false)} style={{ padding: '8px 16px', borderRadius: 6, border: 'none', background: '#666', color: 'white', cursor: 'pointer', fontSize: 14 }}>
                                            質問を表示
                                        </button>
                                    )}
                                </div>
                            ) : hasAnswered ? (
                                /* 回答済み - 結果取得中 */
                                <div style={{ color: '#666', fontStyle: 'italic' }}>結果を読み込み中...</div>
                            ) : (
                                /* 回答フォーム */
                                <div>
                                    {/* テスト用ラジオボタン - バグ検証 */}
                                    <div style={{ background: '#fff3e0', border: '2px dashed #ff5722', borderRadius: 6, padding: 12, marginBottom: 16 }}>
                                        <h4 style={{ margin: '0 0 8px 0', fontSize: 14, color: '#bf360c' }}>🧪 Survey Test Radio</h4>
                                        <div style={{ display: 'flex', flexDirection: 'column', gap: 6 }}>
                                            <label style={{ cursor: 'pointer' }}>
                                                <input type="radio" name="surveyTest" value="opt1" checked={surveyTestVal === 'opt1'} onChange={e => setSurveyTestVal(e.target.value)} style={{ marginRight: 6 }} /> opt1
                                            </label>
                                            <label style={{ cursor: 'pointer' }}>
                                                <input type="radio" name="surveyTest" value="opt2" checked={surveyTestVal === 'opt2'} onChange={e => setSurveyTestVal(e.target.value)} style={{ marginRight: 6 }} /> opt2
                                            </label>
                                            <button onClick={() => setSurveyTestVal('')} style={{ marginTop: 8, fontSize: 12, padding: '4px 8px', background: '#ff5722', color: 'white', border: 'none', borderRadius: 4 }}>リセット</button>
                                        </div>
                                    </div>
                                    {surveyData.questions.map((question, qIndex) => (
                                        <div key={qIndex} style={{ marginBottom: 16, padding: 12, border: '1px solid #e0e0e0', borderRadius: 8, background: 'white' }}>
                                            <h4 style={{ marginBottom: 12, color: '#333', fontSize: 14 }}>{question.questionText}</h4>                            {question.options.map((option, oIndex) => (
                                                <label key={oIndex} style={{ display: 'block', marginBottom: 6, cursor: 'pointer', fontSize: 13 }}>
                                                    <input
                                                        type={question.questionType === 'MULTIPLE_CHOICE' ? 'checkbox' : 'radio'}
                                                        name={`survey_${msg.surveyId}_question_${qIndex}`}
                                                        value={option}
                                                        checked={answers[question.questionText]?.includes(option) || false}
                                                        onChange={() => handleAnswerSelect(question.questionText, option, question.questionType === 'MULTIPLE_CHOICE')}
                                                        style={{ marginRight: 8 }}
                                                    />
                                                    {option}
                                                </label>
                                            ))}
                                        </div>
                                    ))}

                                    {surveyData.questions.some(q => q.allowAnonymous) && (
                                        <label style={{ display: 'block', marginBottom: 12, cursor: 'pointer', fontSize: 13 }}>
                                            <input
                                                type="checkbox"
                                                checked={isAnonymous}
                                                onChange={(e) => setIsAnonymous(e.target.checked)}
                                                style={{ marginRight: 8 }}
                                            />
                                            匿名で回答する
                                        </label>
                                    )}

                                    <button
                                        onClick={handleSubmitAnswer}
                                        disabled={submitting}
                                        style={{
                                            padding: '8px 16px',
                                            borderRadius: 6,
                                            border: 'none',
                                            background: '#2196f3',
                                            color: 'white',
                                            cursor: submitting ? 'not-allowed' : 'pointer',
                                            opacity: submitting ? 0.6 : 1,
                                            fontSize: 14,
                                            fontWeight: 'bold'
                                        }}
                                    >
                                        {submitting ? '送信中...' : '📝 回答を送信'}
                                    </button>
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

// メモ化したSurveyMessageCardをエクスポート
export default React.memo(SurveyMessageCard, (prevProps, nextProps) => {
    // surveyIdが同じなら再レンダリングしない（パフォーマンス最適化）
    return prevProps.msg.surveyId === nextProps.msg.surveyId &&
        prevProps.currentUserId === nextProps.currentUserId
})
