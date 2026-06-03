import { createContext, useState, useCallback, type ReactNode } from 'react';
import { authService } from '@/services/api';
import type { AuthRequest, AuthResponse } from '@/types/api';

interface AuthContextValue {
  isAuthenticated: boolean;
  token: string | null;
  login: (data: AuthRequest) => Promise<AuthResponse>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextValue | null>(null);

/** Devuelve el token solo si existe y no ha expirado (comprueba exp del JWT). */
function readValidToken(): string | null {
  const stored = localStorage.getItem('jwt');
  if (!stored) return null;
  try {
    const payload = JSON.parse(atob(stored.split('.')[1]));
    if (typeof payload.exp === 'number' && payload.exp * 1000 < Date.now()) {
      localStorage.removeItem('jwt');
      return null;
    }
  } catch {
    localStorage.removeItem('jwt');
    return null;
  }
  return stored;
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(readValidToken);

  const login = useCallback(async (data: AuthRequest): Promise<AuthResponse> => {
    const response = await authService.login(data);
    setToken(response.token);
    return response;
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    setToken(null);
  }, []);

  return (
    <AuthContext value={{ isAuthenticated: !!token, token, login, logout }}>
      {children}
    </AuthContext>
  );
}
