import React from 'react'
import GroupChatScreen from '../../../screens/GroupChatScreen'

interface ChatPageProps {
    params: Promise<{
        roomId: string
    }>
}

export default function ChatPage({ params }: ChatPageProps) {
    const { roomId } = React.use(params)
    return <GroupChatScreen roomId={roomId} />
}
