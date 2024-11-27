package com.kovan.junitTestCases;

import com.kovan.entities.*;
import com.kovan.repository.*;
import com.kovan.service.CategoryService;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private final Category category = Category.builder()
                .categoryId(1L)
                .name("Fiction")
                .description("Books in the fiction genre")
                .build();

    @Test
    void testSaveCategory() {

        when(categoryRepository.save(any(Category.class))).thenReturn(category);

        Category result = categoryService.saveCategory(category);

        assertNotNull(result);
        assertEquals(category.getCategoryId(), result.getCategoryId());
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository,  times(1)).save(category);
    }

    @Test
    void testGetCategoryById() {

        when(categoryRepository.findById(category.getCategoryId())).thenReturn(of(category));

        Category result = categoryService.getCategoryById(category.getCategoryId());

        assertNotNull(result);
        assertEquals(category.getCategoryId(), result.getCategoryId());
        assertEquals(category.getName(), result.getName());
        verify(categoryRepository, times(1)).findById(category.getCategoryId());
    }

    @Test
    void testGetCategoryByIdThrowsExceptionWhenNotFound() {

       when(categoryRepository.findById(category.getCategoryId())).thenReturn(empty());

        assertThrows(RuntimeException.class,
                () -> categoryService.getCategoryById(category.getCategoryId()));

       verify(categoryRepository,times(1)).findById(category.getCategoryId());
    }

    @Test
    void testGetAllCategories() {

        List<Category> categories = List.of(category);
        when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getAllCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(category, result.getFirst());
        verify(categoryRepository,times(1)).findAll();
    }

    @Test
    void testDeleteCategory() {

       when(categoryRepository.findById(category.getCategoryId())).thenReturn(of(category));

        categoryService.deleteCategory(category.getCategoryId());

       verify(categoryRepository, times(1)).findById(category.getCategoryId());
      verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void testDeleteCategoryThrowsExceptionWhenNotFound(){

        when(categoryRepository.findById(category.getCategoryId())).thenReturn(empty());

        assertThrows(RuntimeException.class,
                () -> categoryService.deleteCategory(category.getCategoryId()));

        verify(categoryRepository, times(1)).findById(category.getCategoryId());
        verify(categoryRepository, never()).delete(any(Category.class));
    }
}

