package com.myroslav.cosmickitties.service.implementation;


import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.domain.Order;
import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.OrderDTO;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.repository.CustomerRepository;
import com.myroslav.cosmickitties.repository.OrderRepository;
import com.myroslav.cosmickitties.repository.ProductRepository;
import com.myroslav.cosmickitties.service.abstraction.IOrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService implements IOrderService {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;

    public OrderService(OrderRepository orderRepo,
                        ProductRepository productRepo,
                        CustomerRepository customerRepo) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
    }

    @Override
    public OrderDTO create(OrderDTO dto) {
        List<Long> ids = dto.getProductIds() == null ? Collections.emptyList() : dto.getProductIds();

        List<Product> products = productRepo.findAllById(ids);
        if (products.size() != ids.size()) {
            // find which ids missing
            Set<Long> found = products.stream().map(Product::getId).collect(Collectors.toSet());
            List<Long> missing = ids.stream()
                    .filter(id -> !found.contains(id))
                    .collect(Collectors.toList());
            throw new ResourceNotFoundException("Products not found: " + missing);
        }

        Order order = new Order();
        order.setCreatedAt(LocalDateTime.now());
        order.setProducts(new HashSet<>(products));
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepo.findById(dto.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", dto.getCustomerId()));
            order.setCustomer(customer);
        }

        Order saved = orderRepo.save(order);

        return OrderDTO.builder()
                .id(saved.getId())
                .customerId(getCustomerId(saved))
                .productIds(saved.getProducts().stream().map(Product::getId).collect(Collectors.toList()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public OrderDTO getById(Long id) {
        Order saved = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return OrderDTO.builder()
                .id(saved.getId())
                .customerId(getCustomerId(saved))
                .productIds(saved.getProducts().stream().map(Product::getId).collect(Collectors.toList()))
                .createdAt(saved.getCreatedAt())
                .build();
    }

    @Override
    public List<OrderDTO> getAll() {
        return orderRepo.findAll().stream().map(o ->
                OrderDTO.builder()
                        .id(o.getId())
                        .customerId(getCustomerId(o))
                        .productIds(o.getProducts().stream().map(Product::getId).collect(Collectors.toList()))
                        .createdAt(o.getCreatedAt())
                        .build()
        ).collect(Collectors.toList());
    }

    private Long getCustomerId(Order order) {
        return order.getCustomer() != null ? order.getCustomer().getId() : null;
    }
}