package com.myroslav.cosmickitties.domain;

import java.math.BigDecimal;
import java.util.List;

public class Order {
    private Long id;
    private List<Long> productIds;
    private BigDecimal total;

    public Order() {}

    public Order(Long id, List<Long> productIds, BigDecimal total) {
        this.id = id;
        this.productIds = productIds;
        this.total = total;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<Long> getProductIds() { return productIds; }
    public void setProductIds(List<Long> productIds) { this.productIds = productIds; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}
