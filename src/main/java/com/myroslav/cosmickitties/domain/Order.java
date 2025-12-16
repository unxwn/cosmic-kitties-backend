package com.myroslav.cosmickitties.domain;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private Long id;
    private LocalDateTime createdAt;
    private Set<Long> productIds;
    private Long customerId;
}
