package com.kovan.integrationTestCases;

import com.kovan.entities.Category;
import com.kovan.repository.CategoryRepository;
import com.kovan.service.CategoryService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.*;

@SpringBootTest
@Transactional
class CategoryServiceIT {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    public void setup() {
        categoryRepository.deleteAll();
    }

    @Test
    @Transactional
    void saveCategoryTest() {
        Category category = Category.builder()
                .name("Fiction")
                .description("Books that contain fictional stories")
                .build();

        Category savedCategory = categoryService.saveCategory(category);

        assertThat(savedCategory).isNotNull();
        assertThat(savedCategory.getCategoryId()).isNotNull();
        assertThat(savedCategory.getName()).isEqualTo("Fiction");
        assertThat(savedCategory.getDescription()).isEqualTo("Books that contain fictional stories");
    }

    @Test
    void getCategoryByIdTest() {
        Category category = Category.builder()
                .name("Science")
                .description("Books related to science")
                .build();
        Category savedCategory = categoryService.saveCategory(category);

        Category fetchedCategory = categoryService.getCategoryById(savedCategory.getCategoryId());

        assertThat(fetchedCategory).isNotNull();
        assertThat(fetchedCategory.getCategoryId()).isEqualTo(savedCategory.getCategoryId());
        assertThat(fetchedCategory.getName()).isEqualTo("Science");
        assertThat(fetchedCategory.getDescription()).isEqualTo("Books related to science");
    }

    @Test
    void getAllCategoriesTest() {
        Category category1 = Category.builder().name("History").description("Books about history").build();
        Category category2 = Category.builder().name("Technology").description("Books about technology").build();

        categoryService.saveCategory(category1);
        categoryService.saveCategory(category2);

        List<Category> categories = categoryService.getAllCategories();

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(Category::getName).containsExactlyInAnyOrder("History", "Technology");
        assertThat(categories).extracting(Category::getDescription)
                .containsExactlyInAnyOrder("Books about history", "Books about technology");
    }

    @Test
    void deleteCategoryTest() {
        Category category = Category.builder()
                .name("Mystery")
                .description("Books that involve solving mysteries")
                .build();
        Category savedCategory = categoryService.saveCategory(category);

        categoryService.deleteCategory(savedCategory.getCategoryId());

        Optional<Category> deletedCategory = categoryRepository.findById(savedCategory.getCategoryId());
        assertThat(deletedCategory).isNotPresent();
    }
}

