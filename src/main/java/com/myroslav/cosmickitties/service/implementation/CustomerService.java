package com.myroslav.cosmickitties.service.implementation;

import com.myroslav.cosmickitties.domain.Customer;
import com.myroslav.cosmickitties.dto.CustomerDTO;
import com.myroslav.cosmickitties.exception.BadRequestException;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.CustomerMapper;
import com.myroslav.cosmickitties.repository.CustomerRepository;
import com.myroslav.cosmickitties.service.abstraction.ICustomerService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerService implements ICustomerService {

    private final CustomerRepository repo;
    private final CustomerMapper mapper;

    public CustomerService(CustomerRepository repo, CustomerMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }

    @Override
    public CustomerDTO create(CustomerDTO dto) {
        if (dto.getEmail() != null && repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        if (dto.getId() != null) {
            throw new BadRequestException("Cannot specify id when creating a new customer");
        }

        Customer entity = mapper.toEntity(dto);
        Customer saved = repo.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    public CustomerDTO getById(Long id) {
        return repo.findById(id)
                .map(mapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    @Override
    public List<CustomerDTO> getAll() {
        return repo.findAll().stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO update(Long id, CustomerDTO dto) {
        Customer existing = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Customer", id));
        if (dto.getEmail() != null && !dto.getEmail().equals(existing.getEmail())) {
            if (repo.existsByEmail(dto.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            existing.setEmail(dto.getEmail());
        }
        existing.setFirstName(dto.getFirstName());
        existing.setLastName(dto.getLastName());
        Customer saved = repo.save(existing);
        return mapper.toDto(saved);
    }

    @Override
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ResourceNotFoundException("Customer", id);
        repo.deleteById(id);
    }
}
