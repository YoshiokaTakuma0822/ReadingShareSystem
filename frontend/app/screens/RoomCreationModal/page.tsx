'use client'

import { useState, useEffect } from 'react'
import RoomCreationModal from '../RoomCreationModal'

export default function RoomCreationModalPage() {
    const [userId, setUserId] = useState<string>('')

    useEffect(() => {
        // ローカルストレージからユーザーIDを取得
        const storedUserId = localStorage.getItem('reading-share-user-id')
        if (storedUserId) {
            setUserId(storedUserId)
        }
    }, [])

    const handleClose = () => {
        // モーダルを閉じる際の処理（例：前のページに戻る）
        window.history.back()
    }

    const handleCreated = () => {
        // ルーム作成後の処理（例：ホーム画面に戻る）
        window.location.href = '/'
    }

    if (!userId) {
        return <div>Loading...</div>
    }

    return (
        <RoomCreationModal
            open={true}
            userId={userId}
            onClose={handleClose}
            onCreated={handleCreated}
        />
    )
}
