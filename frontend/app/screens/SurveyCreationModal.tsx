"use client"
import React, { useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { CreateSurveyRequest, Question, QuestionType } from '../../types/survey'

interface SurveyCreationModalProps {
    open: boolean
    roomId: string
    onClose: () => void
    onCreated: () => void
}

const SurveyCreationModal: React.FC<SurveyCreationModalProps> = ({ open, roomId, onClose, onCreated }) => {
    const [title, setTitle] = useState("今日はどこの章まで読むか")
    const [options, setOptions] = useState(["第1章", "第2章"])
    const [endDate, setEndDate] = useState("2026-01-01T12:00")
    const [multi, setMulti] = useState(false)
    const [anonymous, setAnonymous] = useState(false)
    const [allowAdd, setAllowAdd] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleCreate = async () => {
        setLoading(true)
        setError(null)
        try {
            const question: Question = {
                questionText: title,
                options: options.filter(opt => opt.trim() !== ''),
                questionType: multi ? 'MULTIPLE_CHOICE' : 'SINGLE_CHOICE',
                allowAnonymous: anonymous,
                allowAddOptions: allowAdd
            }

            const request: CreateSurveyRequest = {
                roomId,
                title,
                questions: [question]
            }

            await surveyApi.createSurvey(request)
            onCreated()
        } catch (e) {
            setError('アンケート作成に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    if (!open) return null

    return (
        <div
            style={{
                position: 'fixed',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                background: 'rgba(0, 0, 0, 0.5)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                zIndex: 1000
            }}
            onClick={onClose}
        >
            <div
                style={{
                    border: "2px solid var(--accent)",
                    background: "var(--background)",
                    borderRadius: 14,
                    width: 700,
                    padding: 36,
                    boxSizing: "border-box",
                    boxShadow: "0 4px 24px 0 rgba(0,0,0,0.1)",
                    maxHeight: '90vh',
                    overflowY: 'auto',
                    color: 'var(--text)'
                }}
                onClick={(e) => e.stopPropagation()}
            >
                <div style={{ fontWeight: "bold", fontSize: 28, marginBottom: 28, color: "var(--accent)" }}>アンケート作成</div>

                {error && <div style={{ color: 'var(--error)', marginBottom: 16, padding: 8, background: 'var(--error-background)', borderRadius: 4 }}>{error}</div>}

                <div style={{ display: "flex", alignItems: "center", marginBottom: 18 }}>
                    <span style={{ width: 90, color: "var(--accent)", fontWeight: 500 }}>・タイトル</span>
                    <input
                        value={title}
                        onChange={e => setTitle(e.target.value)}
                        style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid var(--border)", marginLeft: 8, color: 'var(--text)', background: 'var(--input)' }}
                    />
                </div>

                <div style={{ display: "flex", alignItems: "flex-start", marginBottom: 18 }}>
                    <span style={{ width: 90, marginTop: 8, color: "var(--accent)", fontWeight: 500 }}>・選択肢</span>
                    <div style={{ flex: 1, border: "2px solid var(--border)", borderRadius: 16, padding: 16, display: "flex", flexDirection: "column", gap: 8, marginLeft: 8, background: 'var(--card)' }}>
                        {options.map((opt, i) => (
                            <div key={i} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                                <span style={{ width: 28, textAlign: "right", color: 'var(--accent)', fontWeight: 500 }}>{i + 1}.</span>
                                <input
                                    value={opt}
                                    onChange={e => setOptions(options.map((o, j) => j === i ? e.target.value : o))}
                                    style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid var(--border)", color: 'var(--text)', background: 'var(--input)' }}
                                />
                                {options.length > 2 && (
                                    <button
                                        type="button"
                                        aria-label="選択肢を削除"
                                        style={{ width: 28, height: 28, borderRadius: "50%", border: "1.5px solid var(--error)", color: "var(--error)", background: "var(--background)", fontWeight: "bold", fontSize: 18, cursor: "pointer" }}
                                        onClick={() => setOptions(options.filter((_, j) => j !== i))}
                                    >
                                        ×
                                    </button>
                                )}
                            </div>
                        ))}
                        <button
                            type="button"
                            style={{ margin: "12px auto 0", width: 36, height: 36, borderRadius: "50%", border: "1.5px solid var(--accent)", fontSize: 24, background: "var(--background)", color: 'var(--accent)', cursor: "pointer" }}
                            onClick={() => setOptions([...options, ""])}
                        >
                            ＋
                        </button>
                    </div>
                </div>

                <div style={{ display: "flex", alignItems: "center", marginBottom: 18 }}>
                    <span style={{ width: 90, color: "var(--accent)", fontWeight: 500 }}>・投票終了日時</span>
                    <input
                        type="datetime-local"
                        value={endDate}
                        onChange={e => setEndDate(e.target.value)}
                        style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid var(--border)", marginLeft: 8, color: 'var(--text)', background: 'var(--input)' }}
                    />
                </div>

                <div style={{ display: "flex", gap: 24, marginBottom: 24, marginLeft: 90 }}>
                    <label style={{ color: 'var(--accent)', fontWeight: 500 }}>
                        <input type="checkbox" checked={multi} onChange={e => setMulti(e.target.checked)} /> 複数選択可
                    </label>
                    <label style={{ color: 'var(--accent)', fontWeight: 500 }}>
                        <input type="checkbox" checked={anonymous} onChange={e => setAnonymous(e.target.checked)} /> 匿名投票
                    </label>
                    <label style={{ color: 'var(--accent)', fontWeight: 500 }}>
                        <input type="checkbox" checked={allowAdd} onChange={e => setAllowAdd(e.target.checked)} /> 選択肢の追加を許可
                    </label>
                </div>

                <div style={{ display: "flex", justifyContent: "flex-end", gap: 16 }}>
                    <button
                        onClick={onClose}
                        style={{ border: "1.5px solid var(--accent)", borderRadius: 4, background: "var(--background)", fontSize: 16, padding: "8px 24px", cursor: "pointer", color: 'var(--accent)', fontWeight: 600 }}
                    >
                        キャンセル
                    </button>
                    <button
                        onClick={handleCreate}
                        disabled={loading}
                        style={{
                            border: "1.5px solid var(--accent)",
                            borderRadius: 4,
                            background: loading ? "var(--background)" : "var(--accent)",
                            fontSize: 16,
                            padding: "8px 24px",
                            cursor: loading ? "not-allowed" : "pointer",
                            color: loading ? "var(--text-muted)" : 'white',
                            fontWeight: 600,
                            opacity: loading ? 0.6 : 1
                        }}
                    >
                        {loading ? '作成中...' : '作成'}
                    </button>
                </div>
            </div>
        </div>
    )
}

export default SurveyCreationModal
