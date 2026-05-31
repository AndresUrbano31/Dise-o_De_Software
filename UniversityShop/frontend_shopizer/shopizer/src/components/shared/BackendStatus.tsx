import { WifiOff } from 'lucide-react';

interface BackendStatusProps {
  isDown: boolean;
}

export function BackendStatus({ isDown }: BackendStatusProps) {
  if (!isDown) return null;

  return (
    <div className="fixed top-0 left-0 right-0 z-50 bg-red-600 text-white text-sm text-center py-2 flex items-center justify-center gap-2">
      <WifiOff className="h-4 w-4" />
      Sin conexión con el servidor. Algunas funciones pueden no estar disponibles.
    </div>
  );
}
