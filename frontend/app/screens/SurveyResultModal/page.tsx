'use client'

import { Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import SurveyResultModal from '../SurveyResultModal'

function SurveyResultModalContent() {
    const searchParams = useSearchParams()
    const surveyId = searchParams.get('surveyId') || '1'

    const handleClose = () => {
        window.history.back()
    }

    return (
        <SurveyResultModal
            open={true}
            surveyId={surveyId}
            onClose={handleClose}
        />
    )
}

export default function SurveyResultModalPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <SurveyResultModalContent />
        </Suspense>
    )
}
