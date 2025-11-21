package com.myroslav.cosmickitties.mapper;


import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.ProductDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "categoryId", source = "category.id")
    ProductDTO toDto(Product entity);

    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDTO dto);
}
