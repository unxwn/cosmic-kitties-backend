package com.myroslav.cosmickitties.service.abstraction;

import com.myroslav.cosmickitties.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO create(CustomerDTO dto);
    CustomerDTO getById(Long id);
    List<CustomerDTO> getAll();
    CustomerDTO update(Long id, CustomerDTO dto);
    void delete(Long id);
}
