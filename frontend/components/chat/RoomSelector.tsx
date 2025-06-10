"use client"

import { Room } from "@/types/chat"
import { getRooms } from "@/lib/api"
import { useEffect, useState } from "react"
import { useChatContext } from "@/lib/ChatContext"

export function RoomSelector() {
    const [rooms, setRooms] = useState<Room[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const { currentRoomId, switchRoom } = useChatContext()

    useEffect(() => {
        async function fetchRooms() {
            try {
                const fetchedRooms = await getRooms()
                setRooms(fetchedRooms)
            } catch (error) {
                console.error("Failed to fetch rooms:", error)
                // Default rooms if API fails
                setRooms([
                    { id: 1, name: "General", description: "General discussion" }
                ])
            } finally {
                setIsLoading(false)
            }
        }

        fetchRooms()
    }, [])

    const handleRoomChange = async (roomId: number) => {
        try {
            await switchRoom(roomId)
        } catch (error) {
            console.error("Failed to switch room:", error)
        }
    }

    if (isLoading) {
        return (
            <div className="p-4 bg-white border-b">
                <div className="animate-pulse">
                    <div className="h-4 bg-gray-200 rounded w-24"></div>
                </div>
            </div>
        )
    }

    return (
        <div className="p-4 bg-white border-b">
            <label htmlFor="room-select" className="block text-sm font-medium text-gray-700 mb-2">
                Chat Room
            </label>
            <select
                id="room-select"
                value={currentRoomId}
                onChange={(e) => handleRoomChange(parseInt(e.target.value))}
                className="block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-blue-500 focus:border-blue-500"
            >
                {rooms.map(room => (
                    <option key={room.id} value={room.id}>
                        {room.name}
                        {room.description && ` - ${room.description}`}
                    </option>
                ))}
            </select>
        </div>
    )
}
