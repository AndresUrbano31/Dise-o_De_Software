import { Navigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import type { ReactNode } from 'react';

export function PublicRoute({ children }: { children: ReactNode }) {
  const { isAuthenticated } = useAuth();

  if (isAuthenticated) {
    return <Navigate to="/admin/products" replace />;
  }

  return <>{children}</>;
}
