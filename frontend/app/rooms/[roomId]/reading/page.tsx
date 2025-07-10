import React from 'react'
import ReadingScreen from '../../../../components/ReadingBook'

interface ReadingPageProps {
    params: Promise<{
        roomId: string
    }>
}

export default function ReadingPage({ params }: ReadingPageProps) {
    const { roomId } = React.use(params)
    return <ReadingScreen roomId={roomId} />
}
