import type { NextConfig } from "next"

const nextConfig: NextConfig = {
    // API routes rewrite for development
    async rewrites() {
        return [
            {
                source: '/api/:path*',
                destination: 'http://localhost:8080/api/:path*',
            },
        ]
    },

    // Environment variables
    env: {
        API_BASE_URL: process.env.NODE_ENV === 'production'
            ? '/api'
            : 'http://localhost:8080/api',
    },
}

export default nextConfig
