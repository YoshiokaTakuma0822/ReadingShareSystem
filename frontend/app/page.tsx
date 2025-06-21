"use client"
import HomeScreen from './screens/HomeScreen';
import { useEffect, useState } from 'react';
import { requireAuth } from '../lib/authUtils';

export default function Page() {
  const [checked, setChecked] = useState(false);
  useEffect(() => {
    if (requireAuth()) {
      setChecked(true);
    }
  }, []);
  if (!checked) return null;
  return <HomeScreen />;
}
