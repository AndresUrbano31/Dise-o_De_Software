import { useState } from 'react';
import { Search, X, PackageSearch } from 'lucide-react';
import { useProductList } from '@/hooks/useProducts';
import { useCategoryList } from '@/hooks/useCategories';
import { ProductCard } from '@/components/shared/ProductCard';
import { CategoryPills } from '@/components/shared/CategoryPills';
import { Pagination } from '@/components/shared/Pagination';
import { EmptyState } from '@/components/shared/EmptyState';
import { Skeleton } from '@/components/ui/skeleton';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';

export function CatalogPage() {
  const [page, setPage] = useState(0);
  const [name, setName] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [nameInput, setNameInput] = useState('');

  const { data, isLoading } = useProductList({
    page,
    size: 12,
    name: name || undefined,
    categoryId: categoryId || undefined,
  });
  const { data: categories = [] } = useCategoryList();

  function handleSearch(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setName(nameInput);
    setPage(0);
  }

  function handleCategorySelect(id: string) {
    setCategoryId(id);
    setPage(0);
  }

  function clearFilters() {
    setName('');
    setNameInput('');
    setCategoryId('');
    setPage(0);
  }

  const hasFilters = !!name || !!categoryId;

  return (
    <div>
      {/* Hero */}
      <section className="bg-slate-50 border-b border-slate-200 py-12">
        <div className="max-w-7xl mx-auto px-4 flex flex-col items-center text-center gap-6">
          <div>
            <h1 className="text-3xl font-bold tracking-tight text-slate-900 text-balance">
              Catálogo de la tienda
            </h1>
            <p className="mt-2 text-base text-slate-500">
              Explora todos los productos disponibles
            </p>
          </div>

          <form onSubmit={handleSearch} className="flex w-full max-w-xl gap-2">
            <div className="relative flex-1">
              <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-slate-400" aria-hidden />
              <Input
                value={nameInput}
                onChange={(e) => setNameInput(e.target.value)}
                placeholder="Buscar por nombre…"
                className="h-12 pl-10 text-base"
              />
            </div>
            <Button type="submit" size="lg">
              Buscar
            </Button>
          </form>
        </div>
      </section>

      {/* Sticky filter bar */}
      <div className="sticky top-16 z-30 bg-white/90 backdrop-blur-sm border-b border-slate-200">
        <div className="max-w-7xl mx-auto px-4 py-3 flex items-center justify-between gap-4">
          <CategoryPills
            categories={categories}
            selected={categoryId}
            onSelect={handleCategorySelect}
            className="flex-1 min-w-0"
          />
          <div className="flex items-center gap-2 shrink-0">
            {hasFilters && (
              <Button variant="ghost" size="iconSm" onClick={clearFilters} aria-label="Limpiar filtros">
                <X className="h-4 w-4" aria-hidden />
              </Button>
            )}
            {data && (
              <span className="hidden sm:block text-sm text-slate-500 whitespace-nowrap">
                {data.totalElements} producto{data.totalElements !== 1 ? 's' : ''}
              </span>
            )}
          </div>
        </div>
      </div>

      {/* Grid */}
      <div className="max-w-7xl mx-auto px-4 py-8">
        {isLoading ? (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {Array.from({ length: 8 }).map((_, i) => (
              <div key={i} className="rounded-xl border overflow-hidden">
                <Skeleton className="aspect-[4/3] rounded-none" />
                <div className="p-4 space-y-2">
                  <Skeleton className="h-4 w-20 rounded-full" />
                  <Skeleton className="h-4 w-full" />
                  <Skeleton className="h-3 w-24" />
                  <Skeleton className="h-6 w-28" />
                </div>
              </div>
            ))}
          </div>
        ) : data?.content.length === 0 ? (
          <EmptyState
            icon={PackageSearch}
            title={hasFilters ? 'Sin resultados' : 'Sin productos'}
            description={
              hasFilters
                ? 'No encontramos productos con esos filtros.'
                : 'Aún no hay productos en el catálogo.'
            }
            action={hasFilters ? { label: 'Limpiar filtros', onClick: clearFilters } : undefined}
          />
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
            {data?.content.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}

        {data && data.totalPages > 1 && (
          <div className="mt-8">
            <Pagination
              page={data.page}
              totalPages={data.totalPages}
              onChange={setPage}
            />
          </div>
        )}
      </div>
    </div>
  );
}
