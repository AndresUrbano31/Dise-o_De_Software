import { useEffect, useState } from 'react';
import { healthService, authService, categoryService, productService } from '@/services/api';

interface StepResult {
  id: number;
  label: string;
  status: 'pending' | 'ok' | 'error';
  detail?: string;
  ms?: number;
}

const INIT: StepResult[] = [
  { id: 1, label: 'Health check → GET /health', status: 'pending' },
  { id: 2, label: 'Login admin/admin123 → POST /auth/login', status: 'pending' },
  { id: 3, label: 'Listar categorías → GET /categories', status: 'pending' },
  { id: 4, label: 'Listar productos paginados → GET /products', status: 'pending' },
  { id: 5, label: 'Obtener primer producto → GET /products/:id', status: 'pending' },
  { id: 6, label: 'Crear categoría de prueba → POST /categories', status: 'pending' },
  { id: 7, label: 'Eliminar categoría de prueba → DELETE /categories/:id', status: 'pending' },
  { id: 8, label: 'Forzar error 409 (nombre duplicado)', status: 'pending' },
  { id: 9, label: 'Forzar error 401 (token inválido)', status: 'pending' },
  { id: 10, label: 'Logout (limpieza de token)', status: 'pending' },
];

async function timed<T>(fn: () => Promise<T>): Promise<{ result: T; ms: number }> {
  const start = Date.now();
  const result = await fn();
  return { result, ms: Date.now() - start };
}

export function ConnectionDiagnosticsPage() {
  const [steps, setSteps] = useState<StepResult[]>(INIT);
  const [running, setRunning] = useState(false);

  function update(id: number, patch: Partial<StepResult>) {
    setSteps((prev) => prev.map((s) => (s.id === id ? { ...s, ...patch } : s)));
  }

  async function run() {
    setRunning(true);
    setSteps(INIT.map((s) => ({ ...s, status: 'pending' })));
    let firstProductId = '';
    let testCategoryId = '';
    let firstCategoryName = '';

    // 1 — Health
    try {
      const { result, ms } = await timed(() => healthService.check());
      update(1, { status: 'ok', detail: `status: ${result.status}`, ms });
    } catch (e: unknown) {
      update(1, { status: 'error', detail: String(e) });
      setRunning(false);
      return;
    }

    // 2 — Login
    try {
      const { result, ms } = await timed(() =>
        authService.login({ username: 'admin', password: 'admin123' })
      );
      update(2, { status: 'ok', detail: `token: ${result.token.slice(0, 20)}…`, ms });
    } catch (e: unknown) {
      update(2, { status: 'error', detail: String(e) });
      setRunning(false);
      return;
    }

    // 3 — Categories
    try {
      const { result, ms } = await timed(() => categoryService.list());
      firstCategoryName = result[0]?.name ?? '';
      update(3, { status: 'ok', detail: `${result.length} categorías`, ms });
    } catch (e: unknown) {
      update(3, { status: 'error', detail: String(e) });
    }

    // 4 — Products paged
    try {
      const { result, ms } = await timed(() => productService.list({ page: 0, size: 10 }));
      firstProductId = result.content[0]?.id ?? '';
      update(4, {
        status: 'ok',
        detail: `${result.totalElements} total, page ${result.page + 1}/${result.totalPages}`,
        ms,
      });
    } catch (e: unknown) {
      update(4, { status: 'error', detail: String(e) });
    }

    // 5 — Product by id
    if (firstProductId) {
      try {
        const { result, ms } = await timed(() => productService.getById(firstProductId));
        update(5, { status: 'ok', detail: `"${result.name}" (SKU: ${result.sku})`, ms });
      } catch (e: unknown) {
        update(5, { status: 'error', detail: String(e) });
      }
    } else {
      update(5, { status: 'error', detail: 'Sin productos para probar' });
    }

    // 6 — Create test category
    const testName = `Test ${Date.now()}`;
    try {
      const { result, ms } = await timed(() =>
        categoryService.create({ name: testName, description: 'Categoría de prueba diagnóstica' })
      );
      testCategoryId = result.id;
      update(6, { status: 'ok', detail: `Creada id: ${result.id}`, ms });
    } catch (e: unknown) {
      update(6, { status: 'error', detail: String(e) });
    }

    // 7 — Delete test category
    if (testCategoryId) {
      try {
        const { ms } = await timed(() => categoryService.delete(testCategoryId));
        update(7, { status: 'ok', detail: '204 No Content', ms });
      } catch (e: unknown) {
        update(7, { status: 'error', detail: String(e) });
      }
    } else {
      update(7, { status: 'error', detail: 'Nada que eliminar (paso 6 falló)' });
    }

    // 8 — Force 409
    if (firstCategoryName) {
      try {
        await categoryService.create({ name: firstCategoryName });
        update(8, { status: 'error', detail: 'Esperaba 409 pero no hubo error' });
      } catch (e: unknown) {
        const msg = (e as { response?: { data?: { status?: number; message?: string } } })
          ?.response?.data;
        if (msg?.status === 409) {
          update(8, { status: 'ok', detail: `409 capturado: "${msg.message}"` });
        } else {
          update(8, { status: 'error', detail: `Error inesperado: ${String(e)}` });
        }
      }
    } else {
      update(8, { status: 'error', detail: 'Sin categorías para probar duplicado' });
    }

    // 9 — Force 401: invalidate token
    const backup = localStorage.getItem('jwt') ?? '';
    localStorage.setItem('jwt', backup.slice(0, -5) + 'XXXXX');
    try {
      await productService.list();
      update(9, { status: 'error', detail: 'Esperaba 401 pero la llamada pasó' });
    } catch (e: unknown) {
      const status = (e as { response?: { status?: number } })?.response?.status;
      if (status === 401) {
        update(9, { status: 'ok', detail: '401 capturado correctamente (interceptor limpia token)' });
      } else {
        update(9, { status: 'error', detail: `Error inesperado status ${status}: ${String(e)}` });
      }
    } finally {
      // Restore token for step 10
      localStorage.setItem('jwt', backup);
    }

    // 10 — Logout
    authService.logout();
    const afterLogout = localStorage.getItem('jwt');
    update(10, {
      status: afterLogout ? 'error' : 'ok',
      detail: afterLogout ? 'jwt todavía en localStorage' : 'jwt eliminado',
    });

    setRunning(false);
  }

  useEffect(() => {
    run();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const passed = steps.filter((s) => s.status === 'ok').length;
  const failed = steps.filter((s) => s.status === 'error').length;

  return (
    <div className="max-w-2xl mx-auto p-8 font-mono text-sm">
      <h1 className="text-xl font-bold mb-2">🔌 Diagnóstico de conexión Frontend → Backend</h1>
      <p className="text-slate-500 mb-6 font-sans text-sm">
        Backend: <code className="bg-slate-100 px-1 rounded">{import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080/api/v1'}</code>
      </p>

      <div className="space-y-2 mb-6">
        {steps.map((s) => (
          <div key={s.id} className="flex items-start gap-3 py-2 border-b border-slate-100">
            <span className="w-5 shrink-0 text-center">
              {s.status === 'pending' ? '⏳' : s.status === 'ok' ? '✓' : '✗'}
            </span>
            <div className="flex-1 min-w-0">
              <span className={s.status === 'error' ? 'text-red-600' : s.status === 'ok' ? 'text-green-700' : 'text-slate-500'}>
                {s.id}. {s.label}
              </span>
              {s.detail && (
                <div className="text-xs mt-0.5 text-slate-500 truncate">{s.detail}</div>
              )}
            </div>
            {s.ms !== undefined && (
              <span className="shrink-0 text-xs text-slate-400">{s.ms}ms</span>
            )}
          </div>
        ))}
      </div>

      <div className="flex items-center gap-4">
        <span className="text-green-700 font-bold">{passed}/10 ✓</span>
        {failed > 0 && <span className="text-red-600 font-bold">{failed} ✗</span>}
        <button
          onClick={run}
          disabled={running}
          className="ml-auto px-4 py-1.5 rounded bg-slate-900 text-white text-xs disabled:opacity-50"
        >
          {running ? 'Ejecutando…' : 'Volver a ejecutar'}
        </button>
      </div>

      <p className="mt-8 text-xs text-slate-400 font-sans">
        ⚠️ Esta página es temporal. Bórrala antes de hacer deploy.
      </p>
    </div>
  );
}
