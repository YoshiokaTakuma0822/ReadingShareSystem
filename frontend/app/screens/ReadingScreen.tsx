"use client"
import React, { useEffect, useRef, useState } from "react"
import { authStorage } from '../../lib/authUtils'
import { readingStateApi } from '../../lib/readingStateApi'
import { roomApi } from '../../lib/roomApi'
import { RoomMember } from '../../types/room'
import ReadingProgressModal from "./ReadingProgressModal"
import './ReadingScreen.css'

const maxPage = 300

// 本の進行方向を表す型
type ReadingDirection = 'next' | 'prev'

// アニメーションの物理的な方向を表す型
type AnimationDirection = 'toLeft' | 'toRight'

// アクティブなアニメーションの型
type ActiveAnimation = {
    id: number
    direction: AnimationDirection
    displayPage: number
    pageNumber: string // 捲られるページに表示する番号（表面）
    backPageNumber: string // 捲られるページの裏面に表示する番号
}

/**
 * ReadingScreen - 和書（縦書き）と洋書（横書き）の電子書籍ビューアーコンポーネント
 *
 * 読書の進行方向：
 * - 和書（縦書き）: 右から左へ進む。右ページが偶数、左ページが奇数。
 * - 洋書（横書き）: 左から右へ進む。左ページが偶数、右ページが奇数。
 *
 * ページめくりの動作：
 * 1. 和書の場合：
 *    - 次へ進む: 右ページを左にめくる（右から左へ）
 *    - 前に戻る: 左ページを右にめくる（左から右へ）
 *
 * 2. 洋書の場合：
 *    - 次へ進む: 左ページを右にめくる（左から右へ）
 *    - 前に戻る: 右ページを左にめくる（右から左へ）
 *
 * 自動めくり：
 * - 設定された間隔で次のページに進む
 * - 和書/洋書それぞれの読書方向に従ってめくる
 */

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
    const [activeAnimations, setActiveAnimations] = useState<ActiveAnimation[]>([])
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

        // 常に偶数ページに調整（和書/洋書共に表示ページは偶数ベース）
        const adjustedPage = Math.max(2, page % 2 === 0 ? page : page - 1)

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

    // --- ページめくりのユーティリティ関数 ---

    /**
     * クリックされたページ位置から読書の進行方向を決定
     *
     * 物理的なイメージ：クリックしたページを手で掴んで捲る
     *
     * 和書（右から左へ読む）の場合：
     * - 左ページ（奇数）をクリック → 左ページを手で掴んで右に捲る → 次のページが見える（next）
     * - 右ページ（偶数）をクリック → 右ページを手で掴んで左に捲る → 前のページが見える（prev）
     *
     * 洋書（左から右へ読む）の場合：
     * - 左ページ（偶数）をクリック → 左ページを手で掴んで右に捲る → 前のページが見える（prev）
     * - 右ページ（奇数）をクリック → 右ページを手で掴んで左に捲る → 次のページが見える（next）
     */
    const getReadingDirectionFromClick = (isLeftPage: boolean): ReadingDirection => {
        if (isVerticalText) {
            // 和書：右から左へ読む
            // 左ページを捲ると次へ、右ページを捲ると前へ
            return isLeftPage ? 'next' : 'prev'
        } else {
            // 洋書：左から右へ読む
            // 左ページを捲ると前へ、右ページを捲ると次へ
            return isLeftPage ? 'prev' : 'next'
        }
    }

    /**
     * クリックされたページ位置から物理的なアニメーション方向を決定
     * クリックされたページが捲られる方向
     */
    const getAnimationDirectionFromClick = (isLeftPage: boolean): AnimationDirection => {
        // 左ページをクリック → 右方向に捲れる
        // 右ページをクリック → 左方向に捲れる
        return isLeftPage ? 'toRight' : 'toLeft'
    }

    /**
     * 次/前のページの番号を計算（2ページずつ）
     */
    const getNextPageNumber = (currentPage: number, direction: ReadingDirection): number => {
        const delta = direction === 'next' ? 2 : -2
        const newPage = currentPage + delta
        return Math.max(1, Math.min(newPage, totalPages - 1))
    }

    /**
     * 和書/洋書に応じてページ番号を調整
     */
    const adjustPageNumber = (page: number, direction: ReadingDirection): number => {
        // 和書と洋書で偶数/奇数の配置が逆
        if (isVerticalText) {
            // 和書: 右ページを偶数に（displayPageは偶数）
            return Math.max(2, page % 2 === 0 ? page : page - 1)
        } else {
            // 洋書: 左ページを偶数に（displayPageは偶数）
            return Math.max(2, page % 2 === 0 ? page : page - 1)
        }
    }

    /**
     * 捲られるページの裏面のページ番号を計算
     * 表面のページ番号から裏面の番号を決定
     */
    const getBackPageNumber = (frontPageNumber: string, isLeftPage: boolean): string => {
        const frontNum = parseInt(frontPageNumber)
        if (isNaN(frontNum)) return ''

        // 裏面は表面の前後のページ番号
        // 左ページの裏面は右ページ、右ページの裏面は左ページ
        if (isLeftPage) {
            // 左ページの裏面は右ページ（偶数）
            const backNum = isVerticalText ? frontNum - 1 : frontNum + 1
            return backNum > 0 && backNum <= totalPages ? backNum.toString() : ''
        } else {
            // 右ページの裏面は左ページ（奇数）
            const backNum = isVerticalText ? frontNum + 1 : frontNum - 1
            return backNum > 0 && backNum <= totalPages ? backNum.toString() : ''
        }
    }

    /**
     * ページめくりのメインハンドラ
     * クリックされたページ位置に基づいてアニメーションと読書進行を処理
     */
    const handlePageClick = (isLeftPage: boolean) => {
        const newAnimationId = animationIdCounter
        setAnimationIdCounter(prev => prev + 1)

        // クリックされたページ位置から方向を決定
        const readingDirection = getReadingDirectionFromClick(isLeftPage)
        const animDirection = getAnimationDirectionFromClick(isLeftPage)

        // 捲られるページの番号を決定
        const flippingPageNumber = isLeftPage
            ? (isVerticalText
                ? (displayPage + 1 <= totalPages ? (displayPage + 1).toString() : '') // 和書: 左ページが奇数
                : displayPage.toString() // 洋書: 左ページが偶数
            )
            : (isVerticalText
                ? displayPage.toString() // 和書: 右ページが偶数
                : (displayPage + 1 <= totalPages ? (displayPage + 1).toString() : '') // 洋書: 右ページが奇数
            )

        // 新しいアニメーションを追加
        setActiveAnimations(prev => [...prev, {
            id: newAnimationId,
            direction: animDirection,
            displayPage: displayPage,
            pageNumber: flippingPageNumber,
            backPageNumber: getBackPageNumber(flippingPageNumber, isLeftPage)
        }])

        // ページ更新（2ページずつ）
        setDisplayPage(prev => {
            const newPage = getNextPageNumber(prev, readingDirection)
            const adjustedPage = adjustPageNumber(newPage, readingDirection)
            setCurrentPage(adjustedPage)
            return newPage
        })
    }

    /**
     * 左ページクリックハンドラ
     * 左ページが右方向に捲られる
     */
    const handleLeftPageClick = () => {
        handlePageClick(true)
    }

    /**
     * 右ページクリックハンドラ
     * 右ページが左方向に捲られる
     */
    const handleRightPageClick = () => {
        handlePageClick(false)
    }

    // 自動めくりエフェクト - 読書方向に合わせて自動めくり
    useEffect(() => {
        let timer: NodeJS.Timeout
        if (flipping && !hasActiveAnimations) {
            // 自動めくりでは常に次のページに進む
            // 和書：左ページをクリック（右に捲れて次へ）
            // 洋書：右ページをクリック（左に捲れて次へ）
            timer = setTimeout(() => {
                if (isVerticalText) {
                    handleLeftPageClick() // 和書：左ページをクリック
                } else {
                    handleRightPageClick() // 洋書：右ページをクリック
                }
            }, flipIntervalMs)
        }
        return () => clearTimeout(timer)
    }, [flipping, hasActiveAnimations, displayPage, flipIntervalMs, isVerticalText])

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
                            {displayPage > (isVerticalText ? 2 : 1) && (isVerticalText ? '進む' : '戻る')}
                        </div>
                        <div className="leftPage" onClick={handleLeftPageClick}>
                            <span className={`pageNumber left`}>
                                {isVerticalText
                                    ? (displayPage + 1 <= totalPages ? displayPage + 1 : '') // 和書: 左ページが奇数
                                    : displayPage // 洋書: 左ページが偶数（小さい番号）
                                }
                            </span>
                        </div>
                        <div className="rightPage" onClick={handleRightPageClick}>
                            <span className={`pageNumber right`}>
                                {isVerticalText
                                    ? displayPage // 和書: 右ページが偶数
                                    : (displayPage + 1 <= totalPages ? displayPage + 1 : '') // 洋書: 右ページが奇数（大きい番号）
                                }
                            </span>
                        </div>
                        <div style={{ position: 'absolute', right: '-140px', top: '50%', transform: 'translateY(-50%)', width: 120, textAlign: 'left', color: '#388e3c', fontWeight: 'bold', fontSize: 16, pointerEvents: 'none', userSelect: 'none', zIndex: 100 }}>
                            {displayPage < totalPages - 1 && (isVerticalText ? '戻る' : '進む')}
                        </div>
                        <div className="spine"></div>
                        {/* 複数のアニメーション要素 */}
                        {activeAnimations.map((animation) => (
                            <div
                                key={animation.id}
                                className={`pageFlip${animation.direction === 'toLeft' ? ' animate-left' : ' animate-right'}`}
                                onAnimationEnd={() => onAnimationEnd(animation.id)}
                                style={{ zIndex: 20 + animation.id }}
                            >
                                {/* 表面のページ番号 */}
                                {animation.pageNumber && (
                                    <span className={`pageNumber ${animation.direction === 'toLeft' ? 'left' : 'right'} page-front`}>
                                        {animation.pageNumber}
                                    </span>
                                )}

                                <div className="back">
                                    {/* 裏面のページ番号 */}
                                    {animation.backPageNumber && (
                                        <span className={`pageNumber ${animation.direction === 'toLeft' ? 'right' : 'left'} page-back`}>
                                            {animation.backPageNumber}
                                        </span>
                                    )}
                                </div>
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
 * ReadingScreen - 和書（縦書き）と洋書（横書き）の電子書籍ビューアーコンポーネント
 *
 * 基本原則：「クリックした方のページを手で掴んで捲る」
 *
 * ページ番号の配置：
 * - 和書：右ページが偶数（小さい番号）、左ページが奇数（大きい番号）
 * - 洋書：左ページが偶数（小さい番号）、右ページが奇数（大きい番号）
 *
 * ページめくりの動作：
 *
 * 1. 和書（縦書き）の場合：
 *    - 基本方向: 右から左へ読み進む
 *    - 左ページをクリック → 左ページを手で掴んで右方向に捲る → 次のページが見える
 *    - 右ページをクリック → 右ページを手で掴んで左方向に捲る → 前のページが見える
 *
 * 2. 洋書（横書き）の場合：
 *    - 基本方向: 左から右へ読み進む
 *    - 左ページをクリック → 左ページを手で掴んで右方向に捲る → 前のページが見える
 *    - 右ページをクリック → 右ページを手で掴んで左方向に捲る → 次のページが見える
 *
 * アニメーションの方向：
 * - toLeft: 左方向へ捲れる動作（右ページがクリックされた時）
 * - toRight: 右方向へ捲れる動作（左ページがクリックされた時）
 *
 * 自動めくり：
 * - 和書: 左ページを右方向へ捲る（次のページへ）
 * - 洋書: 右ページを左方向へ捲る（次のページへ）
 */

export default ReadingScreen
