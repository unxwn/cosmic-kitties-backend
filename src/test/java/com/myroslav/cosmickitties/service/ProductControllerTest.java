package com.myroslav.cosmickitties.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myroslav.cosmickitties.controller.ProductController;
import com.myroslav.cosmickitties.dto.ProductDTO;
import com.myroslav.cosmickitties.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static com.myroslav.cosmickitties.ProductFactory.java.ProductFactory.productDto;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller level tests using MockMvc and global exception handler.
 * Tests positive and negative validation flows.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void list_shouldReturn200() throws Exception {
        when(service.getAll()).thenReturn(List.of(productDto(1L, "star", new BigDecimal("1.0"))));

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));

        verify(service).getAll();
    }

    @Test
    void get_existing_shouldReturn200() throws Exception {
        when(service.getById(1L)).thenReturn(productDto(1L, "star", new BigDecimal("1.0")));

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void get_missing_shouldReturn404() throws Exception {
        when(service.getById(99L)).thenThrow(new ResourceNotFoundException("Product 99 not found"));

        mockMvc.perform(get("/api/v1/products/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("99")));
    }

    @Test
    void create_valid_shouldReturnCreated() throws Exception {
        ProductDTO input = productDto(null, "star yarn", new BigDecimal("5.0"));
        ProductDTO out = productDto(2L, "star yarn", new BigDecimal("5.0"));

        when(service.create(any(ProductDTO.class))).thenReturn(out);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    void create_invalid_shouldReturn400() throws Exception {
        // invalid because missing price and non-cosmic name -> fails @NotNull and @CosmicWordCheck
        String payload = "{\"name\":\"Ordinary Yarn\",\"categoryId\":1}";

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void delete_existing_shouldReturnNoContent() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());

        verify(service).delete(1L);
    }
}
