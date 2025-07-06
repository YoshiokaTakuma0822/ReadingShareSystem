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
            {
                source: '/ws/:path*',
                destination: process.env.NODE_ENV === 'production' ?
                    'http://app:8080/ws/:path*' : 'http://localhost:8080/ws/:path*',
            },
            {
                source: '/internal/:path*',
                destination: 'http://host.docker.internal:8888/:path*',
            },
        ]
    },
}

export default nextConfig
