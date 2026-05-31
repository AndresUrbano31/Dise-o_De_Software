import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { Button } from '@/components/ui/button';
import { Wordmark } from '@/components/shared/Wordmark';
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu';
import { LayoutDashboard, LogOut } from 'lucide-react';

export function Header() {
  const { isAuthenticated, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-40 w-full border-b border-slate-200 bg-white/90 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-4 h-16 flex items-center justify-between">
        <Link to="/catalog" className="flex items-center">
          <Wordmark className="hidden sm:flex" />
          <Wordmark iconOnly className="sm:hidden" />
        </Link>

        <nav className="flex items-center gap-3">
          {isAuthenticated ? (
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <button
                  className="h-8 w-8 rounded-full bg-brand-primary text-white text-sm font-bold flex items-center justify-center focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary focus-visible:ring-offset-2"
                  aria-label="Menú de usuario"
                >
                  A
                </button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end" className="w-48">
                <DropdownMenuItem asChild>
                  <Link to="/admin/products" className="flex items-center gap-2 cursor-pointer">
                    <LayoutDashboard className="h-4 w-4" aria-hidden />
                    Panel de admin
                  </Link>
                </DropdownMenuItem>
                <DropdownMenuSeparator />
                <DropdownMenuItem
                  onClick={handleLogout}
                  className="text-danger focus:text-danger cursor-pointer"
                >
                  <LogOut className="h-4 w-4" aria-hidden />
                  Cerrar sesión
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          ) : (
            <Button variant="outline" size="sm" asChild>
              <Link to="/login">Acceder</Link>
            </Button>
          )}
        </nav>
      </div>
    </header>
  );
}
