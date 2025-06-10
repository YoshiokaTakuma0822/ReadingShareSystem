"use client"

import { useAuth } from '@/lib/auth'
import { useTokenMonitor } from '@/lib/useTokenMonitor'
import { useState } from 'react'
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card'
import { Button } from '@/components/ui/button'
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover'

/**
 * Component to display current token status and expiration time
 * Useful for debugging and showing users when their session will expire
 */
export function TokenStatus() {
    const { account, isAuthenticated } = useAuth()
    const {
        expirationTime,
        isExpiring,
        isRefreshing,
        lastRefresh,
        manualRefresh,
        getTimeUntilExpiry
    } = useTokenMonitor()
    const [open, setOpen] = useState(false)

    if (!isAuthenticated || !account) {
        return null
    }

    const timeInfo = getTimeUntilExpiry()

    const handleManualRefresh = async () => {
        try {
            await manualRefresh()
        } catch (error) {
            console.error('Manual refresh failed:', error)
        }
    }

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger asChild>
                <Button variant="outline" size="sm" className="text-sm">
                    認証状態
                    <span className="ml-1 text-xs">
                        {open ? '▲' : '▼'}
                    </span>
                </Button>
            </PopoverTrigger>
            <PopoverContent className="w-80 p-0" align="end">
                <Card className="border-0 shadow-none">
                    <CardHeader className="pb-3">
                        <CardTitle className="text-base">認証状態</CardTitle>
                    </CardHeader>
                    <CardContent className="pt-0 space-y-2">
                        <div>
                            <span className="text-gray-600">ユーザー:</span> {account.email}
                        </div>

                        {expirationTime && (
                            <>
                                <div>
                                    <span className="text-gray-600">トークン期限:</span>{' '}
                                    {new Date(expirationTime).toLocaleString('ja-JP')}
                                </div>

                                {timeInfo && (
                                    <div>
                                        <span className="text-gray-600">残り時間:</span>{' '}
                                        <span className={timeInfo.expired || isExpiring ? 'text-red-600 font-semibold' : 'text-green-600'}>
                                            {timeInfo.formatted}
                                        </span>
                                    </div>
                                )}
                            </>
                        )}

                        <div className="flex items-center gap-2">
                            <span className="text-gray-600">状態:</span>
                            {isRefreshing ? (
                                <span className="text-blue-600">🔄 更新中...</span>
                            ) : isExpiring ? (
                                <span className="text-orange-600">⚠️ まもなく更新</span>
                            ) : (
                                <span className="text-green-600">✅ 正常</span>
                            )}
                        </div>

                        {lastRefresh && (
                            <div className="text-xs text-gray-500">
                                最終更新: {lastRefresh.toLocaleTimeString('ja-JP')}
                            </div>
                        )}

                        <Button
                            onClick={handleManualRefresh}
                            disabled={isRefreshing}
                            className="w-full mt-2"
                            size="sm"
                        >
                            {isRefreshing ? '更新中...' : '手動更新'}
                        </Button>
                    </CardContent>
                </Card>
            </PopoverContent>
        </Popover>
    )
}
