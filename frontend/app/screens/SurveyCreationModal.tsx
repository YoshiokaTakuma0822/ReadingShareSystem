"use client";
import React, { useState } from 'react';
import { surveyService } from '../../services/surveyService';

interface SurveyCreationModalProps {
  open: boolean;
  onClose: () => void;
  onCreated: () => void;
}

const SurveyCreationModal: React.FC<SurveyCreationModalProps> = ({ open, onClose, onCreated }) => {
  const [title, setTitle] = useState('');
  const [options, setOptions] = useState<string[]>(['']);
  const [endDate, setEndDate] = useState('');
  const [multiSelect, setMultiSelect] = useState(false);
  const [anonymous, setAnonymous] = useState(false);
  const [allowAddOption, setAllowAddOption] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleAddOption = () => setOptions([...options, '']);
  const handleOptionChange = (idx: number, value: string) => {
    setOptions(options.map((opt, i) => (i === idx ? value : opt)));
  };
  const handleRemoveOption = (idx: number) => {
    setOptions(options.filter((_, i) => i !== idx));
  };

  const handleCreate = async () => {
    setLoading(true);
    setError(null);
    try {
      await surveyService.createSurvey({
        title,
        options,
        endDate,
        multiSelect,
        anonymous,
        allowAddOption,
      });
      onCreated();
    } catch (e) {
      setError('アンケート作成に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  if (!open) return null;

  return (
    <div style={{ border: '4px solid #388e3c', margin: 24, padding: 32, maxWidth: 700, background: '#f1fdf6', borderRadius: 8, boxShadow: '0 4px 24px #a5d6a7' }}>
      <h2 style={{ fontWeight: 'bold', fontSize: 28, marginBottom: 24, color: '#388e3c' }}>アンケート作成</h2>
      <div style={{ marginBottom: 16, color: '#388e3c', fontSize: 15, fontWeight: 500 }}>
        この画面は開いて確認できません（サンプルはホーム画面からご利用ください）
      </div>
      <div style={{ marginBottom: 16 }}>
        <label>タイトル</label>
        <input type="text" value={title} onChange={e => setTitle(e.target.value)} placeholder="今日はどこの章まで読むか" style={{ width: '100%', padding: 8, marginTop: 4 }} />
      </div>
      <div style={{ marginBottom: 16 }}>
        <label>選択肢</label>
        {options.map((opt, idx) => (
          <div key={idx} style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
            <input type="text" value={opt} onChange={e => handleOptionChange(idx, e.target.value)} style={{ flex: 1, padding: 8 }} placeholder={`選択肢${idx + 1}`} />
            {options.length > 1 && <button onClick={() => handleRemoveOption(idx)} style={{ border: '1px solid #222', borderRadius: 6 }}>削除</button>}
          </div>
        ))}
        <button onClick={handleAddOption} style={{ border: '1px solid #222', borderRadius: '50%', width: 32, height: 32, fontSize: 24, marginTop: 4 }}>＋</button>
      </div>
      <div style={{ marginBottom: 16 }}>
        <label>投票終了日時</label>
        <input type="datetime-local" value={endDate} onChange={e => setEndDate(e.target.value)} style={{ width: '100%', padding: 8, marginTop: 4 }} />
      </div>
      <div style={{ display: 'flex', gap: 16, marginBottom: 16 }}>
        <label><input type="checkbox" checked={multiSelect} onChange={e => setMultiSelect(e.target.checked)} /> 複数選択可</label>
        <label><input type="checkbox" checked={anonymous} onChange={e => setAnonymous(e.target.checked)} /> 匿名投票</label>
        <label><input type="checkbox" checked={allowAddOption} onChange={e => setAllowAddOption(e.target.checked)} /> 選択肢の追加を許可</label>
      </div>
      {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
      <div style={{ display: 'flex', justifyContent: 'flex-end', gap: 16 }}>
        <button onClick={onClose} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>キャンセル</button>
        <button onClick={handleCreate} disabled={loading} style={{ padding: '10px 24px', border: '1px solid #222', borderRadius: 8 }}>{loading ? '作成中...' : '作成'}</button>
      </div>
    </div>
  );
};

export default SurveyCreationModal;
