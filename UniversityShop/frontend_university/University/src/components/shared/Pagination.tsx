import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Button } from '@/components/ui/button';

interface PaginationProps {
  page: number;
  totalPages: number;
  onChange: (page: number) => void;
}

export function Pagination({ page, totalPages, onChange }: PaginationProps) {
  if (totalPages <= 1) return null;

  return (
    <div className="flex items-center justify-center gap-2">
      <Button
        variant="outline"
        size="icon"
        aria-label="Página anterior"
        disabled={page === 0}
        onClick={() => onChange(page - 1)}
      >
        <ChevronLeft className="h-4 w-4" aria-hidden />
      </Button>

      <span className="hidden sm:block text-sm text-slate-600 min-w-[120px] text-center">
        Página {page + 1} de {totalPages}
      </span>
      <span className="sm:hidden text-sm text-slate-600">
        {page + 1} / {totalPages}
      </span>

      <Button
        variant="outline"
        size="icon"
        aria-label="Página siguiente"
        disabled={page >= totalPages - 1}
        onClick={() => onChange(page + 1)}
      >
        <ChevronRight className="h-4 w-4" aria-hidden />
      </Button>
    </div>
  );
}
