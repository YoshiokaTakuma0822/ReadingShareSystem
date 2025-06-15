import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import RoomStateBook from '../app/rooms/[roomId]/RoomStateBook';

jest.mock('@/lib/api', () => ({
  turnPage: jest.fn(async (roomId, userId, direction) => ({ currentPage: direction === 'next' ? 1 : 0 }))
}));

describe('RoomStateBook', () => {
  it('ページ送り・戻しボタンでページ数が変化する', async () => {
    render(<RoomStateBook roomId="room1" />);
    const nextBtn = screen.getByText('進める →');
    const prevBtn = screen.getByText('← 戻す');
    fireEvent.click(nextBtn);
    await waitFor(() => expect(screen.getByText('1ページ目')).toBeInTheDocument());
    fireEvent.click(prevBtn);
    await waitFor(() => expect(screen.getByText('0ページ目')).toBeInTheDocument());
  });
});
