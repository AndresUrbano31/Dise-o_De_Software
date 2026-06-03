import { useState } from 'react';
import { toast } from 'sonner';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import {
  useCategoryList,
  useCreateCategory,
  useUpdateCategory,
  useDeleteCategory,
} from '@/hooks/useCategories';
import { DataTable } from '@/components/shared/DataTable';
import { ConfirmDialog } from '@/components/shared/ConfirmDialog';
import { FormField } from '@/components/shared/FormField';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Textarea } from '@/components/ui/textarea';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { LoadingSpinner } from '@/components/shared/LoadingSpinner';
import { getErrorMessage } from '@/services/api';
import type { CategoryResponse } from '@/types/api';

interface CategoryForm {
  name: string;
  description: string;
}

const emptyForm: CategoryForm = { name: '', description: '' };

export function AdminCategoriesPage() {
  const { data: categories = [], isLoading } = useCategoryList();
  const createMutation = useCreateCategory();
  const [editingId, setEditingId] = useState<string | null>(null);
  const updateMutation = useUpdateCategory(editingId ?? '');
  const deleteMutation = useDeleteCategory();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState<CategoryForm>(emptyForm);
  const [formError, setFormError] = useState('');

  const [deleteTarget, setDeleteTarget] = useState<CategoryResponse | null>(null);
  const [deleteError, setDeleteError] = useState('');

  function openCreate() {
    setEditingId(null);
    setForm(emptyForm);
    setFormError('');
    setDialogOpen(true);
  }

  function openEdit(cat: CategoryResponse) {
    setEditingId(cat.id);
    setForm({ name: cat.name, description: cat.description ?? '' });
    setFormError('');
    setDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError('');
    const payload = { name: form.name.trim(), description: form.description.trim() || null };
    try {
      if (editingId) {
        await updateMutation.mutateAsync(payload);
        toast.success('Categoría actualizada');
      } else {
        await createMutation.mutateAsync(payload);
        toast.success('Categoría creada');
      }
      setDialogOpen(false);
    } catch (err) {
      setFormError(getErrorMessage(err));
    }
  }

  async function handleDelete() {
    if (!deleteTarget) return;
    setDeleteError('');
    try {
      await deleteMutation.mutateAsync(deleteTarget.id);
      toast.success('Categoría eliminada');
      setDeleteTarget(null);
    } catch (err) {
      setDeleteError(getErrorMessage(err));
    }
  }

  const isSaving = createMutation.isPending || updateMutation.isPending;

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-800">Categorías</h1>
          <p className="text-sm text-slate-500">{categories.length} categorías en total</p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Nueva categoría
        </Button>
      </div>

      <DataTable
        isLoading={isLoading}
        data={categories}
        emptyTitle="Sin categorías"
        emptyDescription="Crea tu primera categoría para organizar los productos."
        emptyAction={{ label: 'Nueva categoría', onClick: openCreate }}
        columns={[
          { header: 'Nombre', accessor: (c) => <span className="font-medium">{c.name}</span> },
          {
            header: 'Descripción',
            accessor: (c) => (
              <span className="text-slate-500">{c.description ?? '—'}</span>
            ),
          },
          {
            header: '',
            className: 'w-24 text-right',
            accessor: (c) => (
              <div className="flex justify-end gap-1">
                <Button variant="ghost" size="icon" onClick={() => openEdit(c)}>
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="text-red-500 hover:text-red-700"
                  onClick={() => { setDeleteTarget(c); setDeleteError(''); }}
                >
                  <Trash2 className="h-4 w-4" />
                </Button>
              </div>
            ),
          },
        ]}
      />

      <Dialog open={dialogOpen} onOpenChange={(o) => !isSaving && setDialogOpen(o)}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>{editingId ? 'Editar categoría' : 'Nueva categoría'}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            <FormField label="Nombre" htmlFor="cat-name" required>
              <Input
                id="cat-name"
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                autoFocus
              />
            </FormField>
            <FormField label="Descripción" htmlFor="cat-desc">
              <Textarea
                id="cat-desc"
                value={form.description}
                onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                rows={3}
              />
            </FormField>
            {formError && (
              <p className="text-sm text-red-600 rounded-md bg-red-50 border border-red-200 p-2">
                {formError}
              </p>
            )}
            <DialogFooter className="gap-2">
              <Button type="button" variant="outline" onClick={() => setDialogOpen(false)} disabled={isSaving}>
                Cancelar
              </Button>
              <Button type="submit" disabled={isSaving || !form.name.trim()}>
                {isSaving && <LoadingSpinner size="sm" className="mr-2" />}
                {editingId ? 'Guardar' : 'Crear'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar categoría"
        description={`¿Eliminar "${deleteTarget?.name}"? Esta acción no se puede deshacer.`}
        confirmLabel="Eliminar"
        onConfirm={handleDelete}
        onCancel={() => setDeleteTarget(null)}
        isLoading={deleteMutation.isPending}
        errorMessage={deleteError}
      />
    </div>
  );
}
