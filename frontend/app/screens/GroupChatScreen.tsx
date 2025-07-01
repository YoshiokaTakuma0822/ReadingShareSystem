"use client"
import React, { useState, useEffect, useRef } from 'react'
import SurveyCreationModal from './SurveyCreationModal'
import SurveyAnswerModal from './SurveyAnswerModal'
import { chatApi } from '../../lib/chatApi'
import { ChatMessage } from '../../types/chat'
import ReadingScreenOverlay from './ReadingScreenOverlay'
import { roomApi } from '../../lib/roomApi';
import { Room } from '../../types/room';
import { surveyApi } from '../../lib/surveyApi'
import { Survey } from '../../types/survey'
import { useRouter } from 'next/navigation';

interface Message {
    id: number;
    user: string;
    text: string;
    isCurrentUser: boolean;
    sentAt?: string; // 追加
}

interface GroupChatScreenProps {
    roomTitle?: string
    currentUser?: string
    roomId?: string
}

// グローバルwindowに型を追加
declare global {
  interface Window {
    updateGroupChatScreen?: (data: any) => void;
  }
}

const GroupChatScreen: React.FC<GroupChatScreenProps> = ({ roomTitle = "チャットルーム", currentUser = "あなた", roomId }) => {
    const router = useRouter()
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState("")
    const [msgId, setMsgId] = useState(1); // 追加
    const [showSurveyModal, setShowSurveyModal] = useState(false)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)
    const [showReadingOverlay, setShowReadingOverlay] = useState(false)
    const [roomName, setRoomName] = useState<string>(roomTitle);
    // アンケート回答モーダル制御
    const [showAnswerModal, setShowAnswerModal] = useState(false);
    const [answerSurveyId, setAnswerSurveyId] = useState<string | null>(null);
    // アンケートフォーマット表示用
    const [surveyFormat, setSurveyFormat] = useState<Survey | null>(null);

    // 追加: ユーザーID→ユーザー名のマッピングを保持
    const [userIdToName, setUserIdToName] = useState<Record<string, string>>({});

    // スクロール用ref
    const messagesEndRef = React.useRef<HTMLDivElement | null>(null);

    // コンポーネントマウント時にユーザーIDを取得
    useEffect(() => {
        let userId = localStorage.getItem('reading-share-user-id');
        if (!userId) {
            alert('ユーザー情報が見つかりません。再ログインしてください。');
            window.location.href = '/login'; // ログイン画面へリダイレクト
            return;
        }
        // ハイフン除去・小文字化して保存
        userId = userId.replace(/-/g, '').toLowerCase();
        setCurrentUserId(userId);
    }, [])

    // チャット履歴を取得する関数
    const loadChatHistory = async () => {
        if (!roomId) {
            setLoading(false)
            return
        }
        setLoading(true)
        setError(null)
        try {
            // チャット履歴取得
            const chatHistory = await chatApi.getChatHistory(roomId)
            setMessages(chatHistory.map((msg, idx) => ({
                id: idx, // 連番でnumber型に変換
                user: msg.senderUsername || '匿名', // ユーザー名を表示
                text: typeof msg.content === 'string' ? msg.content : msg.content.value,
                isCurrentUser: String(msg.senderUserId) === String(currentUserId),
            })))
        } catch (e) {
            setError('チャット履歴の取得に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    // チャットストリーム取得
    const loadChatStream = async () => {
        if (!roomId) {
            setLoading(false)
            return
        }
        try {
            setLoading(true)
            setError(null)
            const chatHistory = await chatApi.getChatHistory(roomId)

            console.log('取得したチャット履歴:', chatHistory)

            // ChatMessageをMessage形式に変換
            const convertedMessages: Message[] = chatHistory.map((msg, index) => {
                let messageText = '';
                if (typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content) {
                    messageText = String((msg.content as { value: string }).value || '');
                } else {
                    messageText = String(msg.content || '');
                }
                // 厳密な自分判定（ハイフン除去・小文字化）
                const senderId = (msg.senderUserId ?? '').replace(/-/g, '').toLowerCase();
                // currentUserIdはすでに整形済み
                const myId = currentUserId ?? '';
                // msg.senderUserIdがnullの場合は空文字でアクセスしない
                const username = senderId && msg.senderUserId && userIdToName[msg.senderUserId] ? userIdToName[msg.senderUserId] : (msg.senderUserId || '匿名ユーザー');
                return {
                    id: index + 1,
                    user: username,
                    text: messageText,
                    isCurrentUser: !!(senderId && myId && senderId === myId),
                    sentAt: msg.sentAt
                };
            })

            setMessages(convertedMessages)
            setMsgId(convertedMessages.length + 1)
        } catch (err) {
            setError('チャットストリームの読み込みに失敗しました')
        } finally {
            setLoading(false)
        }
    }

    // コンポーネントマウント時にチャット履歴を読み込む
    useEffect(() => {
        if (currentUserId !== null) {
            loadChatStream()
        }
    }, [roomId, currentUserId])

    // チャットストリーム取得時のアンケート回答状況取得部分を簡略化（setStreamItems, setAnsweredSurveyIdsを削除）
    useEffect(() => {
        if (!roomId || !currentUserId) return;
        const fetchAnswered = async () => {
            // 必要ならここでアンケート回答状況取得処理を追加
        };
        fetchAnswered();
    }, [roomId, currentUserId])

    // チャット自動更新（5秒ごと）
    useEffect(() => {
        if (!roomId || !currentUserId) return;
        const interval = setInterval(() => {
            loadChatHistory();
        }, 5000);
        return () => clearInterval(interval);
    }, [roomId, currentUserId])

    // メッセージ送信
    const handleSend = async () => {
        if (!input.trim() || !roomId) return;
        try {
            // sentAtを付与して型エラーを回避
            await chatApi.sendMessage(roomId, { messageContent: input, sentAt: new Date().toISOString() });
            setInput("");
        } catch (err) {
            console.error('メッセージ送信に失敗しました:', err);
        }
    }

    const handleGoToReading = () => {
        setShowReadingOverlay(true);
    }

    const handleCreateSurvey = () => {
        setShowSurveyModal(true)
    }

    // アンケート作成後はモーダルを閉ち、回答モーダルを開く
    const handleSurveyCreated = (surveyId: string) => {
        setShowSurveyModal(false)
        setAnswerSurveyId(surveyId)
        setShowAnswerModal(true)
    }

    // アンケートフォーマット取得
    useEffect(() => {
        if (answerSurveyId) {
            surveyApi.getSurveyFormat(answerSurveyId)
                .then(data => setSurveyFormat(data))
                .catch(() => setSurveyFormat(null));
        } else {
            setSurveyFormat(null);
        }
    }, [answerSurveyId]);
    // WebSocket受信時に呼ばれるグローバル関数を定義
    useEffect(() => {
        window.updateGroupChatScreen = (data: any) => {
            setMessages(prev => {
                if (prev.some(m => m.sentAt === data.sentAt && m.text === data.content && m.user === data.senderName)) {
                    return prev;
                }
                // 厳密な自分判定（ハイフン除去・小文字化）
                const senderId = (data.senderId ?? '').replace(/-/g, '').toLowerCase();
                // currentUserIdはすでに整形済み
                const myId = currentUserId ?? '';
                const username = userIdToName[data.senderId] || data.senderName || data.senderId || '匿名ユーザー';
                return [
                    ...prev,
                    {
                        id: prev.length + 1,
                        user: username,
                        text: data.content,
                        isCurrentUser: senderId && myId && senderId === myId,
                        sentAt: data.sentAt
                    }
                ];
            });
        };
        return () => {
            window.updateGroupChatScreen = undefined;
        };
    }, [currentUserId])

    // メッセージ追加時に自動スクロール
    useEffect(() => {
        if (messagesEndRef.current) {
            messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
        }
    }, [messages]);

    // 部屋名取得
    useEffect(() => {
        if (roomId) {
            roomApi.getRoom(roomId).then((room: Room) => {
                setRoomName(room.roomName);
            }).catch(() => {
                setRoomName(roomTitle); // 取得失敗時はデフォルト
            });
        }
    }, [roomId]);

    // 部屋メンバー一覧を取得してユーザー名マッピングを作成
    useEffect(() => {
        if (!roomId) return;
        roomApi.getRoomMembers(roomId).then((members: any[]) => {
            const map: Record<string, string> = {};
            members.forEach(m => {
                if (m.userId && m.username) map[m.userId] = m.username;
            });
            setUserIdToName(map);
        });
    }, [roomId]);

    // userIdToNameまたはcurrentUserIdが更新されたら履歴を再生成
    useEffect(() => {
        if (!roomId || !currentUserId || Object.keys(userIdToName).length === 0) return;
        (async () => {
            setLoading(true);
            setError(null);
            try {
                const chatHistory = await chatApi.getChatHistory(roomId);
                const convertedMessages: Message[] = chatHistory.map((msg, index) => {
                    let messageText = '';
                    if (typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content) {
                        messageText = String((msg.content as { value: string }).value || '');
                    } else {
                        messageText = String(msg.content || '');
                    }
                    const senderId = (msg.senderUserId ?? '').replace(/-/g, '').toLowerCase();
                    const myId = currentUserId ?? '';
                    const username = senderId && msg.senderUserId && userIdToName[msg.senderUserId] ? userIdToName[msg.senderUserId] : (msg.senderUserId || '匿名ユーザー');
                    return {
                        id: index + 1,
                        user: username,
                        text: messageText,
                        isCurrentUser: !!(senderId && myId && senderId === myId),
                        sentAt: msg.sentAt
                    };
                });
                setMessages(convertedMessages);
                setMsgId(convertedMessages.length + 1);
            } catch (err) {
                setError('チャット履歴の読み込みに失敗しました');
            } finally {
                setLoading(false);
            }
        })();
    }, [roomId, currentUserId, userIdToName]);

    // JSX返却部
    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 24, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh', position: 'relative' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: '#388e3c' }}>
                {roomName}
            </h2>
            <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
                {answerSurveyId && (
                    <button
                        onClick={() => setShowAnswerModal(true)}
                        style={{ padding: '12px 24px', fontSize: 16, background: '#ff9800', color: 'white', border: 'none', borderRadius: 8, cursor: 'pointer', fontWeight: 'bold', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}
                    >
                        📝 アンケートに回答する
                    </button>
                )}
                <button
                    onClick={handleGoToReading}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#4caf50',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    📖 読書画面をオーバーレイ表示
                </button>
                <button
                    onClick={handleCreateSurvey}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#2196f3',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    📊 アンケート作成
                </button>
                <button
                    onClick={() => window.location.href = '/'}
                    style={{
                        padding: '12px 24px',
                        fontSize: 16,
                        background: '#757575',
                        color: 'white',
                        border: 'none',
                        borderRadius: 8,
                        cursor: 'pointer',
                        fontWeight: 'bold',
                        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                    }}
                >
                    🏠 ホームへ
                </button>
            </div>
            {error && (
                <div style={{
                    background: '#ffebee',
                    color: '#c62828',
                    padding: 12,
                    borderRadius: 8,
                    marginBottom: 16,
                    border: '1px solid #ef5350'
                }}>
                    {error}
                    <button
                        onClick={loadChatHistory}
                        style={{
                            marginLeft: 12,
                            background: '#c62828',
                            color: 'white',
                            border: 'none',
                            padding: '4px 8px',
                            borderRadius: 4,
                            cursor: 'pointer'
                        }}
                    >
                        再試行
                    </button>
                </div>
            )}
            {/* アンケート内容表示（例: 直近作成アンケート） */}
            {surveyFormat && (
                <div style={{ margin: '16px 0', padding: 16, background: '#f9f9f9', border: '1px solid #ccc', borderRadius: 8 }}>
                    <h3 style={{ marginBottom: 8, color: '#1976d2' }}>{surveyFormat.title}</h3>
                    {surveyFormat.questions && surveyFormat.questions.length > 0 && (
                        <ul style={{ paddingLeft: 20, margin: 0 }}>
                            {surveyFormat.questions[0].options.map((opt, i) => (
                                <li key={i} style={{ marginBottom: 4 }}>{opt}</li>
                            ))}
                        </ul>
                    )}
                    <button
                        onClick={() => setShowAnswerModal(true)}
                        style={{
                            marginTop: 8,
                            padding: '12px 24px',
                            borderRadius: 8,
                            border: '1px solid #2e7d32',
                            fontSize: 16,
                            background: '#c8e6c9',
                            color: '#2e7d32',
                            cursor: 'pointer',
                            fontWeight: 'bold',
                            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                        }}
                    >
                        アンケートに回答する
                    </button>
                </div>
            )}
            {/* チャット欄 */}
            <div style={{ flex: 1, overflowY: 'auto', marginBottom: 16, background: '#fff', borderRadius: 8, padding: 16, border: '1px solid #b0b8c9', display: 'flex', flexDirection: 'column', gap: 12 }}>
                {loading ? (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: '100%',
                        color: '#666',
                        fontSize: 16
                    }}>
                        チャット履歴を読み込み中...
                    </div>
                ) : messages.length === 0 ? (
                    <div style={{
                        display: 'flex',
                        justifyContent: 'center',
                        alignItems: 'center',
                        height: '100%',
                        color: '#999',
                        fontSize: 16
                    }}>
                        まだメッセージがありません
                    </div>
                ) : (
                    <>
                    {messages.map(msg => {
                        const isMine = msg.isCurrentUser
                        return (
                            <div
                                key={msg.id}
                                style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 8,
                                    justifyContent: isMine ? 'flex-end' : 'flex-start',
                                }}
                            >
                                {!isMine && (
                                    <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                        {msg.user ? String(msg.user).trim().charAt(0) : '?'}
                                    </span>
                                )}
                                <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                                    {/* 自分のメッセージはタイムスタンプを左側に */}
                                    {isMine && msg.sentAt && (
                                        <span style={{ fontSize: '0.8em', color: '#888', minWidth: 60, textAlign: 'right' }}>
                                            {new Date(msg.sentAt).toLocaleTimeString()}
                                        </span>
                                    )}
                                    <div
                                        style={{
                                            border: '1px solid #222',
                                            borderRadius: 16,
                                            padding: 8,
                                            background: isMine ? '#e0f7fa' : '#fff',
                                            maxWidth: 600,
                                            wordBreak: 'break-word',
                                        }}
                                    >
                                        {String(msg.text)}
                                    </div>
                                    {/* 他人のメッセージはタイムスタンプを右側に */}
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
                    })}
                    <div ref={messagesEndRef} />
                    </>
                )}
                <div ref={messagesEndRef} />
            </div>
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32 }}>
                <input
                    type="text"
                    value={input}
                    onChange={e => setInput(e.target.value)}
                    onKeyDown={e => { if (e.key === 'Enter') handleSend() }}
                    style={{ flex: 1, padding: 12, borderRadius: 8, border: '1px solid #222', fontSize: 18 }}
                    placeholder="メッセージを入力..."
                    disabled={loading}
                />
                <button
                    style={{
                        marginLeft: 8,
                        padding: '12px 24px',
                        borderRadius: 8,
                        border: '1px solid #222',
                        fontSize: 18,
                        background: loading ? '#ccc' : 'white',
                        cursor: loading ? 'not-allowed' : 'pointer'
                    }}
                    onClick={handleSend}
                    disabled={loading}
                >送信</button>
            </div>
            {/* アンケート作成モーダル */}
            {showSurveyModal && roomId && (
                <SurveyCreationModal
                    open={showSurveyModal}
                    roomId={roomId}
                    onClose={() => setShowSurveyModal(false)}
                    onCreated={handleSurveyCreated}
                />
            )}
            <ReadingScreenOverlay roomId={roomId} open={showReadingOverlay} onClose={() => setShowReadingOverlay(false)} />
            {/* アンケート回答モーダル */}
            {showAnswerModal && answerSurveyId && (
                <SurveyAnswerModal
                    open={showAnswerModal}
                    surveyId={answerSurveyId!}
                    onClose={() => setShowAnswerModal(false)}
                />
            )}
        </div>
    )
}

export default GroupChatScreen
