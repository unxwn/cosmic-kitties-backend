package com.myroslav.cosmickitties.repository.abstraction;

import com.myroslav.cosmickitties.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> { }
