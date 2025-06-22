'use client'

import { Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import ReadingScreen from '../ReadingScreen'

function ReadingScreenContent() {
    const searchParams = useSearchParams()
    const roomId = searchParams.get('roomId')

    return (
        <ReadingScreen
            roomId={roomId || undefined}
        />
    )
}

export default function ReadingScreenPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <ReadingScreenContent />
        </Suspense>
    )
}
