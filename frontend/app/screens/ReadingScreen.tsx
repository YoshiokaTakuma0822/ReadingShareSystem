"use client"
import React, { useEffect, useRef, useState } from "react"
import { authStorage } from '../../lib/authUtils'
import { readingStateApi } from '../../lib/readingStateApi'
import { roomApi } from '../../lib/roomApi'
import { RoomMember } from '../../types/room'
import ReadingProgressModal from "./ReadingProgressModal"
import './ReadingScreen.css'

const maxPage = 300

interface ReadingScreenProps {
    roomId?: string
}

const ReadingScreen: React.FC<ReadingScreenProps> = ({ roomId }) => {
    // persistent WebSocket for progress notifications
    const wsRef = useRef<WebSocket | null>(null)
    const [showProgressModal, setShowProgressModal] = useState(false)
    const [currentPage, setCurrentPage] = useState<number>(0)
    const [displayPage, setDisplayPage] = useState<number>(0)
    const [flipping, setFlipping] = useState<boolean>(false)
    // 複数のアニメーションを同時実行するための配列
    const [activeAnimations, setActiveAnimations] = useState<Array<{
        id: number
        direction: 'forward' | 'backward'
        displayPage: number
    }>>([])
    const [animationIdCounter, setAnimationIdCounter] = useState<number>(0)

    // --- 追加: 初期化完了フラグ ---
    const [isInitialized, setIsInitialized] = useState(false)

    // 自動めくり間隔（分単位）をユーザーが自由に入力できる（初期値：3分）
    const [flipIntervalMinutes, setFlipIntervalMinutes] = useState<number>(3)
    const flipIntervalMs = flipIntervalMinutes * 60 * 1000

    // メンバー一覧
    const [members, setMembers] = useState<{ name: string; page: number; color: string; userId: string }[]>([])
    const [totalPages, setTotalPages] = useState<number>(300) // 追加: 本の全ページ数
    const [editingTotalPages, setEditingTotalPages] = useState(false)
    const [inputTotalPages, setInputTotalPages] = useState(totalPages)

    // 作成者名
    const [hostUserId, setHostUserId] = useState<string | null>(null)
    const [creatorName, setCreatorName] = useState<string>("")

    // 縦書き/横書き切り替え用の状態
    const [isVerticalText, setIsVerticalText] = useState<boolean>(false)

    // ページめくり量（1ページか2ページか）
    const [pageFlipAmount, setPageFlipAmount] = useState<number>(1)

    // Removed initial auto-flip effect; replaced below after handlers

    // 部屋情報取得（hostUserIdを保存）
    useEffect(() => {
        if (!roomId) return
        roomApi.getRoom(roomId)
            .then((room) => {
                setHostUserId(room.hostUserId)
                if (room.totalPages) setTotalPages(room.totalPages)
            })
            .catch((e) => console.error('getRoom error:', e))
    }, [roomId])

    // 部屋メンバー取得＆作成者名取得
    useEffect(() => {
        if (!roomId || !hostUserId) return
        roomApi.getRoomMembers(roomId)
            .then((memberList: RoomMember[]) => {
                setMembers(memberList.map(m => ({
                    name: m.username ? m.username.charAt(0) : '？',
                    page: 1,
                    color: '#222',
                    userId: m.userId
                })))
                // userId比較はハイフン除去・小文字化で厳密一致
                const creator = memberList.find(m => m.userId && hostUserId && m.userId.replace(/-/g, '').toLowerCase() === hostUserId.replace(/-/g, '').toLowerCase())
                setCreatorName(creator ? creator.username : '')
            })
            .catch((e) => console.error('getRoomMembers error:', e))
    }, [roomId, hostUserId])

    // --- ページ進捗の永続化 ---
    useEffect(() => {
        if (!roomId) return
        const userId = authStorage.getUserId()
        if (!userId) return
        // ローカルストレージから進捗を優先的に取得
        const localKey = `reading-progress-${roomId}-${userId}`
        const localPage = localStorage.getItem(localKey)
        if (localPage && !isNaN(Number(localPage))) {
            setCurrentPage(Number(localPage))
            setDisplayPage(Number(localPage))
        }
        readingStateApi.getRoomReadingState(roomId, userId).then((res) => {
            if (res && res.userStates && res.userStates.length > 0) {
                const myState = res.userStates.find(u => u.userId === userId)
                if (myState) {
                    setCurrentPage(myState.currentPage)
                    setDisplayPage(myState.currentPage)
                    // サーバー値でローカルも上書き
                    localStorage.setItem(localKey, String(myState.currentPage))
                }
            }
            setIsInitialized(true) // 初期化完了
        }).catch(() => { setIsInitialized(true) })
    }, [roomId])

    // ページ進捗を保存し、WebSocketでブロードキャストする関数
    const saveAndBroadcastProgress = async (page: number) => {
        if (!roomId) return

        // 常に偶数ページに調整（和書/洋書で偶数ページの位置が異なる）
        const adjustedPage = isVerticalText
            ? Math.max(2, page + (page % 2)) // 和書: 右ページを偶数に
            : Math.max(1, page - (page % 2)) // 洋書: 左ページを偶数に

        const userId = authStorage.getUserId()
        if (!userId) return
        const localKey = `reading-progress-${roomId}-${userId}`
        localStorage.setItem(localKey, String(adjustedPage))
        try {
            await readingStateApi.updateUserReadingState(roomId, userId, { userId, currentPage: adjustedPage, comment: '' })
        } catch (e) {
            // 保存失敗時は何もしない
        }
    }

    // --- 部屋退出時にローカル進捗を削除 ---
    const closeReading = () => {
        if (roomId) {
            const userId = authStorage.getUserId()
            if (userId) {
                const localKey = `reading-progress-${roomId}-${userId}`
                localStorage.removeItem(localKey)
            }
            window.location.href = `/rooms/${roomId}/chat`
        }
    }

    // currentPage変更時の保存・WebSocket送信はここで一元化
    const handlePageChange = (page: number) => {
        setCurrentPage(page)
        setDisplayPage(page)
        // 初期化完了後のみ保存・送信
        if (isInitialized) {
            saveAndBroadcastProgress(page)
        }
    }

    // --- 全メンバーのページ進捗を定期取得 ---
    useEffect(() => {
        if (!roomId) return
        let timer: NodeJS.Timeout
        const fetchStates = async () => {
            try {
                // userIdは不要。全メンバー分取得するAPI想定
                const res = await readingStateApi.getRoomReadingState(roomId, 'all')
                if (res && res.userStates) {
                    setMembers((prev) => prev.map(m => {
                        const found = res.userStates.find(u => u.userId === m.userId)
                        return found ? { ...m, page: found.currentPage } : m
                    }))
                }
            } catch { }
            timer = setTimeout(fetchStates, 2000) // 2秒ごとに更新
        }
        fetchStates()
        return () => clearTimeout(timer)
    }, [roomId])

    // userIdを一度だけ整形してuseStateで保持
    const [myUserId, setMyUserId] = useState<string>('')
    useEffect(() => {
        const userId = localStorage.getItem('reading-share-user-id')
        if (!userId) {
            alert('ユーザー情報が見つかりません。再ログインしてください。')
            // 必要ならリダイレクト処理を追加
            return
        }
        setMyUserId(userId.replace(/-/g, '').toLowerCase())
    }, [])

    // 進捗率・メンバーアイコンの配置データ
    const memberProgress = members.map((m) => {
        const memberId = (m.userId || '').replace(/-/g, '').toLowerCase()
        return {
            ...m,
            percent: memberId === myUserId ? currentPage / totalPages : m.page / totalPages,
            isMe: memberId && myUserId && memberId === myUserId,
        }
    })

    useEffect(() => {
        setInputTotalPages(totalPages)
    }, [totalPages])

    // --- WebSocketで進捗リアルタイム共有 ---
    useEffect(() => {
        if (!roomId) return
        // WebSocketエンドポイント
        const wsProtocol = window.location.protocol === 'https:' ? 'wss' : 'ws'
        const wsUrl = `${wsProtocol}://${window.location.hostname}:8080/ws/chat`
        const ws = new WebSocket(wsUrl)
        ws.onopen = () => {
            // サーバー側でSTOMP等が必要な場合はここでプロトコルに合わせて送信
            // ここではシンプルなJSON送受信を仮定
        }
        ws.onmessage = (event) => {
            try {
                const msg = JSON.parse(event.data)
                if (msg.type === 'reading-progress' && msg.roomId === roomId && msg.userId && typeof msg.currentPage === 'number') {
                    setMembers((prev) => prev.map(m => m.userId === msg.userId ? { ...m, page: msg.currentPage } : m))
                }
            } catch { }
        }
        return () => ws.close()
    }, [roomId])

    // --- カウントダウン用 ---
    const [countdown, setCountdown] = useState(flipIntervalMs)
    const hasActiveAnimations = activeAnimations.length > 0

    useEffect(() => {
        if (flipping && !hasActiveAnimations) {
            setCountdown(flipIntervalMs)
            const start = Date.now()
            const interval = setInterval(() => {
                const elapsed = Date.now() - start
                const remain = Math.max(flipIntervalMs - elapsed, 0)
                setCountdown(remain)
                if (remain <= 0) clearInterval(interval)
            }, 100)
            return () => clearInterval(interval)
        } else {
            setCountdown(flipIntervalMs)
        }
    }, [flipping, hasActiveAnimations, flipIntervalMs])

    // アニメーション終了時のコールバック
    const onAnimationEnd = (animationId: number) => {
        setActiveAnimations(prev => prev.filter(anim => anim.id !== animationId))
    }

    // single page flip handler - 複数のアニメーションを同時実行可能
    const handleFlip = (direction: 'forward' | 'backward') => {
        const newAnimationId = animationIdCounter
        setAnimationIdCounter(prev => prev + 1)

        // 新しいアニメーションを追加
        setActiveAnimations(prev => [...prev, {
            id: newAnimationId,
            direction, // クリックしたページの動きに合わせてそのまま使用
            displayPage: displayPage
        }])

        // ページ更新（2ページずつ）
        setDisplayPage((prev) => {
            const newPage = direction === 'forward' ? prev + 2 : prev - 2
            const clamped = Math.max(1, Math.min(newPage, totalPages - 1))
            // 和書と洋書で偶数/奇数の配置が逆
            const adjustedPage = isVerticalText
                ? Math.max(2, clamped + (direction === 'forward' ? 0 : 2)) // 和書
                : Math.max(1, clamped + (direction === 'forward' ? 2 : 0)) // 洋書
            setCurrentPage(adjustedPage)
            return clamped
        })
    }

    // page click handlers - クリックしたページ自体が捲られる方向に修正
    const handleLeftPageClick = () => {
        // 和書: 左ページを右に捲って前のページへ
        // 洋書: 左ページを右に捲って次のページへ
        handleFlip('backward')
    }

    const handleRightPageClick = () => {
        // 和書: 右ページを左に捲って次のページへ
        // 洋書: 右ページを左に捲って前のページへ
        handleFlip('forward')
    }

    // auto-flip effect - 自動めくりの方向も合わせて修正
    useEffect(() => {
        let timer: NodeJS.Timeout
        if (flipping && !hasActiveAnimations) {
            // 和書: 右から左へ（右ページを左に捲る）
            // 洋書: 左から右へ（左ページを右に捲る）
            timer = setTimeout(isVerticalText ? handleRightPageClick : handleLeftPageClick, flipIntervalMs)
        }
        return () => clearTimeout(timer)
    }, [flipping, hasActiveAnimations, displayPage, flipIntervalMs, isVerticalText])

    // ページを偶数に調整する関数（2ページめくりモード用）
    const adjustToEvenPage = (page: number): number => {
        // 2ページずつめくるモードで、奇数ページにいる場合は偶数ページに調整
        if (pageFlipAmount === 2 && page % 2 !== 0) {
            return Math.max(1, page - 1)
        }
        return page
    }

    // pageFlipAmountが変更された時に現在のページを調整
    useEffect(() => {
        if (pageFlipAmount === 2) {
            setDisplayPage(prev => adjustToEvenPage(prev))
            setCurrentPage(prev => adjustToEvenPage(prev))
        }
    }, [pageFlipAmount])

    return (
        <>
            <div className="readingOverlay" onClick={closeReading}>
                <div className="container" onClick={e => e.stopPropagation()}>
                    {/* 進捗バー＋メンバーアイコン */}
                    <div className="progressWrapper">
                        <div className="progressBar">
                            <div
                                className="progress"
                                style={{ width: `${(totalPages > 0 ? currentPage / totalPages : 0) * 100}%` }}
                            ></div>
                        </div>
                        {memberProgress.map((m) => (
                            <div
                                key={m.name}
                                className="memberIcon"
                                style={{
                                    left: `calc(${320 * m.percent}px - 15px)`,
                                    background: m.isMe ? 'var(--green-dark)' : 'var(--white)',
                                    border: m.isMe ? '2px solid var(--green-main)' : '1px solid var(--border)',
                                    color: m.isMe ? 'var(--white)' : 'var(--green-dark)',
                                    boxShadow: m.isMe ? '0 0 8px var(--green-light)' : '0 1px 3px rgba(0,0,0,0.08)',
                                }}
                            >
                                {m.name}
                            </div>
                        ))}
                    </div>
                    {/* 本の表示エリア */}
                    <div className={`bookContainer ${isVerticalText ? 'vertical-text' : ''}`} style={{ position: 'relative' }}>
                        <div style={{ position: 'absolute', left: '-140px', top: '50%', transform: 'translateY(-50%)', width: 120, textAlign: 'right', color: '#388e3c', fontWeight: 'bold', fontSize: 16, pointerEvents: 'none', userSelect: 'none', zIndex: 100 }}>
                            {displayPage > (isVerticalText ? 2 : 1) && (isVerticalText ? '戻る' : '進む')}
                        </div>
                        <div className="leftPage" onClick={handleLeftPageClick}>
                            <span className={`pageNumber left`}>
                                {isVerticalText
                                    ? (displayPage + 1 <= totalPages ? displayPage + 1 : '') // 和書: 左ページが奇数
                                    : (totalPages - displayPage + 1) // 洋書: 左ページ、降順
                                }
                            </span>
                        </div>
                        <div className="rightPage" onClick={handleRightPageClick}>
                            <span className={`pageNumber right`}>
                                {isVerticalText
                                    ? displayPage // 和書: 右ページが偶数
                                    : (displayPage + 1 <= totalPages ? totalPages - displayPage : '') // 洋書: 右ページ、降順
                                }
                            </span>
                        </div>
                        <div style={{ position: 'absolute', right: '-140px', top: '50%', transform: 'translateY(-50%)', width: 120, textAlign: 'left', color: '#388e3c', fontWeight: 'bold', fontSize: 16, pointerEvents: 'none', userSelect: 'none', zIndex: 100 }}>
                            {displayPage < totalPages - 1 && (isVerticalText ? '進む' : '戻る')}
                        </div>
                        <div className="spine"></div>
                        {/* 複数のアニメーション要素 */}
                        {activeAnimations.map((animation) => (
                            <div
                                key={animation.id}
                                className={`pageFlip${animation.direction === 'forward' ? ' animate-forward' : ' animate-backward'}`}
                                onAnimationEnd={() => onAnimationEnd(animation.id)}
                                style={{ zIndex: 20 + animation.id }}
                            >
                                <div className="back"></div>
                            </div>
                        ))}
                    </div>

                    {/* 操作エリア */}
                    <div className="controls">
                        {/* 本の種類切り替えボタン */}
                        <div style={{ display: 'flex', marginRight: 16 }}>
                            <button
                                className={`viewModeButton ${!isVerticalText ? 'active' : ''}`}
                                onClick={() => setIsVerticalText(false)}
                            >
                                洋書
                            </button>
                            <button
                                className={`viewModeButton ${isVerticalText ? 'active' : ''}`}
                                onClick={() => setIsVerticalText(true)}
                            >
                                和書
                            </button>
                        </div>

                        {/* 残り時間カウントダウン */}
                        {(flipping && !hasActiveAnimations) ? (
                            <div style={{ minWidth: 60, textAlign: 'right', fontSize: 22, fontWeight: 'bold', color: '#388e3c', marginRight: 8 }}>
                                {Math.ceil(countdown / 1000)} 秒
                            </div>
                        ) : null}
                        <label className="flipIntervalLabel">
                            <input
                                type="number"
                                min="1"
                                value={flipIntervalMinutes}
                                onChange={(e) => setFlipIntervalMinutes(Number(e.target.value))}
                                placeholder="分単位"
                                className="intervalInput"
                            />
                            <span> 分に一回</span>
                        </label>
                        <button
                            className="controlButton"
                            onClick={() => {
                                setFlipping((f) => {
                                    if (f) {
                                        // 自動めくり停止時は進行中のアニメーションは継続
                                        // アニメーションはそのまま完了させる
                                    }
                                    return !f
                                })
                            }}
                        >
                            {flipping ? "自動めくり停止" : "自動めくり開始"}
                        </button>
                        <button
                            className="controlButton"
                            style={{ padding: '12px 24px', fontSize: 18 }}
                            onClick={() => {
                                setEditingTotalPages(true)
                                setShowProgressModal(true)
                            }}
                        >ページ数を編集</button>
                    </div>

                    {showProgressModal && (
                        <ReadingProgressModal
                            open={showProgressModal}
                            currentPage={currentPage}
                            maxPage={maxPage}
                            onClose={() => setShowProgressModal(false)}
                            onSubmit={(page) => {
                                handlePageChange(page)
                                setShowProgressModal(false)
                            }}
                        />
                    )}
                    {showProgressModal && (
                        <div
                            className="modalContainer"
                            onClick={(e) => {
                                if (e.target === e.currentTarget) {
                                    setShowProgressModal(false) // オーバーレイ以外をクリックした場合にモーダルを閉じる
                                    setTimeout(() => {
                                        window.location.href = `/rooms/${roomId}/chat` // チャット画面に戻る
                                    }, 0) // 非同期で遷移を確実に実行
                                }
                            }}
                        >
                            <div
                                className="modalContent"
                                onClick={(e) => e.stopPropagation()} // モーダル内のクリックを伝播させない
                            >
                                <h2>ページ数を編集</h2>
                                <div className="inputGroup">
                                    <label>現在のページ</label>
                                    <input
                                        type="number"
                                        min={1}
                                        value={currentPage}
                                        onChange={(e) => setCurrentPage(Number(e.target.value))}
                                    />
                                </div>
                                <div className="inputGroup">
                                    <label>本の最大ページ数</label>
                                    <input
                                        type="number"
                                        min={1}
                                        value={inputTotalPages}
                                        onChange={(e) => setInputTotalPages(Number(e.target.value))}
                                    />
                                </div>
                                <div className="buttonGroup">
                                    <button
                                        className="controlButton"
                                        onClick={async () => {
                                            if (inputTotalPages > 0 && currentPage > 0 && roomId) {
                                                try {
                                                    const updated = await roomApi.updateTotalPages(roomId, inputTotalPages)
                                                    setTotalPages(updated.totalPages ?? inputTotalPages)
                                                    saveAndBroadcastProgress(currentPage)
                                                } catch (e) {
                                                    alert("ページ数の更新に失敗しました")
                                                }
                                                setEditingTotalPages(false)
                                                setShowProgressModal(false)
                                            }
                                        }}
                                    >保存</button>
                                    <button
                                        className="controlButton"
                                        onClick={() => {
                                            setInputTotalPages(totalPages)
                                            setCurrentPage(displayPage)
                                            setEditingTotalPages(false)
                                            setShowProgressModal(false)
                                        }}
                                    >キャンセル</button>
                                </div>
                            </div>
                        </div>
                    )}

                </div>
            </div>
        </>
    )
}

/**
 * 読書ビューコンポーネント
 * 
 * ページめくりの仕様:
 * 
 * 1. 和書（縦書き）の場合:
 *    - 基本方向: 右から左へ読み進む
 *    - 右ページが偶数、左ページが奇数
 *    - 右ページをクリック → そのページが左方向へめくれて次のページ（数字増加）
 *    - 左ページをクリック → そのページが右方向へめくれて前のページ（数字減少）
 * 
 * 2. 洋書（横書き）の場合:
 *    - 基本方向: 左から右へ読み進む
 *    - 左ページが偶数、右ページが奇数
 *    - 左ページをクリック → そのページが右方向へめくれて次のページ（数字減少）
 *    - 右ページをクリック → そのページが左方向へめくれて前のページ（数字増加）
 * 
 * アニメーションの方向:
 * - forward: 左方向へめくれる動作
 * - backward: 右方向へめくれる動作
 * 
 * 自動めくり:
 * - 和書: 右ページを左方向へめくる（次のページへ）
 * - 洋書: 左ページを右方向へめくる（次のページへ）
 */

export default ReadingScreen
