"use client";
import React, { useState } from 'react';
import { authService } from '../../services/authService';

const inputStyle = {
  width: '100%',
  padding: 8,
  marginTop: 4,
  border: '2px solid #888',
  borderRadius: 6,
  fontSize: 16,
  boxSizing: 'border-box' as const,
  outline: 'none',
};

const RegisterScreen: React.FC = () => {
  const [id, setId] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleRegister = async () => {
    setLoading(true);
    setError(null);
    setSuccess(false);
    try {
      await authService.register(id, password);
      setSuccess(true);
    } catch (e) {
      setError('登録に失敗しました');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ maxWidth: 500, margin: '40px auto', border: '2px solid #222', padding: 32, borderRadius: 8 }}>
      <h1 style={{ textAlign: 'center', fontSize: 32, marginBottom: 32 }}>会員登録画面</h1>
      <div style={{ marginBottom: 16 }}>
        <label>新規ID</label>
        <input type="text" value={id} onChange={e => setId(e.target.value)} style={inputStyle} />
      </div>
      <div style={{ marginBottom: 24 }}>
        <label>パスワード</label>
        <input type="password" value={password} onChange={e => setPassword(e.target.value)} style={inputStyle} />
      </div>
      {error && <div style={{ color: 'red', marginBottom: 12 }}>{error}</div>}
      {success && <div style={{ color: 'green', marginBottom: 12 }}>登録が完了しました！</div>}
      <button onClick={handleRegister} disabled={loading} style={{ width: '100%', padding: 12, fontSize: 18, borderRadius: 8, border: '1px solid #222' }}>{loading ? '登録中...' : '登録'}</button>
    </div>
  );
};

export default RegisterScreen;
