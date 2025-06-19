"use client"
import React, { useState } from 'react'
import { useRouter } from 'next/navigation'
import { authApi } from '../../lib/authApi'
import { RegisterUserRequest } from '../../types/auth'

const inputStyle = {
    width: '100%',
    padding: 8,
    marginTop: 4,
    border: '2px solid var(--border)',
    borderRadius: 6,
    fontSize: 16,
    boxSizing: 'border-box' as const,
    outline: 'none',
    background: 'var(--white)',
    color: 'var(--text-main)',
}

const RegisterScreen: React.FC = () => {
    const router = useRouter()
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
            // 登録成功後、少し待ってからホームに遷移
            setTimeout(() => {
                router.push('/')
            }, 1500)
        } catch (e) {
            setError('登録に失敗しました')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div style={{ maxWidth: 900, minWidth: 520, width: '60vw', height: 600, position: 'absolute', top: '50%', left: '50%', transform: 'translate(-50%, -50%)', border: '2px solid var(--accent)', padding: 48, borderRadius: 20, background: 'var(--green-bg)', boxShadow: '0 4px 24px var(--green-light)', display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>
            <h1 style={{ textAlign: 'center', fontSize: 32, marginBottom: 32, color: 'var(--accent)' }}>会員登録画面</h1>
            <div style={{ marginBottom: 16 }}>
                <label style={{ color: 'var(--text-main)' }}>ユーザー名</label>
                <input type="text" value={username} onChange={e => setUsername(e.target.value)} style={inputStyle} />
            </div>
            <div style={{ marginBottom: 16 }}>
                <label style={{ color: 'var(--text-main)' }}>メールアドレス</label>
                <input type="email" value={email} onChange={e => setEmail(e.target.value)} style={inputStyle} />
            </div>
            <div style={{ marginBottom: 24 }}>
                <label style={{ color: 'var(--text-main)' }}>パスワード</label>
                <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} />
            </div>
            {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
            {success && <div style={{ color: 'var(--green-main)', marginBottom: 12 }}>登録が完了しました！</div>}
            <button onClick={handleRegister} disabled={loading} style={{ width: '100%', padding: 12, fontSize: 18, borderRadius: 8, border: '1px solid var(--text-main)', background: 'var(--accent)', color: 'var(--white)' }}>{loading ? '登録中...' : '登録'}</button>
        </div>
    )
}

export default RegisterScreen
