/**
 * アンケート作成サンプル画面コンポーネント
 *
 * @author 02001
 * @componentId C1
 * @moduleName アンケート作成サンプル画面
 * @packageDocumentation
 */

"use client"
import React, { useState } from "react"

/**
 * SurveyCreationSample コンポーネント: アンケート作成サンプル画面を表示する Functional Component
 *
 * @returns JSX.Element アンケート作成サンプル画面を描画するReact要素
 */
const SurveyCreationSample: React.FC = () => {
    const [title, setTitle] = useState("今日はどこの章まで読むか")
    const [options, setOptions] = useState(["第1章", "第2章"])
    const [endDate, setEndDate] = useState("2026-01-01T12:00")
    const [multi, setMulti] = useState(false)
    const [anonymous, setAnonymous] = useState(false)
    const [allowAdd, setAllowAdd] = useState(false)

    return (
        <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", background: "#fff" }}>
            <div style={{ border: "2px solid #388e3c", background: "#fff", borderRadius: 14, width: 700, padding: 36, boxSizing: "border-box", boxShadow: "0 4px 24px 0 #b7e5c7" }}>
                <div style={{ fontWeight: "bold", fontSize: 28, marginBottom: 28, color: "#388e3c" }}>アンケート作成</div>
                <div style={{ display: "flex", alignItems: "center", marginBottom: 18 }}>
                    <span style={{ width: 90, color: "#388e3c", fontWeight: 500 }}>・タイトル</span>
                    <input value={title} onChange={e => setTitle(e.target.value)} style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, color: '#222' }} />
                </div>
                <div style={{ display: "flex", alignItems: "flex-start", marginBottom: 18 }}>
                    <span style={{ width: 90, marginTop: 8, color: "#388e3c", fontWeight: 500 }}>・選択肢</span>
                    <div style={{ flex: 1, border: "2px solid #ccc", borderRadius: 16, padding: 16, display: "flex", flexDirection: "column", gap: 8, marginLeft: 8, background: '#f4f4f4' }}>
                        {options.map((opt, i) => (
                            <div key={i} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                                <span style={{ width: 28, textAlign: "right", color: '#388e3c', fontWeight: 500 }}>{i + 1}.</span>
                                <input value={opt} onChange={e => setOptions(options.map((o, j) => j === i ? e.target.value : o))} style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", color: '#222' }} />
                                {options.length > 2 && (
                                    <button type="button" aria-label="選択肢を削除" style={{ width: 28, height: 28, borderRadius: "50%", border: "1.5px solid #c00", color: "#c00", background: "#fff", fontWeight: "bold", fontSize: 18, cursor: "pointer" }} onClick={() => setOptions(options.filter((_, j) => j !== i))}>
                                        ×
                                    </button>
                                )}
                            </div>
                        ))}
                        <button type="button" style={{ margin: "12px auto 0", width: 36, height: 36, borderRadius: "50%", border: "1.5px solid #388e3c", fontSize: 24, background: "#fff", color: '#388e3c', cursor: "pointer" }} onClick={() => setOptions([...options, ""])}
                        >
                            ＋
                        </button>
                    </div>
                </div>
                <div style={{ display: "flex", alignItems: "center", marginBottom: 18 }}>
                    <span style={{ width: 90, color: "#388e3c", fontWeight: 500 }}>・投票終了日時</span>
                    <input type="datetime-local" value={endDate} onChange={e => setEndDate(e.target.value)} style={{ flex: 1, padding: 6, fontSize: 18, border: "1px solid #ccc", marginLeft: 8, color: '#222' }} />
                </div>
                <div style={{ display: "flex", gap: 24, marginBottom: 24, marginLeft: 90 }}>
                    <label style={{ color: '#388e3c', fontWeight: 500 }}><input type="checkbox" checked={multi} onChange={e => setMulti(e.target.checked)} /> 複数選択可</label>
                    <label style={{ color: '#388e3c', fontWeight: 500 }}><input type="checkbox" checked={anonymous} onChange={e => setAnonymous(e.target.checked)} /> 匿名投票</label>
                    <label style={{ color: '#388e3c', fontWeight: 500 }}><input type="checkbox" checked={allowAdd} onChange={e => setAllowAdd(e.target.checked)} /> 選択肢の追加を許可</label>
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end", gap: 16 }}>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 16, padding: "8px 24px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>キャンセル</button>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 16, padding: "8px 24px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>作成</button>
                </div>
            </div>
        </div>
    )
}

export default SurveyCreationSample
