package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.domain.Category;
import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.domain.Order;
import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.OrderDTO;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.repository.CategoryRepository;
import com.myroslav.cosmickitties.repository.CustomerRepository;
import com.myroslav.cosmickitties.repository.OrderRepository;
import com.myroslav.cosmickitties.repository.ProductRepository;
import com.myroslav.cosmickitties.service.abstraction.IOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
class OrderServiceIntegrationTest {

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
    private IOrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Customer testCustomer;
    private Product testProduct1;
    private Product testProduct2;
    private Category testCategory;

    @BeforeEach
    void setUp() {
        // Trigger schema creation by creating dummy entities in dependency order
        Category dummyCategory = Category.builder().name("dummy_cat").build();
        dummyCategory = categoryRepository.saveAndFlush(dummyCategory);
        
        Customer dummyCustomer = Customer.builder()
                .email("dummy@test.com")
                .firstName("Dummy")
                .lastName("User")
                .createdAt(LocalDateTime.now())
                .build();
        dummyCustomer = customerRepository.saveAndFlush(dummyCustomer);
        
        Product dummyProduct = Product.builder()
                .name("dummy_product")
                .price(new BigDecimal("1.00"))
                .available(true)
                .category(dummyCategory)
                .build();
        dummyProduct = productRepository.saveAndFlush(dummyProduct);
        
        // Delete all - skip orders table as it may not exist yet
        // Orders table will be created when first Order is saved in a test
        // Since we're using @Transactional, each test will rollback anyway
        productRepository.deleteAll();
        customerRepository.deleteAll();
        categoryRepository.deleteAll();

        testCategory = Category.builder()
                .name("Electronics")
                .build();
        testCategory = categoryRepository.save(testCategory);

        testCustomer = Customer.builder()
                .email("customer@example.com")
                .firstName("Test")
                .lastName("Customer")
                .createdAt(LocalDateTime.now())
                .build();
        testCustomer = customerRepository.save(testCustomer);

        testProduct1 = Product.builder()
                .name("Product 1")
                .price(new BigDecimal("10.00"))
                .available(true)
                .category(testCategory)
                .build();
        testProduct1 = productRepository.save(testProduct1);

        testProduct2 = Product.builder()
                .name("Product 2")
                .price(new BigDecimal("20.00"))
                .available(true)
                .category(testCategory)
                .build();
        testProduct2 = productRepository.save(testProduct2);
    }

    @Test
    void testCreateOrder() {
        // Given
        OrderDTO dto = OrderDTO.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId(), testProduct2.getId()))
                .build();

        // When
        OrderDTO created = orderService.create(dto);

        // Then
        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertEquals(testCustomer.getId(), created.getCustomerId());
        assertEquals(2, created.getProductIds().size());
        assertTrue(created.getProductIds().contains(testProduct1.getId()));
        assertTrue(created.getProductIds().contains(testProduct2.getId()));
    }

    @Test
    void testCreateOrderWithoutCustomer() {
        // Given
        OrderDTO dto = OrderDTO.builder()
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();

        // When
        OrderDTO created = orderService.create(dto);

        // Then
        assertNotNull(created.getId());
        assertNull(created.getCustomerId());
        assertEquals(1, created.getProductIds().size());
    }

    @Test
    void testCreateOrderWithNonExistentCustomer() {
        // Given
        OrderDTO dto = OrderDTO.builder()
                .customerId(999L)
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.create(dto));
    }

    @Test
    void testCreateOrderWithNonExistentProduct() {
        // Given
        OrderDTO dto = OrderDTO.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId(), 999L))
                .build();

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.create(dto));
    }

    @Test
    void testCreateOrderWithEmptyProductList() {
        // Given
        OrderDTO dto = OrderDTO.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList())
                .build();

        // When
        OrderDTO created = orderService.create(dto);

        // Then
        assertNotNull(created.getId());
        assertTrue(created.getProductIds().isEmpty());
    }

    @Test
    void testGetOrderById() {
        // Given
        OrderDTO dto = OrderDTO.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();
        OrderDTO created = orderService.create(dto);

        // When
        OrderDTO found = orderService.getById(created.getId());

        // Then
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(testCustomer.getId(), found.getCustomerId());
        assertEquals(1, found.getProductIds().size());
        assertTrue(found.getProductIds().contains(testProduct1.getId()));
    }

    @Test
    void testGetOrderByIdNotFound() {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(999L));
    }

    @Test
    void testGetAllOrders() {
        // Given
        OrderDTO dto1 = OrderDTO.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();
        OrderDTO dto2 = OrderDTO.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct2.getId()))
                .build();
        orderService.create(dto1);
        orderService.create(dto2);

        // When
        List<OrderDTO> all = orderService.getAll();

        // Then
        assertEquals(2, all.size());
    }
}
