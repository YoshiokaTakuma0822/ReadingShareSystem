import { Member } from "@/types/auth"
import { MessageResponse, MemberMap } from "@/types/chat"
import { createContext, ReactNode, useContext } from "react"
import { useChat } from "./useChat"

interface ChatContextType {
    messages: MessageResponse[]
    activeUsers: Member[]
    members: MemberMap
    isConnecting: boolean
    sendMessage: (content: string) => void
    loadOlderMessages: (limit?: number) => Promise<void>
    currentUser: Member | null // 現在のユーザー
    currentRoomId: number
    switchRoom: (roomId: number) => Promise<void>
}

export const ChatContext = createContext<ChatContextType | null>(null)

export function useChatContext() {
    const context = useContext(ChatContext)
    if (!context) {
        throw new Error("useChatContext must be used within a ChatProvider")
    }
    return context
}

interface ChatProviderProps {
    children: ReactNode
}

export function ChatProvider({ children }: ChatProviderProps) {
    const chat = useChat()

    return <ChatContext.Provider value={chat as ChatContextType}>{children}</ChatContext.Provider>
}
