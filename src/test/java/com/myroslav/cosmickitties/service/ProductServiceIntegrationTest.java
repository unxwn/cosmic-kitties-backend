package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.dto.ProductDTO;
import com.myroslav.cosmickitties.entity.Category;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.repository.CategoryRepository;
import com.myroslav.cosmickitties.repository.ProductRepository;
import com.myroslav.cosmickitties.service.abstraction.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
class ProductServiceIntegrationTest {

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
    private IProductService productService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        testCategory = Category.builder()
                .name("Electronics")
                .build();
        testCategory = categoryRepository.save(testCategory);
    }

    @Test
    void testCreateProduct() {
        ProductDTO dto = ProductDTO.builder()
                .name("Laptop")
                .description("High-performance laptop")
                .price(new BigDecimal("1299.99"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();

        ProductDTO created = productService.create(dto);

        assertNotNull(created.getId());
        assertEquals("Laptop", created.getName());
        assertEquals("High-performance laptop", created.getDescription());
        assertEquals(new BigDecimal("1299.99"), created.getPrice());
        assertTrue(created.isAvailable());
        assertEquals(testCategory.getId(), created.getCategoryId());
    }

    @Test
    void testCreateProductWithNonExistentCategory() {
        ProductDTO dto = ProductDTO.builder()
                .name("Laptop")
                .price(new BigDecimal("1299.99"))
                .categoryId(999L)
                .build();

        assertThrows(ResourceNotFoundException.class, () -> productService.create(dto));
    }

    @Test
    void testGetProductById() {
        ProductDTO dto = ProductDTO.builder()
                .name("Mouse")
                .price(new BigDecimal("29.99"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();
        ProductDTO created = productService.create(dto);

        ProductDTO found = productService.getById(created.getId());

        assertNotNull(found);
        assertEquals("Mouse", found.getName());
        assertEquals(new BigDecimal("29.99"), found.getPrice());
    }

    @Test
    void testGetProductByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(999L));
    }

    @Test
    void testGetAllProducts() {
        ProductDTO dto1 = ProductDTO.builder()
                .name("Product 1")
                .price(new BigDecimal("10.00"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();
        ProductDTO dto2 = ProductDTO.builder()
                .name("Product 2")
                .price(new BigDecimal("20.00"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();
        productService.create(dto1);
        productService.create(dto2);

        List<ProductDTO> all = productService.getAll();

        assertEquals(2, all.size());
    }

    @Test
    void testUpdateProduct() {
        ProductDTO dto = ProductDTO.builder()
                .name("Keyboard")
                .price(new BigDecimal("79.99"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();
        ProductDTO created = productService.create(dto);

        ProductDTO updateDto = ProductDTO.builder()
                .name("Mechanical Keyboard")
                .description("RGB Mechanical Keyboard")
                .price(new BigDecimal("99.99"))
                .available(false)
                .categoryId(testCategory.getId())
                .build();
        ProductDTO updated = productService.update(created.getId(), updateDto);

        assertEquals("Mechanical Keyboard", updated.getName());
        assertEquals("RGB Mechanical Keyboard", updated.getDescription());
        assertEquals(new BigDecimal("99.99"), updated.getPrice());
        assertFalse(updated.isAvailable());
    }

    @Test
    void testUpdateProductWithNewCategory() {
        Category newCategory = Category.builder()
                .name("Accessories")
                .build();
        newCategory = categoryRepository.save(newCategory);

        ProductDTO dto = ProductDTO.builder()
                .name("Product")
                .price(new BigDecimal("10.00"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();
        ProductDTO created = productService.create(dto);

        ProductDTO updateDto = ProductDTO.builder()
                .name("Product")
                .price(new BigDecimal("10.00"))
                .available(true)
                .categoryId(newCategory.getId())
                .build();
        ProductDTO updated = productService.update(created.getId(), updateDto);

        assertEquals(newCategory.getId(), updated.getCategoryId());
    }

    @Test
    void testUpdateProductNotFound() {
        ProductDTO updateDto = ProductDTO.builder()
                .name("Product")
                .price(new BigDecimal("10.00"))
                .categoryId(testCategory.getId())
                .build();

        assertThrows(ResourceNotFoundException.class, () -> productService.update(999L, updateDto));
    }

    @Test
    void testDeleteProduct() {
        ProductDTO dto = ProductDTO.builder()
                .name("Monitor")
                .price(new BigDecimal("299.99"))
                .available(true)
                .categoryId(testCategory.getId())
                .build();
        ProductDTO created = productService.create(dto);
        Long id = created.getId();

        productService.delete(id);

        assertThrows(ResourceNotFoundException.class, () -> productService.getById(id));
    }

    @Test
    void testDeleteProductNotFound() {
        // When & Then - Should not throw exception for non-existent product
        assertDoesNotThrow(() -> productService.delete(999L));
    }
}
