package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.ProductDTO;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.ProductMapper;
import com.myroslav.cosmickitties.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService implements IProductService {

    private final ProductRepository repo;
    private final ProductMapper mapper;

    public ProductService(ProductRepository repo, ProductMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public ProductDTO create(ProductDTO dto) {
        Product product = mapper.toDomain(dto);
        product.setId(null); // ensure new id
        Product saved = repo.save(product);
        return mapper.toDto(saved);
    }

    @Override
    public ProductDTO getById(Long id) {
        Product product = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        return mapper.toDto(product);
    }

    @Override
    public List<ProductDTO> getAll() {
        return repo.findAll().stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO update(Long id, ProductDTO dto) {
        Product existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setPrice(dto.getPrice());
        existing.setCategoryId(dto.getCategoryId());
        existing.setAvailable(dto.isAvailable());
        Product saved = repo.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
