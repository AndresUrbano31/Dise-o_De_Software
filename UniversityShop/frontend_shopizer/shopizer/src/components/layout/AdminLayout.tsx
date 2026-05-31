import { Outlet } from 'react-router-dom';
import { Header } from './Header';
import { AdminSidebar } from './AdminSidebar';

export function AdminLayout() {
  return (
    <div className="min-h-screen flex flex-col">
      <Header />
      <div className="flex flex-1">
        <AdminSidebar />
        <main className="flex-1 p-6 overflow-auto">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
