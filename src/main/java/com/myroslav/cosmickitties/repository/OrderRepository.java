package com.myroslav.cosmickitties.repository;

import com.myroslav.cosmickitties.domain.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Long> {
    Optional<CustomerOrder> findByCustomerEmail(String email);
}
