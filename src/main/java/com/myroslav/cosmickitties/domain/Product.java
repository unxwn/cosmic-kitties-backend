package com.myroslav.cosmickitties.domain;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean available;
    private Long categoryId;
}
