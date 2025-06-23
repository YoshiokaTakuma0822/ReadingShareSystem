/**
 * アンケート回答サンプル画面コンポーネント
 *
 * @author 02001
 * @componentId C1
 * @moduleName アンケート回答サンプル画面
 * @packageDocumentation
 */

"use client"

import React, { useState } from "react"

const options = ["芝浦 太郎", "芝浦 次郎", "芝浦 三郎"]

/**
 * SurveyAnswerSample コンポーネント: サンプルのアンケート回答画面を表示する Functional Component
 *
 * @returns JSX.Element アンケート回答サンプル画面を描画するReact要素
 */
const SurveyAnswerSample: React.FC = () => {
    const [selected, setSelected] = useState<number | null>(null)
    return (
        <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", background: "#fff" }}>
            <div style={{ border: "2px solid #388e3c", background: "#fff", borderRadius: 14, width: 600, padding: 36, boxSizing: "border-box", position: "relative", boxShadow: "0 4px 24px 0 #b7e5c7" }}>
                <div style={{ fontWeight: "bold", fontSize: 32, textAlign: "center", marginBottom: 32, color: "#388e3c" }}>犯人は誰</div>
                <button style={{ position: "absolute", top: 16, right: 16, width: 36, height: 36, fontSize: 32, border: "1.5px solid #388e3c", background: "#fff", borderRadius: 2, cursor: "pointer", color: '#388e3c' }}>×</button>
                <div style={{ display: "flex", flexDirection: "column", gap: 28, marginBottom: 32 }}>
                    {options.map((opt, i) => (
                        <label key={i} style={{ display: "flex", alignItems: "center", gap: 18, fontSize: 22, color: '#388e3c', fontWeight: 500 }}>
                            <span style={{ width: 32, height: 32, border: "1.5px solid #388e3c", borderRadius: "50%", display: "inline-block", marginRight: 8, background: selected === i ? "#388e3c" : "#fff" }}>
                                <input type="radio" name="answer" checked={selected === i} onChange={() => setSelected(i)} style={{ opacity: 0, width: 0, height: 0 }} />
                            </span>
                            {opt}
                        </label>
                    ))}
                </div>
                <div style={{ display: "flex", justifyContent: "flex-end" }}>
                    <button style={{ border: "1.5px solid #388e3c", borderRadius: 4, background: "#fff", fontSize: 18, padding: "8px 32px", cursor: "pointer", color: '#388e3c', fontWeight: 600 }}>回答する</button>
                </div>
            </div>
        </div>
    )
}

export default SurveyAnswerSample
