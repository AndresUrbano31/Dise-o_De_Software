import { GraduationCap } from 'lucide-react';
import { cn } from '@/lib/utils';

interface WordmarkProps {
  dark?: boolean;
  className?: string;
  iconOnly?: boolean;
}

export function Wordmark({ dark = false, className, iconOnly = false }: WordmarkProps) {
  return (
    <div className={cn('flex items-center gap-2.5', className)}>
      <div
        className={cn(
          'flex h-8 w-8 items-center justify-center rounded-lg shrink-0',
          dark ? 'bg-white/10 text-white' : 'bg-brand-primary text-white'
        )}
      >
        <GraduationCap className="h-4.5 w-4.5" aria-hidden />
      </div>
      {!iconOnly && (
        <span className={cn('font-bold text-base leading-none', dark ? 'text-white' : 'text-slate-900')}>
          University Shop
        </span>
      )}
    </div>
  );
}
