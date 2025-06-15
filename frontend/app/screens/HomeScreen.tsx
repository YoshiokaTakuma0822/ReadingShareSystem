"use client";

import React, { useState } from 'react';
import { Room } from '../../types/room';
import { roomService } from '../../services/roomService';
import RoomCreationModal from './RoomCreationModal';
import SurveyAnswerModal from './SurveyAnswerModal';
import SurveyResultModal from './SurveyResultModal';

const HomeScreen: React.FC = () => {
  const [tab, setTab] = useState<'create' | 'search'>('create');
  const [searchText, setSearchText] = useState('');
  const [rooms, setRooms] = useState<Room[]>([]);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showSurveyAnswerModal, setShowSurveyAnswerModal] = useState(false);
  const [showSurveyResultModal, setShowSurveyResultModal] = useState(false);

  // サンプル用のダミーsurveyId
  const dummySurveyId = 'sample-survey-id-1';

  // 部屋検索API
  const handleSearch = async () => {
    setLoading(true);
    setError(null);
    try {
      const result = await roomService.searchRooms(searchText);
      setRooms(result.rooms || []);
    } catch (e) {
      setError('部屋の取得に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  // 初期表示で全件取得
  React.useEffect(() => {
    if (tab === 'search') handleSearch();
  }, [tab]);

  // 部屋作成後のリスト再取得
  const handleRoomCreated = () => {
    setShowCreateModal(false);
    setTab('search');
    handleSearch();
  };

  return (
    <div style={{ padding: 32 }}>
      <div style={{ display: 'flex', gap: 16, marginBottom: 24 }}>
        <button
          style={{ flex: 1, padding: 16, background: tab === 'create' ? '#eee' : '#fff', borderBottom: tab === 'create' ? '2px solid #222' : '1px solid #ccc' }}
          onClick={() => setTab('create')}
        >部屋作成</button>
        <button
          style={{ flex: 1, padding: 16, background: tab === 'search' ? '#eee' : '#fff', borderBottom: tab === 'search' ? '2px solid #222' : '1px solid #ccc' }}
          onClick={() => setTab('search')}
        >検索</button>
      </div>
      {tab === 'create' && (
        <div style={{ marginBottom: 24 }}>
          <button onClick={() => setShowCreateModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #222' }}>
            部屋を作成する
          </button>
        </div>
      )}
      {tab === 'search' && (
        <div style={{ marginBottom: 24, display: 'flex', gap: 8 }}>
          <input
            type="text"
            value={searchText}
            onChange={e => setSearchText(e.target.value)}
            placeholder="部屋名で検索"
            style={{ flex: 1, padding: 8, fontSize: 16 }}
          />
          <button onClick={handleSearch} style={{ padding: '8px 16px' }}>検索</button>
        </div>
      )}
      {loading ? (
        <div>読み込み中...</div>
      ) : error ? (
        <div style={{ color: 'red' }}>{error}</div>
      ) : (
        <div style={{ border: '2px solid #222', padding: 24 }}>
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: 24 }}>
            {rooms.map((room) => (
              <div key={room.roomId} style={{ border: '1px solid #222', width: 200, height: 100, display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20 }}>
                {room.roomName}
              </div>
            ))}
          </div>
        </div>
      )}
      {showCreateModal && (
        <RoomCreationModal open={showCreateModal} onClose={() => setShowCreateModal(false)} onCreated={handleRoomCreated} />
      )}
      <div style={{ margin: '32px 0' }}>
        <button onClick={() => setShowSurveyAnswerModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #222', background: '#f5f5f5' }}>
          アンケート回答モーダルを開く（サンプル）
        </button>
      </div>
      {showSurveyAnswerModal && (
        <SurveyAnswerModal open={showSurveyAnswerModal} surveyId={dummySurveyId} onClose={() => setShowSurveyAnswerModal(false)} onAnswered={() => { setShowSurveyAnswerModal(false); alert('回答送信完了（ダミー）'); }} />
      )}
      <div style={{ margin: '32px 0' }}>
        <button onClick={() => setShowSurveyResultModal(true)} style={{ padding: '12px 32px', fontSize: 18, borderRadius: 8, border: '1px solid #222', background: '#f5f5f5' }}>
          アンケート結果モーダルを開く（サンプル）
        </button>
      </div>
      {showSurveyResultModal && (
        <SurveyResultModal open={showSurveyResultModal} surveyId={dummySurveyId} onClose={() => setShowSurveyResultModal(false)} />
      )}
    </div>
  );
};

export default HomeScreen;
