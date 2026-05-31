import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { Wordmark } from '@/components/shared/Wordmark';

export function PublicLayout() {
  return (
    <div className="min-h-screen flex flex-col bg-white">
      <Header />
      <main className="flex-1">
        <Outlet />
      </main>
      <footer className="border-t border-slate-100 py-6">
        <div className="max-w-7xl mx-auto px-4 flex items-center justify-between gap-4">
          <Wordmark />
          <p className="text-xs text-slate-400">
            © {new Date().getFullYear()} University Shop. Todos los derechos reservados.
          </p>
        </div>
      </footer>
    </div>
  );
}
