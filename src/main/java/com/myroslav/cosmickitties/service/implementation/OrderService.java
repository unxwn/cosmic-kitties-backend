package com.myroslav.cosmickitties.service.implementation;

import com.myroslav.cosmickitties.dto.OrderCreateRequestDto;
import com.myroslav.cosmickitties.dto.OrderDto;
import com.myroslav.cosmickitties.entity.Customer;
import com.myroslav.cosmickitties.entity.Order;
import com.myroslav.cosmickitties.entity.Product;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.OrderMapper;
import com.myroslav.cosmickitties.repository.CustomerRepository;
import com.myroslav.cosmickitties.repository.OrderRepository;
import com.myroslav.cosmickitties.repository.ProductRepository;
import com.myroslav.cosmickitties.service.abstraction.IOrderService;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrderService implements IOrderService {

    private final CustomerRepository customerRepo;
    private final OrderRepository orderRepo;
    private final ProductRepository productRepo;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepo,
                        ProductRepository productRepo,
                        CustomerRepository customerRepo,
                        OrderMapper orderMapper) {
        this.customerRepo = customerRepo;
        this.orderRepo = orderRepo;
        this.productRepo = productRepo;
        this.orderMapper = orderMapper;
    }

    @Override
    @Transactional
    public OrderDto create(OrderCreateRequestDto request) {
        List<Long> ids = request.getProductIds() == null ? Collections.emptyList() : request.getProductIds();

        List<Product> products = productRepo.findAllById(ids);
        if (products.size() != ids.size()) {
            Set<Long> found = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            List<Long> missing = ids.stream()
                    .filter(id -> !found.contains(id))
                    .collect(Collectors.toList());
            throw new ResourceNotFoundException("Products not found: " + missing);
        }

        Order order = new Order();
        order.setProducts(new HashSet<>(products));
        if (request.getCustomerId() != null) {
            Customer customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
            order.setCustomer(customer);
        }

        Order saved = orderRepo.save(order);
        return orderMapper.toOrderDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto getById(Long id) {
        Order saved = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));
        return orderMapper.toOrderDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto> getAll() {
        return orderMapper.toOrderDtoList(orderRepo.findAll());
    }

    @Override
    @Transactional
    public OrderDto update(Long id, OrderCreateRequestDto request) {
        Order existing = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order", id));

        List<Long> ids = request.getProductIds() == null ? Collections.emptyList() : request.getProductIds();
        List<Product> products = productRepo.findAllById(ids);
        if (products.size() != ids.size()) {
            Set<Long> found = products.stream()
                    .map(Product::getId)
                    .collect(Collectors.toSet());
            List<Long> missing = ids.stream()
                    .filter(pid -> !found.contains(pid))
                    .collect(Collectors.toList());
            throw new ResourceNotFoundException("Products not found: " + missing);
        }

        existing.setProducts(new HashSet<>(products));
        if (request.getCustomerId() != null) {
            Customer customer = customerRepo.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", request.getCustomerId()));
            existing.setCustomer(customer);
        } else {
            existing.setCustomer(null);
        }

        Order saved = orderRepo.save(existing);
        return orderMapper.toOrderDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (orderRepo.existsById(id)) {
            orderRepo.deleteById(id);
        }
    }
}