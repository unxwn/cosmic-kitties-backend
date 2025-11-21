package com.myroslav.cosmickitties.mapper;

import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.dto.CustomerDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    CustomerDTO toDto(Customer entity);
    Customer toEntity(CustomerDTO dto);
}
