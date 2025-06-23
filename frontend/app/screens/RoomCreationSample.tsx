/**
 * 部屋作成サンプル画面コンポーネント
 *
 * @author 02001
 * @componentId C1
 * @moduleName 部屋作成サンプル画面
 * @packageDocumentation
 */

"use client"

import React, { useState } from "react"

/**
 * RoomCreationSample コンポーネント: 部屋作成サンプル画面を表示する Functional Component
 *
 * @returns JSX.Element 部屋作成サンプル画面を描画するReact要素
 */
const RoomCreationSample: React.FC = () => {
    const [roomName, setRoomName] = useState("")
    const [genre, setGenre] = useState("")
    const [capacity, setCapacity] = useState("")
    const [password, setPassword] = useState("")
    const [comment, setComment] = useState("")

    return (
        <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", background: "#fff" }}>
            <div style={{ border: "2px solid #388e3c", background: "#fff", borderRadius: 14, width: 900, padding: 36, boxSizing: "border-box", boxShadow: "0 4px 24px 0 #b7e5c7" }}>
                <div style={{ fontWeight: "bold", fontSize: 28, marginBottom: 28, color: "#388e3c" }}>詳細設定</div>
                <div style={{ display: "flex", gap: 32, marginBottom: 32 }}>
                    <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 18 }}>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・部屋名</span>
                            <input value={roomName} onChange={e => setRoomName(e.target.value)} placeholder="部屋名を入力してください" style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, color: '#222' }} />
                        </div>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・ジャンル</span>
                            <select value={genre} onChange={e => setGenre(e.target.value)} style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, color: '#222' }}>
                                <option value="">ジャンルを選択してください</option>
                                <option value="文学">文学</option>
                                <option value="ミステリー">ミステリー</option>
                                <option value="SF">SF</option>
                                <option value="ファンタジー">ファンタジー</option>
                                <option value="ノンフィクション">ノンフィクション</option>
                                <option value="ビジネス">ビジネス</option>
                                <option value="歴史">歴史</option>
                                <option value="エッセイ">エッセイ</option>
                                <option value="児童書">児童書</option>
                                <option value="その他">その他</option>
                            </select>
                        </div>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・人数</span>
                            <input value={capacity} onChange={e => setCapacity(e.target.value)} placeholder="人数を入力してください" style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, color: '#222' }} />
                        </div>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・パスワード</span>
                            <input value={password} onChange={e => setPassword(e.target.value)} placeholder="パスワードを入力してください" style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, color: '#222' }} />
                        </div>
                    </div>
                    <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 18 }}>
                        <span style={{ width: '100%', color: "#388e3c", fontWeight: 500 }}>ひとことコメント</span>
                        <textarea value={comment} onChange={e => setComment(e.target.value)} placeholder="ひとことコメント" style={{ width: '100%', minHeight: 160, fontSize: 18, border: "1.5px solid #ccc", borderRadius: 4, padding: 8, color: '#222' }} />
                    </div>
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end", gap: 16 }}>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 16, padding: "8px 32px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>キャンセル</button>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 16, padding: "8px 32px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>部屋を作成</button>
                </div>
            </div>
        </div>
    )
}

export default RoomCreationSample
