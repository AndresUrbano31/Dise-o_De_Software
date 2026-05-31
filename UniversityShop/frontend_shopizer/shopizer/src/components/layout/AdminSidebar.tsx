import { NavLink } from 'react-router-dom';
import { Package, Tag } from 'lucide-react';
import { cn } from '@/lib/utils';

const navItems = [
  { to: '/admin/products', label: 'Productos', icon: Package },
  { to: '/admin/categories', label: 'Categorías', icon: Tag },
];

export function AdminSidebar() {
  return (
    <aside className="w-56 shrink-0 border-r bg-slate-50 min-h-[calc(100vh-4rem)]">
      <nav className="p-3 space-y-1">
        {navItems.map(({ to, label, icon: Icon }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) =>
              cn(
                'flex items-center gap-2 px-3 py-2 rounded-md text-sm font-medium transition-colors',
                isActive
                  ? 'bg-slate-900 text-white'
                  : 'text-slate-700 hover:bg-slate-200'
              )
            }
          >
            <Icon className="h-4 w-4" />
            {label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}
