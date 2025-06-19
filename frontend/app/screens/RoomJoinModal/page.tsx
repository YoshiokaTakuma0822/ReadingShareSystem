'use client'

import { useState, useEffect, Suspense } from 'react'
import { useSearchParams } from 'next/navigation'
import RoomJoinModal from '../RoomJoinModal'
import { Room } from '../../../types/room'

function RoomJoinModalContent() {
    const [userId, setUserId] = useState<string>('')
    const searchParams = useSearchParams()

    // デフォルトのルーム情報（実際のアプリではAPIから取得）
    const defaultRoom: Room = {
        id: searchParams.get('roomId') || '1',
        roomName: searchParams.get('roomName') || 'サンプルルーム',
        hostUserId: searchParams.get('hostUserId') || 'host1',
        bookTitle: searchParams.get('bookTitle') || 'サンプル本',
        hasPassword: searchParams.get('hasPassword') === 'true',
        createdAt: new Date().toISOString()
    }

    useEffect(() => {
        const storedUserId = localStorage.getItem('reading-share-user-id')
        if (storedUserId) {
            setUserId(storedUserId)
        }
    }, [])

    const handleClose = () => {
        window.history.back()
    }

    const handleJoined = () => {
        window.location.href = `/rooms/${defaultRoom.id}/chat`
    }

    if (!userId) {
        return <div>Loading...</div>
    }

    return (
        <RoomJoinModal
            open={true}
            room={defaultRoom}
            userId={userId}
            onClose={handleClose}
            onJoined={handleJoined}
        />
    )
}

export default function RoomJoinModalPage() {
    return (
        <Suspense fallback={<div>Loading...</div>}>
            <RoomJoinModalContent />
        </Suspense>
    )
}
