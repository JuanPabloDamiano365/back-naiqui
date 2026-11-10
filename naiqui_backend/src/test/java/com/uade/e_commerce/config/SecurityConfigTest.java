package com.uade.e_commerce.config;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.cors.CorsConfiguration;

import com.uade.e_commerce.service.JwtAuthenticationFilter;

class SecurityConfigTest {

    @Test
    void corsPermitePatchDesdeElFrontend() {
        SecurityConfig securityConfig = new SecurityConfig(mock(JwtAuthenticationFilter.class));
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/usuarios/1/rol");

        CorsConfiguration cors = securityConfig.corsConfigurationSource().getCorsConfiguration(request);

        assertTrue(cors.getAllowedMethods().contains("PATCH"));
    }
}
