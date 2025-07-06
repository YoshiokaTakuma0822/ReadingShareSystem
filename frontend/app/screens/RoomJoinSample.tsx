"use client"
import React, { useState } from "react"
import CustomDropdown from '../../components/CustomDropdown'

const RoomJoinSample: React.FC = () => {
    const [roomName, setRoomName] = useState("")
    const [genre, setGenre] = useState("")
    const [capacity, setCapacity] = useState("")
    const [comment, setComment] = useState("")

    const sampleRoom = {
        roomName: "夏目漱石読書会",
        genre: "文学",
        capacity: "10",
        comment: "毎週金曜夜に集まって読書します！お気軽にどうぞ。"
    }

    return (
        <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", background: "#fff" }}>
            <div style={{ border: "2px solid #388e3c", background: "#fff", borderRadius: 14, width: 900, padding: 36, boxSizing: "border-box", boxShadow: "0 4px 24px 0 #b7e5c7" }}>
                <div style={{ fontWeight: "bold", fontSize: 28, marginBottom: 28, color: "#388e3c" }}>部屋情報</div>
                <div style={{ display: "flex", gap: 32, marginBottom: 32 }}>
                    <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 18 }}>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・部屋名</span>
                            <input value={sampleRoom.roomName} readOnly disabled style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, background: '#f4f4f4', color: '#222' }} />
                        </div>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・ジャンル</span>
                            <div style={{ flex: 1, marginLeft: 8 }}>
                                <CustomDropdown
                                    options={[
                                        { value: '文学', label: '文学' },
                                        { value: 'ミステリー', label: 'ミステリー' },
                                        { value: 'SF', label: 'SF' },
                                        { value: 'ファンタジー', label: 'ファンタジー' },
                                        { value: 'ノンフィクション', label: 'ノンフィクション' },
                                        { value: 'ビジネス', label: 'ビジネス' },
                                        { value: '歴史', label: '歴史' },
                                        { value: 'エッセイ', label: 'エッセイ' },
                                        { value: '児童書', label: '児童書' },
                                        { value: 'その他', label: 'その他' }
                                    ]}
                                    value={sampleRoom.genre}
                                    onChange={() => { }} // 無効化されているので空の関数
                                    disabled={true}
                                />
                            </div>
                        </div>
                        <div style={{ display: "flex", alignItems: "center" }}>
                            <span style={{ width: 80, color: "#388e3c", fontWeight: 500 }}>・人数</span>
                            <input value={sampleRoom.capacity} readOnly disabled style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, background: '#f4f4f4', color: '#222' }} />
                        </div>
                    </div>
                    <div style={{ flex: 1, display: "flex", flexDirection: "column", gap: 18 }}>
                        <span style={{ width: '100%', color: "#388e3c", fontWeight: 500 }}>ひとことコメント</span>
                        <textarea value={sampleRoom.comment} readOnly disabled style={{ width: '100%', minHeight: 160, fontSize: 18, border: "1.5px solid #ccc", borderRadius: 4, padding: 8, background: '#f4f4f4', color: '#222' }} />
                    </div>
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end", gap: 16 }}>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 16, padding: "8px 32px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>キャンセル</button>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 16, padding: "8px 32px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>部屋に参加</button>
                </div>
            </div>
        </div>
    )
}

export default RoomJoinSample
