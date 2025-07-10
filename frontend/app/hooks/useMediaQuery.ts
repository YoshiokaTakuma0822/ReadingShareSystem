import { useEffect, useState } from 'react';

/**
 * Custom hook to subscribe to a media query and update on changes.
 * @param query - CSS media query string, e.g., '(max-width: 767px)'.
 * @returns boolean indicating if the media query matches.
 */
export function useMediaQuery(query: string): boolean {
    // Set initial state based on matchMedia
    const [matches, setMatches] = useState<boolean>(() =>
        typeof window !== 'undefined' ? window.matchMedia(query).matches : false
    );

    useEffect(() => {
        if (typeof window === 'undefined') return;
        const mql = window.matchMedia(query);
        // 初期値をセット
        setMatches(mql.matches);
        const handler = (e: MediaQueryListEvent) => setMatches(e.matches);
        // イベントリスナー登録
        mql.addEventListener('change', handler);
        // クリーンアップ
        return () => mql.removeEventListener('change', handler);
    }, [query]);

    return matches;
}
