package com.myroslav.cosmickitties.service.implementation;

import com.myroslav.cosmickitties.dto.ProductDTO;
import com.myroslav.cosmickitties.domain.Category;
import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.ProductMapper;
import com.myroslav.cosmickitties.repository.CategoryRepository;
import com.myroslav.cosmickitties.repository.ProductRepository;
import com.myroslav.cosmickitties.service.abstraction.IProductService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService implements IProductService {

    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final ProductMapper mapper;

    public ProductService(ProductRepository productRepo,
                          CategoryRepository categoryRepo,
                          ProductMapper mapper) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.mapper = mapper;
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        Category category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
        Product entity = mapper.toEntity(dto);
        entity.setCategory(category);
        Product saved = productRepo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public ProductDTO getById(Long id) {
        Product p = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        return mapper.toDto(p);
    }

    @Override
    public List<ProductDTO> getAll() {
        return productRepo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", id));
        // update fields
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setAvailable(dto.isAvailable());
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(existing.getCategory().getId())) {
            Category cat = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", dto.getCategoryId()));
            existing.setCategory(cat);
        }
        Product saved = productRepo.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (productRepo.existsById(id)) {
            productRepo.deleteById(id);
        }
    }
}
