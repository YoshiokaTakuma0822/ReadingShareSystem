export interface Account {
    id?: number
    email: string
}

export interface LoginRequest {
    email: string
    password: string
}

export interface RegisterRequest {
    email: string
    password: string
}

export interface Member {
    id: number
    name: string
    roomId: number
}
