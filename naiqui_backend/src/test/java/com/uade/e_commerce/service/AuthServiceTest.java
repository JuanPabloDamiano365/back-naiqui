package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.uade.e_commerce.dto.LoginRequestDTO;
import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;

class AuthServiceTest {

    @Test
    void loginConPasswordLegacyEnClaroDebeAceptarYRehash() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Usuario Demo");
        usuario.setEmail("demo@naiqui.com");
        usuario.setPassword("1234");
        usuario.setRol(RolUsuario.USUARIO);

        when(usuarioRepository.findByEmail("demo@naiqui.com")).thenReturn(Optional.of(usuario));

        AuthService authService = new AuthService(usuarioRepository, new JwtService());
        LoginRequestDTO request = new LoginRequestDTO("demo@naiqui.com", "1234");

        var response = authService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("demo@naiqui.com", response.getEmail());
        assertEquals(RolUsuario.USUARIO, response.getRol());
        assertTrue(new JwtService().esValido(response.getToken()));
        verify(usuarioRepository).save(any(Usuario.class));
        assertTrue(new BCryptPasswordEncoder().matches("1234", usuario.getPassword()));
    }
}
