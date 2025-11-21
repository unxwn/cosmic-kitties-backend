package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCustomerEmail(String email);
}
