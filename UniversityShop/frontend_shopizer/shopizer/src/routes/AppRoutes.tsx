import { Routes, Route, Navigate } from 'react-router-dom';
import { PublicLayout } from '@/components/layout/PublicLayout';
import { AdminLayout } from '@/components/layout/AdminLayout';
import { ProtectedRoute } from './ProtectedRoute';
import { PublicRoute } from './PublicRoute';
import { CatalogPage } from '@/pages/CatalogPage';
import { ProductDetailPage } from '@/pages/ProductDetailPage';
import { LoginPage } from '@/pages/LoginPage';
import { AdminProductsPage } from '@/pages/admin/AdminProductsPage';
import { AdminCategoriesPage } from '@/pages/admin/AdminCategoriesPage';
import { NotFoundPage } from '@/pages/NotFoundPage';
import { ConnectionDiagnosticsPage } from '@/pages/diagnostics/ConnectionDiagnosticsPage';

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/catalog" replace />} />

      <Route element={<PublicLayout />}>
        <Route path="/catalog" element={<CatalogPage />} />
        <Route path="/catalog/:id" element={<ProductDetailPage />} />
      </Route>

      <Route
        path="/login"
        element={
          <PublicRoute>
            <LoginPage />
          </PublicRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute>
            <AdminLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="products" replace />} />
        <Route path="products" element={<AdminProductsPage />} />
        <Route path="categories" element={<AdminCategoriesPage />} />
      </Route>

      <Route path="/diagnostics" element={<ConnectionDiagnosticsPage />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
