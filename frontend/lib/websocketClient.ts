// WebSocket client factory with authorization token support
export function createWebSocket(url: string): WebSocket {
  const token = localStorage.getItem('authToken')
  return token
    ? new WebSocket(url, [`Bearer ${token}`])
    : new WebSocket(url)
}
