package com.uade.e_commerce.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uade.e_commerce.dto.LoginRequestDTO;
import com.uade.e_commerce.dto.LoginResponseDTO;
import com.uade.e_commerce.exception.UnauthorizedException;
import com.uade.e_commerce.model.Usuario;
import com.uade.e_commerce.repository.UsuarioRepository;

@Service
@Transactional
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));

        String passwordGuardada = usuario.getPassword();
        if (passwordGuardada == null || passwordGuardada.isBlank()) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        boolean passwordValida =
                passwordGuardada.startsWith("$2a$") || passwordGuardada.startsWith("$2b$") || passwordGuardada.startsWith("$2y$")
                        ? passwordEncoder.matches(request.getPassword(), passwordGuardada)
                        : request.getPassword().equals(passwordGuardada);

        if (!passwordValida) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        if (!passwordGuardada.startsWith("$2a$") && !passwordGuardada.startsWith("$2b$") && !passwordGuardada.startsWith("$2y$")) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuarioRepository.save(usuario);
        }

        String token = jwtService.generarToken(usuario);

        return new LoginResponseDTO(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                token,
                "Login exitoso"
        );
    }
}
