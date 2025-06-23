/**
 * アンケート結果モーダル画面コンポーネント
 *
 * @author 02001
 * @componentId C1
 * @moduleName アンケート結果モーダル画面
 * @packageDocumentation
 */

"use client"
import React, { useEffect, useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { SurveyResult } from '../../types/survey'

interface SurveyResultModalProps {
    open: boolean
    surveyId: string
    onClose: () => void
}

/**
 * SurveyResultModal コンポーネント: アンケート結果表示モーダルを表示する Functional Component
 *
 * @returns JSX.Element | null 結果表示モーダルのReact要素 または非表示時はnull
 */
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
                                {result.votes && Object.entries(result.votes).length > 0 ? (
                                    Object.entries(result.votes).map(([option, votes]) => (
                                        <div key={option} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 8 }}>
                                            <span>{option}</span>
                                            <span>{votes}票</span>
                                        </div>
                                    ))
                                ) : (
                                    <div>回答がありません</div>
                                )}
                            </div>
                        ))}
                    </div>
                ) : (
                    <div>結果がありません</div>
                )}
                <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                    <button
                        onClick={onClose}
                        style={{
                            padding: '12px 24px',
                            border: '2px solid #388e3c',
                            borderRadius: 8,
                            background: '#388e3c',
                            color: 'white',
                            fontSize: 16,
                            cursor: 'pointer'
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
