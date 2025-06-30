"use client"
import React, { useState } from 'react'
import { useRouter } from 'next/navigation'
import apiClient from '@/lib/apiClient'

export default function CreateSurveyPage() {
  const router = useRouter()
  const [roomId, setRoomId] = useState('')
  const [title, setTitle] = useState('')
  const [options, setOptions] = useState<string[]>(['', ''])
  const [isMultiple, setIsMultiple] = useState(false)
  const [isAnonymous, setIsAnonymous] = useState(false)
  const [allowAddOptions, setAllowAddOptions] = useState(false)
  const [endDateTime, setEndDateTime] = useState('')

  const handleOptionChange = (index: number, value: string) => {
    const newOpts = [...options]
    newOpts[index] = value
    setOptions(newOpts)
  }

  const addOptionField = () => {
    setOptions([...options, ''])
  }

  const handleSubmit = async () => {
    try {
      const questions = [{
        questionText: title,
        options,
        questionType: isMultiple ? 'MULTIPLE_CHOICE' : 'SINGLE_CHOICE',
        allowAnonymous: isAnonymous,
        allowAddOptions
      }]
      const requestBody = {
        roomId,
        title,
        questions
      }
      const response = await apiClient.post('/surveys', requestBody)
      const surveyId = response.data
      router.push(`/surveys/${surveyId}/answer`)
    } catch (err) {
      console.error(err)
      alert('アンケートの作成に失敗しました')
    }
  }

  return (
    <div style={{ padding: 20 }}>
      <h1>アンケート作成</h1>
      <div>
        <label>Room ID: <input value={roomId} onChange={e => setRoomId(e.target.value)} /></label>
      </div>
      <div>
        <label>タイトル：<input value={title} onChange={e => setTitle(e.target.value)} /></label>
      </div>
      <div>
        <p>選択肢：</p>
        {options.map((opt, idx) => (
          <div key={idx}>
            <input
              value={opt}
              onChange={e => handleOptionChange(idx, e.target.value)}
              placeholder={`選択肢${idx + 1}`}
            />
          </div>
        ))}
        <button type="button" onClick={addOptionField}>＋</button>
      </div>
      <div>
        <label>投票終了日時：
          <input
            type="datetime-local"
            value={endDateTime}
            onChange={e => setEndDateTime(e.target.value)}
          />
        </label>
      </div>
      <div>
        <label><input type="checkbox" checked={isMultiple} onChange={e => setIsMultiple(e.target.checked)} /> 複数選択可</label>
        <label><input type="checkbox" checked={isAnonymous} onChange={e => setIsAnonymous(e.target.checked)} /> 匿名投票</label>
        <label><input type="checkbox" checked={allowAddOptions} onChange={e => setAllowAddOptions(e.target.checked)} /> 選択肢の追加を許可</label>
      </div>
      <div>
        <button type="button" onClick={() => router.back()}>キャンセル</button>
        <button type="button" onClick={handleSubmit}>作成</button>
      </div>
    </div>
  )
}
