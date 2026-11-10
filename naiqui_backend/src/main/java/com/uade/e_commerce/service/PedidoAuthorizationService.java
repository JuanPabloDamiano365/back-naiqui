package com.uade.e_commerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.repository.PedidoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service("pedidoAuthorizationService")
public class PedidoAuthorizationService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;

    public PedidoAuthorizationService(PedidoRepository pedidoRepository, UsuarioRepository usuarioRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public boolean puedeVerPedido(Long pedidoId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean puedeVerTodos = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_VENDEDOR".equals(authority.getAuthority()));
        if (puedeVerTodos) {
            return true;
        }

        return pedidoRepository.findById(pedidoId)
                .flatMap(pedido -> usuarioRepository.findByEmail(authentication.getName())
                        .map(usuario -> usuario.getId().equals(pedido.getUsuario().getId())))
                .orElse(false);
    }
}
