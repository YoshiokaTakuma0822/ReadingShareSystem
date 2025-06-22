"use client"
import React, { useState } from 'react'

interface ReadingProgressModalProps {
    open: boolean
    currentPage: number
    maxPage: number
    onClose: () => void
    onSubmit: (page: number) => void
}

/**
 * ReadingProgressModal コンポーネント: 読書進捗入力モーダルを表示する Functional Component
 *
 * @param props.open - モーダルの表示/非表示フラグ
 * @param props.currentPage - 現在のページ数
 * @param props.maxPage - 許容される最大ページ数
 * @param props.onClose - モーダルを閉じるコールバック関数
 * @param props.onSubmit - 入力されたページ数を送信するコールバック関数
 *
 * @returns JSX.Element | null 読書進捗入力モーダルのReact要素、非表示時はnull
 */
const ReadingProgressModal: React.FC<ReadingProgressModalProps> = ({ open, currentPage, maxPage, onClose, onSubmit }) => {
    const [inputPage, setInputPage] = useState(currentPage)
    if (!open) return null
    return (
        <div style={{
            position: 'fixed', left: 0, top: 0, width: '100vw', height: '100vh', background: 'rgba(0,0,0,0.3)', zIndex: 1000,
            display: 'flex', alignItems: 'center', justifyContent: 'center'
        }}>
            <div style={{ background: '#fff', borderRadius: 12, padding: 32, minWidth: 320, boxShadow: '0 4px 24px rgba(0,0,0,0.2)' }}>
                <h2 style={{ fontSize: 20, marginBottom: 16 }}>進捗ページを入力</h2>
                <input
                    type="number"
                    min={1}
                    max={maxPage}
                    value={inputPage}
                    onChange={e => setInputPage(Math.max(1, Math.min(maxPage, Number(e.target.value))))}
                    style={{ width: '100%', fontSize: 18, padding: 8, borderRadius: 6, border: '1px solid #ccc', marginBottom: 20 }}
                />
                <div style={{ display: 'flex', gap: 12, justifyContent: 'flex-end' }}>
                    <button onClick={onClose} style={{ padding: '8px 18px', borderRadius: 6, border: '1px solid #aaa', background: '#f5f5f5', fontSize: 16 }}>キャンセル</button>
                    <button onClick={() => onSubmit(inputPage)} style={{ padding: '8px 18px', borderRadius: 6, border: 'none', background: '#2196f3', color: '#fff', fontSize: 16 }}>決定</button>
                </div>
            </div>
        </div>
    )
}

export default ReadingProgressModal
