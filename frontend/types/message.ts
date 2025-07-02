export interface Message {
    id: number
    uuid: string // 一意のID
    user: string // ユーザー名
    text: string
    isCurrentUser: boolean
    sentAt?: string
    messageType?: string
    surveyId?: string
}
