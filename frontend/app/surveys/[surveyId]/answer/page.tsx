"use client"
import React, { useState, useEffect } from 'react'
import { useRouter, useParams } from 'next/navigation'
import apiClient from '@/lib/apiClient'

export default function AnswerSurveyPage() {
  const router = useRouter()
  const params = useParams() as { surveyId: string }
  const surveyId = params.surveyId
  const [survey, setSurvey] = useState<any>(null)
  const [questions, setQuestions] = useState<any[]>([])
  const [answers, setAnswers] = useState<Record<string, string[]>>({})
  const [isAnonymous, setIsAnonymous] = useState(false)

  useEffect(() => {
    async function fetchFormat() {
      try {
        const res = await apiClient.get(`/surveys/${surveyId}/format`)
        setSurvey(res.data)
        const qs = res.data.questions || []
        setQuestions(qs)
        const init: Record<string, string[]> = {}
        qs.forEach((q: any) => { init[q.questionText] = [] })
        setAnswers(init)
      } catch (e) {
        console.error(e)
        alert('アンケートを取得できませんでした')
      }
    }
    fetchFormat()
  }, [surveyId])

  const handleSelect = (questionText: string, option: string, multiple: boolean) => {
    setAnswers(prev => {
      const arr = prev[questionText] || []
      let newArr = [] as string[]
      if (multiple) {
        if (arr.includes(option)) newArr = arr.filter(x => x !== option)
        else newArr = [...arr, option]
      } else {
        newArr = [option]
      }
      return { ...prev, [questionText]: newArr }
    })
  }

  const handleSubmit = async () => {
    try {
      let userId = localStorage.getItem('userId') || ''
      if (!userId) userId = '00000000-0000-0000-0000-000000000000'
      await apiClient.post(`/surveys/${surveyId}/answers`, { userId, answers, isAnonymous })
      router.push(`/surveys/${surveyId}/results`)
    } catch (e) {
      console.error(e)
      alert('回答の送信に失敗しました')
    }
  }

  if (!survey) return <div>Loading...</div>

  return (
    <div style={{ padding: 20 }}>
      <h1>{survey.title}</h1>
      {questions.map((q, idx) => (
        <div key={idx} style={{ marginBottom: 16 }}>
          <p>{q.questionText}</p>
          {q.options.map((opt: string, i: number) => (
            <label key={i} style={{ display: 'block' }}>
              <input
                type={q.questionType === 'MULTIPLE_CHOICE' ? 'checkbox' : 'radio'}
                name={q.questionText}
                value={opt}
                checked={answers[q.questionText]?.includes(opt)}
                onChange={() => handleSelect(q.questionText, opt, q.questionType === 'MULTIPLE_CHOICE')}
              /> {opt}
            </label>
          ))}
        </div>
      ))}
      {survey.questions.length > 0 && survey.questions[0].allowAnonymous && (
        <label>
          <input type="checkbox" checked={isAnonymous} onChange={e => setIsAnonymous(e.target.checked)} /> 匿名で回答
        </label>
      )}
      <div style={{ marginTop: 20 }}>
        <button type="button" onClick={handleSubmit}>回答する</button>
      </div>
    </div>
  )
}
