import { useState } from 'react';
import { toast } from 'sonner';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import {
  useProductList,
  useCreateProduct,
  useUpdateProduct,
  useDeleteProduct,
} from '@/hooks/useProducts';
import { useCategoryList } from '@/hooks/useCategories';
import { DataTable } from '@/components/shared/DataTable';
import { ConfirmDialog } from '@/components/shared/ConfirmDialog';
import { FormField } from '@/components/shared/FormField';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
} from '@/components/ui/dialog';
import { Badge } from '@/components/ui/badge';
import { LoadingSpinner } from '@/components/shared/LoadingSpinner';
import { formatPrice, formatDate } from '@/lib/format';
import { getErrorMessage } from '@/services/api';
import type { ProductResponse } from '@/types/api';

interface ProductForm {
  sku: string;
  name: string;
  price: string;
  categoryId: string;
}

const emptyForm: ProductForm = { sku: '', name: '', price: '', categoryId: '' };

export function AdminProductsPage() {
  const [page, setPage] = useState(0);
  const { data, isLoading } = useProductList({ page, size: 10 });
  const { data: categories = [] } = useCategoryList();

  const createMutation = useCreateProduct();
  const [editingId, setEditingId] = useState<string | null>(null);
  const updateMutation = useUpdateProduct(editingId ?? '');
  const deleteMutation = useDeleteProduct();

  const [dialogOpen, setDialogOpen] = useState(false);
  const [form, setForm] = useState<ProductForm>(emptyForm);
  const [formError, setFormError] = useState('');

  const [deleteTarget, setDeleteTarget] = useState<ProductResponse | null>(null);
  const [deleteError, setDeleteError] = useState('');

  function openCreate() {
    setEditingId(null);
    setForm(emptyForm);
    setFormError('');
    setDialogOpen(true);
  }

  function openEdit(p: ProductResponse) {
    setEditingId(p.id);
    setForm({ sku: p.sku, name: p.name, price: String(p.price), categoryId: p.category.id });
    setFormError('');
    setDialogOpen(true);
  }

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    setFormError('');
    const price = parseFloat(form.price);
    if (isNaN(price) || price <= 0) {
      setFormError('El precio debe ser un número positivo.');
      return;
    }
    try {
      if (editingId) {
        await updateMutation.mutateAsync({ name: form.name.trim(), price, categoryId: form.categoryId });
        toast.success('Producto actualizado');
      } else {
        await createMutation.mutateAsync({ sku: form.sku.trim(), name: form.name.trim(), price, categoryId: form.categoryId });
        toast.success('Producto creado');
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
      toast.success('Producto eliminado');
      setDeleteTarget(null);
    } catch (err) {
      setDeleteError(getErrorMessage(err));
    }
  }

  const isSaving = createMutation.isPending || updateMutation.isPending;
  const isFormValid = form.name.trim() && form.price && form.categoryId && (editingId || form.sku.trim());

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold text-slate-800">Productos</h1>
          <p className="text-sm text-slate-500">
            {data ? `${data.totalElements} productos en total` : 'Cargando...'}
          </p>
        </div>
        <Button onClick={openCreate}>
          <Plus className="h-4 w-4 mr-2" />
          Nuevo producto
        </Button>
      </div>

      <DataTable
        isLoading={isLoading}
        data={data?.content ?? []}
        pagination={data}
        onPageChange={setPage}
        emptyTitle="Sin productos"
        emptyDescription="Crea tu primer producto para empezar."
        emptyAction={{ label: 'Nuevo producto', onClick: openCreate }}
        columns={[
          { header: 'SKU', accessor: (p) => <span className="font-mono text-xs">{p.sku}</span>, className: 'w-28' },
          { header: 'Nombre', accessor: (p) => <span className="font-medium">{p.name}</span> },
          {
            header: 'Categoría',
            accessor: (p) => <Badge variant="secondary">{p.category.name}</Badge>,
          },
          {
            header: 'Precio',
            accessor: (p) => <span className="font-semibold">{formatPrice(p.price)}</span>,
            className: 'w-32 text-right',
          },
          {
            header: 'Creado',
            accessor: (p) => <span className="text-slate-500 text-xs">{formatDate(p.dateCreated)}</span>,
            className: 'w-36',
          },
          {
            header: '',
            className: 'w-24 text-right',
            accessor: (p) => (
              <div className="flex justify-end gap-1">
                <Button variant="ghost" size="icon" onClick={() => openEdit(p)}>
                  <Pencil className="h-4 w-4" />
                </Button>
                <Button
                  variant="ghost"
                  size="icon"
                  className="text-red-500 hover:text-red-700"
                  onClick={() => { setDeleteTarget(p); setDeleteError(''); }}
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
            <DialogTitle>{editingId ? 'Editar producto' : 'Nuevo producto'}</DialogTitle>
          </DialogHeader>
          <form onSubmit={handleSubmit} className="space-y-4">
            {!editingId && (
              <FormField label="SKU" htmlFor="prod-sku" required hint="Identificador único, no se puede cambiar después.">
                <Input
                  id="prod-sku"
                  value={form.sku}
                  onChange={(e) => setForm((f) => ({ ...f, sku: e.target.value }))}
                  autoFocus
                />
              </FormField>
            )}
            <FormField label="Nombre" htmlFor="prod-name" required>
              <Input
                id="prod-name"
                value={form.name}
                onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                autoFocus={!!editingId}
              />
            </FormField>
            <FormField label="Precio (MXN)" htmlFor="prod-price" required>
              <Input
                id="prod-price"
                type="number"
                min="0.01"
                step="0.01"
                value={form.price}
                onChange={(e) => setForm((f) => ({ ...f, price: e.target.value }))}
              />
            </FormField>
            <FormField label="Categoría" htmlFor="prod-cat" required>
              <Select
                value={form.categoryId}
                onValueChange={(v) => setForm((f) => ({ ...f, categoryId: v }))}
              >
                <SelectTrigger id="prod-cat">
                  <SelectValue placeholder="Selecciona una categoría" />
                </SelectTrigger>
                <SelectContent>
                  {categories.map((cat) => (
                    <SelectItem key={cat.id} value={cat.id}>
                      {cat.name}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
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
              <Button type="submit" disabled={isSaving || !isFormValid}>
                {isSaving && <LoadingSpinner size="sm" className="mr-2" />}
                {editingId ? 'Guardar' : 'Crear'}
              </Button>
            </DialogFooter>
          </form>
        </DialogContent>
      </Dialog>

      <ConfirmDialog
        open={!!deleteTarget}
        title="Eliminar producto"
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
