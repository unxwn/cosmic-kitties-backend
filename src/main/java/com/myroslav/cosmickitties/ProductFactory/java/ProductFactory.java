package com.myroslav.cosmickitties.ProductFactory.java;

import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.ProductDTO;

import java.math.BigDecimal;

public class ProductFactory {

    public static ProductDTO productDto(Long id, String name, BigDecimal price) {
        ProductDTO d = new ProductDTO();
        d.setId(id);
        d.setName(name);
        d.setPrice(price);
        d.setCategoryId(1L);
        d.setAvailable(true);
        d.setDescription("desc");
        return d;
    }

    public static Product productDomain(Long id, String name, BigDecimal price) {
        return new Product(id, name, "desc", price, 1L, true);
    }
}
