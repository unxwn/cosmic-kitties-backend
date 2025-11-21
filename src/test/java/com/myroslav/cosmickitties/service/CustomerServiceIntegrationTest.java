package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.dto.CustomerDTO;
import com.myroslav.cosmickitties.exception.BadRequestException;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.repository.CustomerRepository;
import com.myroslav.cosmickitties.service.abstraction.ICustomerService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@Transactional
class CustomerServiceIntegrationTest {

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
    private ICustomerService customerService;

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
        CustomerDTO dto = CustomerDTO.builder()
                .email("john.doe@example.com")
                .firstName("John")
                .lastName("Doe")
                .build();

        // When
        CustomerDTO created = customerService.create(dto);

        // Then
        assertNotNull(created.getId());
        assertEquals("john.doe@example.com", created.getEmail());
        assertEquals("John", created.getFirstName());
        assertEquals("Doe", created.getLastName());
        assertNotNull(created.getCreatedAt());
    }

    @Test
    void testCreateCustomerWithExistingEmail() {
        // Given
        CustomerDTO dto1 = CustomerDTO.builder()
                .email("existing@example.com")
                .firstName("First")
                .lastName("Customer")
                .build();
        customerService.create(dto1);

        CustomerDTO dto2 = CustomerDTO.builder()
                .email("existing@example.com")
                .firstName("Second")
                .lastName("Customer")
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> customerService.create(dto2));
    }

    @Test
    void testCreateCustomerWithIdShouldFail() {
        // Given
        CustomerDTO dto = CustomerDTO.builder()
                .id(1L)
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        // When & Then
        assertThrows(BadRequestException.class, () -> customerService.create(dto));
    }

    @Test
    void testGetCustomerById() {
        // Given
        CustomerDTO dto = CustomerDTO.builder()
                .email("jane.smith@example.com")
                .firstName("Jane")
                .lastName("Smith")
                .build();
        CustomerDTO created = customerService.create(dto);

        // When
        CustomerDTO found = customerService.getById(created.getId());

        // Then
        assertNotNull(found);
        assertEquals("jane.smith@example.com", found.getEmail());
        assertEquals("Jane", found.getFirstName());
        assertEquals("Smith", found.getLastName());
    }

    @Test
    void testGetCustomerByIdNotFound() {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> customerService.getById(999L));
    }

    @Test
    void testGetAllCustomers() {
        // Given
        CustomerDTO dto1 = CustomerDTO.builder()
                .email("customer1@example.com")
                .firstName("First")
                .lastName("Customer")
                .build();
        CustomerDTO dto2 = CustomerDTO.builder()
                .email("customer2@example.com")
                .firstName("Second")
                .lastName("Customer")
                .build();
        customerService.create(dto1);
        customerService.create(dto2);

        // When
        List<CustomerDTO> all = customerService.getAll();

        // Then
        assertEquals(2, all.size());
    }

    @Test
    void testUpdateCustomer() {
        // Given
        CustomerDTO dto = CustomerDTO.builder()
                .email("old.email@example.com")
                .firstName("Old")
                .lastName("Name")
                .build();
        CustomerDTO created = customerService.create(dto);

        // When
        CustomerDTO updateDto = CustomerDTO.builder()
                .email("new.email@example.com")
                .firstName("New")
                .lastName("Name")
                .build();
        CustomerDTO updated = customerService.update(created.getId(), updateDto);

        // Then
        assertEquals("new.email@example.com", updated.getEmail());
        assertEquals("New", updated.getFirstName());
        assertEquals("Name", updated.getLastName());
    }

    @Test
    void testUpdateCustomerWithExistingEmail() {
        // Given
        CustomerDTO dto1 = CustomerDTO.builder()
                .email("existing@example.com")
                .firstName("First")
                .lastName("Customer")
                .build();
        CustomerDTO created1 = customerService.create(dto1);

        CustomerDTO dto2 = CustomerDTO.builder()
                .email("other@example.com")
                .firstName("Second")
                .lastName("Customer")
                .build();
        CustomerDTO created2 = customerService.create(dto2);

        // When
        CustomerDTO updateDto = CustomerDTO.builder()
                .email("existing@example.com")
                .firstName("Second")
                .lastName("Customer")
                .build();

        // Then
        assertThrows(IllegalArgumentException.class, () -> customerService.update(created2.getId(), updateDto));
    }

    @Test
    void testUpdateCustomerNotFound() {
        // Given
        CustomerDTO updateDto = CustomerDTO.builder()
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> customerService.update(999L, updateDto));
    }

    @Test
    void testDeleteCustomer() {
        // Given
        CustomerDTO dto = CustomerDTO.builder()
                .email("delete.me@example.com")
                .firstName("Delete")
                .lastName("Me")
                .build();
        CustomerDTO created = customerService.create(dto);
        Long id = created.getId();

        // When
        customerService.delete(id);

        // Then
        assertThrows(ResourceNotFoundException.class, () -> customerService.getById(id));
    }

    @Test
    void testDeleteCustomerNotFound() {
        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> customerService.delete(999L));
    }
}
