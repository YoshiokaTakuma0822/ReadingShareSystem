"use client"

import React from 'react'
import { Message } from '../types/message'

interface ChatMessageCardProps {
    msg: Message
    isMine: boolean
}

const ChatMessageCard: React.FC<ChatMessageCardProps> = ({ msg, isMine }) => (
    <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8, justifyContent: isMine ? 'flex-end' : 'flex-start' }}>
        {/* アイコン (左固定) */}
        <span style={{ borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: isMine ? '#bbdefb' : '#c8e6c9' }}>
            {msg.user ? String(msg.user).trim().charAt(0) : '?'}
        </span>
        {/* Discord風: ユーザー名＋時間、その下にメッセージ */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 4, flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                <span style={{}}>{msg.user}</span>
                {msg.sentAt && (
                    <span style={{ fontSize: '0.8em', color: '#888' }}>
                        {new Date(msg.sentAt).toLocaleTimeString()}
                    </span>
                )}
            </div>
            <div style={{ wordBreak: 'break-word' }}>
                {String(msg.text)}
            </div>
        </div>
    </div>
)

export default ChatMessageCard
