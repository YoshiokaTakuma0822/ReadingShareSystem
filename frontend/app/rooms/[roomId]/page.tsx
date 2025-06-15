import RoomStateView from './RoomStateView';
import RoomStateBook from './RoomStateBook';

export default function RoomPage({ params }: { params: { roomId: string } }) {
  const { roomId } = params;
  return (
    <main style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
      <RoomStateView roomId={roomId} />
      <RoomStateBook roomId={roomId} />
    </main>
  );
}
