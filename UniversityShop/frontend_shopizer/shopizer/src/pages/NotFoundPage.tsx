import { useNavigate } from 'react-router-dom';
import { Compass } from 'lucide-react';
import { Button } from '@/components/ui/button';

export function NotFoundPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-slate-50 gap-6 p-4 text-center">
      <div className="flex h-20 w-20 items-center justify-center rounded-2xl bg-white border shadow-soft-sm">
        <Compass className="h-10 w-10 text-slate-300" aria-hidden />
      </div>

      <div>
        <p className="text-9xl font-extrabold text-slate-200 leading-none select-none">404</p>
        <h1 className="text-2xl font-bold text-slate-800 mt-2 tracking-tight">
          Página no encontrada
        </h1>
        <p className="text-slate-500 mt-2 text-sm max-w-sm">
          La página que buscas no existe o fue movida a otra dirección.
        </p>
      </div>

      <div className="flex gap-3">
        <Button onClick={() => navigate('/catalog')}>
          Ver catálogo
        </Button>
        <Button variant="outline" onClick={() => navigate(-1)}>
          Volver atrás
        </Button>
      </div>
    </div>
  );
}
