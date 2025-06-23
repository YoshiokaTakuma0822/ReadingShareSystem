import React, { useEffect } from 'react';

interface ChatNotificationProps {
  message: string;
  onClose: () => void;
  visible: boolean;
}

const ChatNotification: React.FC<ChatNotificationProps> = ({ message, onClose, visible }) => {
  useEffect(() => {
    if (visible) {
      const timer = setTimeout(() => {
        onClose();
      }, 4000); // 4秒後に自動で消える
      return () => clearTimeout(timer);
    }
  }, [visible, onClose]);

  return (
    <div
      style={{
        position: 'fixed',
        top: 24,
        left: 24,
        zIndex: 3000,
        minWidth: 220,
        maxWidth: 360,
        background: 'rgba(56,142,60,0.95)',
        color: '#fff',
        borderRadius: 10,
        boxShadow: '0 4px 16px rgba(0,0,0,0.18)',
        padding: '18px 28px',
        fontSize: 16,
        opacity: visible ? 1 : 0,
        transition: 'opacity 1.2s cubic-bezier(0.4,0,0.2,1)',
        pointerEvents: 'none',
      }}
    >
      {message}
    </div>
  );
};

export default ChatNotification;
