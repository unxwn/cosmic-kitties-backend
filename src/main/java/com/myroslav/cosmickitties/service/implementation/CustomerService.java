package com.myroslav.cosmickitties.service.implementation;

import com.myroslav.cosmickitties.dto.CustomerDTO;
import com.myroslav.cosmickitties.entity.Customer;
import com.myroslav.cosmickitties.exception.EmailAlreadyExistsException;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.CustomerMapper;
import com.myroslav.cosmickitties.repository.CustomerRepository;
import com.myroslav.cosmickitties.service.abstraction.ICustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService implements ICustomerService {

    private final CustomerRepository repo;
    private final CustomerMapper mapper;

    public CustomerService(CustomerRepository repo, CustomerMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public CustomerDTO create(CustomerDTO dto) {
        if (dto.getId() != null) {
            throw new IllegalArgumentException("ID must not be provided when creating a new customer");
        }

        if (dto.getEmail() != null && repo.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        Customer entity = mapper.toCustomerEntity(dto);
        Customer saved = repo.save(entity);
        return mapper.toCustomerDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerDTO getById(Long id) {
        return repo.findById(id)
                .map(mapper::toCustomerDto)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAll() {
        return repo.findAll().stream()
                .map(mapper::toCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer existing = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));

        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            if (repo.existsByEmail(dto.getEmail())) {
                throw new EmailAlreadyExistsException(dto.getEmail());
            }
            existing.setEmail(dto.getEmail());
        }

        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        Customer saved = repo.save(existing);
        return mapper.toCustomerDto(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (repo.existsById(id)) {
            repo.deleteById(id);
        }
    }
}
