import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import { useProductDetail } from '@/hooks/useProducts';
import { Breadcrumb } from '@/components/shared/Breadcrumb';
import { ProductImagePlaceholder } from '@/components/shared/ProductImagePlaceholder';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Skeleton } from '@/components/ui/skeleton';
import { formatPrice, formatDate } from '@/lib/format';

function ProductDetailSkeleton() {
  return (
    <div className="grid grid-cols-1 md:grid-cols-2 gap-8 lg:gap-12">
      <Skeleton className="aspect-square rounded-xl" />
      <div className="space-y-4">
        <Skeleton className="h-4 w-40" />
        <Skeleton className="h-8 w-3/4" />
        <Skeleton className="h-5 w-24 rounded-full" />
        <Skeleton className="h-10 w-32" />
        <Skeleton className="h-16 w-full" />
        <Skeleton className="h-px w-full" />
        <Skeleton className="h-4 w-48" />
        <Skeleton className="h-10 w-40" />
      </div>
    </div>
  );
}

export function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { data: product, isLoading, isError } = useProductDetail(id ?? '');

  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {isLoading ? (
        <ProductDetailSkeleton />
      ) : isError || !product ? (
        <div className="flex flex-col items-center justify-center py-24 text-center">
          <p className="text-6xl font-extrabold text-slate-200 mb-4">404</p>
          <h2 className="text-xl font-semibold text-slate-800">Producto no encontrado</h2>
          <p className="text-sm text-slate-500 mt-2">
            Este producto no existe o fue eliminado.
          </p>
          <Button variant="outline" className="mt-6" onClick={() => navigate('/catalog')}>
            <ArrowLeft className="h-4 w-4" aria-hidden />
            Volver al catálogo
          </Button>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-8 lg:gap-12">
          {/* Image */}
          <ProductImagePlaceholder size="lg" className="border rounded-xl" />

          {/* Details */}
          <div className="flex flex-col gap-4">
            <Breadcrumb
              items={[
                { label: 'Catálogo', href: '/catalog' },
                { label: product.name },
              ]}
            />

            <h1 className="text-2xl font-bold tracking-tight text-slate-900 text-balance">
              {product.name}
            </h1>

            <div className="flex items-center gap-3">
              <Badge variant="brand">{product.category.name}</Badge>
              <span className="text-xs font-mono text-slate-400">
                SKU: {product.sku}
              </span>
            </div>

            <p className="text-4xl font-bold text-brand-primary">
              {formatPrice(product.price)}
            </p>

            <Separator />

            <p className="text-xs text-slate-500">
              Agregado al catálogo el{' '}
              <span className="font-medium text-slate-700">
                {formatDate(product.dateCreated)}
              </span>
            </p>

            <Button
              variant="outline"
              className="w-fit"
              onClick={() => navigate('/catalog')}
            >
              <ArrowLeft className="h-4 w-4" aria-hidden />
              Volver al catálogo
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
