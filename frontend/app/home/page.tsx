"use client"
import HomeScreen from '../screens/HomeScreen';
import { requireAuth } from '../../lib/authUtils';
import { useEffect, useState } from 'react';

export default function HomePage() {
  const [checked, setChecked] = useState(false);
  useEffect(() => {
    if (requireAuth('/login')) {
      setChecked(true);
    }
  }, []);
  if (!checked) return null;
  return <HomeScreen />;
}
