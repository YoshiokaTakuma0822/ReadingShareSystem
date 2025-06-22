'use client'

import { Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import SurveyCreationModal from '../SurveyCreationModal'

function SurveyCreationModalContent() {
    const searchParams = useSearchParams()
    const roomId = searchParams.get('roomId') || '1'

    const handleClose = () => {
        window.history.back()
    }

    const handleCreated = () => {
        window.history.back()
    }

    return (
        <SurveyCreationModal
            open={true}
            roomId={roomId}
            onClose={handleClose}
            onCreated={handleCreated}
        />
    )
}

export default function SurveyCreationModalPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <SurveyCreationModalContent />
        </Suspense>
    )
}
