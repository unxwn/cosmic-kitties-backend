package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Customer;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerRepositoryTest {

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
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        // Trigger schema creation by creating a dummy entity
        Customer dummyCustomer = Customer.builder()
                .email("dummy@test.com")
                .firstName("Dummy")
                .lastName("User")
                .createdAt(java.time.LocalDateTime.now())
                .build();
        customerRepository.saveAndFlush(dummyCustomer);
        
        // Now we can safely delete all
        customerRepository.deleteAll();
    }

    @Test
    void testCreateCustomer() {
        // Given
        Customer customer = Customer.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        Customer saved = customerRepository.save(customer);

        // Then
        assertNotNull(saved.getId());
        assertEquals("john.doe@example.com", saved.getEmail());
        assertEquals("John", saved.getFirstName());
        assertEquals("Doe", saved.getLastName());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void testReadCustomer() {
        // Given
        Customer customer = Customer.builder()
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .createdAt(LocalDateTime.now())
                .build();
        Customer saved = customerRepository.save(customer);

        // When
        Optional<Customer> found = customerRepository.findById(saved.getId());

        // Thenі
        assertTrue(found.isPresent());
        assertEquals("jane.smith@example.com", found.get().getEmail());
        assertEquals("Jane", found.get().getFirstName());
        assertEquals("Smith", found.get().getLastName());
    }

    @Test
    void testUpdateCustomer() {
        // Given
        Customer customer = Customer.builder()
                .email("old.email@example.com")
                .firstName("Old")
                .lastName("Name")
                .createdAt(LocalDateTime.now())
                .build();
        Customer saved = customerRepository.save(customer);

        // When
        saved.setEmail("new.email@example.com");
        saved.setFirstName("New");
        saved.setLastName("Name");
        Customer updated = customerRepository.save(saved);

        // Then
        assertEquals("new.email@example.com", updated.getEmail());
        assertEquals("New", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
    }

    @Test
    void testDeleteCustomer() {
        // Given
        Customer customer = Customer.builder()
                .email("delete.me@example.com")
                .firstName("Delete")
                .lastName("Me")
                .createdAt(LocalDateTime.now())
                .build();
        Customer saved = customerRepository.save(customer);
        Long id = saved.getId();

        // When
        customerRepository.deleteById(id);

        // Then
        assertFalse(customerRepository.existsById(id));
        Optional<Customer> found = customerRepository.findById(id);
        assertTrue(found.isEmpty());
    }

    @Test
    void testFindAllCustomers() {
        // Given
        Customer customer1 = Customer.builder()
                .email("customer1@example.com")
                .firstName("First")
                .lastName("Customer")
                .createdAt(LocalDateTime.now())
                .build();
        Customer customer2 = Customer.builder()
                .email("customer2@example.com")
                .firstName("Second")
                .lastName("Customer")
                .createdAt(LocalDateTime.now())
                .build();
        customerRepository.save(customer1);
        customerRepository.save(customer2);

        // When
        List<Customer> all = customerRepository.findAll();

        // Then
        assertEquals(2, all.size());
    }

    @Test
    void testFindByEmail() {
        // Given
        Customer customer = Customer.builder()
                .email("unique@example.com")
                .firstName("Unique")
                .lastName("User")
                .createdAt(LocalDateTime.now())
                .build();
        customerRepository.save(customer);

        // When
        Optional<Customer> found = customerRepository.findByEmail("unique@example.com");

        // Then
        assertTrue(found.isPresent());
        assertEquals("unique@example.com", found.get().getEmail());
    }

    @Test
    void testExistsByEmail() {
        // Given
        Customer customer = Customer.builder()
                .email("exists@example.com")
                .firstName("Exists")
                .lastName("User")
                .createdAt(LocalDateTime.now())
                .build();
        customerRepository.save(customer);

        // When
        boolean exists = customerRepository.existsByEmail("exists@example.com");
        boolean notExists = customerRepository.existsByEmail("notexists@example.com");

        // Then
        assertTrue(exists);
        assertFalse(notExists);
    }

    @Test
    void testCustomerNotFound() {
        // When
        Optional<Customer> found = customerRepository.findById(999L);
        Optional<Customer> foundByEmail = customerRepository.findByEmail("nonexistent@example.com");

        // Then
        assertTrue(found.isEmpty());
        assertTrue(foundByEmail.isEmpty());
    }
}
