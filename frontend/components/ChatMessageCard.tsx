"use client"

import React from 'react'
import { Message } from '../types/message'

interface ChatMessageCardProps {
    msg: Message
    isMine: boolean
    showAvatar?: boolean
    showTime?: boolean
    showName?: boolean
}

const ChatMessageCard: React.FC<ChatMessageCardProps> = ({ msg, isMine, showAvatar = true, showTime = true, showName = true }) => (
    <div style={{ display: 'flex', alignItems: 'flex-start', gap: 8, justifyContent: 'flex-start' }}>
        <span style={{
            borderRadius: '50%', width: 32, height: 32, marginTop: -4,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            background: showAvatar ? (isMine ? '#bbdefb' : '#c8e6c9') : 'transparent',
            visibility: showAvatar ? 'visible' : 'hidden'
        }}>
            {showAvatar ? (msg.user ? String(msg.user).trim().charAt(0) : '?') : null}
        </span>
        {/* Discord風: ユーザー名＋時間、その下にメッセージ */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 4, flex: 1 }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                {showName && <span>{msg.user}</span>}
                {msg.sentAt && showTime && (
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
