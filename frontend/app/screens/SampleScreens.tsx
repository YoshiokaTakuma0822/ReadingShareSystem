/**
 * サンプル画面集コンポーネント
 *
 * @author 02001
 * @componentId C1
 * @moduleName サンプル画面集
 * @packageDocumentation
 */

"use client"

import React from "react"

const links = [
    { label: "HomeScreen (W1)", path: "/screens/HomeScreen" },
    { label: "LoginScreen (W2)", path: "/screens/LoginScreen" },
    { label: "RegisterScreen (W3)", path: "/screens/RegisterScreen" },
    { label: "GroupChatScreen (W5)", path: "/screens/GroupChatScreen" },
    { label: "RoomCreationModal (W4)", path: "/screens/RoomCreationModal" },
    { label: "RoomJoinModal (W4)", path: "/screens/RoomJoinModal" },
    { label: "SurveyCreationModal (W6)", path: "/screens/SurveyCreationModal" },
    { label: "SurveyAnswerModal (W7)", path: "/screens/SurveyAnswerModal" },
    { label: "SurveyResultModal (W8)", path: "/screens/SurveyResultModal" },
    { label: "ReadingScreen (W10)", path: "/screens/ReadingScreen" },
]

/**
 * SampleScreensコンポーネント: 全W画面サンプル集を表示するFunctional Component
 *
 * @returns JSX.Element 画面サンプルの一覧を描画するReact要素
 */
const SampleScreens: React.FC = () => (
    <div style={{ background: '#f5f5f5', minHeight: '100vh', padding: 40 }}>
        <h1 style={{ fontSize: 32, marginBottom: 32, textAlign: 'center', color: '#1976d2' }}>全W画面サンプル集</h1>
        <ul style={{ maxWidth: 500, margin: '0 auto', padding: 0, listStyle: 'none', display: 'flex', flexDirection: 'column', gap: 24 }}>
            {links.map(link => (
                <li key={link.path}>
                    <a href={link.path} style={{ display: 'block', background: '#fff', borderRadius: 12, padding: 24, fontSize: 20, color: '#1976d2', textDecoration: 'none', boxShadow: '0 2px 8px rgba(0,0,0,0.08)', textAlign: 'center', fontWeight: 600 }}>
                        {link.label}
                    </a>
                </li>
            ))}
        </ul>
    </div>
)

export default SampleScreens
