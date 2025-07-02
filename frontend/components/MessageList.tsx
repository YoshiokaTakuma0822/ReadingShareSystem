"use client"

import React from 'react'
import { Message } from '../types/message'
import ChatMessageCard from './ChatMessageCard'
import SurveyMessageCard from './SurveyMessageCard'

interface MessageListProps {
    messages: Message[]
    loading: boolean
    currentUserId: string | null
    onAnswerClick: (surveyId: string) => void
    onResultClick: (surveyId: string) => void
    onLoadingComplete: (messageId: number) => void
}

const MessageList: React.FC<MessageListProps> = ({ messages, loading, currentUserId, onAnswerClick, onResultClick, onLoadingComplete }) => {
    if (loading) {
        return (
            <div key="loading" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: '#666', fontSize: 16 }}>
                チャット履歴を読み込み中...
            </div>
        )
    }
    if (messages.length === 0) {
        return (
            <div key="empty" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%', color: '#999', fontSize: 16 }}>
                まだメッセージがありません
            </div>
        )
    }
    return (
        <>
            {messages.map(msg => {
                const isMine = msg.isCurrentUser
                if (msg.messageType === 'SURVEY') {
                    return (
                        <SurveyMessageCard
                            key={msg.uuid}
                            msg={msg}
                            isMine={isMine}
                            currentUserId={currentUserId}
                            onAnswerClick={onAnswerClick}
                            onResultClick={onResultClick}
                            onLoadingComplete={() => onLoadingComplete(msg.id)}
                        />
                    )
                }
                return <ChatMessageCard key={msg.uuid} msg={msg} isMine={isMine} />
            })}
        </>
    )
}

export default MessageList
