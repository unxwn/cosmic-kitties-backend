package com.myroslav.cosmickitties.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ApiKeyAuthenticationFilterTest {

    private final ApiKeyAuthenticationFilter filter = new ApiKeyAuthenticationFilter("expected-key");

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Missing API key results in 401")
    void whenMissingApiKey_thenUnauthorized() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains("Invalid or missing API key");
        verify(chain, never()).doFilter(Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("Invalid API key results in 401")
    void whenInvalidApiKey_thenUnauthorized() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ApiKeyAuthenticationFilter.API_KEY_HEADER, "wrong-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_UNAUTHORIZED);
        assertThat(response.getContentAsString()).contains("Invalid or missing API key");
        verify(chain, never()).doFilter(Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("Valid API key continues filter chain")
    void whenValidApiKey_thenChainContinues() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader(ApiKeyAuthenticationFilter.API_KEY_HEADER, "expected-key");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.isCommitted()).isFalse();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().isAuthenticated()).isTrue();
        verify(chain).doFilter(Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
    }

    @Test
    @DisplayName("Bearer token skips API key validation")
    void whenBearerToken_thenSkipsApiKey() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer some-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        // Filter should pass through without checking API key
        assertThat(response.isCommitted()).isFalse();
        verify(chain).doFilter(Mockito.any(HttpServletRequest.class), Mockito.any(HttpServletResponse.class));
    }
}
