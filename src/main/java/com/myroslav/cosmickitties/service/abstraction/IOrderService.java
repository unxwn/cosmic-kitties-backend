package com.myroslav.cosmickitties.service.abstraction;

import com.myroslav.cosmickitties.dto.OrderDTO;

import java.util.List;

public interface IOrderService {
    OrderDTO create(OrderDTO dto);
    OrderDTO getById(Long id);
    List<OrderDTO> getAll();
}
