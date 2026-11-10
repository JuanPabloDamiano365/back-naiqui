package com.uade.e_commerce.controller;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.uade.e_commerce.dto.LoginRequestDTO;
import com.uade.e_commerce.dto.LoginResponseDTO;
import com.uade.e_commerce.dto.UsuarioSesionDTO;
import com.uade.e_commerce.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;
    private final CsrfTokenRepository csrfTokenRepository;
    private final boolean cookieSecure;
    private final String cookieSameSite;

    public AuthController(
            AuthService authService,
            CsrfTokenRepository csrfTokenRepository,
            @Value("${naiqui.auth.cookie-secure:false}") boolean cookieSecure,
            @Value("${naiqui.auth.cookie-same-site:Lax}") String cookieSameSite) {
        this.authService = authService;
        this.csrfTokenRepository = csrfTokenRepository;
        this.cookieSecure = cookieSecure;
        this.cookieSameSite = cookieSameSite;
    }

    @GetMapping("/csrf")
    public Map<String, String> csrfToken(HttpServletRequest request, HttpServletResponse response) {
        CsrfToken csrfToken = csrfTokenRepository.loadToken(request);
        if (csrfToken == null) {
            csrfToken = csrfTokenRepository.generateToken(request);
            csrfTokenRepository.saveToken(csrfToken, request, response);
        }
        return Map.of("token", csrfToken.getToken());
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO login = authService.login(request);
        ResponseCookie authCookie = ResponseCookie.from("naiqui_auth", login.getToken())
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api")
                .maxAge(Duration.ofHours(8))
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authCookie.toString())
                .body(login);
    }

    @GetMapping("/me")
    public UsuarioSesionDTO currentUser(Authentication authentication) {
        return authService.obtenerSesion(authentication.getName());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie expiredAuthCookie = ResponseCookie.from("naiqui_auth", "")
                .httpOnly(true)
                .secure(cookieSecure)
                .sameSite(cookieSameSite)
                .path("/api")
                .maxAge(Duration.ZERO)
                .build();
        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, expiredAuthCookie.toString())
                .build();
    }
}
