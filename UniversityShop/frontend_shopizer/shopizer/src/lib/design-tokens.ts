/**
 * Design tokens — source of truth for all brand values.
 * The CSS @theme block in index.css mirrors these values.
 */

export const colors = {
  brand: {
    primary:      'hsl(217 91% 35%)',
    primaryHover: 'hsl(217 91% 30%)',
    accent:       'hsl(38 92% 50%)',
  },
  status: {
    success: 'hsl(142 71% 38%)',
    warning: 'hsl(38 92% 50%)',
    danger:  'hsl(0 72% 51%)',
    info:    'hsl(199 89% 48%)',
  },
} as const;

export const shadows = {
  softSm: '0 1px 2px rgba(15,23,42,.06)',
  softMd: '0 4px 12px rgba(15,23,42,.08)',
  softLg: '0 12px 32px rgba(15,23,42,.10)',
} as const;

export const radius = {
  input:  '0.375rem', // rounded-md
  card:   '0.75rem',  // rounded-xl
  badge:  '9999px',   // rounded-full
} as const;

export const typography = {
  fontSans: '"Inter Variable", system-ui, sans-serif',
  fontMono: '"JetBrains Mono", ui-monospace, monospace',
} as const;
