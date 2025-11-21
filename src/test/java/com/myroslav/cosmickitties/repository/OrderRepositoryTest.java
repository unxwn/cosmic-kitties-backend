package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Category;
import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.domain.Order;
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
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

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
        // Since @DataJpaTest uses transactions, each test will rollback anyway
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
        Set<Product> products = new HashSet<>();
        products.add(testProduct1);
        products.add(testProduct2);

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products)
                .build();

        // When
        Order saved = orderRepository.save(order);

        // Then
        assertNotNull(saved.getId());
        assertNotNull(saved.getCreatedAt());
        assertEquals(testCustomer.getId(), saved.getCustomer().getId());
        assertEquals(2, saved.getProducts().size());
    }

    @Test
    void testReadOrder() {
        // Given
        Set<Product> products = new HashSet<>();
        products.add(testProduct1);

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products)
                .build();
        Order saved = orderRepository.save(order);

        // When
        Optional<Order> found = orderRepository.findById(saved.getId());

        // Then
        assertTrue(found.isPresent());
        assertEquals(testCustomer.getId(), found.get().getCustomer().getId());
        assertEquals(1, found.get().getProducts().size());
    }

    @Test
    void testUpdateOrder() {
        // Given
        Set<Product> products = new HashSet<>();
        products.add(testProduct1);

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products)
                .build();
        Order saved = orderRepository.save(order);

        // When
        Set<Product> newProducts = new HashSet<>();
        newProducts.add(testProduct2);
        saved.setProducts(newProducts);
        Order updated = orderRepository.save(saved);

        // Then
        assertEquals(1, updated.getProducts().size());
        assertTrue(updated.getProducts().contains(testProduct2));
    }

    @Test
    void testDeleteOrder() {
        // Given
        Set<Product> products = new HashSet<>();
        products.add(testProduct1);

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products)
                .build();
        Order saved = orderRepository.save(order);
        Long id = saved.getId();

        // When
        orderRepository.deleteById(id);

        // Then
        assertFalse(orderRepository.existsById(id));
        Optional<Order> found = orderRepository.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAllOrders() {
        // Given
        Set<Product> products1 = new HashSet<>();
        products1.add(testProduct1);

        Set<Product> products2 = new HashSet<>();
        products2.add(testProduct2);

        Order order1 = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products1)
                .build();

        Order order2 = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products2)
                .build();

        orderRepository.save(order1);
        orderRepository.save(order2);

        // When
        List<Order> all = orderRepository.findAll();

        // Then
        assertEquals(2, all.size());
    }

    @Test
    void testFindByCustomerEmail() {
        // Given
        Set<Product> products = new HashSet<>();
        products.add(testProduct1);

        Order order = Order.builder()
                .createdAt(LocalDateTime.now())
                .customer(testCustomer)
                .products(products)
                .build();
        orderRepository.save(order);

        // When
        Optional<Order> found = orderRepository.findByCustomerEmail(testCustomer.getEmail());

        // Then
        assertTrue(found.isPresent());
        assertEquals(testCustomer.getEmail(), found.get().getCustomer().getEmail());
    }

    @Test
    void testOrderNotFound() {
        // When
        Optional<Order> found = orderRepository.findById(999L);
        Optional<Order> foundByEmail = orderRepository.findByCustomerEmail("nonexistent@example.com");

        // Then
        assertTrue(found.isEmpty());
        assertTrue(foundByEmail.isEmpty());
    }
}
