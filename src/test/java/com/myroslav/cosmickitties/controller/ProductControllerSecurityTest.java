package com.myroslav.cosmickitties.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Request without API key or token is unauthorized")
    void whenNoAuth_thenUnauthorized() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Request with valid API key is authorized")
    void whenValidApiKey_thenOk() throws Exception {
        mockMvc.perform(get("/api/products")
                        .header("X-API-KEY", "cosmic-kitties-secret"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Request with JWT bearer token is authorized")
    void whenJwtToken_thenOk() throws Exception {
        mockMvc.perform(get("/api/products").with(jwt()))
                .andExpect(status().isOk());
    }
}
