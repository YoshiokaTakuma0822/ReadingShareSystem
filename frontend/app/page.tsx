"use client"
import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { authStorage } from '../lib/authUtils';

export default function Page() {
  const router = useRouter();
  useEffect(() => {
    if (authStorage.isLoggedIn()) {
      router.replace('/home');
    } else {
      router.replace('/login');
    }
  }, [router]);
  return null;
}
