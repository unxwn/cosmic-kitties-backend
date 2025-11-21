package com.myroslav.cosmickitties.controller;

import com.myroslav.cosmickitties.dto.CustomerDTO;
import com.myroslav.cosmickitties.service.implementation.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService svc;

    public CustomerController(CustomerService svc) { this.svc = svc; }

    @GetMapping
    public List<CustomerDTO> list() { return svc.getAll(); }

    @GetMapping("/{id}")
    public CustomerDTO get(@PathVariable Long id) { return svc.getById(id); }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO create(@RequestBody CustomerDTO dto) { return svc.create(dto); }

    @PutMapping("/{id}")
    public CustomerDTO update(@PathVariable Long id, @RequestBody CustomerDTO dto) { return svc.update(id, dto); }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { svc.delete(id); }
}
