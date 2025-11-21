package com.myroslav.cosmickitties.mapper;

import com.myroslav.cosmickitties.domain.Category;
import com.myroslav.cosmickitties.dto.CategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDTO toDto(Category entity);
    Category toEntity(CategoryDTO dto);
}
