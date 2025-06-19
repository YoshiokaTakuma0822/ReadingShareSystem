"use client"
import { useEffect, ReactNode } from 'react'
import { requireAuth } from '../lib/authUtils'

interface AuthGuardProps {
    children: ReactNode
    redirectTo?: string
}

/**
 * 認証が必要なページを保護するコンポーネント
 * ログインしていない場合、指定されたページにリダイレクトする
 */
export const AuthGuard: React.FC<AuthGuardProps> = ({ children, redirectTo = '/login' }) => {
    useEffect(() => {
        // クライアントサイドでのみ認証チェックを実行
        requireAuth(redirectTo)
    }, [redirectTo])

    return <>{children}</>
}

export default AuthGuard
