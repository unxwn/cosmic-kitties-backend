package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Product;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryProductRepository implements ProductRepository {
    private final Map<Long, Product> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @PostConstruct
    public void init() {
        save(new Product(null, "Almighty pen🖋️ - create your future", "A pen that was created in kitten R&D laboratories. The best invention in the human galaxy.", new BigDecimal("12.50"), 1L, true));
        save(new Product(null, "Cosmic Avocado🥑 - the ripest avocado ever", "A special avocado yielded in interstellar fields.", new BigDecimal("4.99"), 2L, true));
    }

    @Override
    public Product save(Product product) {
        if (product.getId() == null) {
            long id = idGenerator.incrementAndGet();
            product.setId(id);
        }
        store.put(product.getId(), product);
        return product;
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void deleteById(Long id) {
        store.remove(id);
    }

    @Override
    public void deleteAll() {
        store.clear();
    }
}
