"use client"
import React, { useState } from 'react'
import { authApi } from '../../lib/authApi'
import { RegisterUserRequest } from '../../types/auth'

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

const RegisterScreen: React.FC = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const [email, setEmail] = useState('')
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [success, setSuccess] = useState(false)

    const handleRegister = async () => {
        setLoading(true)
        setError(null)
        setSuccess(false)
        try {
            const request: RegisterUserRequest = {
                username,
                password,
                email
            }
            const user = await authApi.register(request)
            console.log('Registration successful:', user)
            setSuccess(true)
        } catch (e) {
            setError('登録に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{ maxWidth: 900, minWidth: 520, width: '60vw', height: 600, position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', border: '2px solid #388e3c', padding: 48, borderRadius: 20, background: 'linear-gradient(135deg, #e0f7ef 0%, #f1fdf6 100%)', boxShadow: '0 4px 24px #a5d6a7', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <h1 style={{ textAlign: 'center', fontSize: 32, marginBottom: 32, color: '#388e3c' }}>会員登録画面</h1>
            <div style={{ marginBottom: 16 }}>
                <label>ユーザー名</label>
                <input type="text" value={username} onChange={e => setUsername(e.target.value)} style={inputStyle} />
            </div>
            <div style={{ marginBottom: 16 }}>
                <label>メールアドレス</label>
                <input type="email" value={email} onChange={e => setEmail(e.target.value)} style={inputStyle} />
            </div>
            <div style={{ marginBottom: 24 }}>
                <label>パスワード</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} />
            </div>
            {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
            {success && <div style={{ color: 'green', marginBottom: 12 }}>登録が完了しました！</div>}
            <button onClick={handleRegister} disabled={loading} style={{ width: '100%', padding: 12, fontSize: 18, borderRadius: 8, border: '1px solid #222' }}>{loading ? '登録中...' : '登録'}</button>
        </div>
    )
}

export default RegisterScreen
