package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.dto.OrderCreateRequestDto;
import com.myroslav.cosmickitties.dto.OrderDto;
import com.myroslav.cosmickitties.entity.Category;
import com.myroslav.cosmickitties.entity.Customer;
import com.myroslav.cosmickitties.entity.Product;
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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

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
        orderRepository.deleteAll();
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
        OrderCreateRequestDto request = OrderCreateRequestDto.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId(), testProduct2.getId()))
                .build();

        OrderDto created = orderService.create(request);

        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());
        assertEquals(testCustomer.getId(), created.getCustomerId());
        assertEquals(2, created.getProductIds().size());
        assertTrue(created.getProductIds().contains(testProduct1.getId()));
        assertTrue(created.getProductIds().contains(testProduct2.getId()));
    }

    @Test
    void testCreateOrderWithoutCustomer() {
        OrderCreateRequestDto request = OrderCreateRequestDto.builder()
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();

        OrderDto created = orderService.create(request);

        assertNotNull(created.getId());
        assertNull(created.getCustomerId());
        assertEquals(1, created.getProductIds().size());
    }

    @Test
    void testCreateOrderWithNonExistentCustomer() {
        OrderCreateRequestDto request = OrderCreateRequestDto.builder()
                .customerId(999L)
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> orderService.create(request));
    }

    @Test
    void testCreateOrderWithNonExistentProduct() {
        OrderCreateRequestDto request = OrderCreateRequestDto.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId(), 999L))
                .build();

        assertThrows(ResourceNotFoundException.class, () -> orderService.create(request));
    }

    @Test
    void testCreateOrderWithEmptyProductList() {
        OrderCreateRequestDto request = OrderCreateRequestDto.builder()
                .customerId(testCustomer.getId())
                .productIds(List.of())
                .build();

        OrderDto created = orderService.create(request);

        assertNotNull(created.getId());
        assertTrue(created.getProductIds().isEmpty());
    }

    @Test
    void testGetOrderById() {
        OrderCreateRequestDto request = OrderCreateRequestDto.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();
        OrderDto created = orderService.create(request);

        OrderDto found = orderService.getById(created.getId());

        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals(testCustomer.getId(), found.getCustomerId());
        assertEquals(1, found.getProductIds().size());
        assertTrue(found.getProductIds().contains(testProduct1.getId()));
    }

    @Test
    void testGetOrderByIdNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> orderService.getById(999L));
    }

    @Test
    void testGetAllOrders() {
        OrderCreateRequestDto request1 = OrderCreateRequestDto.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct1.getId()))
                .build();
        OrderCreateRequestDto request2 = OrderCreateRequestDto.builder()
                .customerId(testCustomer.getId())
                .productIds(Arrays.asList(testProduct2.getId()))
                .build();
        orderService.create(request1);
        orderService.create(request2);

        List<OrderDto> all = orderService.getAll();

        assertEquals(2, all.size());
    }
}
