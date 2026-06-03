import { cn } from '@/lib/utils';
import type { CategoryResponse } from '@/types/api';

interface CategoryPillsProps {
  categories: CategoryResponse[];
  selected: string;
  onSelect: (id: string) => void;
  className?: string;
}

export function CategoryPills({ categories, selected, onSelect, className }: CategoryPillsProps) {
  const all = [{ id: '', name: 'Todas' } as CategoryResponse, ...categories];

  return (
    <div className={cn('flex gap-2 overflow-x-auto no-scrollbar', className)}>
      {all.map((cat) => {
        const active = selected === cat.id;
        return (
          <button
            key={cat.id}
            onClick={() => onSelect(cat.id)}
            className={cn(
              'shrink-0 rounded-full px-4 py-1.5 text-sm font-medium transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-brand-primary focus-visible:ring-offset-2',
              active
                ? 'bg-brand-primary text-white'
                : 'border border-slate-200 bg-white text-slate-700 hover:bg-slate-50'
            )}
          >
            {cat.name}
          </button>
        );
      })}
    </div>
  );
}
