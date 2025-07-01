import React, { useEffect, useRef, useState } from 'react';
import ReadingScreen from './ReadingScreen';
import ChatNotification from './ChatNotification';

interface ReadingScreenOverlayProps {
  roomId?: string;
  open: boolean;
  onClose: () => void;
}

const ReadingScreenOverlay: React.FC<ReadingScreenOverlayProps> = ({ roomId, open, onClose }) => {
  const [notification, setNotification] = useState<string | null>(null);
  const [badgeCount, setBadgeCount] = useState(0);
  const [badgeSenders, setBadgeSenders] = useState<string[]>([]);
  const [badgeSenderCounts, setBadgeSenderCounts] = useState<{ [sender: string]: number }>({}); // 送信者ごとの件数を管理
  const [visible, setVisible] = useState(false);
  const [showNewMessageBanner, setShowNewMessageBanner] = useState(false);
  const wsRef = useRef<WebSocket | null>(null);
  const userIdRef = useRef<string | null>(null);
  const clientRef = useRef<any>(null);
  const connectedRef = useRef(false);

  // ユーザーID取得
  useEffect(() => {
    if (typeof window !== 'undefined') {
      userIdRef.current = localStorage.getItem('reading-share-user-id');
    }
  }, []);

  // 通知を一定時間で消す
  useEffect(() => {
    if (notification) {
      setVisible(true);
      const timer = setTimeout(() => {
        setVisible(false);
      }, 4000);
      return () => clearTimeout(timer);
    }
  }, [notification]);

  // WebSocketでリアルタイム通知
  useEffect(() => {
      clientRef.current = null;
      connectedRef.current = false;
     // roomIdが未設定の場合は接続しない
     if (!roomId) return;
      // CDNからstompjsを動的import
    const loadStompAndConnect = async () => {
      if (typeof window === "undefined") return;
      // @ts-ignore
      if (!window.Stomp) {
        await new Promise((resolve) => {
          const script = document.createElement("script");
          script.src = "https://cdn.jsdelivr.net/npm/stompjs@2.3.3/lib/stomp.min.js";
          script.onload = resolve;
          document.body.appendChild(script);
        });
      }
      // SockJSもCDNから動的import
      // @ts-ignore
      if (!window.SockJS) {
        await new Promise((resolve) => {
          const script = document.createElement("script");
          script.src = "https://cdn.jsdelivr.net/npm/sockjs-client@1/dist/sockjs.min.js";
          script.onload = resolve;
          document.body.appendChild(script);
        });
      }
      // @ts-ignore
      const socket = new window.SockJS("http://localhost:8080/ws");
      // @ts-ignore
      const Stomp = window.Stomp;
      clientRef.current = Stomp.over(socket);
      clientRef.current.debug = (msg: string) => console.log("[STOMP]", msg);
      clientRef.current.connect(
        {},
        () => {
          connectedRef.current = true;
          console.log('[STOMP] Connected');
          console.log('[STOMP] サブスクライブ先', `/topic/chat/${roomId}`);
          clientRef.current.subscribe(`/topic/chat/${roomId}`, (message: any) => {
            const data = JSON.parse(message.body);
            console.log('受信データ:', data);
            console.log('自分のuserId:', userIdRef.current, '現在のroomId:', roomId);
            console.log('data.senderId:', data.senderId, 'data.roomId:', data.roomId);

            // チャット画面も必ずリアルタイム更新
            if (window.updateGroupChatScreen) {
              window.updateGroupChatScreen(data);
            }

            // roomId比較を厳密に（両方string化・trim・小文字化）
            const receivedRoomId = String(data.roomId || '').trim().toLowerCase();
            const currentRoomId = String(roomId || '').trim().toLowerCase();

            if (
              data.senderId && userIdRef.current &&
              data.senderId !== userIdRef.current &&
              receivedRoomId && currentRoomId &&
              receivedRoomId === currentRoomId
            ) {
              setShowNewMessageBanner(true);
              setTimeout(() => setShowNewMessageBanner(false), 3000);
              setNotification('他のメンバーから新しいメッセージが届きました');
              setBadgeCount(c => c + 1);
              setBadgeSenders(prev => prev.includes(data.senderName) ? prev : [...prev, data.senderName]);
              setBadgeSenderCounts(prev => ({
                ...prev,
                [data.senderName]: (prev[data.senderName] || 0) + 1
              }));
            } else {
              console.log('通知条件に合致せず: ', {
                senderId: data.senderId,
                userId: userIdRef.current,
                receivedRoomId,
                currentRoomId
              });
            }
          });
        },
        (error: any) => {
          console.error("STOMP接続エラー", error);
        }
      );
      clientRef.current.onStompError = (frame: any) => {
        console.error('[STOMP] STOMPプロトコルエラー', frame);
      };
      clientRef.current.onWebSocketError = (event: any) => {
        console.error('[STOMP] WebSocketエラー', event);
      };
    };

    loadStompAndConnect();

    return () => {
      if (clientRef.current && connectedRef.current) clientRef.current.disconnect();
    };
  }, [roomId]);

  // ユーザー情報取得
  const userId = userIdRef.current || "";
  const userName = (typeof window !== 'undefined' && localStorage.getItem("reading-share-user-name")) || "";
  // メッセージ送信時の例
  const sendMessage = (content: string) => {
    console.log("送信関数呼び出し", { client: clientRef.current, connected: connectedRef.current, content, userId, userName });
    if (!clientRef.current || !connectedRef.current) {
      console.warn("WebSocket未接続のため送信中断");
      return;
    }
    console.log("送信", content);
    clientRef.current.send(
      '/app/chat.sendMessage',
      {},
      JSON.stringify({
        roomId,
        senderId: userId,
        senderName: userName,
        content,
        sentAt: new Date().toISOString(),
      })
    );
  };

  // オーバーレイを閉じる時にバッジと送信者リストをリセット
  const handleClose = () => {
    setBadgeCount(0);
    setBadgeSenders([]);
    onClose();
  };

  if (!open) return null;
  return (
    <div onClick={handleClose} style={{
      position: 'fixed',
      top: 0,
      left: 0,
      width: '100vw',
      height: '100vh',
      background: 'rgba(0,0,0,0.35)',
      zIndex: 2000,
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    }}>
      {/* 新着メッセージバナー */}
      {showNewMessageBanner && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          width: '100vw',
          background: '#1976d2',
          color: '#fff',
          textAlign: 'center',
          fontWeight: 'bold',
          fontSize: 18,
          padding: '12px 0',
          zIndex: 4000,
          boxShadow: '0 2px 8px rgba(0,0,0,0.12)'
        }}>
          新着メッセージがあります
        </div>
      )}
      {/* 通知バッジ */}
      {badgeCount > 0 && (
        <div
          style={{
            position: 'fixed',
            top: 18,
            left: 18,
            zIndex: 3001,
            background: '#d32f2f',
            color: '#fff',
            borderRadius: '50%',
            width: 32,
            height: 32,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            fontWeight: 'bold',
            fontSize: 18,
            boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
            cursor: 'pointer',
          }}
          title={
            Object.keys(badgeSenderCounts).length > 0
              ? Object.entries(badgeSenderCounts)
                  .map(([name, count]) => `${name}(${count})`).join(', ')
              : ''
          }
        >
          {badgeCount}
        </div>
      )}
      {/* 通知表示 */}
      <ChatNotification message={notification || ''} visible={visible} onClose={() => setNotification(null)} />
      <div onClick={e => e.stopPropagation()} style={{
        background: '#fff',
        borderRadius: 16,
        boxShadow: '0 8px 32px rgba(0,0,0,0.2)',
        padding: '16px 24px',
        width: '60vw',
        maxWidth: '90vw',
        maxHeight: '98vh',
        /* 横スクロールを隠して縦スクロールのみ許可 */
        overflowY: 'auto',
        overflowX: 'hidden',
        position: 'relative',
        display: 'flex',
        flexDirection: 'column',
      }}>
        <ReadingScreen roomId={roomId} />
      </div>
    </div>
  );
};

export default ReadingScreenOverlay;
