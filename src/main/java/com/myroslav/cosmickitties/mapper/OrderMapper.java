package com.myroslav.cosmickitties.mapper;

import com.myroslav.cosmickitties.dto.OrderDto;
import com.myroslav.cosmickitties.entity.Order;
import com.myroslav.cosmickitties.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(target = "customerId",
            expression = "java(order.getCustomer() != null ? order.getCustomer().getId() : null)")
    @Mapping(target = "productIds",
            expression = "java(mapProductIds(order.getProducts() == null ? java.util.Set.of() : order.getProducts()))")
    OrderDto toOrderDto(Order order);

    List<OrderDto> toOrderDtoList(List<Order> orders);

    default List<Long> mapProductIds(java.util.Set<Product> products) {
        return products.stream()
                .map(Product::getId)
                .collect(Collectors.toList());
    }
}


