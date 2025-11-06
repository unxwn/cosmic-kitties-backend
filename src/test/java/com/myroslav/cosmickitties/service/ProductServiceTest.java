package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.ProductDTO;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.ProductMapper;
import com.myroslav.cosmickitties.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Use Mockito for repo + mapper, test happy and error flows.
 */
class ProductServiceTest {

    private ProductRepository repo;
    private ProductMapper mapper;
    private ProductService service;

    @BeforeEach
    void setUp() {
        repo = mock(ProductRepository.class);
        mapper = mock(ProductMapper.class);
        service = new ProductService(repo, mapper);
    }

    private Product makeDomain(Long id, String name, BigDecimal price) {
        return new Product(id, name, "desc", price, 1L, true);
    }

    private ProductDTO makeDto(Long id, String name, BigDecimal price) {
        ProductDTO d = new ProductDTO();
        d.setId(id);
        d.setName(name);
        d.setPrice(price);
        d.setCategoryId(1L);
        d.setDescription("desc");
        d.setAvailable(true);
        return d;
    }

    @Test
    void create_shouldReturnDtoWithId() {
        ProductDTO input = makeDto(null, "star yarn", new BigDecimal("9.99"));
        Product domain = makeDomain(null, input.getName(), input.getPrice());
        Product saved = makeDomain(1L, input.getName(), input.getPrice());
        ProductDTO outDto = makeDto(1L, input.getName(), input.getPrice());

        when(mapper.toDomain(input)).thenReturn(domain);
        when(repo.save(domain)).thenReturn(saved);
        when(mapper.toDto(saved)).thenReturn(outDto);

        ProductDTO result = service.create(input);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(repo).save(domain);
        verify(mapper).toDto(saved);
    }

    @Test
    void getById_existing_shouldReturnDto() {
        Product domain = makeDomain(2L, "galaxy milk", new BigDecimal("3.50"));
        ProductDTO dto = makeDto(2L, domain.getName(), domain.getPrice());

        when(repo.findById(2L)).thenReturn(Optional.of(domain));
        when(mapper.toDto(domain)).thenReturn(dto);

        ProductDTO result = service.getById(2L);

        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getName()).isEqualTo("galaxy milk");
    }

    @Test
    void getById_missing_shouldThrow() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAll_shouldReturnList() {
        Product p1 = makeDomain(1L, "star 1", new BigDecimal("1.00"));
        Product p2 = makeDomain(2L, "star 2", new BigDecimal("2.00"));
        when(repo.findAll()).thenReturn(List.of(p1, p2));
        when(mapper.toDto(p1)).thenReturn(makeDto(1L, p1.getName(), p1.getPrice()));
        when(mapper.toDto(p2)).thenReturn(makeDto(2L, p2.getName(), p2.getPrice()));

        var list = service.getAll();

        assertThat(list).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    void update_existing_shouldReturnUpdated() {
        Product existing = makeDomain(3L, "old star", new BigDecimal("5.00"));
        ProductDTO updateDto = makeDto(null, "new star", new BigDecimal("6.00"));

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDto(any(Product.class))).thenReturn(makeDto(3L, "new star", new BigDecimal("6.00")));

        ProductDTO result = service.update(3L, updateDto);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("new star");

        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(repo).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("new star");
    }

    @Test
    void update_missing_shouldThrow() {
        when(repo.findById(10L)).thenReturn(Optional.empty());
        ProductDTO dto = makeDto(null, "star", new BigDecimal("1.00"));

        assertThatThrownBy(() -> service.update(10L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existing_shouldCallRepoDelete() {
        Product existing = makeDomain(4L, "star", new BigDecimal("2.00"));
        when(repo.findById(4L)).thenReturn(Optional.of(existing));
        doNothing().when(repo).deleteById(4L);

        service.delete(4L);

        verify(repo).deleteById(4L);
    }
}
