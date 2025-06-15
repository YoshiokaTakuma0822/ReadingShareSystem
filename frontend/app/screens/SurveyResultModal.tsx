"use client";
import React, { useEffect, useState } from 'react';
import { surveyService } from '../../services/surveyService';

interface SurveyResultModalProps {
  open: boolean;
  surveyId: string;
  onClose: () => void;
}

const SurveyResultModal: React.FC<SurveyResultModalProps> = ({ open, surveyId, onClose }) => {
  const [title, setTitle] = useState('');
  const [results, setResults] = useState<{ option: string; votes: number }[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!open) return;
    setLoading(true);
    setError(null);
    surveyService.getSurveyResult(surveyId)
      .then(data => {
        setTitle(data.title);
        setResults(data.results);
      })
      .catch(() => setError('アンケート結果の取得に失敗しました'))
      .finally(() => setLoading(false));
  }, [open, surveyId]);

  if (!open) return null;

  return (
    <div style={{ border: '4px solid #222', margin: 24, padding: 32, maxWidth: 600, background: '#fff', borderRadius: 8 }}>
      <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 24 }}>{title || 'アンケート結果'}</h2>
      {loading ? (
        <div>読み込み中...</div>
      ) : error ? (
        <div style={{ color: 'red', marginBottom: 16 }}>{error}</div>
      ) : (
        <div style={{ marginBottom: 32 }}>
          {results.map((r, idx) => (
            <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
              <span>{r.option}</span>
              <span>{r.votes}票</span>
            </div>
          ))}
        </div>
      )}
      <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
        <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>閉じる</button>
      </div>
    </div>
  );
};

export default SurveyResultModal;
