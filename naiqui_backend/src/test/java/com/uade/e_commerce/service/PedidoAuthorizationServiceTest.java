package com.uade.e_commerce.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.uade.e_commerce.model.Pedido;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

class PedidoAuthorizationServiceTest {

    @Test
    void vendedorYAdministradorPuedenVerCualquierPedido() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        PedidoAuthorizationService service = new PedidoAuthorizationService(pedidoRepository, usuarioRepository);

        assertTrue(service.puedeVerPedido(25L, autenticacion("ROLE_VENDEDOR")));
        assertTrue(service.puedeVerPedido(25L, autenticacion("ROLE_ADMIN")));
    }

    @Test
    void usuarioSoloPuedeVerSusPedidos() {
        PedidoRepository pedidoRepository = mock(PedidoRepository.class);
        UsuarioRepository usuarioRepository = mock(UsuarioRepository.class);
        Usuario comprador = new Usuario();
        comprador.setId(8L);
        Usuario otroUsuario = new Usuario();
        otroUsuario.setId(9L);
        Pedido pedido = new Pedido();
        pedido.setUsuario(comprador);

        when(pedidoRepository.findById(25L)).thenReturn(Optional.of(pedido));
        when(usuarioRepository.findByEmail("comprador@example.com")).thenReturn(Optional.of(comprador));
        when(usuarioRepository.findByEmail("otro@example.com")).thenReturn(Optional.of(otroUsuario));

        PedidoAuthorizationService service = new PedidoAuthorizationService(pedidoRepository, usuarioRepository);

        assertTrue(service.puedeVerPedido(25L, autenticacion("ROLE_USUARIO", "comprador@example.com")));
        assertFalse(service.puedeVerPedido(25L, autenticacion("ROLE_USUARIO", "otro@example.com")));
    }

    private UsernamePasswordAuthenticationToken autenticacion(String role) {
        return autenticacion(role, "staff@example.com");
    }

    private UsernamePasswordAuthenticationToken autenticacion(String role, String email) {
        return new UsernamePasswordAuthenticationToken(
                email, null, List.of(new SimpleGrantedAuthority(role)));
    }
}
