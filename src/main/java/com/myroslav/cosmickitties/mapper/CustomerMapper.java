package com.myroslav.cosmickitties.mapper;

import com.myroslav.cosmickitties.entity.Customer;
import com.myroslav.cosmickitties.dto.CustomerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    CustomerDTO toCustomerDto(Customer entity);

    @Mapping(target = "createdAt", ignore = true)
    Customer toCustomerEntity(CustomerDTO dto);
}
