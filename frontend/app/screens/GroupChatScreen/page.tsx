'use client'

import { Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import GroupChatScreen from '../GroupChatScreen'

function GroupChatScreenContent() {
    const searchParams = useSearchParams()
    const roomId = searchParams.get('roomId')
    const roomTitle = searchParams.get('roomTitle')
    const currentUser = searchParams.get('currentUser')

    return (
        <GroupChatScreen
            roomId={roomId || undefined}
            roomTitle={roomTitle || undefined}
            currentUser={currentUser || undefined}
        />
    )
}

export default function GroupChatScreenPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <GroupChatScreenContent />
        </Suspense>
    )
}
