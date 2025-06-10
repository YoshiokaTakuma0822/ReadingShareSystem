"use client"

import { ActiveUsersList, ChatInput, MessageList, UserAvatar } from "@/components/chat"
import { TokenStatus } from "@/components/auth/TokenStatus"
import { AuthProvider, useAuth } from "@/lib/AuthContext"
import { ChatProvider, useChatContext } from "@/lib/ChatContext"

function ChatContent() {
    const { isConnecting } = useChatContext()

    if (isConnecting) {
        return (
            <div className="flex items-center justify-center flex-1 text-gray-500">
                接続中...
            </div>
        )
    }

    return (
        <>
            <ActiveUsersList />
            <MessageList />
            <ChatInput />
        </>
    )
}

export default function ChatPage() {
    return (
        <AuthProvider>
            <AuthenticatedChat />
        </AuthProvider>
    )
}

function AuthenticatedChat() {
    const { member, isLoading } = useAuth()
    if (isLoading) {
        return (
            <div className="flex items-center justify-center flex-1 text-gray-500">
                認証中...
            </div>
        )
    }
    if (!member) {
        return <div>認証情報が見つかりませんでした</div>
    }
    return (
        <ChatProvider>
            <ChatPageContent />
        </ChatProvider>
    )
}

function ChatPageContent() {
    const { currentUser } = useChatContext()

    return (
        <main className="flex flex-col h-screen">
            <div className="p-4 border-b flex items-center justify-between">
                <h1 className="text-2xl font-bold">チャットルーム</h1>
                <div className="flex items-center space-x-2 relative">
                    <TokenStatus />
                    <UserAvatar user={currentUser} />
                </div>
            </div>
            <div className="flex-1 flex flex-col min-h-0">
                <ChatContent />
            </div>
        </main>
    )
}
