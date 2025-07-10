"use client"

import React, { useCallback, useEffect, useState } from 'react'
import { MdPoll } from 'react-icons/md'
import { surveyApi } from '../lib/surveyApi'
import { ApiErrorResponse, SurveyErrorCode } from '../types/error'
import { Message } from '../types/message'
import { SubmitSurveyAnswerRequest, Survey, SurveyResult } from '../types/survey'

interface SurveyMessageCardProps {
    msg: Message
    isMine: boolean
    currentUserId: string | null
    onLoadingComplete?: () => void
    showAvatar?: boolean  // add showAvatar prop
    // 外部からの再フェッチ用トリガー
    refreshTrigger?: number
}

const SurveyMessageCard: React.FC<SurveyMessageCardProps> = ({ msg, isMine, currentUserId, onLoadingComplete, showAvatar = true, refreshTrigger }) => {
    const [surveyData, setSurveyData] = useState<Survey | null>(null)
    const [loading, setLoading] = useState(true)
    const [hasAnswered, setHasAnswered] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [showingResults, setShowingResults] = useState(false)
    const [answers, setAnswers] = useState<Record<string, string[]>>({})
    const [isAnonymous, setIsAnonymous] = useState(false)
    const [submitting, setSubmitting] = useState(false)
    const [results, setResults] = useState<SurveyResult | null>(null)
    const [newOptionInputs, setNewOptionInputs] = useState<Record<string, string>>({})

    // アンケートが終了しているかどうかをチェックする関数
    const isExpired = useCallback(() => {
        if (!surveyData?.endTime) return false
        return new Date() > new Date(surveyData.endTime)
    }, [surveyData?.endTime])

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
    }, [answers, msg.surveyId, localStorageKey])

    const handleShowFormat = useCallback(async () => {
        if (!msg.surveyId) return
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
                    // 回答済みまたは期限切れなら自動的に結果を取得
                    if (answered || isExpired()) {
                        handleShowResults()
                    }
                })
                .catch(() => setHasAnswered(false))
        }
    }, [msg.surveyId, currentUserId, handleShowResults, isExpired])

    // アンケートの終了時刻をチェックして自動的に結果表示に移行
    useEffect(() => {
        if (surveyData?.endTime && !showingResults && !hasAnswered) {
            const endTime = new Date(surveyData.endTime)
            const now = new Date()

            if (now > endTime) {
                // 終了時刻を過ぎている場合は結果表示に移行
                handleShowResults()
                return
            }

            // 終了時刻まで待機するタイマーを設定
            const timeUntilEnd = endTime.getTime() - now.getTime()
            const timer = setTimeout(() => {
                handleShowResults()
            }, timeUntilEnd)

            return () => clearTimeout(timer)
        }
    }, [surveyData, showingResults, hasAnswered, handleShowResults])

    useEffect(() => {
        handleShowFormat()
        if (showingResults || hasAnswered || isExpired()) {
            handleShowResults()
        }
    }, [refreshTrigger, showingResults, hasAnswered, msg.surveyId, handleShowResults, isExpired])

    const handleAnswerSelect = useCallback((questionText: string, option: string, isMultiple: boolean) => {
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

            // ローカルストレージに即時保存（状態の永続化）
            const key = localStorageKey()
            if (key) {
                try {
                    localStorage.setItem(key, JSON.stringify(result))
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
            }
        } catch (error: any) {
            console.error('回答送信エラー:', error)
            // エラーコードで分岐して日本語メッセージを表示
            const data = error.response?.data as ApiErrorResponse | undefined
            if (data?.code === SurveyErrorCode.SURVEY_EXPIRED) {
                alert('アンケートの有効期限が切れています')
                // 終了している場合は結果表示に移行
                handleShowResults()
            } else {
                alert('回答の送信に失敗しました')
            }
        } finally {
            setSubmitting(false)
        }
    }

    const handleAddOption = async (questionText: string) => {
        const val = (newOptionInputs[questionText] || '').trim()
        if (!val || !surveyData) return
        setSubmitting(true)
        try {
            await surveyApi.addOption(surveyData.id, questionText, val)
            // 最新のアンケート情報を再取得
            const updated = await surveyApi.getSurveyFormat(surveyData.id)
            setSurveyData(updated)
            setNewOptionInputs(prev => ({ ...prev, [questionText]: '' }))
        } catch (e: any) {
            console.error('選択肢追加エラー:', e)
            // エラーコードで分岐して日本語メッセージを表示
            const data = e.response?.data as ApiErrorResponse | undefined
            if (data?.code === SurveyErrorCode.SURVEY_EXPIRED) {
                alert('アンケートの有効期限が切れています')
                // 終了している場合は結果表示に移行
                handleShowResults()
            } else {
                alert('選択肢の追加に失敗しました')
            }
        } finally {
            setSubmitting(false)
        }
    }

    return (
        <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8, justifyContent: 'flex-start', marginBottom: 12 }}>
            <span style={{
                borderRadius: '50%', width: 32, height: 32, marginTop: -4,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                background: showAvatar ? (isMine ? '#bbdefb' : '#c8e6c9') : 'transparent',
                visibility: showAvatar ? 'visible' : 'hidden'
            }}>
                {showAvatar && (msg.user ? String(msg.user).trim().charAt(0) : '?')}
            </span>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 4, flex: 1, alignItems: 'flex-start' }}>
                {showAvatar && (
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span>{msg.user}</span>
                        {msg.sentAt && <span style={{ fontSize: '0.8em', color: '#888' }}>{new Date(msg.sentAt).toLocaleTimeString()}</span>}
                    </div>
                )}
                <div style={{ border: '2px solid #2196f3', borderRadius: 12, padding: 16, background: 'linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%)', maxWidth: 500, minWidth: 300, boxShadow: '0 2px 8px rgba(33,150,243,0.2)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
                        <MdPoll style={{ fontSize: 25, color: '#1976d2', marginTop: -1.5 }} />
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

                            {/* 終了時刻表示 */}
                            {surveyData.endTime && (
                                <div style={{ marginBottom: 12, fontSize: 12, color: '#666' }}>
                                    終了時刻: {new Date(surveyData.endTime).toLocaleString()}
                                    {isExpired() && (
                                        <span style={{ color: '#d32f2f', fontWeight: 'bold', marginLeft: 8 }}>
                                            [終了済み]
                                        </span>
                                    )}
                                </div>
                            )}

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
                                    {!hasAnswered && !isExpired() && (
                                        <button onClick={() => setShowingResults(false)} style={{ padding: '8px 16px', borderRadius: 6, border: 'none', background: '#666', color: 'white', cursor: 'pointer', fontSize: 14 }}>
                                            質問を表示
                                        </button>
                                    )}
                                </div>
                            ) : hasAnswered ? (
                                /* 回答済み - 結果取得中 */
                                <div style={{ color: '#666', fontStyle: 'italic' }}>結果を読み込み中...</div>
                            ) : isExpired() ? (
                                /* アンケート終了済み */
                                <div style={{ color: '#d32f2f', fontWeight: 'bold', textAlign: 'center', padding: '16px' }}>
                                    このアンケートは終了しています
                                    <div style={{ marginTop: 8 }}>
                                        <button onClick={handleShowResults} style={{ padding: '8px 16px', borderRadius: 6, border: 'none', background: '#1976d2', color: 'white', cursor: 'pointer', fontSize: 14 }}>
                                            結果を表示
                                        </button>
                                    </div>
                                </div>
                            ) : (
                                /* 回答フォーム */
                                <div>
                                    {surveyData.questions.map((question, qIndex) => (
                                        <div key={qIndex} style={{ marginBottom: 16, padding: 12, border: '1px solid #e0e0e0', borderRadius: 8, background: 'white' }}>
                                            <h4 style={{ marginBottom: 12, color: '#333', fontSize: 14 }}>{question.questionText}</h4>
                                            {question.options.map((option, oIndex) => (
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
                                            {/* 選択肢追加UI */}
                                            {question.allowAddOptions && (
                                                <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginTop: 8 }}>
                                                    <input
                                                        type="text"
                                                        value={newOptionInputs?.[question.questionText] || ''}
                                                        onChange={e => setNewOptionInputs(prev => ({ ...prev, [question.questionText]: e.target.value }))}
                                                        placeholder="新しい選択肢を追加"
                                                        style={{ flex: 1, padding: 6, fontSize: 13, border: '1px solid #ccc' }}
                                                        disabled={submitting}
                                                    />
                                                    <button
                                                        type="button"
                                                        onClick={() => handleAddOption(question.questionText)}
                                                        style={{ padding: '6px 12px', borderRadius: 6, border: '1px solid #1976d2', background: '#e3f2fd', color: '#1976d2', fontWeight: 600, cursor: 'pointer' }}
                                                        disabled={submitting || !(newOptionInputs?.[question.questionText] || '').trim()}
                                                    >追加</button>
                                                </div>
                                            )}
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
        </div>
    )
}

// SurveyMessageCard を直接エクスポート
export default SurveyMessageCard
