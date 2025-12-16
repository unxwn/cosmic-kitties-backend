package com.myroslav.cosmickitties.mapper;

import com.myroslav.cosmickitties.entity.Category;
import com.myroslav.cosmickitties.dto.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryDTO toCategoryDto(Category entity);

    Category toCategoryEntity(CategoryDTO dto);
}
