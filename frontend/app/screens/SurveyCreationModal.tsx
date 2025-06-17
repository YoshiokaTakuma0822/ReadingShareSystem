"use client"
import React, { useState } from 'react'
import { surveyApi } from '../../lib/surveyApi'
import { CreateSurveyRequest, Question, QuestionType } from '../../types/survey'

interface SurveyCreationModalProps {
    open: boolean
    roomId: string // 追加: 部屋ID
    onClose: () => void
    onCreated: () => void
}

const SurveyCreationModal: React.FC<SurveyCreationModalProps> = ({ open, roomId, onClose, onCreated }) => {
    const [title, setTitle] = useState('')
    const [questionText, setQuestionText] = useState('')
    const [options, setOptions] = useState<string[]>(['', ''])
    const [questionType, setQuestionType] = useState<QuestionType>('SINGLE_CHOICE')
    const [allowAnonymous, setAllowAnonymous] = useState(false)
    const [allowAddOptions, setAllowAddOptions] = useState(false)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleAddOption = () => setOptions([...options, ''])
    const handleOptionChange = (idx: number, value: string) => {
        setOptions(options.map((opt, i) => (i === idx ? value : opt)))
    }
    const handleRemoveOption = (idx: number) => {
        setOptions(options.filter((_, i) => i !== idx))
    }

    const handleCreate = async () => {
        setLoading(true)
        setError(null)
        try {
            const question: Question = {
                questionText,
                options: options.filter(opt => opt.trim() !== ''),
                questionType,
                allowAnonymous,
                allowAddOptions
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
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 32, maxWidth: 700, background: '#f1fdf6', borderRadius: 8, boxShadow: '0 4px 24px #a5d6a7' }}>
            <h2 style={{ fontWeight: 'bold', fontSize: 28, marginBottom: 24, color: '#388e3c' }}>アンケート作成</h2>
            <div style={{ marginBottom: 16, color: '#388e3c', fontSize: 15, fontWeight: 500 }}>
                この画面は開いて確認できません（サンプルはホーム画面からご利用ください）
            </div>
            <div style={{ marginBottom: 16 }}>
                <label>タイトル</label>
                <input type="text" value={title} onChange={e => setTitle(e.target.value)} placeholder="今日はどこの章まで読むか" style={{ width: '100%', padding: 8, marginTop: 4 }} />
            </div>
            <div style={{ marginBottom: 16 }}>
                <label>質問</label>
                <input type="text" value={questionText} onChange={e => setQuestionText(e.target.value)} placeholder="質問内容を入力してください" style={{ width: '100%', padding: 8, marginTop: 4 }} />
            </div>
            <div style={{ marginBottom: 16 }}>
                <label>質問タイプ</label>
                <select value={questionType} onChange={e => setQuestionType(e.target.value as QuestionType)} style={{ width: '100%', padding: 8, marginTop: 4 }}>
                    <option value="SINGLE_CHOICE">単一選択</option>
                    <option value="MULTIPLE_CHOICE">複数選択</option>
                </select>
            </div>
            <div style={{ marginBottom: 16 }}>
                <label>選択肢</label>
                {options.map((opt, idx) => (
                    <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
                        <input type="text" value={opt} onChange={e => handleOptionChange(idx, e.target.value)} style={{ flex: 1, padding: 8 }} placeholder={`選択肢${idx + 1}`} />
                        {options.length > 2 && <button onClick={() => handleRemoveOption(idx)} style={{ border: '1px solid #222', borderRadius: 6 }}>削除</button>}
                    </div>
                ))}
                <button onClick={handleAddOption} style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, fontSize: 24, marginTop: 4 }}>＋</button>
            </div>
            <div style={{ display: 'flex', gap: 16, marginBottom: 16 }}>
                <label><input type="checkbox" checked={allowAnonymous} onChange={e => setAllowAnonymous(e.target.checked)} /> 匿名投票</label>
                <label><input type="checkbox" checked={allowAddOptions} onChange={e => setAllowAddOptions(e.target.checked)} /> 選択肢の追加を許可</label>
            </div>
            {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16 }}>
                <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>キャンセル</button>
                <button onClick={handleCreate} disabled={loading} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>{loading ? '作成中...' : '作成'}</button>
            </div>
        </div>
    )
}

export default SurveyCreationModal
