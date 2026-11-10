package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

class ProductoAuthorizationServiceTest {

    private ProductoRepository productoRepository;
    private UsuarioRepository usuarioRepository;
    private ProductoAuthorizationService authorizationService;

    @BeforeEach
    void setUp() {
        productoRepository = org.mockito.Mockito.mock(ProductoRepository.class);
        usuarioRepository = org.mockito.Mockito.mock(UsuarioRepository.class);
        authorizationService = new ProductoAuthorizationService(productoRepository, usuarioRepository);
    }

    @Test
    void vendedorPuedeGestionarSoloSuProducto() {
        Usuario creator = new Usuario();
        creator.setEmail("seller@example.com");
        Producto producto = new Producto();
        producto.setCreador(creator);
        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));

        assertTrue(authorizationService.puedeGestionar(7L, authentication("seller@example.com", "ROLE_VENDEDOR")));
        assertFalse(authorizationService.puedeGestionar(7L, authentication("other@example.com", "ROLE_VENDEDOR")));
    }

    @Test
    void adminPuedeGestionarCualquierProducto() {
        Producto producto = new Producto();
        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));

        assertTrue(authorizationService.puedeGestionar(7L, authentication("admin@example.com", "ROLE_ADMIN")));
    }

    @Test
    void productoLegacySeAtribuyeAlAdminAntesDeAutorizar() {
        Usuario admin = new Usuario();
        admin.setEmail("admin@example.com");
        Producto producto = new Producto();
        when(productoRepository.findById(7L)).thenReturn(Optional.of(producto));
        when(usuarioRepository.findFirstByRolOrderByIdAsc(RolUsuario.ADMIN)).thenReturn(Optional.of(admin));

        assertFalse(authorizationService.puedeGestionar(7L, authentication("seller@example.com", "ROLE_VENDEDOR")));

        assertTrue(producto.getCreador() == admin);
        verify(productoRepository).save(producto);
    }

    private UsernamePasswordAuthenticationToken authentication(String email, String role) {
        return new UsernamePasswordAuthenticationToken(
                email,
                null,
                List.of(new SimpleGrantedAuthority(role)));
    }
}
