"use client"

import { useAuth } from "@/lib/auth"
import { TokenStatus } from "@/components/auth/TokenStatus"
import { UserAvatar } from "@/components/chat/UserAvatar"

export function Navigation() {
    const { account, member, isAuthenticated } = useAuth()
    return (
        <nav className="fixed top-4 right-4 flex items-center space-x-2 z-50">
            {isAuthenticated && account && (
                <>
                    <TokenStatus />
                    <UserAvatar
                        user={
                            member || {
                                id: account.id || 0,
                                name: account.email,
                                roomId: 1, // Default to room 1
                            }
                        }
                    />
                </>
            )}
        </nav>
    )
}
