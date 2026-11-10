package com.uade.e_commerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.model.Producto;
import com.uade.e_commerce.model.RolUsuario;
import com.uade.e_commerce.repository.ProductoRepository;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service("productoAuthorizationService")
public class ProductoAuthorizationService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoAuthorizationService(ProductoRepository productoRepository, UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public boolean puedeGestionar(Long productoId, Authentication authentication) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null || authentication == null) return false;
        if (authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        if (producto.getCreador() == null) {
            usuarioRepository.findFirstByRolOrderByIdAsc(RolUsuario.ADMIN).ifPresent(admin -> {
                producto.setCreador(admin);
                productoRepository.save(producto);
            });
        }
        return producto.getCreador() != null
                && authentication.getName().equalsIgnoreCase(producto.getCreador().getEmail());
    }
}
