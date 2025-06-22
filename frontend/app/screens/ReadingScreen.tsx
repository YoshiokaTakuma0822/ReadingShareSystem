"use client"
import React, { useState, useEffect, useRef } from 'react'
import ReadingProgressModal from './ReadingProgressModal' // 同じディレクトリにあると仮定
import { roomApi } from '../../lib/roomApi';

// 仮の部屋メンバー進捗データ
const members = [
    { name: 'N', page: 126, color: '#222' },
    { name: 'K', page: 180, color: '#222' },
    { name: 'Y', page: 90, color: '#222' },
    { name: 'A', page: 150, color: '#2196f3' }, // 自分
]
const selfName = 'A' // 自分の頭文字
const PAGE_FLIP_INTERVAL = 3000 // 3秒ごとにページをめくる（デバッグ用）

interface ReadingScreenProps {
    roomId?: string
}

const ReadingScreen: React.FC<ReadingScreenProps> = ({ roomId }) => {
    // ユーザーID・ホストIDの状態
    const [currentUserId, setCurrentUserId] = useState<string | undefined>(undefined)
    const [hostUserId, setHostUserId] = useState<string | undefined>(undefined)

    // ページ数編集モーダル表示制御
    const [showProgressModal, setShowProgressModal] = useState(false)

    // ページ数編集用
    const [maxPage, setMaxPage] = useState<number | undefined>(undefined)
    const [editPageSpeed, setEditPageSpeed] = useState<number>(3)

    const [currentPage, setCurrentPage] = useState(150) // 自分のページ
    const [flipping, setFlipping] = useState(false)
    const [displayPage, setDisplayPage] = useState(currentPage)
    const [flippingPage, setFlippingPage] = useState<number | null>(null)
    const flipTimeout = useRef<NodeJS.Timeout | null>(null)

    // ページ変更ハンドラ
    const handlePageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCurrentPage(Number(e.target.value))
    }

    // 設定保存ハンドラ（API連携は後で追加）
    const handleSaveSettings = async () => {
        if (roomId && maxPage) {
            await roomApi.updateRoom(roomId, { maxPage })
        }
        setShowProgressModal(false)
    }

    // 自動ページ送り開始・停止・チャット遷移（仮実装）
    const handleStartAutoFlip = () => setFlipping(true)
    const handleStopAutoFlip = () => setFlipping(false)
    const handleGoToChat = () => {/* 画面遷移処理 */}

    // 部屋情報取得
    useEffect(() => {
        if (roomId) {
            roomApi.getRoom(roomId).then(room => {
                setMaxPage(room.maxPage)
                setHostUserId(room.hostUserId)
                // 必要なら他の部屋情報もここでセット
            })
        }
        // 仮: ログインユーザーID取得（本来は認証情報から取得）
        setCurrentUserId('dummy-user-id')
    }, [roomId])

    // ページ自動めくり
    useEffect(() => {
        if (flipping) {
            if (displayPage < (maxPage || 0)) {
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
    const progressPercent = currentPage / (maxPage || 1)

    // ユーザー名から色を決定する関数
    function getUserColor(name: string) {
        const colors = [
            '#388e3c', // 緑
            '#1976d2', // 青
            '#fbc02d', // 黄
            '#d32f2f', // 赤
            '#8e24aa', // 紫
            '#00838f', // シアン
            '#f57c00', // オレンジ
            '#5d4037', // 茶
        ];
        let hash = 0;
        for (let i = 0; i < name.length; i++) {
            hash = name.charCodeAt(i) + ((hash << 5) - hash);
        }
        return colors[Math.abs(hash) % colors.length];
    }

    // maxPage未設定時の分岐
    if (maxPage === undefined || maxPage === 0) {
        if (currentUserId && hostUserId && currentUserId === hostUserId) {
            // 作成者→ページ数入力モーダル
            return (
                <ReadingProgressModal
                    open={true}
                    currentPage={1}
                    maxPage={9999}
                    onClose={() => setShowProgressModal(false)}
                    onSubmit={page => setMaxPage(page)}
                />
            );
        } else {
            // 参加者→待機中オーバーレイ
            return (
                <div style={{ position: 'fixed', top: 0, left: 0, width: '100vw', height: '100vh', background: 'rgba(0,0,0,0.5)', zIndex: 1000, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <div style={{ background: '#fff', borderRadius: 16, padding: 40, minWidth: 320, boxShadow: '0 8px 32px rgba(0,0,0,0.2)', textAlign: 'center' }}>
                        <h2 style={{ fontSize: 22, marginBottom: 16 }}>部屋作成者がページ数を入力するまでお待ちください</h2>
                        <div style={{ fontSize: 16, color: '#888' }}>しばらくお待ちください...</div>
                    </div>
                </div>
            );
        }
    }

    // 本の見開きUI用：左ページ・右ページ番号
    const leftPage = displayPage % 2 === 0 ? displayPage - 1 : displayPage
    const rightPage = leftPage + 1

    // 進捗バーのリアルタイム化（API連携雛形）
    const [memberStates, setMemberStates] = useState(members)
    useEffect(() => {
        // TODO: WebSocketやAPIで定期的に他ユーザーの進捗を取得してsetMemberStatesする
        // ここでは仮に3秒ごとにランダム進捗を更新
        const interval = setInterval(() => {
            setMemberStates(prev => prev.map(m => ({ ...m, page: Math.max(1, Math.min((maxPage || 300), m.page + (Math.random() > 0.5 ? 1 : 0))) })))
        }, 3000)
        return () => clearInterval(interval)
    }, [maxPage])
    const memberProgress = memberStates.map(m => ({
        ...m,
        percent: m.name === selfName ? displayPage / (maxPage || 1) : m.page / (maxPage || 1),
        isMe: m.name === selfName,
        color: getUserColor(m.name),
    }))

    return (
        <div style={{
            position: 'fixed',
            top: 0,
            left: 0,
            width: '100vw',
            height: '100vh',
            background: 'rgba(0,0,0,0.6)',
            zIndex: 1000,
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
        }}>
            <div style={{ background: '#fff', borderRadius: 16, padding: 32, minWidth: 500, minHeight: 520, boxShadow: '0 8px 32px rgba(0,0,0,0.2)', position: 'relative', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                {/* 本の見開きUI＋進捗バー */}
                <div style={{ position: 'relative', width: 420, height: 320, marginBottom: 24, display: 'flex', flexDirection: 'row', boxShadow: '0 4px 16px rgba(0,0,0,0.10)', borderRadius: 16, background: '#f5f5dc' }}>
                    {/* 左ページ */}
                    <div style={{ flex: 1, borderRight: '2px solid #c0b283', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
                        <span style={{ fontSize: 48, color: '#bdbdbd' }}>📖</span>
                        <span style={{ position: 'absolute', bottom: 8, left: 12, fontSize: 16, color: '#888' }}>{leftPage > 0 ? leftPage : ''}</span>
                    </div>
                    {/* 綴じ目 */}
                    <div style={{ width: 8, background: 'linear-gradient(90deg, #c0b283 0%, #fff 100%)' }} />
                    {/* 右ページ */}
                    <div style={{ flex: 1, borderLeft: '2px solid #c0b283', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', position: 'relative' }}>
                        <span style={{ fontSize: 48, color: '#bdbdbd' }}>📖</span>
                        <span style={{ position: 'absolute', bottom: 8, right: 12, fontSize: 16, color: '#888' }}>{rightPage <= (maxPage || 0) ? rightPage : ''}</span>
                    </div>
                    {/* 進捗バー（本の上部に重ねる） */}
                    <div style={{ position: 'absolute', top: -24, left: 0, width: 420, height: 40 }}>
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
                        {/* メンバーアイコン（頭文字） */}
                        {memberProgress.map(m => (
                            <div
                                key={m.name}
                                style={{
                                    position: 'absolute',
                                    left: `calc(${420 * m.percent}px - 15px)`,
                                    top: 0,
                                    width: 30,
                                    height: 30,
                                    borderRadius: '50%',
                                    background: m.color,
                                    border: m.isMe ? '2px solid var(--green-main)' : '1px solid var(--border)',
                                    display: 'flex',
                                    alignItems: 'center',
                                    justifyContent: 'center',
                                    color: '#fff',
                                    fontWeight: 'bold',
                                    boxShadow: m.isMe ? '0 0 8px var(--green-light)' : '0 1px 3px rgba(0,0,0,0.08)',
                                    zIndex: 2,
                                    fontSize: 16,
                                    transition: 'left 0.3s ease-out'
                                }}
                            >{m.name.charAt(0).toUpperCase()}</div>
                        ))}
                    </div>
                </div>
                {/* ページ数・速度の編集UI（編集可能に） */}
                <div style={{ marginBottom: 16, display: 'flex', flexDirection: 'column', gap: 8, alignItems: 'center' }}>
                    <div>
                        <label>現在のページ：</label>
                        <button onClick={() => setDisplayPage(p => Math.max(1, p - 2))} style={{ marginRight: 8, padding: '2px 8px', borderRadius: 6, background: '#bdbdbd', color: '#333', border: 'none' }}>＜</button>
                        <input type="number" value={displayPage} min={1} max={maxPage} onChange={e => setDisplayPage(Number(e.target.value))} style={{ width: 60, marginRight: 8 }} />
                        <button onClick={() => setDisplayPage(p => Math.min((maxPage || 1), p + 2))} style={{ marginRight: 16, padding: '2px 8px', borderRadius: 6, background: '#bdbdbd', color: '#333', border: 'none' }}>＞</button>
                        <span>/ {maxPage}ページ</span>
                    </div>
                    <div>
                        <label>めくり速度（秒）：</label>
                        <input type="number" value={editPageSpeed} min={1} max={30} onChange={e => setEditPageSpeed(Number(e.target.value))} style={{ width: 60, marginRight: 8 }} />
                        <button onClick={handleSaveSettings} style={{ marginLeft: 8, padding: '4px 12px', borderRadius: 6, background: '#388e3c', color: '#fff', border: 'none' }}>保存</button>
                    </div>
                    <div style={{ display: 'flex', gap: 8, marginTop: 8 }}>
                        <button onClick={handleStartAutoFlip} style={{ padding: '4px 12px', borderRadius: 6, background: '#1976d2', color: '#fff', border: 'none' }}>自動ページ送り開始</button>
                        <button onClick={handleStopAutoFlip} style={{ padding: '4px 12px', borderRadius: 6, background: '#bdbdbd', color: '#333', border: 'none' }}>自動ページ送り停止</button>
                        <button onClick={handleGoToChat} style={{ padding: '4px 12px', borderRadius: 6, background: '#388e3c', color: '#fff', border: 'none' }}>チャット画面に戻る</button>
                    </div>
                </div>
                {/* 閉じるボタン */}
                <button onClick={() => setShowProgressModal(false)} style={{ position: 'absolute', top: 16, right: 16, background: '#388e3c', color: '#fff', border: 'none', borderRadius: 8, padding: '6px 16px', cursor: 'pointer' }}>閉じる</button>
            </div>
        </div>
    )
}

export default ReadingScreen
