import { Package } from 'lucide-react';
import { cn } from '@/lib/utils';

interface ProductImagePlaceholderProps {
  size?: 'sm' | 'md' | 'lg';
  className?: string;
}

const sizeConfig = {
  sm:  { wrapper: 'h-10 w-10 rounded-md',   icon: 'h-5 w-5' },
  md:  { wrapper: 'w-full aspect-[4/3] rounded-t-lg', icon: 'h-10 w-10' },
  lg:  { wrapper: 'w-full aspect-square rounded-xl', icon: 'h-16 w-16' },
};

export function ProductImagePlaceholder({ size = 'md', className }: ProductImagePlaceholderProps) {
  const { wrapper, icon } = sizeConfig[size];
  return (
    <div className={cn('flex items-center justify-center bg-slate-100', wrapper, className)}>
      <Package className={cn('text-slate-300', icon)} aria-hidden />
    </div>
  );
}
