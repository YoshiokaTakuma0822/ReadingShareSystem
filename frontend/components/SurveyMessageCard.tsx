"use client"

import React, { useEffect, useState } from 'react'
import { surveyApi } from '../lib/surveyApi'
import { Message } from '../types/message'
import { SubmitSurveyAnswerRequest, Survey, SurveyResult } from '../types/survey'

interface SurveyMessageCardProps {
    msg: Message
    isMine: boolean
    currentUserId: string | null
    onAnswerClick?: (surveyId: string) => void
    onResultClick?: (surveyId: string) => void
    onLoadingComplete?: () => void
}

const SurveyMessageCard: React.FC<SurveyMessageCardProps> = ({ msg, isMine, currentUserId, onAnswerClick, onResultClick, onLoadingComplete }) => {
    const [surveyData, setSurveyData] = useState<Survey | null>(null)
    const [loading, setLoading] = useState(true)
    const [hasAnswered, setHasAnswered] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [showAnswer, setShowAnswer] = useState(false)
    const [showResults, setShowResults] = useState(false)
    const [answers, setAnswers] = useState<Record<string, string[]>>({})
    const [isAnonymous, setIsAnonymous] = useState(false)
    const [submitting, setSubmitting] = useState(false)
    const [results, setResults] = useState<SurveyResult | null>(null)
    const [loadingResults, setLoadingResults] = useState(false)

    useEffect(() => {
        if (msg.surveyId) {
            surveyApi.getSurveyFormat(msg.surveyId)
                .then(data => {
                    setSurveyData(data)
                    // 回答の初期化
                    const initAnswers: Record<string, string[]> = {}
                    data.questions.forEach(q => {
                        initAnswers[q.questionText] = []
                    })
                    setAnswers(initAnswers)
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

    const handleAnswerSelect = (questionText: string, option: string, isMultiple: boolean) => {
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

            return { ...prev, [questionText]: newAnswers }
        })
    }

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
            setShowAnswer(false)
            alert('回答が送信されました')
        } catch (error) {
            console.error('回答送信エラー:', error)
            alert('回答の送信に失敗しました')
        } finally {
            setSubmitting(false)
        }
    }

    const handleShowResults = async () => {
        if (!msg.surveyId) return

        setLoadingResults(true)
        try {
            const result = await surveyApi.getSurveyResult(msg.surveyId)
            setResults(result)
            setShowResults(true)
        } catch (error) {
            console.error('結果取得エラー:', error)
            alert('結果の取得に失敗しました')
        } finally {
            setLoadingResults(false)
        }
    }

    const handleAnswerClick = () => {
        if (onAnswerClick && msg.surveyId) {
            onAnswerClick(msg.surveyId)
        } else {
            setShowAnswer(true)
        }
    }

    const handleResultClick = () => {
        if (onResultClick && msg.surveyId) {
            onResultClick(msg.surveyId)
        } else {
            handleShowResults()
        }
    }

    return (
        <>
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
                                            <button onClick={handleResultClick} style={{ flex: 1, padding: '12px 24px', borderRadius: 8, border: 'none', fontSize: 16, background: '#4caf50', color: 'white', cursor: 'pointer', fontWeight: 'bold', boxShadow: '0 2px 4px rgba(76,175,80,0.3)' }}>
                                                📊 結果を表示
                                            </button>
                                        ) : (
                                            <button onClick={handleAnswerClick} style={{ flex: 1, padding: '12px 24px', borderRadius: 8, border: 'none', fontSize: 16, background: '#2196f3', color: 'white', cursor: 'pointer', fontWeight: 'bold', boxShadow: '0 2px 4px rgba(33,150,243,0.3)' }}>
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

            {/* 回答モーダル */}
            {showAnswer && surveyData && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                    <div style={{ background: 'white', borderRadius: 12, padding: 24, maxWidth: 600, width: '90%', maxHeight: '80vh', overflowY: 'auto' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                            <h2 style={{ margin: 0, color: '#1976d2' }}>📝 アンケートに回答</h2>
                            <button onClick={() => setShowAnswer(false)} style={{ background: 'none', border: 'none', fontSize: 24, cursor: 'pointer' }}>×</button>
                        </div>
                        <h3 style={{ marginBottom: 20, color: '#333' }}>{surveyData.title}</h3>

                        {surveyData.questions.map((question, qIndex) => (
                            <div key={qIndex} style={{ marginBottom: 24, padding: 16, border: '1px solid #e0e0e0', borderRadius: 8 }}>
                                <h4 style={{ marginBottom: 12, color: '#333' }}>{question.questionText}</h4>
                                {question.options.map((option, oIndex) => (
                                    <label key={oIndex} style={{ display: 'block', marginBottom: 8, cursor: 'pointer' }}>
                                        <input
                                            type={question.questionType === 'MULTIPLE_CHOICE' ? 'checkbox' : 'radio'}
                                            name={question.questionText}
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
                            <label style={{ display: 'block', marginBottom: 20, cursor: 'pointer' }}>
                                <input
                                    type="checkbox"
                                    checked={isAnonymous}
                                    onChange={(e) => setIsAnonymous(e.target.checked)}
                                    style={{ marginRight: 8 }}
                                />
                                匿名で回答する
                            </label>
                        )}

                        <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
                            <button onClick={() => setShowAnswer(false)} style={{ padding: '12px 24px', borderRadius: 8, border: '1px solid #ccc', background: 'white', cursor: 'pointer' }}>
                                キャンセル
                            </button>
                            <button onClick={handleSubmitAnswer} disabled={submitting} style={{ padding: '12px 24px', borderRadius: 8, border: 'none', background: '#2196f3', color: 'white', cursor: submitting ? 'not-allowed' : 'pointer', opacity: submitting ? 0.6 : 1 }}>
                                {submitting ? '送信中...' : '回答を送信'}
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* 結果モーダル */}
            {showResults && results && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.5)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                    <div style={{ background: 'white', borderRadius: 12, padding: 24, maxWidth: 600, width: '90%', maxHeight: '80vh', overflowY: 'auto' }}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
                            <h2 style={{ margin: 0, color: '#4caf50' }}>📊 アンケート結果</h2>
                            <button onClick={() => setShowResults(false)} style={{ background: 'none', border: 'none', fontSize: 24, cursor: 'pointer' }}>×</button>
                        </div>
                        <h3 style={{ marginBottom: 8, color: '#333' }}>{results.title}</h3>
                        <p style={{ marginBottom: 20, color: '#666', fontSize: 14 }}>回答者数: {results.totalRespondents}人</p>

                        {results.results.map((questionResult, qIndex) => (
                            <div key={qIndex} style={{ marginBottom: 24, padding: 16, border: '1px solid #e0e0e0', borderRadius: 8 }}>
                                <h4 style={{ marginBottom: 12, color: '#333' }}>{questionResult.questionText}</h4>
                                <div style={{ fontSize: 14 }}>
                                    {Object.entries(questionResult.votes).map(([option, count]) => {
                                        const percentage = results.totalRespondents > 0 ? ((count / results.totalRespondents) * 100).toFixed(1) : '0'
                                        return (
                                            <div key={option} style={{ marginBottom: 8 }}>
                                                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 4 }}>
                                                    <span>{option}</span>
                                                    <span>{count}票 ({percentage}%)</span>
                                                </div>
                                                <div style={{ background: '#f0f0f0', borderRadius: 4, height: 20, overflow: 'hidden' }}>
                                                    <div style={{ background: '#4caf50', height: '100%', width: `${percentage}%`, transition: 'width 0.3s ease' }}></div>
                                                </div>
                                            </div>
                                        )
                                    })}
                                </div>
                            </div>
                        ))}

                        <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                            <button onClick={() => setShowResults(false)} style={{ padding: '12px 24px', borderRadius: 8, border: 'none', background: '#4caf50', color: 'white', cursor: 'pointer' }}>
                                閉じる
                            </button>
                        </div>
                    </div>
                </div>
            )}

            {/* ローディングオーバーレイ */}
            {loadingResults && (
                <div style={{ position: 'fixed', top: 0, left: 0, right: 0, bottom: 0, background: 'rgba(0,0,0,0.3)', display: 'flex', alignItems: 'center', justifyContent: 'center', zIndex: 1000 }}>
                    <div style={{ background: 'white', borderRadius: 8, padding: 20, display: 'flex', alignItems: 'center', gap: 12 }}>
                        <div style={{ width: 20, height: 20, border: '2px solid #f3f3f3', borderTop: '2px solid #4caf50', borderRadius: '50%', animation: 'spin 1s linear infinite' }}></div>
                        <span>結果を読み込み中...</span>
                    </div>
                </div>
            )}
        </>
    )
}

export default SurveyMessageCard
