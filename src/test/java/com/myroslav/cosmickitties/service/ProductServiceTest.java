package com.myroslav.cosmickitties.service;

import com.myroslav.cosmickitties.domain.Product;
import com.myroslav.cosmickitties.dto.ProductDTO;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import com.myroslav.cosmickitties.mapper.ProductMapper;
import com.myroslav.cosmickitties.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static com.myroslav.cosmickitties.ProductFactory.java.ProductFactory.productDomain;
import static com.myroslav.cosmickitties.ProductFactory.java.ProductFactory.productDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProductService.
 * Use Mockito for repo + mapper, test happy and error flows.
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repo;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService service;

    @Test
    void create_shouldReturnDtoWithId() {
        ProductDTO input = productDto(null, "star yarn", new BigDecimal("9.99"));
        Product domain = productDomain(null, input.getName(), input.getPrice());
        Product saved = productDomain(1L, input.getName(), input.getPrice());
        ProductDTO outDto = productDto(1L, input.getName(), input.getPrice());

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
        Product domain = productDomain(2L, "galaxy milk", new BigDecimal("3.50"));
        ProductDTO dto = productDto(2L, domain.getName(), domain.getPrice());

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
        Product p1 = productDomain(1L, "star 1", new BigDecimal("1.00"));
        Product p2 = productDomain(2L, "star 2", new BigDecimal("2.00"));
        when(repo.findAll()).thenReturn(List.of(p1, p2));
        when(mapper.toDto(p1)).thenReturn(productDto(1L, p1.getName(), p1.getPrice()));
        when(mapper.toDto(p2)).thenReturn(productDto(2L, p2.getName(), p2.getPrice()));

        var list = service.getAll();

        assertThat(list).hasSize(2);
        verify(repo).findAll();
    }

    @Test
    void update_existing_shouldReturnUpdated() {
        Product existing = productDomain(3L, "old star", new BigDecimal("5.00"));
        ProductDTO updateDto = productDto(null, "new star", new BigDecimal("6.00"));

        when(repo.findById(3L)).thenReturn(Optional.of(existing));
        when(repo.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));
        when(mapper.toDto(any(Product.class))).thenReturn(productDto(3L, "new star", new BigDecimal("6.00")));

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
        ProductDTO dto = productDto(null, "star", new BigDecimal("1.00"));

        assertThatThrownBy(() -> service.update(10L, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_existing_shouldCallRepoDelete() {
        Product existing = productDomain(4L, "star", new BigDecimal("2.00"));
        when(repo.findById(4L)).thenReturn(Optional.of(existing));
        doNothing().when(repo).deleteById(4L);

        service.delete(4L);

        verify(repo).deleteById(4L);
    }
}
