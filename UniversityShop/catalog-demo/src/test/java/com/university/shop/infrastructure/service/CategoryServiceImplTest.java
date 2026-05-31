package com.university.shop.infrastructure.service;

import com.university.shop.application.dto.CategoryRequestDTO;
import com.university.shop.domain.Category;
import com.university.shop.domain.exception.CategoryHasProductsException;
import com.university.shop.domain.exception.CategoryNameAlreadyExistsException;
import com.university.shop.domain.exception.CategoryNotFoundException;
import com.university.shop.infrastructure.CategoryRepository;
import com.university.shop.infrastructure.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void createCategory_duplicateName_throws() {
        when(categoryRepository.existsByName("Dup")).thenReturn(true);

        CategoryRequestDTO req = new CategoryRequestDTO();
        req.setName("Dup");

        assertThatThrownBy(() -> categoryService.createCategory(req))
                .isInstanceOf(CategoryNameAlreadyExistsException.class);
        verify(categoryRepository, never()).save(any());
    }

    @Test
    void updateCategory_duplicateName_throws() {
        Category existing = new Category();
        existing.setId("1");
        existing.setName("Old");

        when(categoryRepository.findById("1")).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameAndIdNot("Taken", "1")).thenReturn(true);

        CategoryRequestDTO req = new CategoryRequestDTO();
        req.setName("Taken");

        assertThatThrownBy(() -> categoryService.updateCategory("1", req))
                .isInstanceOf(CategoryNameAlreadyExistsException.class);
    }

    @Test
    void updateCategory_success() {
        Category existing = new Category();
        existing.setId("2");
        existing.setName("A");

        when(categoryRepository.findById("2")).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNameAndIdNot("B", "2")).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoryRequestDTO req = new CategoryRequestDTO();
        req.setName("B");
        req.setDescription("d");

        var dto = categoryService.updateCategory("2", req);

        assertThat(dto.getName()).isEqualTo("B");
        assertThat(dto.getDescription()).isEqualTo("d");

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("B");
    }

    @Test
    void deleteCategory_whenHasProducts_throws() {
        when(categoryRepository.existsById("9")).thenReturn(true);
        when(productRepository.countByCategoryId("9")).thenReturn(2L);

        assertThatThrownBy(() -> categoryService.deleteCategory("9"))
                .isInstanceOf(CategoryHasProductsException.class);
        verify(categoryRepository, never()).deleteById("9");
    }

    @Test
    void deleteCategory_success() {
        when(categoryRepository.existsById("3")).thenReturn(true);
        when(productRepository.countByCategoryId("3")).thenReturn(0L);

        categoryService.deleteCategory("3");

        verify(categoryRepository).deleteById("3");
    }

    @Test
    void deleteCategory_missing_throws() {
        when(categoryRepository.existsById("99")).thenReturn(false);

        assertThatThrownBy(() -> categoryService.deleteCategory("99"))
                .isInstanceOf(CategoryNotFoundException.class);
    }
}
