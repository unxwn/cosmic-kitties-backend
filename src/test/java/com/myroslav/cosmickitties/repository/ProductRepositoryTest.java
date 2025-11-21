//package com.myroslav.cosmickitties.repository;
//
//import org.springframework.boot.test.context.SpringBootTest;
//
//@SpringBootTest
//@Testcontainers
//public class ProductRepositoryTest {
//
//    @Container
//    public static PostgreSQLContainer<?> postgres =
//            new PostgreSQLContainer<>("postgres:16-alpine")
//                    .withDatabaseName("testdb")
//                    .withUsername("postgres")
//                    .withPassword("password");
//
//    @DynamicPropertySource
//    static void configure(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", postgres::getJdbcUrl);
//        registry.add("spring.datasource.username", postgres::getUsername);
//        registry.add("spring.datasource.password", postgres::getPassword);
//    }
//
//    @Autowired
//    private ProductRepository productRepository;
//
//    @Test
//    void testCreateProduct() {
//        Product p = new Product();
//        p.setName("Test");
//        p.setPrice(123);
//
//        productRepository.save(p);
//
//        assertEquals(1, productRepository.findAll().size());
//    }
//}
