import { render, screen } from '@testing-library/react';
import RoomStateView from '../app/rooms/[roomId]/RoomStateView';

jest.mock('swr', () => () => ({
  data: {
    userStates: [
      { userId: 'user1', userName: 'ユーザー1', iconUrl: '/icons/user1.png', currentPage: 5 },
      { userId: 'user2', userName: 'ユーザー2', iconUrl: '/icons/user2.png', currentPage: 2 },
    ],
  },
  error: null,
}));

describe('RoomStateView', () => {
  it('全ユーザの進捗・アイコン・名前が表示される', () => {
    render(<RoomStateView roomId="room1" />);
    expect(screen.getByText('ユーザー1')).toBeInTheDocument();
    expect(screen.getByText('ユーザー2')).toBeInTheDocument();
    expect(screen.getAllByRole('img').length).toBe(2);
  });
});
