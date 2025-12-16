package com.myroslav.cosmickitties.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCreateRequestDto {

    private Long customerId;

    @NotEmpty
    private List<@NotNull Long> productIds;
}


