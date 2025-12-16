package com.myroslav.cosmickitties.service.abstraction;

import com.myroslav.cosmickitties.dto.OrderCreateRequestDto;
import com.myroslav.cosmickitties.dto.OrderDto;

import java.util.List;

public interface IOrderService {

    OrderDto create(OrderCreateRequestDto request);

    OrderDto getById(Long id);

    List<OrderDto> getAll();

    OrderDto update(Long id, OrderCreateRequestDto request);

    void delete(Long id);
}
