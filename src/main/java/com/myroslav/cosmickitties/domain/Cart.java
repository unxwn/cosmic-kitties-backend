package com.myroslav.cosmickitties.domain;

import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
public class Cart {
    Long id;
    List<Long> productIds;

    public Cart() {}
    public Cart(Long id) { this.id = id; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }
}
