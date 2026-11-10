package com.uade.e_commerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.model.ItemPedido;
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

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return true;
        }

        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_VENDEDOR"))) {
            return pedidoRepository.findById(pedidoId)
                    .map(pedido -> pedido.getItems().stream().anyMatch(item -> perteneceAlVendedor(item, authentication)))
                    .orElse(false);
        }

        return pedidoRepository.findById(pedidoId)
                .flatMap(pedido -> usuarioRepository.findByEmail(authentication.getName())
                        .map(usuario -> usuario.getId().equals(pedido.getUsuario().getId())))
                .orElse(false);
    }

    private boolean perteneceAlVendedor(ItemPedido item, Authentication authentication) {
        return item.getProducto().getCreador() != null
                && authentication.getName().equalsIgnoreCase(item.getProducto().getCreador().getEmail());
    }
}
