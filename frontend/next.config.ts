import type { NextConfig } from "next"

const nextConfig: NextConfig = {
    // API routes rewrite for development and production
    async rewrites() {
        return [
            {
                source: '/api/:path*',
                destination: process.env.NODE_ENV === 'production' ?
                    'http://app:8080/api/:path*' : 'http://localhost:8080/api/:path*',
            },
        ]
    },
}

export default nextConfig
