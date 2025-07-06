"use client"

import React from 'react'
import { Message } from '../types/message'

interface ChatMessageCardProps {
    msg: Message
    isMine: boolean
}

const ChatMessageCard: React.FC<ChatMessageCardProps> = ({ msg, isMine }) => (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: isMine ? 'flex-end' : 'flex-start' }}>
        {/* アイコン (左固定) */}
        <span style={{ borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: isMine ? '#ffc107' : '#fff' }}>
            {msg.user ? String(msg.user).trim().charAt(0) : '?'}
        </span>
        {/* メッセージとタイムスタンプ */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8, flex: 1, justifyContent: 'flex-start' }}>
            <div style={{ padding: 8, maxWidth: 600, wordBreak: 'break-word' }}>
                {String(msg.text)}
            </div>
            {msg.sentAt && (
                <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'right' }}>
                    {new Date(msg.sentAt).toLocaleTimeString()}
                </span>
            )}
        </div>
    </div>
)

export default ChatMessageCard
