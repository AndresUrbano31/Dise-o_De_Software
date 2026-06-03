import { Link } from 'react-router-dom';
import type { ProductResponse } from '@/types/api';
import { formatPrice } from '@/lib/format';
import { Badge } from '@/components/ui/badge';
import { ProductImagePlaceholder } from './ProductImagePlaceholder';

interface ProductCardProps {
  product: ProductResponse;
}

export function ProductCard({ product }: ProductCardProps) {
  return (
    <Link
      to={`/catalog/${product.id}`}
      className="group block rounded-xl border bg-white transition-all duration-200 hover:shadow-soft-md hover:-translate-y-0.5 overflow-hidden"
    >
      <ProductImagePlaceholder size="md" />

      <div className="p-4 flex flex-col gap-2">
        <Badge variant="brand" className="w-fit">
          {product.category.name}
        </Badge>

        <h3 className="font-semibold text-slate-800 leading-snug line-clamp-2 group-hover:text-brand-primary transition-colors">
          {product.name}
        </h3>

        <p className="text-xs font-mono text-slate-400">
          SKU: {product.sku}
        </p>

        <p className="text-xl font-bold text-brand-primary mt-1">
          {formatPrice(product.price)}
        </p>
      </div>
    </Link>
  );
}
