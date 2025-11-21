package com.myroslav.cosmickitties.mapper;

import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDto(Customer entity);
    
    @Mapping(target = "createdAt", ignore = true)
    Customer toEntity(CustomerDTO dto);
}
