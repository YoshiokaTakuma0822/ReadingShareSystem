"use client"
import React, { useEffect, useRef, useState } from 'react'
import ReadingProgressModal from './ReadingProgressModal' // 同じディレクトリにあると仮定

// 仮の部屋メンバー進捗データ
const members = [
    { name: 'N', page: 126, color: '#222' },
    { name: 'K', page: 180, color: '#222' },
    { name: 'Y', page: 90, color: '#222' },
    { name: 'A', page: 150, color: '#2196f3' }, // 自分
]
const maxPage = 300
const selfName = 'A' // 自分の頭文字
const PAGE_FLIP_INTERVAL = 3000 // 3秒ごとにページをめくる（デバッグ用）

interface ReadingScreenProps {
    roomId?: string
}

/**
 * ReadingScreen コンポーネント: 読書進捗表示と自動ページめくり機能を含む読書画面
 *
 * @param props.roomId - チャットページへ戻る際に使用するルームID（オプション）
 * @returns JSX.Element 読書画面を描画するReact要素
 */
const ReadingScreen: React.FC<ReadingScreenProps> = ({ roomId }) => {
    const [showProgressModal, setShowProgressModal] = useState(false)
    const [currentPage, setCurrentPage] = useState(150) // 自分のページ
    const [flipping, setFlipping] = useState(false)
    const [displayPage, setDisplayPage] = useState(currentPage)
    const [flippingPage, setFlippingPage] = useState<number | null>(null)
    const flipTimeout = useRef<NodeJS.Timeout | null>(null)

    // ページ自動めくり
    useEffect(() => {
        if (flipping) {
            if (displayPage < maxPage) {
                flipTimeout.current = setTimeout(() => {
                    setFlippingPage(displayPage + 1)
                    setDisplayPage(p => p + 1)
                }, PAGE_FLIP_INTERVAL)
            } else {
                setFlipping(false)
            }
        }
        return () => { if (flipTimeout.current) clearTimeout(flipTimeout.current) }
    }, [flipping, displayPage])

    // ページめくり終了時にcurrentPageを更新
    useEffect(() => {
        if (!flipping && displayPage !== currentPage) {
            setCurrentPage(displayPage)
        }
    }, [flipping, displayPage, currentPage])

    // ページめくりアニメーション用クラス
    const [pageFlipAnim, setPageFlipAnim] = useState(false)
    useEffect(() => {
        if (flippingPage !== null) {
            setPageFlipAnim(true)
            const t = setTimeout(() => {
                setPageFlipAnim(false)
                setFlippingPage(null)
            }, 1500)
            return () => clearTimeout(t)
        }
    }, [flippingPage])

    // 進捗率（例: 0.7 = 70%）
    const progressPercent = currentPage / maxPage

    // メンバー進捗（0~1）
    const memberProgress = members.map(m => ({
        ...m,
        percent: m.name === selfName ? currentPage / maxPage : m.page / maxPage,
        isMe: m.name === selfName,
    }))

    return (
        <div className="flex flex-col items-center justify-center w-full h-full" style={{ minHeight: '100vh', backgroundColor: 'var(--green-bg)' }}>
            {/* 進捗バー＋アイコン */}
            <div style={{ position: 'relative', width: 320, height: 40, marginBottom: 16 }}>
                {/* 枠線とゲージ（自分の進捗） */}
                <div style={{
                    position: 'absolute',
                    top: 10,
                    left: 0,
                    width: '100%',
                    height: 12,
                    borderRadius: 6,
                    backgroundColor: 'var(--green-bg)',
                    overflow: 'hidden',
                    border: '1px solid var(--border)'
                }}>
                    <div style={{
                        width: `${progressPercent * 100}%`,
                        height: '100%',
                        backgroundColor: 'var(--green-main)',
                        borderRadius: 6,
                    }}></div>
                </div>
                {/* メンバーアイコン */}
                {memberProgress.map(m => (
                    <div
                        key={m.name}
                        style={{
                            position: 'absolute',
                            left: `calc(${320 * m.percent}px - 15px)`,
                            top: 0,
                            width: 30,
                            height: 30,
                            borderRadius: '50%',
                            background: m.isMe ? 'var(--green-dark)' : 'var(--white)',
                            border: m.isMe ? '2px solid var(--green-main)' : '1px solid var(--border)',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            color: m.isMe ? 'var(--white)' : 'var(--green-dark)',
                            fontWeight: 'bold',
                            boxShadow: m.isMe ? '0 0 8px var(--green-light)' : '0 1px 3px rgba(0,0,0,0.08)',
                            zIndex: 2,
                            fontSize: 16,
                            transition: 'left 0.3s ease-out'
                        }}
                    >{m.name}</div>
                ))}
            </div>

            {/* 本のコンテナ */}
            <div style={{
                position: 'relative',
                width: 450,
                height: 300,
                margin: '0 auto',
                perspective: '1000px',
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center'
            }}>
                {/* 本の左ページ */}
                <div style={{
                    position: 'absolute',
                    left: '50%',
                    transform: 'translateX(-100%)', // 中央の左に配置
                    width: 200,
                    height: 280,
                    backgroundColor: 'var(--white)',
                    border: '1px solid #ccc',
                    borderRight: 'none',
                    boxShadow: 'inset 5px 0 15px rgba(0,0,0,0.1)',
                    borderRadius: '2px 0 0 2px',
                    zIndex: 3 // めくりページの下になるように調整
                }}>
                    {/* 左ページ内容 (空) */}
                    <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    </div>
                </div>

                {/* 本の右ページ（めくられていない状態） */}
                <div style={{
                    position: 'absolute',
                    left: '50%',
                    width: 200,
                    height: 280,
                    backgroundColor: 'var(--white)',
                    border: '1px solid #ccc',
                    borderLeft: 'none',
                    boxShadow: 'inset -5px 0 15px rgba(0,0,0,0.1)',
                    borderRadius: '0 2px 2px 0',
                    zIndex: 2 // めくりページの下になるように調整
                }}>
                    {/* 右ページ内容 (空) */}
                    <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    </div>
                </div>

                {/* 背表紙 */}
                <div style={{
                    position: 'absolute',
                    left: '50%',
                    top: '50%',
                    transform: 'translate(-50%, -50%)',
                    width: 10,
                    height: 280,
                    backgroundColor: '#8b4513',
                    borderRadius: '0 0 0 0',
                    zIndex: 5, // 最前面
                    boxShadow: '0 0 10px rgba(0,0,0,0.5)'
                }}></div>

                {/* 新しい「めくれ上がるページ」 (左ページがめくられるイメージ) */}
                {flippingPage !== null && (
                    <div style={{
                        position: 'absolute',
                        left: '50%',
                        top: '50%',
                        transformOrigin: '0% 50%', // 中央の背表紙を軸に
                        width: 200,
                        height: 280,
                        backgroundColor: 'var(--white)',
                        border: '1px solid #ccc',
                        transition: 'transform 1.5s cubic-bezier(.4, 0.0, .2, 1), box-shadow 1.5s ease-out',
                        zIndex: 10,
                        backfaceVisibility: 'hidden',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        fontSize: '2em',
                        fontWeight: 'bold',
                        color: '#555',
                        borderRadius: '2px 0 0 2px',
                        boxShadow: pageFlipAnim ? '0 10px 20px rgba(0,0,0,0.3)' : '0 2px 5px rgba(0,0,0,0.1)',
                        // 左ページが右ページに重なるアニメーション
                        transform: pageFlipAnim
                            ? 'translateY(-50%) rotateY(0deg)'
                            : 'translateY(-50%) rotateY(-180deg)',
                        transformStyle: 'preserve-3d',
                        WebkitTransformStyle: 'preserve-3d',
                        MozTransformStyle: 'preserve-3d',
                    }}>
                        {/* めくられているページの裏側 */}
                        <div style={{
                            position: 'absolute',
                            left: 0,
                            top: 0,
                            width: '100%',
                            height: '100%',
                            backgroundColor: '#eee',
                            transform: 'rotateY(180deg)',
                            backfaceVisibility: 'hidden',
                            borderRadius: '2px 0 0 2px',
                            zIndex: -1,
                        }}></div>
                    </div>
                )}
            </div>

            {/* ページ数表示 */}
            <div className="mt-2 text-lg">{displayPage + 1} / {maxPage}</div>

            {/* メッセージ欄・進捗入力・ページめくり操作・チャットに戻るボタン */}
            <div style={{ display: 'flex', alignItems: 'center', marginTop: 32, gap: 8, flexWrap: 'wrap', justifyContent: 'center' }}>
                <input
                    type="text"
                    style={{
                        flex: 1,
                        padding: 12,
                        borderRadius: 8,
                        border: '1px solid var(--border)',
                        fontSize: 18,
                        minWidth: '200px',
                        background: 'var(--green-bg)',
                        color: 'var(--accent)'
                    }}
                    placeholder="メッセージを入力..."
                />
                <button
                    style={{
                        padding: '12px 24px',
                        borderRadius: 8,
                        border: '1px solid var(--border)',
                        fontSize: 18,
                        background: 'var(--green-light)',
                        color: 'var(--accent)',
                        cursor: 'pointer',
                        boxShadow: 'none',
                        fontWeight: 'bold'
                    }}
                >
                    送信
                </button>
                <button
                    onClick={() => setShowProgressModal(true)}
                    style={{
                        padding: '12px 24px',
                        borderRadius: 8,
                        border: '1px solid var(--border)',
                        fontSize: 18,
                        background: 'var(--green-bg)',
                        color: 'var(--accent)',
                        cursor: 'pointer',
                        boxShadow: 'none',
                        fontWeight: 'bold'
                    }}
                >
                    進捗入力
                </button>
                <button
                    onClick={() => { setFlipping(f => !f); setDisplayPage(currentPage) }}
                    style={{
                        padding: '12px 24px',
                        borderRadius: 8,
                        border: '1px solid var(--border)',
                        fontSize: 18,
                        background: flipping ? 'var(--green-light)' : 'var(--white)',
                        color: 'var(--accent)',
                        cursor: 'pointer',
                        boxShadow: 'none',
                        fontWeight: 'bold'
                    }}
                >
                    {flipping ? '自動めくり停止' : '自動めくり開始'}
                </button>
                {roomId && (
                    <button
                        onClick={() => window.location.href = `/rooms/${roomId}/chat`}
                        style={{
                            padding: '12px 24px',
                            borderRadius: 8,
                            border: '1px solid var(--border)',
                            fontSize: 18,
                            background: '#2196f3',
                            color: 'white',
                            cursor: 'pointer',
                            boxShadow: 'none',
                            fontWeight: 'bold'
                        }}
                    >
                        💬 チャットに戻る
                    </button>
                )}
            </div>

            {/* 進捗入力モーダル */}
            {showProgressModal && (
                <ReadingProgressModal
                    open={showProgressModal}
                    currentPage={currentPage}
                    maxPage={maxPage}
                    onClose={() => setShowProgressModal(false)}
                    onSubmit={page => { setCurrentPage(page); setDisplayPage(page); setShowProgressModal(false) }}
                />
            )}
        </div>
    )
}

export default ReadingScreen
