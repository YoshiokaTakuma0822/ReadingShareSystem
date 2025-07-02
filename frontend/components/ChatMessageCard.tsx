"use client"

import React from 'react'
import { Message } from '../types/message'

interface ChatMessageCardProps {
    msg: Message
    isMine: boolean
}

const ChatMessageCard: React.FC<ChatMessageCardProps> = ({ msg, isMine }) => (
    <div style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: isMine ? 'flex-end' : 'flex-start' }}>
        {!isMine && (
            <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                {msg.user ? String(msg.user).trim().charAt(0) : '?'}
            </span>
        )}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            {isMine && msg.sentAt && (
                <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'right' }}>
                    {new Date(msg.sentAt).toLocaleTimeString()}
                </span>
            )}
            <div style={{ border: '1px solid #222', borderRadius: 16, padding: 8, background: isMine ? '#e0f7fa' : '#fff', maxWidth: 600, wordBreak: 'break-word' }}>
                {String(msg.text)}
            </div>
            {!isMine && msg.sentAt && (
                <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'left' }}>
                    {new Date(msg.sentAt).toLocaleTimeString()}
                </span>
            )}
        </div>
        {isMine && (
            <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>
                {msg.user ? String(msg.user).trim().charAt(0) : '?'}
            </span>
        )}
    </div>
)

export default ChatMessageCard
