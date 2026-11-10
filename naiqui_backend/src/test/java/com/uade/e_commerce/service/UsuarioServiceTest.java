package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.uade.e_commerce.dto.UsuarioDTO;
import com.uade.e_commerce.dto.UsuarioRegistroDTO;
import com.uade.e_commerce.exception.BusinessException;
import com.uade.e_commerce.model.Carrito;
import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.CarritoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

class UsuarioServiceTest {

    @Test
    void registrarUsuarioDebeRespetarElRolVendedorSolicitado() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        when(usuarioRepository.existsByEmail("vendedor@naiqui.com")).thenReturn(false);
        when(usuarioRepository.existsByUsername("vendedor")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);
        UsuarioRegistroDTO request = new UsuarioRegistroDTO();
        request.setUsername("vendedor");
        request.setNombre("Nuevo");
        request.setApellido("Vendedor");
        request.setEmail("vendedor@naiqui.com");
        request.setPassword("clave-segura-123");
        request.setRol(RolUsuario.VENDEDOR);

        UsuarioDTO response = usuarioService.registrarUsuario(request);

        assertEquals(RolUsuario.VENDEDOR, response.getRol());
        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        assertEquals(RolUsuario.VENDEDOR, usuarioCaptor.getValue().getRol());
    }

    @Test
    void registrarUsuarioDebeRechazarRolAdministrador() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);
        UsuarioRegistroDTO request = new UsuarioRegistroDTO();
        request.setUsername("admin-falso");
        request.setNombre("Admin");
        request.setApellido("Falso");
        request.setEmail("admin-falso@naiqui.com");
        request.setPassword("clave-segura-123");
        request.setRol(RolUsuario.ADMIN);

        assertThrows(BusinessException.class, () -> usuarioService.registrarUsuario(request));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void registrarUsuarioDebeGuardarCuentaSeguraYCrearCarrito() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        when(usuarioRepository.existsByEmail("nuevo@naiqui.com")).thenReturn(false);
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuario = invocation.getArgument(0);
            usuario.setId(12L);
            return usuario;
        });

        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);
        UsuarioDTO request = new UsuarioDTO(
                null, "Nuevo Usuario", "nuevo@naiqui.com", "clave-segura-123",
                RolUsuario.ADMIN, null, null);

        UsuarioDTO response = usuarioService.registrarUsuario(request);

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(usuarioCaptor.capture());
        Usuario usuarioGuardado = usuarioCaptor.getValue();
        assertEquals(RolUsuario.USUARIO, usuarioGuardado.getRol());
        assertTrue(new BCryptPasswordEncoder().matches("clave-segura-123", usuarioGuardado.getPassword()));
        assertEquals(RolUsuario.USUARIO, response.getRol());
        assertEquals("nuevo@naiqui.com", response.getEmail());
        assertNull(response.getPassword());

        ArgumentCaptor<Carrito> carritoCaptor = ArgumentCaptor.forClass(Carrito.class);
        verify(carritoRepository).save(carritoCaptor.capture());
        assertEquals(usuarioGuardado, carritoCaptor.getValue().getUsuario());
        assertNotNull(carritoCaptor.getValue().getFechaCreacion());
    }

    @Test
    void registrarUsuarioDebeRechazarEmailDuplicado() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        when(usuarioRepository.existsByEmail("existente@naiqui.com")).thenReturn(true);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);
        UsuarioDTO request = new UsuarioDTO(
                null, "Usuario", "existente@naiqui.com", "clave-segura-123",
                null, null, null);

        assertThrows(BusinessException.class, () -> usuarioService.registrarUsuario(request));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void registrarUsuarioDebeRechazarPasswordCorta() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        when(usuarioRepository.existsByEmail("nuevo@naiqui.com")).thenReturn(false);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);
        UsuarioDTO request = new UsuarioDTO(
                null, "Usuario", "nuevo@naiqui.com", "corta",
                null, null, null);

        assertThrows(BusinessException.class, () -> usuarioService.registrarUsuario(request));

        verify(usuarioRepository, never()).save(any(Usuario.class));
        verify(carritoRepository, never()).save(any(Carrito.class));
    }

    @Test
    void cambiarRolDebeGuardarYDevolverElRolActualizado() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        Usuario usuario = new Usuario();
        usuario.setId(21L);
        usuario.setNombre("Vendedora");
        usuario.setEmail("vendedora@naiqui.com");
        usuario.setRol(RolUsuario.USUARIO);
        when(usuarioRepository.findById(21L)).thenReturn(java.util.Optional.of(usuario));
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);

        UsuarioDTO response = usuarioService.cambiarRol(21L, RolUsuario.VENDEDOR);

        assertEquals(RolUsuario.VENDEDOR, usuario.getRol());
        assertEquals(RolUsuario.VENDEDOR, response.getRol());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarRolDebeRechazarUnRolNulo() {
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        CarritoRepository carritoRepository = mock(CarritoRepository.class);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository, carritoRepository);

        assertThrows(BusinessException.class, () -> usuarioService.cambiarRol(21L, null));

        verify(usuarioRepository, never()).findById(21L);
    }
}
