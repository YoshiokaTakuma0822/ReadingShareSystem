"use client"
import React, { useState, useEffect } from 'react'
import { useRouter, useParams } from 'next/navigation'
import apiClient from '@/lib/apiClient'
import type { SurveyResult } from '@/types/survey'

export default function SurveyResultsPage() {
  const router = useRouter()
  const params = useParams() as { surveyId: string }
  const surveyId = params.surveyId
  const [result, setResult] = useState<SurveyResult | null>(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    async function fetchResults() {
      try {
        const res = await apiClient.get<SurveyResult>(`/surveys/${surveyId}/results`)
        setResult(res.data)
      } catch (e: any) {
        console.error(e)
        setError('結果を取得できませんでした')
      } finally {
        setLoading(false)
      }
    }
    fetchResults()
  }, [surveyId])

  if (loading) return <div>Loading...</div>
  if (error) return <div>{error}</div>
  if (!result) return <div>結果がありません</div>

  return (
    <div style={{ padding: 20 }}>
      <button onClick={() => router.back()} style={{ float: 'right' }}>×</button>
      <h1>{result.title}</h1>
      {result.results.map((qr, idx) => (
        <div key={idx} style={{ marginBottom: 24 }}>
          <h2>{qr.questionText}</h2>
          <ul style={{ listStyle: 'none', padding: 0 }}>
            {(Object.entries(qr.votes) as [string, number][]).map(([option, count]) => (
              <li key={option} style={{ display: 'flex', justifyContent: 'space-between', maxWidth: 400 }}>
                <span>{option}</span>
                <span>{count}票</span>
              </li>
            ))}
          </ul>
        </div>
      ))}
    </div>
  )
}
