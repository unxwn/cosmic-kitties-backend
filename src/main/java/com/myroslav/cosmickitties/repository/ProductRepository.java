package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(Long id);
    List<Product> findAll();
    void deleteById(Long id);
    void deleteAll();
}
