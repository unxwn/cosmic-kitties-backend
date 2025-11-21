package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameAndCategoryId(String name, Long categoryId);
    List<Product> findAllByCategoryId(Long categoryId);
}
