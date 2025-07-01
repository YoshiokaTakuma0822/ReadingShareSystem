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
                    border: '4px solid #2196f3',
                    padding: 32,
                    maxWidth: 700,
                    width: '90%',
                    background: '#f8f9ff',
                    borderRadius: 12,
                    boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
                    maxHeight: '80vh',
                    overflowY: 'auto'
                }}
                onClick={(e) => e.stopPropagation()}
            >
                <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 24 }}>
                    <h2 style={{ fontSize: 28, color: '#1976d2', margin: 0, display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span>📊</span>
                        {surveyResult?.title || 'アンケート結果'}
                    </h2>
                    <button
                        onClick={onClose}
                        style={{
                            background: 'none',
                            border: 'none',
                            fontSize: 24,
                            cursor: 'pointer',
                            color: '#666',
                            padding: 8,
                            borderRadius: 4
                        }}
                    >
                        ✕
                    </button>
                </div>

                {loading ? (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: 200,
                        color: '#666',
                        fontSize: 16
                    }}>
                        結果を読み込み中...
                    </div>
                ) : error ? (
                    <div style={{
                        color: '#d32f2f',
                        marginBottom: 16,
                        padding: 16,
                        background: '#ffebee',
                        borderRadius: 8,
                        border: '1px solid #ffcdd2'
                    }}>
                        {error}
                    </div>
                ) : surveyResult ? (
                    <div>
                        <div style={{
                            background: '#e3f2fd',
                            padding: 16,
                            borderRadius: 8,
                            marginBottom: 24,
                            border: '1px solid #bbdefb'
                        }}>
                            <p style={{ color: '#1565c0', margin: 0, fontSize: 16 }}>
                                回答者数: <strong>{surveyResult.totalRespondents}人</strong>
                            </p>
                        </div>

                        {surveyResult.results.map((questionResult, index) => {
                            const votes = questionResult.votes || questionResult.answers || {}
                            const totalVotes = Object.values(votes).reduce((sum: number, count) => sum + (typeof count === 'number' ? count : 0), 0)

                            return (
                                <div key={index} style={{
                                    marginBottom: 32,
                                    padding: 20,
                                    background: 'white',
                                    borderRadius: 8,
                                    border: '1px solid #e0e0e0',
                                    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                                }}>
                                    <h4 style={{
                                        color: '#333',
                                        marginBottom: 16,
                                        fontSize: 18,
                                        fontWeight: 'bold'
                                    }}>
                                        {questionResult.questionText}
                                    </h4>

                                    <div style={{ marginBottom: 12 }}>
                                        {Object.entries(votes).map(([option, count]) => {
                                            const voteCount = typeof count === 'number' ? count : 0
                                            const percentage = totalVotes > 0 ? Math.round((voteCount / totalVotes) * 100) : 0

                                            return (
                                                <div key={option} style={{ marginBottom: 12 }}>
                                                    <div style={{
                                                        display: 'flex',
                                                        justifyContent: 'space-between',
                                                        alignItems: 'center',
                                                        marginBottom: 4
                                                    }}>
                                                        <span style={{ fontSize: 16, color: '#333' }}>{option}</span>
                                                        <span style={{
                                                            fontSize: 14,
                                                            color: '#666',
                                                            fontWeight: 'bold'
                                                        }}>
                                                            {voteCount}票 ({percentage}%)
                                                        </span>
                                                    </div>
                                                    <div style={{
                                                        background: '#f5f5f5',
                                                        height: 24,
                                                        borderRadius: 12,
                                                        overflow: 'hidden',
                                                        position: 'relative'
                                                    }}>
                                                        <div style={{
                                                            background: 'linear-gradient(135deg, #42a5f5 0%, #1976d2 100%)',
                                                            height: '100%',
                                                            width: `${percentage}%`,
                                                            borderRadius: 12,
                                                            transition: 'width 0.3s ease-in-out'
                                                        }} />
                                                    </div>
                                                </div>
                                            )
                                        })}
                                    </div>

                                    <div style={{
                                        fontSize: 14,
                                        color: '#666',
                                        fontStyle: 'italic',
                                        textAlign: 'right'
                                    }}>
                                        合計: {totalVotes}票
                                    </div>
                                </div>
                            )
                        })}
                    </div>
                ) : (
                    <div style={{
                        color: '#666',
                        fontStyle: 'italic',
                        textAlign: 'center',
                        padding: 40
                    }}>
                        アンケート結果がありません
                    </div>
                )}

                <div style={{
                    display: 'flex',
                    justifyContent: 'center',
                    marginTop: 24,
                    paddingTop: 16,
                    borderTop: '1px solid #e0e0e0'
                }}>
                    <button
                        onClick={onClose}
                        style={{
                            padding: '12px 32px',
                            border: '2px solid #1976d2',
                            borderRadius: 8,
                            background: '#1976d2',
                            color: 'white',
                            fontSize: 16,
                            cursor: 'pointer',
                            fontWeight: 'bold'
                        }}
                    >
                        閉じる
                    </button>
                </div>
            </div>
        </div>
    )
}

export default SurveyResultModal
