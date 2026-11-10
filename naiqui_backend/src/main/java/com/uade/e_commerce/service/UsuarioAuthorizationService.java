package com.uade.e_commerce.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.uade.e_commerce.repository.UsuarioRepository;

@Service("usuarioAuthorizationService")
public class UsuarioAuthorizationService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAuthorizationService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public boolean puedeAcceder(Long usuarioId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        boolean esAdministrador = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
        if (esAdministrador) {
            return true;
        }

        return usuarioRepository.findByEmail(authentication.getName())
                .map(usuario -> usuarioId.equals(usuario.getId()))
                .orElse(false);
    }
}
