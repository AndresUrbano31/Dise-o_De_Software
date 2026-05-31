import type { ReactNode } from 'react';
import { Button } from '@/components/ui/button';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { EmptyState } from './EmptyState';
import { Skeleton } from './Skeleton';

interface Column<T> {
  header: string;
  accessor: (row: T) => ReactNode;
  className?: string;
}

interface PaginationMeta {
  page: number;
  totalPages: number;
  totalElements: number;
  first: boolean;
  last: boolean;
  size: number;
}

interface DataTableProps<T> {
  columns: Column<T>[];
  data: T[];
  pagination?: PaginationMeta;
  onPageChange?: (page: number) => void;
  isLoading?: boolean;
  emptyTitle?: string;
  emptyDescription?: string;
  emptyAction?: { label: string; onClick: () => void };
}

export function DataTable<T>({
  columns,
  data,
  pagination,
  onPageChange,
  isLoading = false,
  emptyTitle = 'Sin resultados',
  emptyDescription,
  emptyAction,
}: DataTableProps<T>) {
  if (isLoading) {
    return (
      <div className="space-y-2">
        {Array.from({ length: 5 }).map((_, i) => (
          <Skeleton key={i} className="h-12 w-full" />
        ))}
      </div>
    );
  }

  if (data.length === 0) {
    return <EmptyState title={emptyTitle} description={emptyDescription} action={emptyAction} />;
  }

  const start = pagination ? pagination.page * pagination.size + 1 : 1;
  const end = pagination ? Math.min(start + pagination.size - 1, pagination.totalElements) : data.length;

  return (
    <div className="space-y-4">
      <div className="rounded-md border overflow-hidden">
        <table className="w-full text-sm">
          <thead className="bg-slate-50 border-b">
            <tr>
              {columns.map((col, i) => (
                <th key={i} className={`px-4 py-3 text-left font-medium text-slate-600 ${col.className ?? ''}`}>
                  {col.header}
                </th>
              ))}
            </tr>
          </thead>
          <tbody className="divide-y">
            {data.map((row, ri) => (
              <tr key={ri} className="hover:bg-slate-50 transition-colors">
                {columns.map((col, ci) => (
                  <td key={ci} className={`px-4 py-3 ${col.className ?? ''}`}>
                    {col.accessor(row)}
                  </td>
                ))}
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {pagination && pagination.totalPages > 1 && (
        <div className="flex items-center justify-between text-sm text-slate-600">
          <span>
            Mostrando {start}–{end} de {pagination.totalElements}
          </span>
          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              disabled={pagination.first}
              onClick={() => onPageChange?.(pagination.page - 1)}
            >
              <ChevronLeft className="h-4 w-4" />
              Anterior
            </Button>
            <span>
              Página {pagination.page + 1} de {pagination.totalPages}
            </span>
            <Button
              variant="outline"
              size="sm"
              disabled={pagination.last}
              onClick={() => onPageChange?.(pagination.page + 1)}
            >
              Siguiente
              <ChevronRight className="h-4 w-4" />
            </Button>
          </div>
        </div>
      )}
    </div>
  );
}
