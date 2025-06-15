"use client";
import React, { useEffect, useState } from 'react';
import { surveyService } from '../../services/surveyService';

interface SurveyAnswerModalProps {
  open: boolean;
  surveyId: string;
  onClose: () => void;
  onAnswered: () => void;
}

const SurveyAnswerModal: React.FC<SurveyAnswerModalProps> = ({ open, surveyId, onClose, onAnswered }) => {
  const [title, setTitle] = useState('');
  const [options, setOptions] = useState<string[]>([]);
  const [selected, setSelected] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!open) return;
    setLoading(true);
    setError(null);
    surveyService.getSurvey(surveyId)
      .then(data => {
        setTitle(data.title);
        setOptions(data.options);
      })
      .catch(() => setError('アンケート情報の取得に失敗しました'))
      .finally(() => setLoading(false));
  }, [open, surveyId]);

  const handleAnswer = async () => {
    if (!selected) return;
    setLoading(true);
    setError(null);
    try {
      await surveyService.answerSurvey(surveyId, { answer: selected });
      onAnswered();
    } catch (e) {
      setError('回答送信に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  if (!open) return null;

  return (
    <div style={{ border: '4px solid #222', margin: 24, padding: 32, maxWidth: 600, background: '#fff', borderRadius: 8 }}>
      <h2 style={{ textAlign: 'center', fontSize: 28, marginBottom: 24 }}>{title || 'アンケート'}</h2>
      {loading ? (
        <div>読み込み中...</div>
      ) : error ? (
        <div style={{ color: 'red', marginBottom: 16 }}>{error}</div>
      ) : (
        <div style={{ marginBottom: 32 }}>
          {options.map((opt, idx) => (
            <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: 16, marginBottom: 16 }}>
              <input type="radio" name="survey_option" checked={selected === opt} onChange={() => setSelected(opt)} /> {opt}
            </div>
          ))}
        </div>
      )}
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16 }}>
        <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>キャンセル</button>
        <button onClick={handleAnswer} disabled={!selected || loading} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>{loading ? '送信中...' : '回答する'}</button>
      </div>
    </div>
  );
};

export default SurveyAnswerModal;
