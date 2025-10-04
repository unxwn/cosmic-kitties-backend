package com.myroslav.cosmickitties.domain;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Long id;
    private List<Long> productIds = new ArrayList<>();

    public Cart() {}
    public Cart(Long id) { this.id = id; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}
