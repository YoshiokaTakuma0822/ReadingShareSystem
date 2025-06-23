/**
 * アンケート結果サンプル画面コンポーネント
 *
 * @author 02001
 * @componentId C1
 * @moduleName アンケート結果サンプル画面
 * @packageDocumentation
 */

"use client"

import React from "react"

const results = [
    { name: "芝浦 太郎", votes: 1 },
    { name: "芝浦 次郎", votes: 5 },
    { name: "芝浦 三郎", votes: 3 },
]

/**
 * SurveyResultSampleコンポーネント: サンプルアンケート結果を表示するFunctional Component
 *
 * @returns JSX.Element サンプル結果を描画するReact要素
 */
const SurveyResultSample: React.FC = () => {
    return (
        <div style={{ minHeight: "100vh", display: "flex", alignItems: "center", justifyContent: "center", background: "#fff" }}>
            <div style={{ border: "2px solid #388e3c", background: "#fff", borderRadius: 14, width: 600, padding: 36, boxSizing: "border-box", position: "relative", boxShadow: "0 4px 24px 0 #b7e5c7" }}>
                <div style={{ fontWeight: "bold", fontSize: 32, textAlign: "center", marginBottom: 32, color: "#388e3c" }}>犯人は誰</div>
                <button style={{ position: "absolute", top: 16, right: 16, width: 36, height: 36, fontSize: 32, border: "1.5px solid #388e3c", background: "#fff", borderRadius: 2, cursor: "pointer", color: '#388e3c' }}>×</button>
                <div style={{ display: "flex", flexDirection: "column", gap: 36, marginBottom: 32 }}>
                    {results.map((r, i) => (
                        <div key={i} style={{ display: "flex", alignItems: "center", justifyContent: "space-between", fontSize: 22, color: '#388e3c', fontWeight: 500 }}>
                            <span>{r.name}</span>
                            <span>{r.votes}票</span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}

export default SurveyResultSample
