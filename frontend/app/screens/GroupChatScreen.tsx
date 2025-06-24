"use client"
import React, { useState, useEffect, useRef } from 'react'
import SurveyCreationModal from './SurveyCreationModal'
import { chatApi } from '../../lib/chatApi'
import { ChatMessage, ChatStreamItem } from '../../types/chat'
import { surveyApi } from '../../lib/surveyApi'
import { Survey } from '../../types/survey'
import SurveyResultModal from './SurveyResultModal'
import { roomApi } from '../../lib/roomApi';
import { Room } from '../../types/room';

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
    const [messages, setMessages] = useState<Message[]>([])
    const [input, setInput] = useState("")
    const [msgId, setMsgId] = useState(1); // 追加
    const [showSurveyModal, setShowSurveyModal] = useState(false)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const [currentUserId, setCurrentUserId] = useState<string | null>(null)
    const [streamItems, setStreamItems] = useState<ChatStreamItem[]>([])
    const [answeringSurveyId, setAnsweringSurveyId] = useState<string | null>(null)
    const [surveyAnswers, setSurveyAnswers] = useState<Record<string, string[]>>({})
    const [showSurveyResultModal, setShowSurveyResultModal] = useState(false)
    const [resultSurveyId, setResultSurveyId] = useState<string | null>(null)
    const [answeredSurveyIds, setAnsweredSurveyIds] = useState<string[]>([])
    const [showReadingOverlay, setShowReadingOverlay] = useState(false)
    const [roomName, setRoomName] = useState<string>(roomTitle);

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
            const stream = await chatApi.getChatStream(roomId)
            setStreamItems(stream)
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

    // チャットストリーム取得時に各アンケートの回答状況も取得
    useEffect(() => {
        if (!roomId || !currentUserId) return;
        const fetchAnswered = async () => {
            const stream = await chatApi.getChatStream(roomId);
            setStreamItems(stream);
            // アンケートID一覧
            const surveyIds = stream.filter(item => item.type === 'survey').map(item => item.survey.id);
            // サーバーに問い合わせ
            const results = await Promise.all(
                surveyIds.map(sid => surveyApi.hasUserAnswered(sid, currentUserId))
            );
            setAnsweredSurveyIds(surveyIds.filter((_, i) => results[i]));
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

    const handleSend = async () => {
        if (!input.trim() || !roomId) return;
        try {
            // サーバーにメッセージを送信（送信時刻を付与）
            await chatApi.sendMessage(roomId, {
                messageContent: input,
                sentAt: new Date().toISOString(),
            })
            // 送信直後に全員のチャット履歴を即時再取得
            await loadChatHistory()
            setInput("")
        } catch (err) {
            console.error('メッセージ送信に失敗しました:', err)
            setInput("")
        }
    }

    const handleGoToReading = () => {
        setShowReadingOverlay(true);
    }

    const handleCreateSurvey = () => {
        setShowSurveyModal(true)
    }

    const handleSurveyCreated = () => {
        setShowSurveyModal(false)
        loadChatStream() // 作成後に即リロード
    }

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

    // survey回答送信
    const handleSurveyAnswer = async (survey: Survey) => {
        if (!surveyAnswers[survey.id]) return
        const answerObj: Record<string, string[]> = {};
        survey.questions.forEach((q, qIdx) => {
            const ans = surveyAnswers[survey.id]?.filter(opt => q.options.includes(opt) || (addedOptions[survey.id + '-' + qIdx] || []).includes(opt)) || [];
            answerObj[q.questionText] = ans;
        });
        // 追加: 追加選択肢も送信
        const added: Record<string, string[]> = {};
        survey.questions.forEach((q, qIdx) => {
            if (addedOptions[survey.id + '-' + qIdx] && addedOptions[survey.id + '-' + qIdx].length > 0) {
                added[q.questionText] = addedOptions[survey.id + '-' + qIdx];
            }
        });
        try {
            await surveyApi.answerSurvey(survey.id, {
                surveyId: survey.id,
                userId: currentUserId!,
                answers: answerObj,
                addedOptions: added
            })
            setAnsweringSurveyId(null)
            setAnsweredSurveyIds(prev => [...prev, survey.id])
            loadChatStream()
        } catch (e: any) {
            // すでに回答済みの場合はUIを切り替える
            if (typeof e?.response?.data === 'string' && e.response.data.includes('duplicate key')) {
                alert('すでにこのアンケートに回答済みです。')
                setAnsweredSurveyIds(prev => [...prev, survey.id])
            } else {
                alert('アンケート回答送信に失敗しました')
            }
            setAnsweringSurveyId(null)
        }
    }

    // ユーザーがこのアンケートに回答済みかどうかを判定する
    const hasAnsweredSurvey = (surveyId: string) => {
        return answeredSurveyIds.includes(surveyId);
    }

    // チャットストリームが更新されたら一番下にスクロール
    useEffect(() => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
    }, [streamItems, showSurveyResultModal])

    // --- 追加: 各アンケートごとにローカルで追加選択肢を管理 ---
    const [addedOptions, setAddedOptions] = useState<Record<string, string[]>>({})
    const [newOptionInput, setNewOptionInput] = useState<Record<string, string>>({})
    // --- ここまで ---

    // 1. チャットのユーザーアイコンを頭文字一文字に
    const getUserInitial = (user: string) => user.charAt(0).toUpperCase();

    return (
        <div style={{ border: '4px solid #388e3c', margin: 24, padding: 24, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', borderRadius: 12, maxWidth: 1200, minHeight: 600, marginLeft: 'auto', marginRight: 'auto', display: 'flex', flexDirection: 'column', height: '80vh', position: 'relative' }}>
            <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 16, color: '#388e3c' }}>
                {roomName}
            </h2>

            {/* ナビゲーションボタン */}
            <div style={{ display: 'flex', gap: 12, justifyContent: 'center', marginBottom: 16, flexWrap: 'wrap' }}>
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

            {/* エラー表示 */}
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

            {/* チャット欄 */}
            <div style={{ flex: 1, overflowY: 'auto', marginBottom: 16, background: '#fff', borderRadius: 8, padding: 16, border: '1px solid #b0b8c9', display: 'flex', flexDirection: 'column', gap: 12 }}>
                {streamItems.length === 0 ? (
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
                        {streamItems.sort((a, b) => new Date(a.createdAt).getTime() - new Date(b.createdAt).getTime()).map((item, idx) => {
                            if (item.type === 'message') {
                                const msg = item.message;
                                const isMine = msg.senderUserId === currentUserId;
                                return (
                                    <div key={msg.id} style={{ display: 'flex', alignItems: 'center', gap: 8, justifyContent: isMine ? 'flex-end' : 'flex-start' }}>
                                        {!isMine && (
                                            <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                                                {String(msg.senderUserId || '匿名').charAt(0).toUpperCase()}
                                            </span>
                                        )}
                                        <div style={{ border: '1px solid #222', borderRadius: 16, padding: 8, background: isMine ? '#e0f7fa' : '#fff', maxWidth: 600, wordBreak: 'break-word' }}>
                                            {typeof msg.content === 'object' && msg.content !== null && 'value' in msg.content ? msg.content.value : String(msg.content)}
                                        </div>
                                        {isMine && (
                                            <span style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, display: 'flex', alignItems: 'center', justifyContent: 'center', background: '#e0f7fa' }}>
                                                {String(msg.senderUserId || 'あ').charAt(0).toUpperCase()}
                                            </span>
                                        )}
                                    </div>
                                );
                            } else if (item.type === 'survey') {
                                const survey = item.survey;
                                const answered = hasAnsweredSurvey(survey.id);
                                return (
                                    <div key={survey.id} style={{ border: '2px solid #2196f3', borderRadius: 12, padding: 16, margin: 8, background: '#e3f2fd' }}>
                                        <div style={{ fontWeight: 'bold', marginBottom: 8 }}>アンケート: {survey.title}</div>
                                        {answered ? (
                                            <button
                                                onClick={() => { setResultSurveyId(survey.id); setShowSurveyResultModal(false); setTimeout(() => setShowSurveyResultModal(true), 0); }}
                                                style={{ marginTop: 8, padding: '6px 16px', borderRadius: 6, background: '#388e3c', color: 'white', border: 'none', cursor: 'pointer' }}
                                            >アンケートの結果を表示する</button>
                                        ) : (
                                            <>
                                                {survey.questions.map((q, qIdx) => {
                                                    const allOptions = [...q.options, ...(addedOptions[survey.id + '-' + qIdx] || [])];
                                                    return (
                                                        <div key={qIdx} style={{ marginBottom: 8 }}>
                                                            <div>{q.questionText}</div>
                                                            {allOptions.map((opt, oIdx) => (
                                                                <label key={oIdx} style={{ marginRight: 12 }}>
                                                                    <input
                                                                        type={q.questionType === 'SINGLE_CHOICE' ? 'radio' : 'checkbox'}
                                                                        name={`survey-${survey.id}-q${qIdx}`}
                                                                        value={opt}
                                                                        checked={surveyAnswers[survey.id]?.includes(opt) || false}
                                                                        onChange={e => {
                                                                            setSurveyAnswers(prev => {
                                                                                const prevAns = prev[survey.id] || [];
                                                                                if (q.questionType === 'SINGLE_CHOICE') {
                                                                                    return { ...prev, [survey.id]: [opt] };
                                                                                } else {
                                                                                    if (e.target.checked) {
                                                                                        return { ...prev, [survey.id]: [...prevAns, opt] };
                                                                                    } else {
                                                                                        return { ...prev, [survey.id]: prevAns.filter(a => a !== opt) };
                                                                                    }
                                                                                }
                                                                            });
                                                                        }}
                                                                    />
                                                                    {opt}
                                                                </label>
                                                            ))}
                                                            {/* 選択肢追加欄 */}
                                                            {q.allowAddOptions && (
                                                                <div style={{ display: 'flex', gap: 8, marginTop: 4 }}>
                                                                    <input
                                                                        type="text"
                                                                        placeholder="選択肢を追加"
                                                                        value={newOptionInput[survey.id + '-' + qIdx] || ''}
                                                                        onChange={e => setNewOptionInput(prev => ({ ...prev, [survey.id + '-' + qIdx]: e.target.value }))}
                                                                        style={{ padding: '4px 8px', borderRadius: 4, border: '1px solid #b0b8c9', fontSize: 14 }}
                                                                    />
                                                                    <button
                                                                        onClick={() => {
                                                                            const val = (newOptionInput[survey.id + '-' + qIdx] || '').trim();
                                                                            if (!val) return;
                                                                            setAddedOptions(prev => ({
                                                                                ...prev,
                                                                                [survey.id + '-' + qIdx]: [...(prev[survey.id + '-' + qIdx] || []), val]
                                                                            }));
                                                                            setNewOptionInput(prev => ({ ...prev, [survey.id + '-' + qIdx]: '' }));
                                                                        }}
                                                                        style={{ padding: '4px 12px', borderRadius: 4, background: '#2196f3', color: '#fff', border: 'none', fontSize: 14 }}
                                                                    >追加</button>
                                                                </div>
                                                            )}
                                                        </div>
                                                    );
                                                })}
                                                {/* 回答ボタン */}
                                                <div style={{ textAlign: 'right', marginTop: 8 }}>
                                                    <button
                                                        onClick={() => handleSurveyAnswer(survey)}
                                                        style={{ padding: '8px 24px', borderRadius: 6, background: '#388e3c', color: '#fff', border: 'none', fontWeight: 'bold', fontSize: 16 }}
                                                    >回答する</button>
                                                </div>
                                            </>
                                        )}
                                    </div>
                                );
                            }
                            return null;
                        })}
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

            {/* アンケート結果モーダル */}
            {showSurveyResultModal && resultSurveyId && (
                <SurveyResultModal
                    key={resultSurveyId + '-' + Date.now()}
                    open={showSurveyResultModal}
                    surveyId={resultSurveyId}
                    onClose={() => setShowSurveyResultModal(false)}
                />
            )}
        </div>
    )
}

export default GroupChatScreen
