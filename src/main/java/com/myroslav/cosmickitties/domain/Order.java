package com.myroslav.cosmickitties.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_order",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"customer_email"})})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CustomerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="customer_email", nullable = false)
    private String customerEmail;

    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
