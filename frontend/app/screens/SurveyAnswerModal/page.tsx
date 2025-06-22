'use client'

import { Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import SurveyAnswerModal from '../SurveyAnswerModal'

function SurveyAnswerModalContent() {
    const searchParams = useSearchParams()
    const surveyId = searchParams.get('surveyId') || '1'

    const handleClose = () => {
        window.history.back()
    }

    const handleAnswered = () => {
        window.history.back()
    }

    return (
        <SurveyAnswerModal
            open={true}
            surveyId={surveyId}
            onClose={handleClose}
            onAnswered={handleAnswered}
        />
    )
}

export default function SurveyAnswerModalPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <SurveyAnswerModalContent />
        </Suspense>
    )
}
