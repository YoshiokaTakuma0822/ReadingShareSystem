'use client';
import { useState } from 'react';
import { turnPage } from '@/lib/api';

export default function RoomStateBook({ roomId }: { roomId: string }) {
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const userId = 'user1'; // 仮: ログインユーザID

  const handleTurn = async (direction: 'next' | 'prev') => {
    setLoading(true);
    const res = await turnPage(roomId, userId, direction);
    setPage(res.currentPage);
    setLoading(false);
  };

  return (
    <div style={{ margin: 32, textAlign: 'center' }}>
      <div style={{ fontSize: 48, border: '2px solid #aaa', borderRadius: 8, width: 320, height: 420, margin: '0 auto', background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center', boxShadow: '0 4px 16px #0002' }}>
        <span>📖<br />{page}ページ目</span>
      </div>
      <div style={{ marginTop: 16 }}>
        <button onClick={() => handleTurn('prev')} disabled={loading || page === 0} style={{ fontSize: 24, marginRight: 24 }}>← 戻す</button>
        <button onClick={() => handleTurn('next')} disabled={loading} style={{ fontSize: 24 }}>進める →</button>
      </div>
    </div>
  );
}
