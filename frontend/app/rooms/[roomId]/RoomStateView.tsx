import useSWR from 'swr';
import { fetchRoomState } from '@/lib/api';

function UserIcon({ userName }: { userName: string }) {
  // ユーザ名の頭文字を丸で表示
  return (
    <div style={{
      width: 32, height: 32, borderRadius: '50%', background: '#fff', border: '2px solid #888',
      display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold', fontSize: 18, color: '#555',
    }}>
      {userName.charAt(0)}
    </div>
  );
}

export default function RoomStateView({ roomId, userId = 'user1' }: { roomId: string, userId?: string }) {
  const { data, error, mutate } = useSWR(roomId, fetchRoomState, { refreshInterval: 2000 });
  if (error) return <div>エラーが発生しました</div>;
  if (!data) return <div>読書状況を取得中...</div>;
  const myState = data.userStates.find((u: any) => u.userId === userId);
  const myPage = myState ? myState.currentPage : 0;
  const maxPage = Math.max(...data.userStates.map((u: any) => u.currentPage));
  const leftUsers = data.userStates.filter((u: any) => u.currentPage <= myPage && u.userId !== userId);
  const rightUsers = data.userStates.filter((u: any) => u.currentPage > myPage);

  // ページ数を直接操作できるUI
  const handleSetPage = async (targetUserId: string, page: number) => {
    // API呼び出し例: await updateUserPage(roomId, targetUserId, page)
    // ここではダミーでmutate()のみ
    mutate();
  };

  return (
    <div style={{ width: 600, margin: '32px 0', position: 'relative', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
      <div style={{ fontSize: 32, marginBottom: 8 }}>みんなの読書メーター</div>
      <div style={{ height: 32, background: '#eee', borderRadius: 16, position: 'relative', width: '100%' }}>
        {/* 左側（自分より進んでいない人） */}
        {leftUsers.map((u: any, i: number) => (
          <div key={u.userId} style={{
            position: 'absolute',
            left: `${30 - (leftUsers.length - i) * 20}px`,
            top: 0,
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            cursor: 'pointer',
          }} onClick={() => handleSetPage(u.userId, u.currentPage + 1)} title="クリックで+1ページ進める">
            <UserIcon userName={u.userName} />
            <span style={{ fontSize: 12 }}>{u.userName}</span>
            <span style={{ fontSize: 10, color: '#888' }}>{u.currentPage}p</span>
          </div>
        ))}
        {/* 自分 */}
        <div style={{
          position: 'absolute',
          left: '50%',
          top: 0,
          transform: 'translate(-50%, 0)',
          display: 'flex', flexDirection: 'column', alignItems: 'center',
          zIndex: 2,
        }}>
          <UserIcon userName={myState?.userName || '自分'} />
          <span style={{ fontSize: 13, color: '#1976d2', fontWeight: 'bold' }}>自分</span>
          <span style={{ fontSize: 11, color: '#1976d2' }}>{myPage}p</span>
        </div>
        {/* 右側（自分より進んでいる人） */}
        {rightUsers.map((u: any, i: number) => (
          <div key={u.userId} style={{
            position: 'absolute',
            left: `${570 + i * 20}px`,
            top: 0,
            display: 'flex', flexDirection: 'column', alignItems: 'center',
            cursor: 'pointer',
          }} onClick={() => handleSetPage(u.userId, u.currentPage - 1)} title="クリックで-1ページ戻す">
            <UserIcon userName={u.userName} />
            <span style={{ fontSize: 12 }}>{u.userName}</span>
            <span style={{ fontSize: 10, color: '#888' }}>{u.currentPage}p</span>
          </div>
        ))}
      </div>
    </div>
  );
}
