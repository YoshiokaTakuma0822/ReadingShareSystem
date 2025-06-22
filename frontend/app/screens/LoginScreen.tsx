"use client"
import React, { useState } from 'react'
import { authApi } from '../../lib/authApi'
import { LoginRequest } from '../../types/auth'

const inputStyle = {
    width: '100%',
    padding: 8,
    marginTop: 4,
    border: '2px solid #888',
    borderRadius: 6,
    fontSize: 16,
    boxSizing: 'border-box' as const,
    outline: 'none',
}

const LoginScreen: React.FC = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const handleLogin = async () => {
        setLoading(true)
        setError(null)
        try {
            const request: LoginRequest = {
                username,
                password
            }
            const response = await authApi.login(request)
            // ログイン成功後、ホーム画面にリダイレクト
            console.log('Login successful:', response)
            window.location.href = '/'
        } catch (e) {
            setError('ログインに失敗しました')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{ maxWidth: 900, minWidth: 520, width: '60vw', height: 540, position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', border: '2px solid #388e3c', padding: 48, borderRadius: 20, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', boxShadow: '0 4px 24px #a5d6a7', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <h1 style={{ textAlign: 'center', fontSize: 36, marginBottom: 32, color: '#388e3c' }}>読書共有システム</h1>
            <div style={{ marginBottom: 16 }}>
                <label>ユーザー名</label>
                <input type="text" value={username} onChange={e => setUsername(e.target.value)} style={inputStyle} />
            </div>
            <div style={{ marginBottom: 24 }}>
                <label>パスワード</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} />
            </div>
            {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
            <button onClick={handleLogin} disabled={loading} style={{ width: '100%', padding: 12, fontSize: 18, borderRadius: 8, border: '1px solid #222' }}>{loading ? 'ログイン中...' : 'ログイン'}</button>
            <div style={{ textAlign: 'right', marginTop: 16 }}>
                <a href="/register">会員登録はこちら</a>
            </div>
        </div>
    )
}

export default LoginScreen
