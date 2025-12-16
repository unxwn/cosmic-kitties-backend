package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.entity.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.liquibase.enabled", () -> "false");
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();
    }

    @Test
    void testCreateCategory() {
        // Given
        Category category = Category.builder()
                .name("Electronics")
                .build();

        // When
        Category saved = categoryRepository.save(category);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Electronics", saved.getName());
    }

    @Test
    void testReadCategory() {
        // Given
        Category category = Category.builder()
                .name("Books")
                .build();
        Category saved = categoryRepository.save(category);

        // When
        Optional<Category> found = categoryRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Books", found.get().getName());
    }

    @Test
    void testUpdateCategory() {
        // Given
        Category category = Category.builder()
                .name("Old Category")
                .build();
        Category saved = categoryRepository.save(category);

        // When
        saved.setName("New Category");
        Category updated = categoryRepository.save(saved);

        // Then
        assertEquals("New Category", updated.getName());
    }

    @Test
    void testDeleteCategory() {
        // Given
        Category category = Category.builder()
                .name("To Delete")
                .build();
        Category saved = categoryRepository.save(category);
        Long id = saved.getId();

        // When
        categoryRepository.deleteById(id);

        // Then
        assertFalse(categoryRepository.existsById(id));
        Optional<Category> found = categoryRepository.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAllCategories() {
        // Given
        Category category1 = Category.builder()
                .name("Category 1")
                .build();
        Category category2 = Category.builder()
                .name("Category 2")
                .build();
        Category category3 = Category.builder()
                .name("Category 3")
                .build();
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);

        // When
        List<Category> all = categoryRepository.findAll();

        // Then
        assertEquals(3, all.size());
    }

    @Test
    void testCategoryNotFound() {
        // When
        Optional<Category> found = categoryRepository.findById(999L);

        // Then
        assertTrue(found.isEmpty());
    }
}
