package com.myroslav.cosmickitties.service.abstraction;

import com.myroslav.cosmickitties.dto.ProductDTO;

import java.util.List;

public interface IProductService {
    ProductDTO create(ProductDTO dto);
    ProductDTO getById(Long id);
    List<ProductDTO> getAll();
    ProductDTO update(Long id, ProductDTO dto);
    void delete(Long id);
}
