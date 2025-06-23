/**
 * デバッグ用の画面を表示するReactコンポーネント
 * 
 * @author 02001
 * @componentId C1
 * @moduleName デバッグ画面
 * @packageDocumentation
 */

"use client"

import React from 'react'

/**
 * デバッグ画面コンポーネント
 * 
 * @returns JSX.Element デバッグ画面を描画するReact要素
 */
const DebugScreen: React.FC = () => {
    return (
        <div style={{ padding: 32, background: 'var(--green-bg)', minHeight: '100vh' }}>
            <h1 style={{ color: 'var(--accent)', fontSize: 28, marginBottom: 24 }}>デバッグ画面</h1>

            {/* 各W画面の確認方法案内 */}
            <div style={{ background: 'var(--green-bg)', border: '1px solid var(--border)', borderRadius: 10, padding: 24, marginBottom: 32 }}>
                <h2 style={{ color: 'var(--accent)', fontSize: 22, marginBottom: 12 }}>各W画面の確認方法</h2>
                <ul style={{ lineHeight: 2, fontSize: 16, color: 'var(--accent)', marginLeft: 16 }}>
                    <li><a href="/screens/HomeScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W1: ホーム画面</a></li>
                    <li><a href="/screens/LoginScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W2: ログイン画面</a></li>
                    <li><a href="/screens/RegisterScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W3: 会員登録画面</a></li>
                    <li><a href="/screens/RoomCreationModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W4: 部屋作成画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/RoomJoinModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W5: 部屋参加画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/GroupChatScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W6: グループチャット画面</a></li>
                    <li><a href="/screens/SurveyCreationModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W7: アンケート作成画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/SurveyAnswerModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W8: アンケート回答画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/SurveyResultModal" style={{ color: '#8d6748', textDecoration: 'underline' }}>W9: アンケート結果画面</a> <span style={{ color: '#b0b8c9', fontSize: 13 }}>(この画面は開いても白い画面しか表示されません)</span></li>
                    <li><a href="/screens/ReadingScreen" style={{ color: '#8d6748', textDecoration: 'underline' }}>W10: 読書画面</a></li>
                </ul>
                <div style={{ marginTop: 12, color: 'var(--accent)', fontWeight: 'bold' }}>
                    全W画面サンプル集は現在ご利用いただけません
                </div>
            </div>

            <div style={{ marginTop: 32, display: 'flex', gap: 16, flexWrap: 'wrap' }}>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/RoomCreationSample', '_blank')}>部屋作成サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/RoomJoinSample', '_blank')}>部屋参加サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/SurveyCreationSample', '_blank')}>アンケート作成サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/SurveyAnswerSample', '_blank')}>アンケート回答サンプル（ウィンドウ型）</button>
                <button type="button" style={{ padding: '16px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #388e3c', background: '#e0f7ef', color: '#388e3c', fontWeight: 600 }} onClick={() => window.open('/screens/SurveyResultSample', '_blank')}>アンケート結果サンプル（ウィンドウ型）</button>
            </div>

            <div style={{ marginTop: 32, padding: 16, background: '#f5f5f5', borderRadius: 8 }}>
                <p style={{ color: '#666', fontSize: 14 }}>
                    <strong>注意:</strong> この画面はデバッグ用です。プロダクション環境では使用しないでください。
                </p>
            </div>
        </div>
    )
}

export default DebugScreen
