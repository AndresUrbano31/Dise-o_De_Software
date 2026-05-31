import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { productService } from '@/services/api';
import type { ProductRequest, ProductUpdate } from '@/types/api';

interface ProductListParams {
  page?: number;
  size?: number;
  name?: string;
  categoryId?: string;
}

export function useProductList(params: ProductListParams = {}) {
  return useQuery({
    queryKey: ['products', params],
    queryFn: () => productService.list(params),
  });
}

export function useProductDetail(id: string) {
  return useQuery({
    queryKey: ['products', id],
    queryFn: () => productService.getById(id),
    enabled: !!id,
  });
}

export function useCreateProduct() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: ProductRequest) => productService.create(data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['products'] });
    },
  });
}

export function useUpdateProduct(id: string) {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (data: ProductUpdate) => productService.update(id, data),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['products'] });
      qc.invalidateQueries({ queryKey: ['products', id] });
    },
  });
}

export function useDeleteProduct() {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: (id: string) => productService.delete(id),
    onSuccess: () => {
      qc.invalidateQueries({ queryKey: ['products'] });
    },
  });
}
