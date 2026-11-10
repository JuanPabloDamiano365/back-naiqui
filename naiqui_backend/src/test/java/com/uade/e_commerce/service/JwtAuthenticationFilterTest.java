package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class JwtAuthenticationFilterTest {

    @AfterEach
    void limpiarContextoDeSeguridad() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void utilizaElRolActualDelUsuarioEnLaBaseParaLaSesionActiva() throws ServletException, IOException {
        JwtService jwtService = mock(JwtService.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        FilterChain filterChain = mock(FilterChain.class);
        Usuario usuario = new Usuario();
        usuario.setEmail("admin@naiqui.com");
        usuario.setRol(RolUsuario.VENDEDOR);
        when(jwtService.esValido("token-activo")).thenReturn(true);
        when(jwtService.obtenerEmail("token-activo")).thenReturn("admin@naiqui.com");
        when(usuarioRepository.findByEmail("admin@naiqui.com")).thenReturn(Optional.of(usuario));
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer token-activo");

        new JwtAuthenticationFilter(jwtService, usuarioRepository)
                .doFilter(request, response, filterChain);

        assertEquals(
                "ROLE_VENDEDOR",
                SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next()
                        .getAuthority());
        verify(filterChain).doFilter(request, response);
    }
}
