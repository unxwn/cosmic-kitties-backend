package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Category;
import com.myroslav.cosmickitties.domain.Product;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

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
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Trigger schema creation by creating a dummy entity
        Category dummyCategory = Category.builder().name("dummy").build();
        categoryRepository.saveAndFlush(dummyCategory);
        
        // Now we can safely delete all
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        testCategory = Category.builder()
                .name("Electronics")
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void testCreateProduct() {
        // Given
        Product product = Product.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("1299.99"))
                .available(true)
                .category(testCategory)
                .build();

        // When
        Product saved = productRepository.save(product);

        // Then
        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
        assertEquals("High-performance laptop", saved.getDescription());
        assertEquals(new BigDecimal("1299.99"), saved.getPrice());
        assertTrue(saved.isAvailable());
        assertEquals(testCategory.getId(), saved.getCategory().getId());
    }

    @Test
    void testReadProduct() {
        // Given
        Product product = Product.builder()
                .name("Mouse")
                .price(new BigDecimal("29.99"))
                .available(true)
                .category(testCategory)
                .build();
        Product saved = productRepository.save(product);

        // When
        Optional<Product> found = productRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Mouse", found.get().getName());
        assertEquals(new BigDecimal("29.99"), found.get().getPrice());
    }

    @Test
    void testUpdateProduct() {
        // Given
        Product product = Product.builder()
                .name("Keyboard")
                .price(new BigDecimal("79.99"))
                .available(true)
                .category(testCategory)
                .build();
        Product saved = productRepository.save(product);

        // When
        saved.setName("Mechanical Keyboard");
        saved.setPrice(new BigDecimal("99.99"));
        saved.setAvailable(false);
        Product updated = productRepository.save(saved);

        // Then
        assertEquals("Mechanical Keyboard", updated.getName());
        assertEquals(new BigDecimal("99.99"), updated.getPrice());
        assertFalse(updated.isAvailable());
    }

    @Test
    void testDeleteProduct() {
        // Given
        Product product = Product.builder()
                .name("Monitor")
                .price(new BigDecimal("299.99"))
                .available(true)
                .category(testCategory)
                .build();
        Product saved = productRepository.save(product);
        Long id = saved.getId();

        // When
        productRepository.deleteById(id);

        // Then
        assertFalse(productRepository.existsById(id));
        Optional<Product> found = productRepository.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAllProducts() {
        // Given
        Product product1 = Product.builder()
                .name("Product 1")
                .price(new BigDecimal("10.00"))
                .available(true)
                .category(testCategory)
                .build();
        Product product2 = Product.builder()
                .name("Product 2")
                .price(new BigDecimal("20.00"))
                .available(true)
                .category(testCategory)
                .build();
        productRepository.save(product1);
        productRepository.save(product2);

        // When
        List<Product> all = productRepository.findAll();

        // Then
        assertEquals(2, all.size());
    }

    @Test
    void testFindByNameAndCategoryId() {
        // Given
        Product product = Product.builder()
                .name("Unique Product")
                .price(new BigDecimal("50.00"))
                .available(true)
                .category(testCategory)
                .build();
        productRepository.save(product);

        // When
        Optional<Product> found = productRepository.findByNameAndCategoryId("Unique Product", testCategory.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals("Unique Product", found.get().getName());
    }

    @Test
    void testFindAllByCategoryId() {
        // Given
        Category category2 = Category.builder()
                .name("Books")
                .build();
        category2 = categoryRepository.save(category2);

        Product product1 = Product.builder()
                .name("Product Cat1")
                .price(new BigDecimal("10.00"))
                .available(true)
                .category(testCategory)
                .build();
        Product product2 = Product.builder()
                .name("Product Cat1-2")
                .price(new BigDecimal("20.00"))
                .available(true)
                .category(testCategory)
                .build();
        Product product3 = Product.builder()
                .name("Product Cat2")
                .price(new BigDecimal("30.00"))
                .available(true)
                .category(category2)
                .build();
        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);

        // When
        List<Product> categoryProducts = productRepository.findAllByCategoryId(testCategory.getId());

        // Then
        assertEquals(2, categoryProducts.size());
        assertTrue(categoryProducts.stream().allMatch(p -> p.getCategory().getId().equals(testCategory.getId())));
    }

    @Test
    void testProductNotFound() {
        // When
        Optional<Product> found = productRepository.findById(999L);

        // Then
        assertTrue(found.isEmpty());
    }
}
