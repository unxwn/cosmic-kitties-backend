package com.myroslav.cosmickitties.dto;

import com.myroslav.cosmickitties.validation.CosmicWordCheck;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductDTO {

    private Long id;

    @NotNull(message = "name must not be null")
    @Size(min = 3, max = 100, message = "name length must be between 3 and 100")
    @CosmicWordCheck
    private String name;

    @Size(max = 500, message = "description max length is 500")
    private String description;

    @NotNull(message = "price must not be null")
    @DecimalMin(value = "0.01", inclusive = true, message = "price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "categoryId must not be null")
    private Long categoryId;

    private boolean available = true;

    public ProductDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}
