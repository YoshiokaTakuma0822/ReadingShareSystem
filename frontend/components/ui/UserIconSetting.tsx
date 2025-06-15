import { useRef, useState } from 'react';

export default function UserIconSetting({ onIconChange }: { onIconChange: (file: File) => void }) {
  const [preview, setPreview] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setPreview(URL.createObjectURL(file));
      onIconChange(file);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8 }}>
      <div style={{ width: 80, height: 80, borderRadius: '50%', border: '2px solid #888', overflow: 'hidden', background: '#eee', marginBottom: 8 }}>
        {preview ? (
          <img src={preview} alt="preview" style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
        ) : (
          <span style={{ fontSize: 40, color: '#bbb', lineHeight: '80px', display: 'block', textAlign: 'center' }}>＋</span>
        )}
      </div>
      <input type="file" accept="image/*" ref={inputRef} style={{ display: 'none' }} onChange={handleFileChange} />
      <button onClick={() => inputRef.current?.click()} style={{ fontSize: 16, padding: '4px 16px' }}>アイコンを選択</button>
    </div>
  );
}
