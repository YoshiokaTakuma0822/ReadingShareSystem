export async function fetchRoomState(roomId: string) {
  const res = await fetch(`/api/room-reading-state/${roomId}`);
  if (!res.ok) throw new Error('Failed to fetch room state');
  return res.json();
}

export async function turnPage(roomId: string, userId: string, direction: 'next' | 'prev') {
  const res = await fetch(`/api/room-reading-state/${roomId}/user/turn?userId=${userId}&direction=${direction}`, {
    method: 'POST',
  });
  if (!res.ok) throw new Error('Failed to turn page');
  return res.json();
}
