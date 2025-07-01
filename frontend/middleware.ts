import { NextRequest, NextResponse } from 'next/server';

export function middleware(request: NextRequest) {
  // cookieに 'authToken' があればログイン済みとみなす
  const isLoggedIn = request.cookies.get('authToken');
  const isLoginOrRegisterPage =
    request.nextUrl.pathname === '/login' || request.nextUrl.pathname === '/register';

  if (!isLoggedIn && !isLoginOrRegisterPage) {
    // 未ログインなら/loginへリダイレクト
    return NextResponse.redirect(new URL('/login', request.url));
  }
  // ログイン済み or /login or /registerページはそのまま
  return NextResponse.next();
}

export const config = {
  matcher: ['/((?!_next|api|favicon.ico).*)'],
};
